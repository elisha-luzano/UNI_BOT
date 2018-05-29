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

public class FeeCategory {
    private Integer id;
    private String name;
    private String bill_date;
    private String due_date;

    public FeeCategory() {}

    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBillDate(String bill_date) {
        this.bill_date = bill_date;
    }
    public void setDueDate(String due_date) {
        this.due_date = due_date;
    }

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getBillDate() {
        return this.bill_date;
    }
    public String getDueDate() {
        return this.due_date;
    }


    public static ArrayList<FeeCategory> getAll(DBHelper database) {
        return database.getAllFeeCategories();
    }

    public static Boolean insert(String name, String bill_date, String due_date, DBHelper database) {
        return database.insertFeeCategory(name, bill_date, due_date);
    }

    public static Boolean delete(String name, DBHelper database) {
        return database.deleteFeeCategory(name);
    }

    public static Boolean update(JsonElement name, JsonElement bill_date, JsonElement due_date, String old_name, DBHelper database) {
        return database.updateFeeCategory(name, bill_date, due_date, old_name);
    }

    public static ArrayList<FeeCategory> getSome(JsonElement params_name, JsonElement params_bill_date, JsonElement params_due_date,
                                                              DBHelper database) {
        String query = "select * from fee_categories where ";
        ArrayList<String> arguments_al = new ArrayList<String>();
        String name = "null";
        String bill_date = "null";
        String due_date = "null";

        if(params_name != null) {
            name = params_name.getAsString();
            query += "name = ? and ";
            arguments_al.add(name);
        }
        if(params_bill_date != null) {
            bill_date = params_bill_date.getAsString();
            query += "bill_date = ? and ";
            arguments_al.add(bill_date);
        }
        if(params_due_date != null) {
            due_date = params_due_date.getAsString();
            query += "due_date = ? and ";
            arguments_al.add(due_date);
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
        ArrayList<FeeCategory> fee_categories = database.getSomeFeeCategories(query, arguments);
        return fee_categories;
    }

    public static FeeCategory getByName(String name, DBHelper database) {
        return database.getFeeCategoryByName(name);
    }
}
