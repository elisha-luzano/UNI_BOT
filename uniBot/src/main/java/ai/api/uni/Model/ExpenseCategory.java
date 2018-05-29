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

public class ExpenseCategory {
    private Integer id;
    private String name;

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static Boolean insert(String name, DBHelper database) {
        return database.insertExpenseCategory(name);
    }

    public static ExpenseCategory getOne(String name, DBHelper database) {
        return database.getOneExpenseCategory(name);
    }

    public static  ArrayList<ExpenseCategory> getAll(DBHelper database) {
        return database.getAllExpenseCategories();
    }

    public static  void delete(Integer id, DBHelper database) {
        database.deleteExpenseCategory(id);
    }

    public static  Boolean delete(String name, DBHelper database) {
        return database.deleteExpenseCategory(name);
    }

    public static  Boolean update(Integer id, String name, DBHelper database) {
        return database.updateExpenseCategory(id, name);
    }

    public static  Boolean update(String old_name, String new_name, DBHelper database) {
        return database.updateExpenseCategory(old_name, new_name);
    }


}
