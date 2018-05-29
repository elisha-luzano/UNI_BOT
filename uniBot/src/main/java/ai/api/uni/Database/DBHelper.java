package ai.api.uni.Database;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonElement;

import ai.api.uni.Model.Expense;
import ai.api.uni.Model.ExpenseCategory;
import ai.api.uni.Model.Fee;
import ai.api.uni.Model.FeeCategory;
import ai.api.uni.Model.Folder;
import ai.api.uni.Model.Grade;
import ai.api.uni.Model.Photo;
import ai.api.uni.Model.Subject;
import ai.api.uni.Model.Task;
import ai.api.uni.Model.BaseMessage;

import static ai.api.uni.Activity.ChatActivity.TAG;
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "uni.db";

    public static final String TASKS_TABLE_NAME = "task";
    public static final String TASKS_COLUMN_ID = "id";
    public static final String TASKS_COLUMN_NAME = "name";
    public static final String TASKS_COLUMN_DATE = "date";
    public static final String TASKS_COLUMN_TIME_START = "time_start";
    public static final String TASKS_COLUMN_TIME_END = "time_end";
    public static final String TASKS_COLUMN_SPECIFIC = "specific";
    public static final String TASKS_COLUMN_PRIORITY = "priority";
    public static final String TASKS_COLUMN_NOTE = "note";

    public static final String FEE_CATEGORY_TABLE_NAME = "fee_categories";
    public static final String FEE_CATEGORY_COLUMN_ID = "id";
    public static final String FEE_CATEGORY_COLUMN_NAME = "name";
    public static final String FEE_CATEGORY_COLUMN_BILL_DATE = "bill_date";
    public static final String FEE_CATEGORY_COLUMN_DUE_DATE = "due_date";

    public static final String FEE_TABLE_NAME = "fees";
    public static final String FEE_COLUMN_ID = "id";
    public static final String FEE_COLUMN_CATEGORY_ID = "fee_category_id";
    public static final String FEE_COLUMN_BILL_DATE = "bill_date";
    public static final String FEE_COLUMN_BILL_MONTH = "bill_month";

    public static final String FOLDER_TABLE_NAME = "folders";
    public static final String FOLDER_COLUMN_ID = "id";
    public static final String FOLDER_COLUMN_NAME = "name";

    public static final String PHOTO_TABLE_NAME = "photos";
    public static final String PHOTO_COLUMN_ID = "id";
    public static final String PHOTO_COLUMN_NAME = "name";
    public static final String PHOTO_COLUMN_FOLDER_ID = "folder_id";

    public static final String GRADE_TABLE_NAME = "grades";
    public static final String GRADE_COLUMN_CODE = "code";
    public static final String GRADE_COLUMN_GRADE = "grade";
    public static final String GRADE_COLUMN_TERM = "term";
    public static final String GRADE_COLUMN_TITLE = "title";
    public static final String GRADE_COLUMN_UNITS = "units";

    public static final String EXPENSE_CATEGORY_TABLE_NAME = "expense_categories";
    public static final String EXPENSE_CATEGORY_COLUMN_ID = "id";
    public static final String EXPENSE_CATEGORY_COLUMN_NAME = "name";

    public static final String EXPENSE_TABLE_NAME = "expenses";
    public static final String EXPENSE_COLUMN_ID = "id";
    public static final String EXPENSE_COLUMN_AMOUNT = "amount";
    public static final String EXPENSE_COLUMN_DATE = "date";
    public static final String EXPENSE_COLUMN_CATEGORY_ID = "category_id";

    public static final String MESSAGE_TABLE_NAME = "messages";
    public static final String MESSAGE_COLUMN_MESSAGE = "message";
    public static final String MESSAGE_COLUMN_DATE_TIME = "date_time";
    public static final String MESSAGE_COLUMN_IMAGE_URL = "image_url";
    public static final String MESSAGE_COLUMN_POST_URL = "post_url";
    public static final String MESSAGE_COLUMN_SENDER = "sender";

    public static final String SUBJECT_TABLE_NAME = "subjects";
    public static final String SUBJECT_COLUMN_ID = "id";
    public static final String SUBJECT_COLUMN_NAME = "name";
    public static final String SUBJECT_COLUMN_DATE_TIME = "date_time";
    public static final String SUBJECT_COLUMN_LOCATION = "location";
    public static final String SUBJECT_COLUMN_ABSENCES = "absences";

    private DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table tasks " +
                        "(id integer primary key, name text, date text, time_start text, time_end text, specific text," +
                        "priority text, note text)"
        );
        db.execSQL(
                "create table fee_categories " +
                        "(id integer primary key, name text unique, bill_date text, due_date text)"
        );
        db.execSQL(
                "create table fees " +
                        "(id integer primary key, fee_category_id text, bill_month text, bill_date text)"
        );
        db.execSQL(
                "create table folders " +
                        "(id integer primary key, name text unique)"
        );
        db.execSQL(
                "create table photos " +
                        "(id integer primary key, name text, folder_id integer)"
        );
        db.execSQL(
                "create table grades " +
                        "(id integer primary key, code text, grade float, term text, title text, units text)"
        );
        db.execSQL(
                "create table expense_categories " +
                        "(id integer primary key, name text unique)"
        );
        db.execSQL(
                "create table expenses " +
                        "(id integer primary key, amount float, date text, category_id integer)"
        );
        db.execSQL(
                "create table subjects " +
                        "(id integer primary key, name text, date_time text, location text, absences integer)"
        );
        db.execSQL(
                "create table messages " +
                        "(id integer primary key, message text, date_time text, image_url text, post_url text, sender text)"
        );
        db.execSQL(
                "create table help " +
                        "(id integer primary key, name text, yes_no int)" //1 if yes
        );
        db.execSQL(
                "create table holiday " +
                        "(id integer primary key, name text, date text)"
        );

        ContentValues contentValues = new ContentValues();
/*
        contentValues.put(MESSAGE_COLUMN_MESSAGE, "Hello! I'm UNI, your personal assistant.\n" +
                "I'm here to help you through your university life!");
        contentValues.put(MESSAGE_COLUMN_DATE_TIME, date_format.format(new java.util.Date()).toString());
        contentValues.put(MESSAGE_COLUMN_IMAGE_URL, "");
        contentValues.put(MESSAGE_COLUMN_POST_URL, "");
        contentValues.put(MESSAGE_COLUMN_SENDER, "BOT");
        Long id = db.insert(MESSAGE_TABLE_NAME, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN_MESSAGE, "If you ever need it, just say 'help' anytime!");
        contentValues.put(MESSAGE_COLUMN_DATE_TIME, date_format.format(new java.util.Date()).toString());
        contentValues.put(MESSAGE_COLUMN_IMAGE_URL, "");
        contentValues.put(MESSAGE_COLUMN_POST_URL, "");
        contentValues.put(MESSAGE_COLUMN_SENDER, "BOT");
        db.insert(MESSAGE_TABLE_NAME, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN_MESSAGE, "You might be wondering, that's a lot of things that I can do. Don't worry, I got you. Let's have a walkthrough all of the things that I can do.");
        contentValues.put(MESSAGE_COLUMN_DATE_TIME, date_format.format(new java.util.Date()).toString());
        contentValues.put(MESSAGE_COLUMN_IMAGE_URL, "");
        contentValues.put(MESSAGE_COLUMN_POST_URL, "");
        contentValues.put(MESSAGE_COLUMN_SENDER, "BOT");
        db.insert(MESSAGE_TABLE_NAME, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN_MESSAGE, "First, try to ask me if there are classes today. Who knows, there might be class suspensions that you aren't aware of.");
        contentValues.put(MESSAGE_COLUMN_DATE_TIME, date_format.format(new java.util.Date()).toString());
        contentValues.put(MESSAGE_COLUMN_IMAGE_URL, "");
        contentValues.put(MESSAGE_COLUMN_POST_URL, "");
        contentValues.put(MESSAGE_COLUMN_SENDER, "BOT");
        db.insert(MESSAGE_TABLE_NAME, null, contentValues);*/
/*
        contentValues = new ContentValues();
        contentValues.put("name", "expense_add");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "expense_show_all");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "bill_add");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "bill_show_all");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "bill_show_one");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "folder_add");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "folder_show_all");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "grades_load");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "add_task");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "task_show_all");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);
*/
/*

        contentValues = new ContentValues();
        contentValues.put("name", "twitter");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "ARE_THERE_CLASSES");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "SHOW_SCHEDULE");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "ADD_ABSENCE");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "ADD_TASK");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "SHOW_TASKS");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "ADD_EXPENSES");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "SHOW_EXPENSES");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "CAMERA");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "ADD_BILLS");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "SHOW_BILLS");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "GRADES");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "EVENTS");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "BUY");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "LAST");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);


*/

        contentValues = new ContentValues();
        contentValues.put("name", "twitter");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "expenses");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "bills");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("name", "folders");
        contentValues.put("yes_no", 0);
        db.insert("help", null, contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public boolean insertTask (String name, String date, String time_start, String time_end, String specific, String priority,
                                String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASKS_COLUMN_NAME, name);
        contentValues.put(TASKS_COLUMN_DATE, date);
        contentValues.put(TASKS_COLUMN_TIME_START, time_start);
        contentValues.put(TASKS_COLUMN_TIME_END, time_end);
        contentValues.put(TASKS_COLUMN_SPECIFIC, specific);
        contentValues.put(TASKS_COLUMN_PRIORITY, priority);
        contentValues.put(TASKS_COLUMN_NOTE, note);
        db.insert("tasks", null, contentValues);
        System.out.println("CV " + contentValues);
        return true;
    }

    public Cursor getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tasks where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TASKS_TABLE_NAME);
        return numRows;
    }

    public Integer deleteTask (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("tasks",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tasks", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Task task = new Task();
            task.setId(Integer.valueOf(res.getString(res.getColumnIndex(TASKS_COLUMN_ID))));
            task.setName(res.getString(res.getColumnIndex(TASKS_COLUMN_NAME)));
            task.setDate(res.getString(res.getColumnIndex(TASKS_COLUMN_DATE)));
            task.setTimeStart(res.getString(res.getColumnIndex(TASKS_COLUMN_TIME_START)));
            task.setTimeEnd(res.getString(res.getColumnIndex(TASKS_COLUMN_TIME_END)));
            task.setSpecific(res.getString(res.getColumnIndex(TASKS_COLUMN_SPECIFIC)));
            task.setPriority(res.getString(res.getColumnIndex(TASKS_COLUMN_PRIORITY)));
            task.setNote(res.getString(res.getColumnIndex(TASKS_COLUMN_NOTE)));
            tasks.add(task);
            res.moveToNext();
        }
        return tasks;
    }

    public ArrayList<Task> getSomeTask(String query, String [] arguments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Task> tasks = new ArrayList<Task>();

        Cursor res =  db.rawQuery(query, arguments);

        res.moveToFirst();

        while(res.isAfterLast() == false){
            Task task = new Task();
            task.setId(Integer.valueOf(res.getString(res.getColumnIndex(TASKS_COLUMN_ID))));
            task.setName(res.getString(res.getColumnIndex(TASKS_COLUMN_NAME)));
            task.setDate(res.getString(res.getColumnIndex(TASKS_COLUMN_DATE)));
            task.setTimeStart(res.getString(res.getColumnIndex(TASKS_COLUMN_TIME_START)));
            task.setTimeEnd(res.getString(res.getColumnIndex(TASKS_COLUMN_TIME_END)));
            task.setSpecific(res.getString(res.getColumnIndex(TASKS_COLUMN_SPECIFIC)));
            task.setPriority(res.getString(res.getColumnIndex(TASKS_COLUMN_PRIORITY)));
            task.setNote(res.getString(res.getColumnIndex(TASKS_COLUMN_NOTE)));
            tasks.add(task);
            res.moveToNext();
        }
        return tasks;
    }

    public Boolean updateTask(JsonElement name, JsonElement date, JsonElement time_start, JsonElement time_end, JsonElement specific, JsonElement priority, JsonElement note, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(name != null) contentValues.put(TASKS_COLUMN_NAME, name.getAsString());
        if(date != null) contentValues.put(TASKS_COLUMN_DATE, date.getAsString());
        if(time_start != null) contentValues.put(TASKS_COLUMN_TIME_START, time_start.getAsString());
        if(time_end != null) contentValues.put(TASKS_COLUMN_TIME_END, time_end.getAsString());
        if(specific != null) contentValues.put(TASKS_COLUMN_SPECIFIC, specific.getAsString());
        if(priority != null) contentValues.put(TASKS_COLUMN_PRIORITY, priority.getAsString());
        if(note != null) contentValues.put(TASKS_COLUMN_NOTE, note.getAsString());
        Log.i(TAG, "CAN THIS BE READ HERE " + contentValues);
        try {
            Integer success = db.update("tasks", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
            if(success == 1) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    //fees
    public boolean insertFeeCategory (String name, String bill_date, String due_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FEE_CATEGORY_COLUMN_NAME, name);
        contentValues.put(FEE_CATEGORY_COLUMN_BILL_DATE, bill_date);
        contentValues.put(FEE_CATEGORY_COLUMN_DUE_DATE, due_date);
        try {
            Long success = db.insert(FEE_CATEGORY_TABLE_NAME, null, contentValues);
            Log.i(TAG, "CV " + contentValues);
            if(success == -1) return false;
            else {
                java.util.Date date = new java.util.Date();
                if(date.getDate() == Integer.valueOf(bill_date)) insertFee(Integer.valueOf(success.toString()), date.getMonth() + 1, Integer.valueOf(bill_date));
                return true;
            }
        } catch(Exception e) {
            return false;
        }
    }

    public ArrayList<FeeCategory> getAllFeeCategories() {
        ArrayList<FeeCategory> fee_categories = new ArrayList<FeeCategory>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + FEE_CATEGORY_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            FeeCategory fee_category = new FeeCategory();
            fee_category.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_ID))));
            fee_category.setName(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_NAME)));
            fee_category.setBillDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_BILL_DATE)));
            fee_category.setDueDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_DUE_DATE)));
            fee_categories.add(fee_category);
            res.moveToNext();
        }
        return fee_categories;
    }

    public ArrayList<FeeCategory> getSomeFeeCategories(String query, String [] arguments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<FeeCategory> fee_categories = new ArrayList<FeeCategory>();

        Cursor res =  db.rawQuery(query, arguments);

        res.moveToFirst();

        while(res.isAfterLast() == false){
            FeeCategory fee_category = new FeeCategory();
            fee_category.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_ID))));
            fee_category.setName(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_NAME)));
            fee_category.setBillDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_BILL_DATE)));
            fee_category.setDueDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_DUE_DATE)));
            fee_categories.add(fee_category);
            res.moveToNext();
        }
        return fee_categories;
    }

    public Boolean deleteFeeCategory (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete(FEE_CATEGORY_TABLE_NAME,
                "name = ? ",
                new String[] { name });
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateFeeCategory(JsonElement name, JsonElement bill_date, JsonElement due_date, String old_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(name != null) contentValues.put(FEE_CATEGORY_COLUMN_NAME, name.getAsString());
        if(bill_date != null) contentValues.put(FEE_CATEGORY_COLUMN_BILL_DATE, bill_date.getAsString());
        if(due_date != null) contentValues.put(FEE_CATEGORY_COLUMN_DUE_DATE, due_date.getAsString());
        Log.i(TAG, "CAN THIS BE READ HERE " + contentValues);
        try {
            Integer success = db.update("fee_categories", contentValues, "name = ? ", new String[] { old_name } );
            if(success == 1) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean insertFee(Integer fee_category_id, Integer bill_month, Integer bill_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FEE_COLUMN_CATEGORY_ID, fee_category_id);
        contentValues.put(FEE_COLUMN_BILL_MONTH, bill_month);
        contentValues.put(FEE_COLUMN_BILL_DATE, bill_date);
        db.insert(FEE_TABLE_NAME, null, contentValues);
        Log.i(TAG, "CV " + contentValues);
        return true;
    }

    public ArrayList<Fee> getAllFees() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Fee> fees = new ArrayList<Fee>();

        Cursor res =  db.rawQuery( "select * from fees;", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            Fee fee = new Fee();
            fee.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_ID))));
            fee.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_CATEGORY_ID))));
            fee.setBillMonth(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_BILL_MONTH))));
            fee.setBillDate(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_BILL_DATE))));
            fees.add(fee);
            res.moveToNext();
        }

        return fees;
    }

    public ArrayList<Fee> getFeeUsingCategory(Integer category_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Fee> fees = new ArrayList<Fee>();

        Cursor res =  db.rawQuery( "select * from fees where fee_category_id=?;",
                new String[] { Integer.toString(category_id) } );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            Fee fee = new Fee();
            fee.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_ID))));
            fee.setBillMonth(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_BILL_MONTH))));
            fee.setBillDate(Integer.valueOf(res.getString(res.getColumnIndex(FEE_COLUMN_BILL_DATE))));
            fees.add(fee);
            res.moveToNext();
        }

        return fees;
    }

    public FeeCategory getFeeCategoryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        FeeCategory fee_category = new FeeCategory();

        Cursor res =  db.rawQuery( "select * from fee_categories where name=? collate nocase;",
                new String[] { name } );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            fee_category.setId(Integer.valueOf(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_ID))));
            fee_category.setName(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_NAME)));
            fee_category.setBillDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_BILL_DATE)));
            fee_category.setDueDate(res.getString(res.getColumnIndex(FEE_CATEGORY_COLUMN_DUE_DATE)));
            res.moveToNext();
        }

        return fee_category;
    }

    public Integer deleteFee (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("fees",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    //folders
    public Boolean insertFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLDER_COLUMN_NAME, name);
        try {
            Long success = db.insert(FOLDER_TABLE_NAME, null, contentValues);
            Log.i(TAG, "CV " + contentValues);
            if (success == -1) return false;
            else return true;}
        catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Folder> getAllFolders() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Folder> folders = new ArrayList<Folder>();

        Cursor res =  db.rawQuery( "select * from folders", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Folder folder = new Folder();
            folder.setId(Integer.valueOf(res.getString(res.getColumnIndex(FOLDER_COLUMN_ID))));
            folder.setName(res.getString(res.getColumnIndex(FOLDER_COLUMN_NAME)));
            folders.add(folder);
            res.moveToNext();
        }
        return folders;
    }

    public Folder getOneFolder(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from folders where name=? collate nocase", new String[] { name } );
        res.moveToFirst();
        Folder folder = new Folder();

        while(res.isAfterLast() == false){
            folder.setId(Integer.valueOf(res.getString(res.getColumnIndex(FOLDER_COLUMN_ID))));
            folder.setName(res.getString(res.getColumnIndex(FOLDER_COLUMN_NAME)));
            res.moveToNext();
        }
        return folder;
    }

    public Folder getOneFolder(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from folders where id=?", new String[] { id.toString() } );
        res.moveToFirst();
        Folder folder = new Folder();

        while(res.isAfterLast() == false){
            folder.setId(Integer.valueOf(res.getString(res.getColumnIndex(FOLDER_COLUMN_ID))));
            folder.setName(res.getString(res.getColumnIndex(FOLDER_COLUMN_NAME)));
            res.moveToNext();
        }
        return folder;
    }

    public Boolean deleteFolder(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete("folders",
                "id = ? ",
                new String[] { Integer.toString(id) });
        if(success == 1) return true;
        else return false;
    }

    public Boolean deleteFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete("folders",
                "name = ? collate nocase",
                new String[] { name });
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateFolder(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLDER_COLUMN_NAME, name);
        try {
            Integer success = db.update("folders", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
            if(success == 1) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateFolder(String old_name, String new_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOLDER_COLUMN_NAME, new_name);
        try {
            Integer success = db.update("folders", contentValues, "name = ? collate nocase", new String[] { old_name } );
            if(success == 1) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteAllFolders () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ FOLDER_TABLE_NAME);
    }

    //photos
    public Boolean insertPhoto(String name, Integer folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHOTO_COLUMN_NAME, name);
        contentValues.put(PHOTO_COLUMN_FOLDER_ID, folder);
        db.insert(PHOTO_TABLE_NAME, null, contentValues);
        Log.i(TAG, "CV " + contentValues);
        return true;
    }

    public ArrayList<Photo> getAllPhotos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Photo> photos = new ArrayList<Photo>();

        Cursor res =  db.rawQuery( "select * from photos", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Photo photo = new Photo();
            photo.setId(Integer.valueOf(res.getString(res.getColumnIndex(PHOTO_COLUMN_ID))));
            photo.setName(res.getString(res.getColumnIndex(PHOTO_COLUMN_NAME)));
            photo.setFolder(Integer.valueOf(res.getString(res.getColumnIndex(PHOTO_COLUMN_FOLDER_ID))));
            photos.add(photo);
            res.moveToNext();
        }
        return photos;
    }

    public ArrayList<Photo> getAllPhotos(Integer folder_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Photo> photos = new ArrayList<Photo>();

        Cursor res =  db.rawQuery( "select * from photos where folder_id = ?;",
                new String[] { Integer.toString(folder_id) }  );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Photo photo = new Photo();
            photo.setId(Integer.valueOf(res.getString(res.getColumnIndex(PHOTO_COLUMN_ID))));
            photo.setName(res.getString(res.getColumnIndex(PHOTO_COLUMN_NAME)));
            photo.setFolder(Integer.valueOf(res.getString(res.getColumnIndex(PHOTO_COLUMN_FOLDER_ID))));
            photos.add(photo);
            res.moveToNext();
        }

        return photos;
    }

    public Integer deletePhoto (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("photos",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteAllPhotos () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ PHOTO_TABLE_NAME);
    }

    public void updatePhoto(Integer id, Integer folder_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHOTO_COLUMN_FOLDER_ID, folder_id);
        db.update("photos", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    public void updatePhoto(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHOTO_COLUMN_NAME, name);
        db.update("photos", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
    }

    //grades
    public Boolean insertGrade(String code, Float grade, String term, String title, Float units) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GRADE_COLUMN_CODE, code);
        contentValues.put(GRADE_COLUMN_GRADE, grade);
        contentValues.put(GRADE_COLUMN_TERM, term);
        contentValues.put(GRADE_COLUMN_TITLE, title);
        contentValues.put(GRADE_COLUMN_UNITS, units);
        db.insert(GRADE_TABLE_NAME, null, contentValues);
        Log.i(TAG, "CV " + contentValues);
        return true;
    }

    public ArrayList<Grade> getAllGrades() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Grade> grades = new ArrayList<Grade>();

        Cursor res =  db.rawQuery( "select * from grades;", null  );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Grade grade = new Grade(
                    res.getString(res.getColumnIndex(GRADE_COLUMN_CODE)),
                    Float.parseFloat(res.getString(res.getColumnIndex(GRADE_COLUMN_GRADE))),
                    res.getString(res.getColumnIndex(GRADE_COLUMN_TERM)),
                    res.getString(res.getColumnIndex(GRADE_COLUMN_TITLE)),
                    Float.parseFloat(res.getString(res.getColumnIndex(GRADE_COLUMN_UNITS)))

            );
            grades.add(grade);
            res.moveToNext();
        }

        return grades;
    }

    public void deleteAllGrades () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ GRADE_TABLE_NAME);
    }

    //expense categories
    public Boolean insertExpenseCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_CATEGORY_COLUMN_NAME, name);
        try {
            Long success = db.insert(EXPENSE_CATEGORY_TABLE_NAME, null, contentValues);
            Log.i(TAG, "CV " + contentValues);
            if(success == -1) return false;
            else return true;
        } catch(Exception e) {
            return false;
        }
    }

    public ArrayList<ExpenseCategory> getAllExpenseCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ExpenseCategory> expense_categories = new ArrayList<ExpenseCategory>();

        Cursor res =  db.rawQuery( "select * from expense_categories", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ExpenseCategory expense_category = new ExpenseCategory();
            expense_category.setId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_CATEGORY_COLUMN_ID))));
            expense_category.setName(res.getString(res.getColumnIndex(EXPENSE_CATEGORY_COLUMN_NAME)));
            expense_categories.add(expense_category);
            res.moveToNext();
        }
        return expense_categories;
    }

    public ExpenseCategory getOneExpenseCategory(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from expense_categories where name=? collate nocase", new String[] { name } );
        res.moveToFirst();
        ExpenseCategory expense_category = new ExpenseCategory();

        while(res.isAfterLast() == false){
            expense_category.setId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_CATEGORY_COLUMN_ID))));
            expense_category.setName(res.getString(res.getColumnIndex(EXPENSE_CATEGORY_COLUMN_NAME)));
            res.moveToNext();
        }
        return expense_category;
    }

    public Boolean deleteExpenseCategory(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete("expense_categories",
                "id = ? ",
                new String[] { Integer.toString(id) });
        if(success == 1) return true;
        else return false;
    }

    public Boolean deleteExpenseCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete("expense_categories",
                "name = ? collate nocase",
                new String[] { name });
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateExpenseCategory(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_CATEGORY_COLUMN_NAME, name);
        try {
            Integer success = db.update("expense_categories", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
            if(success == 1) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean updateExpenseCategory(String old_name, String new_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_CATEGORY_COLUMN_NAME, new_name);
        try {
            Integer success = db.update("expense_categories", contentValues, "name = ? collate nocase", new String[] { old_name } );
            if(success == 1) return true;
            else return false;
        } catch(Exception e) {
            return false;
        }
    }

    //expenses
    public Boolean insertExpense(Float amount, Integer category_id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_COLUMN_AMOUNT, amount);
        contentValues.put(EXPENSE_COLUMN_CATEGORY_ID, category_id);
        contentValues.put(EXPENSE_COLUMN_DATE, date);
        Long success = db.insert(EXPENSE_TABLE_NAME, null, contentValues);
        Log.i(TAG, "CV " + contentValues);
        if(success == -1) return false;
        else return true;
    }

    public ArrayList<Expense> getAllExpenses(Integer category_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Expense> expenses = new ArrayList<Expense>();

        Cursor res =  db.rawQuery( "select * from expenses where category_id = ?", new String[] { category_id.toString() } );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Expense expense = new Expense();
            expense.setId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_ID))));
            expense.setAmount(Float.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_AMOUNT))));
            expense.setCategoryId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_CATEGORY_ID))));
            expense.setDate(res.getString(res.getColumnIndex(EXPENSE_COLUMN_DATE)));
            expenses.add(expense);
            res.moveToNext();
        }
        return expenses;
    }

    public Expense getOneExpense(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from expenses where id=?", new String[] { id.toString() } );
        res.moveToFirst();
        Expense expense = new Expense();

        while(res.isAfterLast() == false){
            expense.setId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_ID))));
            expense.setAmount(Float.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_AMOUNT))));
            expense.setCategoryId(Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_CATEGORY_ID))));
            expense.setDate(res.getString(res.getColumnIndex(EXPENSE_COLUMN_DATE)));
            res.moveToNext();
        }
        return expense;
    }

    public Boolean deleteExpense(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer success = db.delete("expenses",
                "id = ? ",
                new String[] { Integer.toString(id) });
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateExpenseDate(Integer id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_COLUMN_DATE, date);
        Integer success = db.update("expenses", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateExpenseAmount(Integer id, Float amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSE_COLUMN_AMOUNT, amount);
        Integer success = db.update("expenses", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        if(success == 1) return true;
        else return false;
    }

    //subjects
    public Integer insertSubject(String name, String date_time, String location, Integer absences) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_COLUMN_NAME, name);
        contentValues.put(SUBJECT_COLUMN_DATE_TIME, date_time);
        contentValues.put(SUBJECT_COLUMN_LOCATION, location);
        contentValues.put(SUBJECT_COLUMN_ABSENCES, absences);
        Long id = db.insert(SUBJECT_TABLE_NAME, null, contentValues);
        Log.i(TAG, "CV " + contentValues);
        if(id!=null) return (int) (long) id;
        else return 0;
    }

    public Boolean addSubjectAbsent(Integer id, Integer old_amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_COLUMN_ABSENCES, (old_amount+1));
        Integer success = db.update("subjects", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        if(success == 1) return true;
        else return false;
    }

    public Boolean updateSubjectAbsent(Integer id, Integer new_amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_COLUMN_ABSENCES, (new_amount));
        Integer success = db.update("subjects", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        if(success == 1) return true;
        else return false;
    }

    public void deleteAllSubjects() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ SUBJECT_TABLE_NAME);
    }


    public ArrayList<Subject> getAllSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Subject> subjects = new ArrayList<Subject>();

        Cursor res =  db.rawQuery( "select * from subjects", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Subject subject = new Subject();
            subject.setId(Integer.valueOf(res.getString(res.getColumnIndex(SUBJECT_COLUMN_ID))));
            subject.setName(res.getString(res.getColumnIndex(SUBJECT_COLUMN_NAME)));
            subject.setDate_time(res.getString(res.getColumnIndex(SUBJECT_COLUMN_DATE_TIME)));
            subject.setLocation(res.getString(res.getColumnIndex(SUBJECT_COLUMN_LOCATION)));
            subject.setAbsences(Integer.valueOf(res.getString(res.getColumnIndex(SUBJECT_COLUMN_ABSENCES))));
            subjects.add(subject);
            res.moveToNext();
        }
        return subjects;
    }


    //messages
    public Integer insertMessage(String message, String date_time, String image_url, String post_url, String sender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN_MESSAGE, message);
        contentValues.put(MESSAGE_COLUMN_DATE_TIME, date_time);
        contentValues.put(MESSAGE_COLUMN_IMAGE_URL, image_url);
        contentValues.put(MESSAGE_COLUMN_POST_URL, post_url);
        contentValues.put(MESSAGE_COLUMN_SENDER, sender);
        Long id = db.insert(MESSAGE_TABLE_NAME, null, contentValues);
        if(id!=null) return (int) (long) id;
        else return 0;
    }

    public ArrayList<BaseMessage> getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BaseMessage> messages = new ArrayList<BaseMessage>();

        Cursor res =  db.rawQuery( "select * from messages", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            BaseMessage message = new BaseMessage(
                    Integer.valueOf(res.getString(res.getColumnIndex(EXPENSE_COLUMN_ID))),
                    res.getString(res.getColumnIndex(MESSAGE_COLUMN_MESSAGE)),
                    res.getString(res.getColumnIndex(MESSAGE_COLUMN_DATE_TIME)),
                    res.getString(res.getColumnIndex(MESSAGE_COLUMN_IMAGE_URL)),
                    res.getString(res.getColumnIndex(MESSAGE_COLUMN_POST_URL)),
                    res.getString(res.getColumnIndex(MESSAGE_COLUMN_SENDER))
            );
            messages.add(message);
            res.moveToNext();
        }
        return messages;
    }

    public void deleteAllMessages () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ MESSAGE_TABLE_NAME);
    }

    public Integer getHelpValue (String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select yes_no from help where name = ?", new String[] { name });
        res.moveToFirst();

        if(res.isAfterLast() == false) return Integer.valueOf(res.getString(res.getColumnIndex("yes_no")));
        else return 1;
    }

    public void setHelpValue (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("yes_no", 1);
        Integer success = db.update("help", contentValues, "name = ? ", new String[] { name } );
    }

    public void revertHelpValue (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("yes_no", 2);
        Integer success = db.update("help", contentValues, "name = ? ", new String[] { name } );
    }

    public int addHoliday(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("date", date);
        Long id = db.insert("holiday", null, contentValues);
        if(id!=null) return (int) (long) id;
        else return 0;
    }

    public String getHoliday(String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from holiday where date = ?", new String[] { date });
        res.moveToFirst();

        if(res.isAfterLast() == false) return res.getString(res.getColumnIndex("name"));
        else return "none";
    }

    public void deleteAllHolidays () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from holiday");
    }

}