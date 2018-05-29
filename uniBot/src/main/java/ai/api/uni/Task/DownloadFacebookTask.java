package ai.api.uni.Task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ai.api.uni.Activity.ChatActivity;

import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 3/20/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadFacebookTask extends AsyncTask<String, Void, String> {
    private ChatActivity activity;
    private String token;
    private String type;
    private String date;
    private String location;

    public DownloadFacebookTask(ChatActivity activity, String token, String type, String date, String location) {
        this.activity = activity;
        this.token = token;
        this.type = type;
        this.date = date;
        this.location = location;
    }

    @Override
    protected String doInBackground(String... arg0) {
        String results = "";
        try {
            //URL urlToRequest = new URL("https://graph.facebook.com/v2.11/search?q=marathon%20dublin&type=event&fields=name,id,category,description,place&access_token="+token);
            String new_location = this.location.replaceAll("\\s", "%20");
            Log.i(TAG, token + "TOKEN!!");
            URL urlToRequest = new URL("https://graph.facebook.com/v2.11/search?q=" + new_location + "&type=event&fields=name,id,category,description,place,time,start_time,end_time,cover&access_token="+token);
            Log.i(TAG, "https://graph.facebook.com/v2.11/search?q=" + new_location + "&type=event&fields=name,id,category,description,place,time,start_time,end_time,cover&access_token=");
            Log.i(TAG, "0");
            HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            Log.i(TAG, "1");
            /*urlConnection.setConnectTimeout(CONN_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);*/

            urlConnection.setDoInput(true);
            //urlConnection.setDoOutput(false);

            int statusCode = urlConnection.getResponseCode();
            Log.i(TAG, "2");
            Log.i(TAG, "status code " + statusCode);
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Log.i(TAG, "ays tol");
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "not ays");
            }

            InputStream responseStream = new
                    BufferedInputStream(urlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            results = response;
            responseStream.close();

            urlConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }


    @Override
    protected void onPostExecute(String result) {
        ArrayList<JSONObject> events = new ArrayList<JSONObject>();
        JSONObject place = null;
        JSONObject location = null;
        String city = "";
        String country = "";

        if(!result.equals("")) {
            try {
                JSONArray data = (JSONArray) new JSONObject(result).get("data");
                Log.i(TAG, "SIZE" + data.length());
                Integer count = 0;
                String data_string = "";
                String cover_url = "";
                String event_url = "";
                //DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                SimpleDateFormat outgoingFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", java.util.Locale.getDefault());
                Calendar cal = Calendar.getInstance();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject current_data = (JSONObject) data.get(i);
                    if (current_data.has("place")) place = (JSONObject) current_data.get("place");
                    if (place != null && place.has("location"))
                        location = (JSONObject) place.get("location");
                    if (location != null && location.has("city"))
                        city = location.get("city").toString().toLowerCase();
                    if (location != null && location.has("country"))
                        country = location.get("country").toString().toLowerCase();

                    if (city.equals(this.location) && country.equals("philippines")) {
                        count++;
                        if (count == 1)
                            this.activity.addMessage("These are the events loaded from Facebook:", "", "", "BOT");
                        Log.i(TAG, "ONE HERE!" + ((JSONObject) data.get(i)).toString());
                        events.add((JSONObject) data.get(i));
                        data_string = ("[" + count + "] ");
                        if (((JSONObject) data.get(i)).has("name"))
                            data_string += "Name: " + ((JSONObject) data.get(i)).get("name").toString();
                        if (place != null)
                            data_string += "\nLocation: " + place.get("name").toString();
                        if (((JSONObject) data.get(i)).has("start_time")) {
                            String start_time = ((JSONObject) data.get(i)).get("start_time").toString();
                            Date date = incomingFormat.parse(start_time);
                            data_string += "\nStarts at: " + outgoingFormat.format(date);
                        }
                        if (((JSONObject) data.get(i)).has("end_time")) {
                            String end_time = ((JSONObject) data.get(i)).get("end_time").toString();
                            Date date = incomingFormat.parse(end_time);
                            data_string += "\nEnds at: " + outgoingFormat.format(date);
                        }
                        if (((JSONObject) data.get(i)).has("cover")) {
                            cover_url = ((JSONObject) ((JSONObject) data.get(i)).get("cover")).get("source").toString();
                        }
                        event_url = "https://www.facebook.com/" + ((JSONObject) data.get(i)).get("id");
                        this.activity.addMessage(data_string, cover_url, event_url, "FACEBOOK");
                    }
                    place = null;
                    location = null;
                    city = "";
                    country = "";
                    cover_url = "";
                    event_url = "";
                }

                if (count == 0)
                    this.activity.addMessage("No events found from Facebook.", "", "", "BOT");
                else {
                    this.activity.addMessage("If you want to get reminded of these events, just reply with the number of the event.", "", "", "BOT");
                }

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        } else {
            this.activity.addMessage("No events found from Facebook.", "", "", "BOT");
        }



    }
}
