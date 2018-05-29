package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 3/14/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class BaseMessage {
    private Integer id;
    private String message;
    private String date;
    private String image_url;
    private String post_url;
    private String sender;

    public BaseMessage(Integer id, String message, String date, String image_url, String post_url, String sender) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.image_url = image_url;
        this.post_url = post_url;
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getMessage() {
        return this.message;
    }
    public String getDate() {
        return this.date;
    }
    public String getSender() {
        return this.sender;
    }
    public String getImage_url() {
        return this.image_url;
    }
    public String getPost_url() {
        return this.post_url;
    }

    public static Integer insert(String message, String date_time, String image_url, String post_url, String sender, DBHelper database){
        return database.insertMessage(message,date_time,image_url,post_url,sender);
    }

    public static ArrayList<BaseMessage> getAll(DBHelper database) {
        return database.getAllMessages();
    }

    public static void deleteAll(DBHelper database) {
        database.deleteAllMessages();
    }
}
