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

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 3/18/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadGradesTask extends AsyncTask<String, Void, String> {
    private ChatActivity activity;

    public DownloadGradesTask(ChatActivity activity) {
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
        Grade.deleteAll(database);
        String message = "";
        if(result.equals("")) return;
        Document doc = Jsoup.parse(result);
        Element tbody = doc.getElementById("CRSE_HIST$scroll$0").child(0);
        Integer rows_count = tbody.children().size();
        ArrayList<Grade> grades = new ArrayList<Grade>();
        Float gwa = 0.0f;
        Float units = 0.0f;
        String code_str = "CRSE_NAME$";
        String grade_str = "IBT_GRD_LTR_WRK_DESCR$";
        String term_str = "CRSE_TERM$";
        String title_str = "CRSE_LINK$";
        String units_str = "CRSE_UNITS$";


        for(int i=0; i<rows_count-1; i++) {
            if(i==0) {
                this.activity.addMessage("YOUR GRADES", "", "", "SCHEDULE_HEADER");
                this.activity.addMessage("SUBJECT", "GRADE", "UNITS", "GRADE_NEW");
            }
            Float gr = Float.parseFloat("0");
            if(!doc.getElementById(grade_str+i).text().equals("") && !doc.getElementById(grade_str+i).text().equals("S")
                    && !doc.getElementById(grade_str+i).text().equals("Incomplete") && !doc.getElementById(grade_str+i).text().equals("U")
                    && !doc.getElementById(grade_str+i).text().equals("DRP") && !doc.getElementById(grade_str+i).text().equals("Drop") &&
                    !doc.getElementById(grade_str+i).text().equals("Dropped"))
                gr = Float.parseFloat(doc.getElementById(grade_str+i).text());
            else if(doc.getElementById(grade_str+i).text().equals("S")) gr = 7.0f;
            else if(doc.getElementById(grade_str+i).text().equals("U")) gr = 8.0f;
            else if(doc.getElementById(grade_str+i).text().equals("INC") || doc.getElementById(grade_str+i).text().equals("Incomplete")) gr = 9.0f;
            else if(doc.getElementById(grade_str+i).text().equals("DRP") || doc.getElementById(grade_str+i).text().equals("Drop")
                    || doc.getElementById(grade_str+i).text().equals("Dropped")) gr = 10.0f;

            Grade grade = new Grade(
                    doc.getElementById(code_str+i).text(),
                    gr,
                    doc.getElementById(term_str+i).text(),
                    doc.getElementById(title_str+i).text(),
                    Float.parseFloat(doc.getElementById(units_str+i).text())
            );

            grades.add(grade);
            if(gr != 0.0f){
                Grade.insert(grade.getCode(), grade.getGrade(), grade.getTerm(), grade.getTitle(), grade.getUnits(), database);
                if(gr != 7.0f && gr != 8.0f && gr != 9.0f && gr != 10.0f && !grade.getCode().startsWith("NSTP") && !grade.getCode().startsWith("PE 2") && !grade.getCode().startsWith("PE 1")){
                    units += grade.getUnits();
                    gwa += (grade.getGrade() * grade.getUnits());
                }
                message += (grade.getCode() + "  -  " + grade.getGrade() + "\n");
                this.activity.addMessage(grade.getCode(), grade.getGrade().toString(), grade.getUnits().toString(), "GRADE_NEW");
            }
        }
        if(message.equals("")) message = "You have no grades loaded from SAIS.";
        else {
            message = "Here are your grades.";
            gwa = gwa/units;
            Log.i(TAG, "GWA KO!" + gwa);
            message = message + " Based from this, your General Weighted Average is " + gwa + ".";
        }
        this.activity.addMessage(message, "", "", "BOT");

        /*if(database.getHelpValue("grades_load") == 0) {
            this.activity.addMessage("The ones in blue are 1.00. The ones in red are your failing grades. Try to work on them next time. You got this!", "", "", "BOT");
            database.setHelpValue("grades_load");
            if(database.getHelpValue("GRADES") == 0) database.setHelpValue("GRADES");

        }*/
        Log.i(TAG, grades.toString());

        /*for (Element row : rows) {
            Element detachedChild = new Element(Tag.valueOf(row.tagName()),
                    row.baseUri(), row.attributes().clone());
            Log.i(TAG, detachedChild.toString());
        }*/

        if(database.getHelpValue("GRADES") == 0) {
            database.setHelpValue("GRADES");
            this.activity.addMessage("Easy as that! Okay, now, let's move on to my organizing skills. First, I can " +
                    "also help you manage your tasks. Just tell me to schedule a task.", "", "", "BOT");
            database.revertHelpValue("GRADES");
        }
    }
}
