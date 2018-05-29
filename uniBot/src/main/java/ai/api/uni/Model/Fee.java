package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 2/24/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Fee {
    private Integer id;
    private Integer bill_month;
    private Integer bill_date;
    public Fee() {}

    public Integer getId() {
        return this.id;
    }
    public Integer getBillMonth() {
        return this.bill_month;
    }
    public Integer getBillDate() {
        return this.bill_date;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setBillMonth(Integer bill_month) {
        this.bill_month = bill_month;
    }
    public void setBillDate(Integer bill_date) {
        this.bill_date = bill_date;
    }

    public static void insert(Integer fee_category_id, Integer bill_month, Integer bill_date, DBHelper database) {
        database.insertFee(fee_category_id, bill_month, bill_date);
    }

    public static void delete(Integer id, DBHelper database) {
        database.deleteFee(id);
    }

    public static ArrayList<Fee> getUsingCategory(Integer category_id, DBHelper database) {
        return database.getFeeUsingCategory(category_id);
    }
}
