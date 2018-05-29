package ai.api.uni.Task;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import ai.api.uni.Activity.ChatActivity;
import ai.api.uni.Model.Holiday;

import static ai.api.uni.Activity.ChatActivity.database;
import static com.twitter.sdk.android.core.TwitterCore.TAG;

/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DownloadTwitterTask extends AsyncTask<String, Void, String> {
    final static String CONSUMER_KEY = /*twitter consumer key*/;
    final static String CONSUMER_SECRET = /*twitter consumer secret*/;
    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    private ChatActivity activity;
    private String location;
    private String date;
    private String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

    public DownloadTwitterTask(ChatActivity activity, String location, String date) {
        this.location = location;
        this.date = date;
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... screenNames) {
        Log.i(TAG, "pls");
        String result = null;

        Log.i(TAG, screenNames[0] + " sc ko to ");
        if (screenNames.length > 0) {
            result = getTwitterStream(screenNames[0]);
        }
        return result;
    }

    // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
    @Override
    protected void onPostExecute(String result) {
        Boolean flag = true;
        try {
            String name = Holiday.get(this.date, database);
            Log.i(TAG, this.date + " DATE HERE POH ");
            if(!name.equals("none")) {
                this.activity.addMessage("There are no classes since it's " + name + "!", "", "", "BOT");
                return;
            }
            JSONArray tweets;
            if(result.startsWith("{")) {
                JSONObject obj = new JSONObject(result);
                tweets = (JSONArray) obj.get("statuses");
                Log.i(TAG, "RESULT1" + tweets);

                if(tweets.length() == 0) {
                    flag = false;
                }
                else {
                    ArrayList<String> users = new ArrayList<String>();
                    this.activity.addMessage("Looks like there are no classes. See the Tweets that I have loaded for you. You can click on them to view the actual Tweet.", "", "", "BOT");
                    for(int i=0; i<tweets.length(); i++) {
                        String author = ((JSONObject) new JSONObject(tweets.get(i).toString()).get("user")).get("screen_name").toString();
                        String tweet = new JSONObject(tweets.get(i).toString()).get("full_text").toString();
                        String image_url = "";
                        JSONObject entities = new JSONObject(((JSONObject)(tweets.get(i))).get("entities").toString());

                        JSONObject user = new JSONObject(((JSONObject)(tweets.get(i))).get("user").toString());
                        String user_id = user.get("id").toString();
                        if(users.contains(user_id)) continue;
                        users.add(user_id);

                        if(!(entities.has("media"))) {
                            String url_post = "";
                            if(((JSONArray) entities.get("urls")).length() == 0) continue;
                            else url_post =  ((JSONObject)(((JSONArray) entities.get("urls")).get(0))).get("url").toString();
                            image_url = user.get("profile_banner_url").toString();
                            this.activity.addMessage("@" + author + ": " + tweet, image_url, url_post, "TWITTER");
                            continue;
                        }

                        JSONObject media = (JSONObject) ((JSONArray)(entities.get("media"))).get(0);
                        String post_url = media.get("url").toString();
                        if(media.has("media_url_https") && !media.get("media_url_https").equals("")) {
                            image_url = media.get("media_url_https").toString();
                            Log.i(TAG, "img_url1" + image_url);
                        } else {
                            image_url = user.get("profile_banner_url").toString();
                            Log.i(TAG, "img_url2" + image_url);
                        }
                        this.activity.addMessage("@" + author + ": " + tweet, image_url, post_url, "TWITTER");
                    }
                }
            }
            else {
                tweets = new JSONArray(result);

                SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
                SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                try {
                    Date date = format1.parse(this.date);
                    String actual_date = format2.format(date);
                    cal.setTime(sdf.parse(actual_date));
                    String month = new SimpleDateFormat("MMM").format(cal.getTime());
                    String day = new SimpleDateFormat("dd").format(cal.getTime());
                    String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                    Integer count = 0;

                    for(int i=0; i<tweets.length(); i++) {
                        String tweet = new JSONObject(tweets.get(i).toString()).get("full_text").toString().toLowerCase();
                        if(tweet.contains(this.location.toLowerCase()) && tweet.contains(month.toLowerCase()) &&
                                tweet.contains(day) && tweet.contains(year)) {
                            this.activity.addMessage(new JSONObject(tweets.get(i).toString()).get("full_text").toString(), "", "", "BOT");
                            count ++;
                        }
                    }
                    if(count == 0) {
                        flag = false;
                    }
                } catch(ParseException e) {
                    flag = false;
                    Log.i(TAG, e.getMessage() + " ERR HERE PARSE");
                }
            }

        } catch(JSONException e) {
            flag = false;
            Log.i(TAG, e.getMessage() + " " + e.getCause() + " JSON ERR");
        }

        if(flag == false) {
            this.activity.addMessage("I've searched for suspensions and holidays; however, I didn't find any. Sorry, I guess school isn't cancelled.", "", "", "BOT");
        }

        if(database.getHelpValue("twitter") == 0){
            this.activity.addMessage("To ensure credibility, I only load Tweets from verified news sources" +
                    " like @GMANews, @ABSCBNews, @GovPH, @ManillaBulletin, and @PhilippineStar. And I also search for holidays on Google.", "", "", "BOT");
            database.setHelpValue("twitter");
        }

        if(database.getHelpValue("ARE_THERE_CLASSES") == 0) {
            database.setHelpValue("ARE_THERE_CLASSES");
            this.activity.addMessage("Great! Next, since we're talking about school, try asking me to show your class schedule.", "", "", "BOT");
            database.revertHelpValue("ARE_THERE_CLASSES");
        }
    }

    private String getTwitterStream(String link) {
        String results = "";
        try {

            String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

            String combined = urlApiKey + ":" + urlApiSecret;

            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);
            String basicAuth = "Basic " + base64Encoded;

            URL myURL = new URL(TwitterTokenURL);
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty ("Authorization", basicAuth);
            myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);

            String res = "";
            OutputStream os = myURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("grant_type=client_credentials");
            writer.flush();
            writer.close();
            os.close();
            int responseCode=myURLConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    res+=line;
                }
                br.close();
            }

            JSONObject jsonRes = new JSONObject(res);
            if (res != null && jsonRes.get("token_type").equals("bearer")) {
                URL twitterUrl = new URL(link);
                HttpURLConnection twitterUrlConnection = (HttpURLConnection)twitterUrl.openConnection();
                twitterUrlConnection.setRequestMethod("GET");
                twitterUrlConnection.setRequestProperty ("Authorization", "Bearer " + jsonRes.get("access_token").toString());
                twitterUrlConnection.setRequestProperty("Content-type", "application/json");

                twitterUrlConnection.setDoInput(true);
                twitterUrlConnection.setDoOutput(false);
                Log.i(TAG, link + " " + twitterUrlConnection.getResponseCode() + " twitter response");

                Log.i(TAG, "1");
                InputStream responseStream = new
                        BufferedInputStream(twitterUrlConnection.getInputStream());

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
                    twitterUrlConnection.disconnect();
            }
            myURLConnection.disconnect();
        } catch (UnsupportedEncodingException ex) {
            Log.i(TAG, ex.getStackTrace() + " ERR HERE1 ");
        } catch (IllegalStateException ex1) {
            Log.i(TAG, ex1.getStackTrace() + " ERR HERE2 ");
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }catch (JSONException ex3) {
            Log.i(TAG, ex3.getStackTrace() + " ERR HERE4 ");
        }
        return results;
    }
}