package ai.api.uni.Model;

import android.util.Log;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 2/23/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Task {
    private Integer id;
    private String name;
    private String date;
    private String time_start;
    private String time_end;
    private String specific;
    private String priority;
    private String note;

    public Task () {}

    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setTimeStart(String time_start) {
        this.time_start = time_start;
    }
    public void setTimeEnd(String time_end) {
        this.time_end = time_end;
    }
    public void setSpecific(String specific) {
        this.specific = specific;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setNote(String note) {
        this.note = note;
    }

    // getters
    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDate() {
        return this.date;
    }
    public String getTimeStart() {
        return this.time_start;
    }
    public String getTimeEnd() {
        return this.time_end;
    }
    public String getSpecific() {
        return this.specific;
    }
    public String getPriority() {
        return this.priority;
    }
    public String getNote() {
        return this.note;
    }

    public static void insert(String name, String date, String time_start, String time_end, String specific, String priority,
                              String note, DBHelper database) {
        database.insertTask(name, date, time_start, time_end, specific, priority, note);
    }

    public static void delete(Integer id, DBHelper database) {
        database.deleteTask(id);
    }

    public static Boolean update(JsonElement name, JsonElement date, JsonElement time_start, JsonElement time_end, JsonElement specific, JsonElement priority, JsonElement note, Integer id, DBHelper database) {
        return database.updateTask(name, date, time_start, time_end, specific, priority, note, id);
    }

    public static ArrayList<Task> getAll(DBHelper database) {
        return database.getAllTasks();
    }

    public static ArrayList<Task> getSome(JsonElement params_name, JsonElement params_date, JsonElement params_time_start,
                                       JsonElement params_time_end, JsonElement params_specific, JsonElement params_priority, DBHelper database) {
        String query = "select * from tasks where ";
        ArrayList<String> arguments_al = new ArrayList<String>();
        String name = "null";
        String date = "null";
        String time_start = "null";
        String time_end = "null";
        String specific = "null";
        String priority = "null";

        if(params_name != null) {
            name = params_name.getAsString();
            query += "name = ? and ";
            arguments_al.add(name);
        }if(params_date != null) {
            date = params_date.getAsString();
            query += "date = ? and ";
            arguments_al.add(date);
        }if(params_time_start != null) {
            time_start = params_time_start.getAsString();
            query += "time_start = ? and ";
            arguments_al.add(time_start);
        }if(params_time_end != null) {
            time_end = params_time_end.getAsString();
            query += "time_end = ? and ";
            arguments_al.add(time_end);
        }if(params_specific != null) {
            specific = params_specific.getAsString();
            query += "specific = ? and ";
            arguments_al.add(specific);
        }if(params_priority != null) {
            priority = params_priority.getAsString();
            query += "priority = ? and ";
            arguments_al.add(priority);
        }

        String[] arguments = arguments_al.toArray(new String[arguments_al.size()]);

        if(query.substring(query.length() - 5).equals(" and ")) {
            query = query.substring(0, query.length() - 5);
            query += ";";
        } else if(query.substring(query.length() - 7).equals(" where ")) {
            query = query.substring(0, query.length() - 7);
            query += ";";
        }
        Log.i(TAG, query);
        ArrayList<Task> tasks = database.getSomeTask(query, arguments);
        return tasks;
    }
}
