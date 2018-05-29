package ai.api.uni.Task;

import android.location.Address;
import android.location.Geocoder;
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
import java.util.Date;
import java.util.List;

import ai.api.uni.Activity.ChatActivity;

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 4/14/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadEventbriteTask extends AsyncTask<String, Void, String> {
    private String location;
    private String date;
    private String type;
    private String date_value;
    private ChatActivity activity;

    public DownloadEventbriteTask(ChatActivity activity, String location, String date, String type) {
        this.activity = activity;
        this.location = location;
        this.date = date;
        this.type = type;
        this.date_value = date;
    }

    @Override
    protected String doInBackground(String... arg0) {
        Log.i(TAG, "hi");
        String results = "";
        try {
            double latitude = 0.0;
            double longitude = 0.0;
            Boolean flag = false;
            Geocoder geocoder = new Geocoder(this.activity);
            while(flag == false) {
                List<Address> addresses;
                if(location.toLowerCase().contains("nearby") ||
                        location.toLowerCase().contains("near")) location = "calamba";
                if(location.toLowerCase().contains("here") ||
                        location.toLowerCase().contains("in here")) location = "los baños";
                addresses = geocoder.getFromLocationName(location + ", philippines", 1);
                if (addresses.size() > 0) {
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                    flag = true;
                }
                Log.i(TAG, "henlo");
            }
            if(!this.date.equals("")) this.date = "&start_date.range_start=" + this.date + "T00:00:00Z";
            if(!this.type.equals("")) this.type = "q=" + type + "&";
            URL urlToRequest = new URL("https://www.eventbriteapi.com/v3/events/search/?" + this.type + "location.latitude=" + latitude + "&location.longitude=" + longitude + this.date + "&expand=venue&token=BAR7GCSMIBZG3KX7U22P");
            Log.i(TAG, latitude + " " + longitude);
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
            Log.i(TAG, e.getMessage());
        }
        return results;
    }
    //nuull venue

    @Override
    protected void onPostExecute(String result) {
        JSONArray events = new JSONArray();
        try {
            Log.i(TAG, result);
            JSONObject result_object = new JSONObject(result);
            events = (JSONArray) result_object.get("events");
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat outgoingFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", java.util.Locale.getDefault());
            Date date = new Date();
            String name = "";
            String location = "";
            String start = "";
            String end = "";
            String price = "";
            String event_url = "";
            String image_url = "";
            String content = "";
            if(events.length() != 0) {
                this.activity.addMessage("EVENTBRITE EVENTS", "", "", "SCHEDULE_HEADER");
                for(int i=0; i<events.length(); i++) {
                    if(((JSONObject) events.get(i)).has("name"))
                        name = ((JSONObject)((JSONObject) events.get(i)).get("name")).get("text").toString();

                    if( ((JSONObject) events.get(i)).has("venue") && ((JSONObject)((JSONObject) events.get(i)).get("venue")).has("name") &&
                            !((JSONObject)((JSONObject) events.get(i)).get("venue")).get("name").toString().equals("null"))
                        location = "\nVenue: " + ((JSONObject)((JSONObject) events.get(i)).get("venue")).get("name").toString();
                    else location = "\nVenue: Not available";

                    if(((JSONObject) events.get(i)).has("start") && ((JSONObject)((JSONObject) events.get(i)).get("start")).has("utc")) {
                        date = incomingFormat.parse(
                                ((JSONObject)((JSONObject) events.get(i)).get("start")).get("utc").toString()
                        );
                    }
                    String date_remind = outgoingFormat.format(date);
                    if(outgoingFormat.format(date) != null) start = "\nStarts at: " + outgoingFormat.format(date);
                    else start = "\nStarts at: Not available";

                    if(!((JSONObject)((JSONObject) events.get(i)).get("start")).get("utc").toString().startsWith(this.date_value)) {
                        continue;
                    }

                    if(((JSONObject) events.get(i)).has("end") && ((JSONObject)((JSONObject) events.get(i)).get("end")).has("utc")) {
                        date = incomingFormat.parse(
                                ((JSONObject)((JSONObject) events.get(i)).get("end")).get("utc").toString()
                        );
                    }
                    if(outgoingFormat.format(date) != null) end = "\nEnds at: " + outgoingFormat.format(date);
                    else end = "Ends at: Not available";

                    event_url = ((JSONObject) events.get(i)).get("url").toString();
                    if( (JSONObject) events.get(i) != null && (((JSONObject) events.get(i)).has("logo")) &&
                        ((JSONObject)((JSONObject) events.get(i)).get("logo")).has("url") ) {
                        image_url = ((JSONObject)((JSONObject) events.get(i)).get("logo")).get("url").toString();
                    } else image_url = "";
                    content = name + location + start + end;

                    this.activity.addMessage(content, event_url + " " + image_url, date_remind, "FACEBOOK");
                }
                this.activity.addMessage("You can also click on the event to open its link.", "", "", "BOT");
                this.activity.addMessage("You can also click on the calendar icon to add the event to your calendar.", "", "", "BOT");
            } else {
                this.activity.addMessage("Oops! Unfortunately, there are no upcoming events in that location.", "", "", "BOT");
            }
        } catch (Exception e) {
            if(events.length() == 0) this.activity.addMessage("I have failed to retrieve the events. Please check your Internet connection.", "", "", "BOT");

            Log.i(TAG, "JSON ERR:" + e.getMessage());
        }
        if(database.getHelpValue("EVENTS") == 0) {
            database.setHelpValue("EVENTS");
            this.activity.addMessage("Down to the last! Alright, so you might be looking for places to eat, maybe chicken? Ask me where to buy chicken and I'll search for you.", "", "", "BOT");
            database.setHelpValue("LAST");
            database.revertHelpValue("EVENTS");
        }
    }
}
