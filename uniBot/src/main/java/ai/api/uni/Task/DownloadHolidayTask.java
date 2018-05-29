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

import ai.api.uni.Activity.ChatActivity;
import ai.api.uni.Model.Holiday;

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 5/8/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadHolidayTask  extends AsyncTask<String, Void, String> {
    private ChatActivity activity;

    public DownloadHolidayTask(ChatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... url) {
        String results = "error";
        try {
            URL urlToRequest = new URL(url[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            Log.i(TAG, "1");
            /*urlConnection.setConnectTimeout(CONN_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);*/

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);

            int statusCode = urlConnection.getResponseCode();
            Log.i(TAG, "2");
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
        Log.i("Holiday", result);
        if(!result.equals("error")) {
            Holiday.deleteAll(database);
            try {
                JSONObject response = new JSONObject(result);

                JSONArray items;
                if(response.has("items")) items = (JSONArray) response.get("items");
                else return;

                for(int i=0; i<items.length(); i++) {
                    JSONObject holiday = (JSONObject) items.get(i);
                    String name = holiday.get("summary").toString();
                    String date = ((JSONObject) holiday.get("start")).get("date").toString();

                    Holiday.add(name, date, database);

                    Log.i(TAG, "HOLIDAY! " + name + date);
                }
            } catch(Exception e) {
                Log.i(TAG, "JSON ERR " + e.getMessage());
            }
        }
    }
}
