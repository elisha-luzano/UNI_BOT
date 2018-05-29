package ai.api.uni.Task;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ai.api.uni.Activity.ChatActivity;
import ai.api.uni.Model.Grade;
import ai.api.uni.Model.Subject;

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 3/18/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadSubjectsTask extends AsyncTask<String, Void, String> {
    private ChatActivity activity;

    public DownloadSubjectsTask(ChatActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... url) {
        String results = "error";
        try {
            URL urlToRequest = new URL(url[0]);
            String cookie = url[1];
            String[] cookies = cookie.split("; ");
            Log.i(TAG, "0");
            HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();

            urlConnection.setRequestProperty("Cookie", cookie);


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
        Subject.deleteAll(database);
        String message = "";

        Document doc = Jsoup.parse(result);
        Element tbody = doc.getElementById("STDNT_WEEK_SCHD$scroll$0").child(0);
        Integer rows_count = tbody.children().size();
        ArrayList<Grade> grades = new ArrayList<Grade>();
        String code_str = "win0divCLASS_NAME$";
        String date_time_location_str = "DERIVED_SSS_SCL_SSR_MTG_SCHED_LONG$";


        for(int i=0; i<rows_count-2; i++) {
            if(i==0) this.activity.addMessage("CLASS SCHEDULE","","","SCHEDULE_HEADER");
            Subject subject = new Subject();
            String name = doc.getElementById(code_str+i).child(0).text();
            subject.setName(name);
            String date_time_location = doc.getElementById(date_time_location_str+i).wholeText();
            String[] date_time_location_split = date_time_location.split("\n");
            if(date_time_location_split.length == 1) subject.setLocation(date_time_location_split[0]);
            else if(date_time_location_split.length == 2) {
                Log.i(TAG, "DATETIME" + date_time_location_split[0]);
                if(date_time_location_split[0] == null) subject.setDate_time("N/A");
                else subject.setDate_time(date_time_location_split[0]);
                String loc = "";
                if(date_time_location_split[1].contains("null")) loc = "Not available";
                else loc = date_time_location_split[1];
                subject.setLocation(loc);
            }
            subject.setAbsences(0);
            Subject.insert(subject.getName(), subject.getDate_time(), subject.getLocation(),
                    subject.getAbsences(), database);

            message += name + "\n\tVenue: " + subject.getLocation() + "\n\tSchedule: " + subject.getDate_time();
            this.activity.addMessage(name, subject.getLocation() + "\n" + subject.getDate_time(), "Absences: 0", "TASK");

        }

        if(message.equals("")) {
            message = "You have no subjects loaded from SAIS.";
            this.activity.addMessage(message, "", "", "BOT");
        }
        else {
            this.activity.addMessage("", "", "", "SUBJECTS_BUTTON");
            message = "Here are your subjects  for this semester.";
            this.activity.addMessage(message, "", "", "BOT");
            this.activity.addMessage("Currently, I have no record of your absences. But you can always add to these records by telling me that you missed a class.\n" +
                    "I will also remind you if you are nearing the maximum absences allowed.", "", "", "BOT");
            Log.i(TAG, grades.toString());
        }

        if(database.getHelpValue("SHOW_SCHEDULE") == 0) {
            database.setHelpValue("SHOW_SCHEDULE");
            this.activity.addMessage("I hope you're getting the hang of it. Now, try telling me that you missed a class. Let's see what I'll do.", "", "", "BOT");
            database.revertHelpValue("SHOW_SCHEDULE");
        }

    }
}
