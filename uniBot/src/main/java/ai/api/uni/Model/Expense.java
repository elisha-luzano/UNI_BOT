package ai.api.uni.Model;

import java.util.ArrayList;

import ai.api.uni.Database.DBHelper;

/**
 * Created by Lenovo-G4030 on 3/9/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Expense {
    private Integer id;
    private Float amount;
    private String date;
    private Integer category_id;

    public Integer getId() {
        return this.id;
    }
    public Float getAmount() {
        return this.amount;
    }
    public String getDate() {
        return this.date;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setAmount(Float amount) {
        this.amount = amount;
    }
    public void setDate(String date) {
        this.date = date;
    }public void setCategoryId(Integer category_id) {
        this.category_id = category_id;
    }

    public static Boolean insert(Float amount, String date, Integer category_id, DBHelper database) {
        return database.insertExpense(amount, category_id, date);
    }

    public static Expense getOne(Integer id, DBHelper database) {
        return database.getOneExpense(id);
    }

    public static  ArrayList<Expense> getAll(Integer category_id, DBHelper database) {
        return database.getAllExpenses(category_id);
    }

    public static Boolean delete(Integer id, DBHelper database) {
        return database.deleteExpense(id);
    }

    public static Boolean updateDate(Integer id, String date, DBHelper database) {
        return database.updateExpenseDate(id, date);
    }

    public static Boolean updateAmount(Integer id, Float amount, DBHelper database) {
        return database.updateExpenseAmount(id, amount);
    }

}