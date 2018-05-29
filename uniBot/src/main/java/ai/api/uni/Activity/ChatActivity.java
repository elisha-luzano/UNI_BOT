/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* REFERENCES:
    https://blog.sendbird.com/android-chat-tutorial-building-a-messaging-ui
    https://github.com/dialogflow/dialogflow-android-client/
    Changed name from ChatActivity to ChatActivity
    Added final variables
    Modified onCreate
    Modified onResult added if else clauses for intents
    Modified onOptionsItemSelected
    Modified sendRequest
    Added new methods
*/

package ai.api.uni.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Status;
import ai.api.ui.AIDialog;
import ai.api.uni.Model.Alarm;
import ai.api.uni.Model.BaseMessage;
import ai.api.uni.Config;
import ai.api.uni.Database.DBHelper;
import ai.api.uni.Task.DownloadEventbriteTask;
import ai.api.uni.Task.DownloadFacebookTask;
import ai.api.uni.Task.DownloadGradesTask;
import ai.api.uni.Task.DownloadHolidayTask;
import ai.api.uni.Task.DownloadSubjectsTask;
import ai.api.uni.Task.DownloadTwitterTask;
import ai.api.uni.Model.Expense;
import ai.api.uni.Model.ExpenseCategory;
import ai.api.uni.Model.Fee;
import ai.api.uni.Model.FeeCategory;
import ai.api.uni.Model.Folder;
import ai.api.uni.Model.Grade;
import ai.api.uni.Model.ImageFileFilter;
import ai.api.uni.Adapter.MessageListAdapter;
import ai.api.uni.Model.Photo;
import ai.api.uni.R;
import ai.api.uni.Model.Subject;
import ai.api.uni.TTS;
import ai.api.uni.Model.Task;


public class ChatActivity extends BaseActivity implements AIDialog.AIDialogListener {

    public static final String TAG = ChatActivity.class.getName();

    private TextView resultTextView;
    private AIDialog aiDialog;

    private Gson gson = GsonFactory.getGson();

    public static DBHelper database;
    public static String current = "";
    private Task task = new Task();
    public ArrayAdapter<String> adapter;
    public ArrayList<String> conversation=new ArrayList<String>();
    private Iterator delete_itr;
    private Iterator update_itr;
    private Iterator pay_itr;
    private Cursor res;
    private ImageView imageView;
    private Uri file;
    private Integer folder_id;
    private String folder_name;
    private static String time_stamp;
    private String add_folder_name;
    private String delete_folder;
    private String delete_fee_category;
    private String expense_name;
    private Float expense_amount;
    private String expense_date;
    private Integer update_id;
    private Integer delete_id;
    private ArrayList<Expense> delete_expenses_array;
    private ArrayList<Expense> update_expenses_array;
    private String delete_expense_category;
    private String delete_type = "";
    private String delete_name = "";
    private long new_id = 0;


    private ArrayList<Long[]> delete_tasks_ids;
    private ArrayList<Long[]> update_tasks_ids;

    private ArrayList<Task> delete_tasks_array;
    private ArrayList<Task> update_tasks_array;
    private ArrayList<FeeCategory> delete_fee_categories_array;
    private ArrayList<FeeCategory> update_fee_categories_array;
    private ArrayList<Fee> view_fees_array;
    private ArrayList<Subject> subjects_update = new ArrayList<Subject>();
    private Integer absences;
    private HashMap<String, JsonElement> update_params;

    private String cookie = "";

    //INTENTS
    final private String ARE_THERE_CLASSES_INTENT = "ARE THERE CLASSES";

    final private String ADD_TASK_INTENT = "TASK Add";
    final private String TASK_SPECIFIC_INTENT = "TASK Add specific";
    final private String TASK_PRIORITY_INTENT = "TASK Add priority";
    final private String NO_NOTES = "TASK No notes";
    final private String TASK_NOTE_INTENT = "TASK Add note";
    final private String UPDATE_TASK_INTENT = "TASK Update";
    final private String WHICH_TASKS_UPDATE_INTENT = "TASK Update select";
    final private String DELETE_TASK_INTENT = "TASK Delete";
    final private String WHICH_TASKS_INTENT = "TASK Delete select";
    final private String SURE_DELETE_TASKS_INTENT = "TASK Delete yes";
    final private String SHOW_MY_TASKS_INTENT = "TASK View all";
    final private String SHOW_SOME_TASKS_INTENT = "TASK View some";

    public static  final String FEE_CATEGORY_VIEW_INTENT = "FEE CATEGORY View";
    public static  final String FEE_CATEGORY_ADD_INTENT = "FEE CATEGORY Add";
    public static  final String FEE_CATEGORY_DELETE_INTENT = "FEE CATEGORY Delete";
    public static  final String FEE_CATEGORY_DELETE_SELECT_INTENT = "FEE CATEGORY Delete select";
    public static  final String FEE_CATEGORY_DELETE_SURE_INTENT = "FEE CATEGORY Delete sure";
    public static  final String FEE_CATEGORY_UPDATE_INTENT = "FEE CATEGORY Update";
    public static  final String FEE_CATEGORY_UPDATE_SELECT_INTENT = "FEE CATEGORY Update select";
    public static  final String FEE_CATEGORY_VIEW_YES_SELECT = "FEE CATEGORY View yes select";

    public static  final String FEE_VIEW_INTENT = "FEE View";
    public static final String FEE_PAY_SELECT_INTENT = "FEE Pay select";
    public static final String FEE_PAY_SELECT_YES_INTENT = "FEE Pay select yes";

    public static final String FOLDER_ADD_INTENT = "FOLDER Add";
    public static final String FOLDER_VIEW_INTENT = "FOLDER View";
    public static final String FOLDER_VIEW_ALL_INTENT = "FOLDER View all";
    public static final String FOLDER_DELETE_FOLDER = "FOLDER Delete";
    public static final String FOLDER_DELETE_SURE_INTENT = "FOLDER Delete sure";
    public static final String FOLDER_UPDATE_INTENT = "FOLDER Update";

    public static final String PHOTO_ADD_INTENT = "PHOTO Add";
    public static final String PHOTO_ADD_YES_INTENT = "PHOTO Add yes";

    public static final String WHERE_TO_BUY_INTENT = "WHERE TO BUY";

    public static final String EXPENSE_CATEGORY_ADD_INTENT = "EXPENSE CATEGORY Add";
    public static final String EXPENSE_CATEGORY_UPDATE_INTENT = "EXPENSE CATEGORY Update";
    public static final String EXPENSE_CATEGORY_DELETE_INTENT = "EXPENSE CATEGORY Delete";
    public static final String EXPENSE_CATEGORY_DELETE_SURE_INTENT = "EXPENSE CATEGORY Delete sure";
    public static final String EXPENSE_CATEGORY_VIEW_INTENT = "EXPENSE CATEGORY View";
    public static final String EXPENSE_CATEGORY_VIEW_ALL_INTENT = "EXPENSE CATEGORY View all";

    public static final String EXPENSE_ADD_INTENT = "EXPENSE Add";
    public static final String EXPENSE_ADD_YES_INTENT = "EXPENSE Add yes";
    public static final String EXPENSE_DELETE_INTENT = "EXPENSE Delete";
    public static final String EXPENSE_DELETE_SELECT_INTENT = "EXPENSE Delete select";
    public static final String EXPENSE_DELETE_SURE_INTENT = "EXPENSE Delete select sure";
    public static final String EXPENSE_UPDATE_INTENT = "EXPENSE Update";
    public static final String EXPENSE_UPDATE_SELECT_INTENT = "EXPENSE Update select";
    public static final String EXPENSE_UPDATE_AMOUNT_INTENT = "EXPENSE Update amount";
    public static final String EXPENSE_UPDATE_AMOUNT_VALUE_INTENT = "EXPENSE Update amount value";
    public static final String EXPENSE_UPDATE_DATE_INTENT = "EXPENSE Update date";
    public static final String EXPENSE_UPDATE_DATE_VALUE_INTENT = "EXPENSE Update date value";

    public static final String SHOW_MY_GRADES_INTENT = "GRADE Show";
    public static final String SHOW_MY_GRADES_YES_INTENT = "GRADE Show yes";
    public static final String SHOW_MY_GRADES_NO_INTENT = "GRADE Show no";

    public static final String ATTENDANCE_VIEW_SCHEDULE_INTENT = "ATTENDANCE View schedule";
    public static final String ATTENDANCE_VIEW_SCHEDULE_YES_NO_INTENT = "ATTENDANCE View schedule yes no";
    public static final String ATTENDANCE_ADD_ABSENT_INTENT = "ATTENDANCE Add absent";
    public static final String ATTENDANCE_ADD_ABSENT_SELECT_INTENT = "ATTENDANCE Add absent select";
    public static final String ATTENDANCE_UPDATE_ABSENT_INTENT = "ATTENDANCE Update absent";
    public static final String ATTENDANCE_UPDATE_ABSENT_SELECT_INTENT = "ATTENDANCE Update absent select";

    public static final String YES_OR_NO = "yes_no";
    public static final String YES = "yes";
    public static final String NO = "no";

    //DATABASE
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
    public static final String FEE_CATEGORY_COLUMN_NAME = "name";
    public static final String FEE_CATEGORY_COLUMN_BILL_DATE = "bill_date";
    public static final String FEE_CATEGORY_COLUMN_DUE_DATE = "due_date";

    public static final String FOLDER_TABLE_NAME = "folders";
    public static final String FOLDER_COLUMN_ID = "id";
    public static final String FOLDER_COLUMN_NAME = "name";


    public JSONObject tweets;

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<BaseMessage> messageList = new ArrayList<BaseMessage>();
    private CallbackManager callbackManager;

    private AIDataService aiDataService;
    private DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private DateFormat date_only_format = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat date_pre = new SimpleDateFormat("MM-dd-yyyy");
    private DateFormat time_format = new SimpleDateFormat("hh:mma");
    private DateFormat time_pre = new SimpleDateFormat("HH:mm:ss");


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        TTS.init(getApplicationContext());

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        mMessageAdapter = new MessageListAdapter(ChatActivity.this, this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        String[] arr = {
                "are there classes", "class suspension", "cancellation of classes",

                "add task", "schedule a task", "i have an appointment", "show tasks", "show my tasks",

                "show bills", "add a bill", "rename bill", "delete bill", "show my bills",
                //"view bills", "insert a bill", "change bill", "remove bill",

                "add folder", "rename folder", "delete folder", "show folders", "show a folder",  "open a folder", "show my folders",
                "open camera", "add photo",

                "where to buy ", "i want to buy ", "where can i buy", "show me where to buy ",

                "show my grades", "grades", "show grades",

                "show events ", "any upcoming events ", "show events nearby", "are there any upcoming events",

                "i spent today", "add to my expenses", "remove expenses", "rename expenses", "show expenses", "show my expenses",

                "show my class schedule", "i was absent today", "i did not go to class", "i missed class", "how many absences do i have",

                "hello", "okay", "thank you", "ok", "cancel", "yes", "no", "facebook", "eventbrite", "both"


        };

        AutoCompleteTextView autocomplete = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, arr);

        autocomplete.setThreshold(1);
        autocomplete.setAdapter(adapter);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(this, config);
        aiDialog = new AIDialog(this, config);
        aiDialog.setResultsListener(this);



        database = new DBHelper(this);

        ArrayList<BaseMessage> messages_initial = BaseMessage.getAll(database);
        for(int i=0; i<messages_initial.size(); i++) {
            messageList.add(messages_initial.get(i));
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.smoothScrollToPosition(messageList.size());
        }

        TwitterConfig conf = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("ZJArH0bZNuFWbFNbL9rK76kza", "45QPZlBnDaUALMU4XGa6SqDaCBIYC8yHupf7bXdWLlJddXGDbU"))
                .debug(true)
                .build();
        Twitter.initialize(conf);

        Alarm alarm = new Alarm();
        alarm.setAlarm(ChatActivity.this);

        /*imageView = (ImageView) findViewById(R.id.imageView);*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO }, 0);
        } else {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CameraDemo");

            Log.i(TAG, "DIR" + mediaStorageDir);

            File[] files = new File("/storage/emulated/0/Pictures").listFiles(new ImageFileFilter());
            Folder.deleteAll(database);
            Photo.deleteAll(database);
            Log.i(TAG, files + " FILES ");
            if(files != null && files.length > 0) {
                for (File file : files) {
                    // Add the directories containing images or sub-directories
                    if (file.isDirectory()
                            /*&& file.listFiles(new ImageFileFilter()).length > 0*/) {
                        Log.i(TAG, "FOLDER: " + file.getAbsolutePath());
                        Folder.insert(file.getAbsolutePath().replaceAll("/storage/emulated/0/Pictures/", ""), database);
                    }
                }
            }
            ArrayList<Folder> folders = Folder.getAll(database);
            for(int i=0; i<folders.size(); i++) {
                Integer folderid = Folder.getOne(folders.get(i).getName(), database).getId();
                File[] files_folder = new File("/storage/emulated/0/Pictures/" + folders.get(i).getName()).listFiles(new ImageFileFilter());
                if(files_folder != null) {
                    for (File file : files_folder) {
                        // Add the directories containing images or sub-directories
                        //if (!file.isDirectory()) {
                        Log.i(TAG, "PHOTO: " + file.getAbsolutePath());
                        Photo.insert(file.getAbsolutePath(), folderid, database);
                        //}
                    }
                }
            }
        }
        //takePicture(1);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i(TAG, "LOGIN SUCCESS");
                        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                        LoginManager.getInstance().logInWithReadPermissions(ChatActivity.this, Arrays.asList("public_profile"));
                        String access_token = AccessToken.getCurrentAccessToken().getToken();
                        new DownloadFacebookTask(ChatActivity.this, access_token, "", "", "").execute();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "LOGIN CANCEL");
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG, "LOGIN ERROR " + exception.getMessage());
                        // App code
                    }
                });

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 0);
        }*/

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new DownloadHolidayTask(ChatActivity.this)
                        .execute("https://www.googleapis.com/calendar/v3/calendars/en.philippines"
                + "%23holiday%40group.v.calendar.google.com/events?key=AIzaSyD_uFtC9TIqIxWOIGuIsRSQf8UC32yiHPc");
            }
        });
    }


    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
              @Override
              public void run() {

                  Log.d(TAG, "onResult");

                  resultTextView.setText(gson.toJson(response));

                  Log.i(TAG, "Received success response");

                  // this is example how to get different parts of result object
                  final Status status = response.getStatus();
                  Log.i(TAG, "Status code: " + status.getCode());
                  Log.i(TAG, "Status type: " + status.getErrorType());

                  //final Result result = response.getResult();
                  Log.i(TAG, "Resolved query: " + response.getResult().getResolvedQuery());

                  Log.i(TAG, "Action: " + response.getResult().getAction());
                  final String speech = response.getResult().getFulfillment().getSpeech();
                  Log.i(TAG, "Speech: " + speech);

                  for(int a = 0; a<response.getResult().getContexts().toArray().length; a++)
                    Log.i(TAG, "Context: " + response.getResult().getContexts().get(a).getName());

                  final Metadata metadata = response.getResult().getMetadata();
                  if (metadata != null) {
                      Log.i(TAG, "Intent id: " + metadata.getIntentId());
                      Log.i(TAG, "Intent name: " + metadata.getIntentName());
                  }

                  if(!speech.equals("") &&
                          !speech.equals("Where should we add this? To your tasks? Bills? Expenses? Folders?") &&
                          !speech.equals("Where do you want to see it from?") &&
                          !speech.equals("Where should I rename it from?") &&
                            !speech.equals("Where should I delete it from?")){
                      addMessage(speech, "", "", "BOT");
                      TTS.speak(speech);
                  }

                  final HashMap<String, JsonElement> params = response.getResult().getParameters();
                  if (params != null && !params.isEmpty()) {
                      Log.i(TAG, "Parameters: ");
                      for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                          Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                      }
                  }

                  final HashMap<String, JsonElement> responseParams = response.getResult().getParameters();
                  ArrayList<Integer> delete_tasks = new ArrayList<Integer>();
                  String intent = metadata.getIntentName();



                  if(intent.equals("GENERAL Update")) {
                      String old_name = "";
                      String new_name = "";
                      String type = "";
                      if(responseParams.get("old_name") != null && responseParams.get("new_name") != null && responseParams.get("type") == null && !current.equals("")) {
                          old_name = responseParams.get("old_name").getAsString();
                          new_name = responseParams.get("new_name").getAsString();
                          resendNewRequest(current);
                      }
                      else if(responseParams.get("old_name") != null & responseParams.get("new_name") != null & responseParams.get("type") != null) {
                          old_name = responseParams.get("old_name").getAsString();
                          new_name = responseParams.get("new_name").getAsString();
                          type = responseParams.get("type").getAsString();
                      } else if(responseParams.get("old_name") != null && responseParams.get("new_name") != null) {
                          addMessage("Okay, so I should rename " + responseParams.get("old_name").getAsString() + " to " +  responseParams.get("new_name").getAsString() + ". However, I missed where I should rename them from? Are they from your folders? Tasks? Epenses? Bills?", "", "", "BOT");
                      }

                          if(type.equals("bill")) {
                              Boolean success = FeeCategory.update(responseParams.get("new_" + FEE_CATEGORY_COLUMN_NAME),
                                      null, null,
                                      responseParams.get("old_" + FEE_CATEGORY_COLUMN_NAME).getAsString(), database);
                              if (success) addMessage("I've changed your " + old_name + " bills to " + new_name + ". Pay monthly!", "", "", "BOT");
                              else addMessage("Oopsies! I was not able to rename " + old_name + ". It may not exist or the " + new_name + " already exists.", "", "", "BOT");
                          }


                          else if(type.equals("folder")) {
                              Boolean success = Folder.update(old_name, new_name, database);
                              if (success) {
                                  File oldFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), old_name);
                                  File newFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), new_name);
                                  success = oldFolder.renameTo(newFolder);
                                  if(success) {
                                      Integer id = Folder.getOne(responseParams.get("new_name").getAsString(), database).getId();
                                      ArrayList<Photo> photos = Photo.getAllInFolder(id, database);
                                      String filename = "";
                                      String old_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + old_name;
                                      String new_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + new_name;
                                      for(int i=0; i<photos.size(); i++) {
                                          filename = photos.get(i).getName();
                                          filename = filename.replaceAll(old_dir, new_dir);
                                          Photo.update(photos.get(i).getId(), filename, database);
                                      }
                                      addMessage("I've changed the name of your " + old_name + " folder to " + new_name + ".", "", "", "BOT");
                                  }
                                  else {
                                      success = Folder.update(new_name, old_name, database);
                                      addMessage("Sorry, I cannot rename " + old_name + " folder. Either it does not exist oryou already have a folder named " + new_name + ".", "", "", "BOT");
                                  }
                              }
                              else addMessage("Sorry, I cannot rename " + old_name + " folder. Either it does not exist oryou already have a folder named " + new_name + ".", "", "", "BOT");
                          }


                          else if(type.equals("expense")) {
                              Boolean success = ExpenseCategory.update(old_name, new_name, database);
                              if (success)
                                  addMessage("I've successfully changed your " + old_name + " expenses to " + new_name + ".", "", "", "BOT");
                              else addMessage("I've failed to rename your " + old_name + " expenses. Either it does not exist or you already have " + new_name + " in your expenses.", "", "", "BOT");
                          }
                  }



                  else if(intent.equals("GENERAL View all")) {
                      if(responseParams.get("type") != null) {
                          String type = responseParams.get("type").getAsString();

                          if(type.equals("bill")) {
                              current = "bills";
                              ArrayList<FeeCategory> fee_categories = FeeCategory.getAll(database);
                              if(fee_categories.size() == 0) addMessage("Oops, you don't have anything on your bills yet. But you can certainly add one, for example, try saying \"Add water to my bills.\"", "", "", "BOT");
                              else {
                                  addMessage("BILLS","","","SCHEDULE_HEADER");
                                  stringify(fee_categories);
                                  addMessage("Here are your bills. I will remind you to pay it 3 days before the due date. Don't fret.", "", "", "BOT");

                                  if(database.getHelpValue("bills") == 0){
                                      addMessage("To view your specific bill or mark it as paid, say something like \"Mark water bills as paid.\"", "", "", "BOT");
                                      database.setHelpValue("bills");
                                  }
                                  /*if(database.getHelpValue("bill_show_all") == 0) {
                        addMessage("You can also view the bills individually and mark some of the bills as paid. Just say 'show' and the name of the bill.", "", "", "BOT");
                        database.setHelpValue("bill_show_all");
                    }*/
                      }
                  }

                          else if(type.equals("folder")) {
                              current = "folders";
                              ArrayList<Folder> folders = Folder.getAll(database);
                              String folders_string = "Here are your folders: \n\n";
                              if(folders.size() ==0) {
                                  addMessage("Sorry, you do not have folders of images yet. Try adding one.", "", "", "BOT");
                              }
                              else {
                                  addMessage("YOUR FOLDERS", "", "", "SCHEDULE_HEADER");
                                  for(int i=0; i<folders.size(); i++) {
                                      folders_string += "[" + (i+1) + "]" + " " + folders.get(i).getName() + "\n";
                                      addMessage(folders.get(i).getName(), "", "", "FOLDER");
                                  }
                                  addMessage("Got it! Here are your folders of pictures.", "", "", "BOT");
                                  if(database.getHelpValue("folders") == 0){
                                      addMessage("To view the contents of a folder, try saying something like \"Open my acads folder.\"\n" +
                                                      "To add a new photo, just say \"Open camera.\"", "", "", "BOT");
                                      database.setHelpValue("folders");
                                  }
                    /*if(database.getHelpValue("folder_show_all") == 0) {
                        addMessage("To open a folder, simply say 'open' and the name of the folder.", "", "", "BOT");
                        addMessage("To take a photo, simply say 'open camera ' and the name of the folder and I'll file the photo under it.", "", "", "BOT");
                        database.setHelpValue("folder_show_all");
                    }*/
                              }
                          }

                          else if(type.equals("expense")) {
                              current = "expenses";
                              ArrayList<ExpenseCategory> expense_categories = ExpenseCategory.getAll(database);
                              if(expense_categories.size() == 0) addMessage("Oops. I have no record of your expenses yet. But you can definitely add one. Try saying something like \"Add food to my expenses.\"", "", "", "BOT");
                              else {
                                  addMessage("EXPENSES", "", "", "SCHEDULE_HEADER");
                                  addMessage("Category", "Total", "", "GRADE");
                                  for(int i=0; i<expense_categories.size(); i++) {
                                      Float total = 0.0f;
                                      ArrayList<Expense> expenses = Expense.getAll(expense_categories.get(i).getId(), database);
                                      if(expenses.size() > 0) {
                                          for(int j=0; j<expenses.size(); j++) {
                                              total += expenses.get(j).getAmount();
                                          }
                                      }
                                      addMessage(expense_categories.get(i).getName(), "P" + total.toString(), "", "GRADE");
                                  }
                                  String expense_string = "Here are all your expenses and the total amount that you've spent on each of them.";
                                  if(database.getHelpValue("expenses") == 0){
                                      addMessage("To view a breakdown of your expenses, try saying something like \"View my water expenses.\"", "", "", "BOT");
                                      database.setHelpValue("expenses");
                                  }
                                  addMessage(expense_string, "", "", "BOT");
                    /*if(database.getHelpValue("expense_show_all") == 0) {
                        addMessage("To view a break down of each, simply say 'show' and the name of the expense.", "", "", "BOT");
                        database.setHelpValue("expense_show_all");
                    }*/
                              }
                          }

                          else if(type.equals("task")) {
                              current = "tasks";
                              allTasks();
                          }
                      }
                  }




                  else if(intent.equals("GENERAL View")) {
                      String name = "";
                      String type = "";
                      if(responseParams.get("name") != null && responseParams.get("type") == null && !current.equals("")) {
                          name = responseParams.get("name").getAsString();
                          resendNewRequest(current);
                      }
                      else if(responseParams.get("name") != null & responseParams.get("type") != null) {
                          name = responseParams.get("name").getAsString();
                          type = responseParams.get("type").getAsString();
                      } else if(responseParams.get("name") != null) {
                          addMessage("Where should we view " + responseParams.get("name").getAsString() + " from? From your tasks? Bills? Expenses? Folders?", "", "", "BOT");
                      }

                          if(type.equals("bill")) {
                              current = "bills";
                              ArrayList<FeeCategory> view_fee_categories = FeeCategory.getSome(responseParams.get(FEE_CATEGORY_COLUMN_NAME),
                                      responseParams.get(FEE_CATEGORY_COLUMN_BILL_DATE),responseParams.get(FEE_CATEGORY_COLUMN_DUE_DATE), database);
                              Log.i(TAG, "fee_categories" +  view_fee_categories);

                              Integer fee_category_id = null;
                              for(int i=0; i<view_fee_categories.size(); i++) {
                                  if(view_fee_categories.get(i).getName().equals(name)) {
                                      fee_category_id = view_fee_categories.get(i).getId();
                                      break;
                                  }
                              }
                              if(fee_category_id != null) {
                                  view_fees_array = Fee.getUsingCategory(fee_category_id, database);
                                  ArrayList<FeeCategory> fee_categories = new ArrayList<FeeCategory>();
                                  fee_categories.add(FeeCategory.getByName(name, database));
                                  addMessage(name.toUpperCase() + " BILL","","","SCHEDULE_HEADER");
                                  stringify(fee_categories);
                                  /*if(view_fees_array.size() != 0) */addMessage(name, fee_category_id.toString(), "", "FEE_BUTTON");
                              }
                              else {
                                  addMessage("Aw, you don't have " + name + " bills on my list. We can't view that.", "", "", "BOT");
                              }
                          }

                          if(type.equals("folder")) {
                              current = "folders";
                              Folder folder = Folder.getOne(name, database);
                              if(folder.getId() != null) {
                                  addMessage("Okay. I'm opening " + name + " folder.", "", "", "BOT");
                                  Integer id = folder.getId();
                                  Intent i = new Intent(getBaseContext(), PhotoActivity.class);
                                  Bundle bundle = new Bundle();
                                  bundle.putInt("id", id);
                                  i.putExtras(bundle);
                                  startActivity(i);
                              } else {
                                  addMessage("Oops, you don't have " + name + " in your folders.", "", "", "BOT");
                              }
                          }

                          if(type.equals("expense")) {
                              current = "expenses";
                              ExpenseCategory expense_category = ExpenseCategory.getOne(name, database);
                              if(expense_category.getId() == null) {
                                  addMessage("Sorry. No expense named " + name + ".", "", "", "BOT");
                              }
                              else {
                                  Integer id = expense_category.getId();
                                  ArrayList<Expense> expenses = Expense.getAll(id, database);
                                  Float total = (float) 0;
                                  if(expenses.size() == 0) addMessage("You have no record of expenses under " + name + " yet.", "", "", "BOT");
                                  else {
                                      addMessage(name.toUpperCase() + " EXPENSES","","","SCHEDULE_HEADER");
                                      addMessage("Amount", "Date", "", "GRADE");
                                      String expense_string = "Here are your records of " + responseParams.get("name").getAsString() + " expenses.";
                                      for(int i=0; i<expenses.size(); i++) {
                                          total += expenses.get(i).getAmount();
                                          addMessage("P"+expenses.get(i).getAmount().toString(), expenses.get(i).getDate(), "", "GRADE");
                                      }
                                      Log.i(TAG, "ID ID ID " + id.toString());
                                      addMessage(name, id.toString(), "", "EXPENSES_BUTTON");
                                      expense_string += ("\nYou spent a total of P" + total + " for " + name + ".");
                                      addMessage(expense_string, "", "", "BOT");

                                  }

                              }
                          }
                          if(type.equals("task")) {
                              parseTask(responseParams);
                          }
                  }



                  else if(intent.equals("GENERAL Delete")) {
                      String name = "";
                      String type = "";
                      if(responseParams.get("name") != null && responseParams.get("type") == null && !current.equals("")) {
                          name = responseParams.get("name").getAsString();
                          resendNewRequest(current);
                      }
                      else if(responseParams.get("name") != null & responseParams.get("type") != null) {
                          name = responseParams.get("name").getAsString();
                          type = responseParams.get("type").getAsString();
                      } else if(responseParams.get("name") != null) {
                          addMessage("Where should we delete " + responseParams.get("name").getAsString() + " from? From your tasks? Bills? Expenses? Folders?", "", "", "BOT");
                      }

                          delete_type = type;
                          delete_name = name;

                          if(type.equals("bill")) {
                              if(FeeCategory.getByName(name, database).getId() == null) {
                                  sendEvent("GENERAL_DELETE_NOT_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                              else {
                                  sendEvent("GENERAL_DELETE_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                          }

                          else if(type.equals("folder")) {
                              if(Folder.getOne(name, database).getId() == null) {
                                  sendEvent("GENERAL_DELETE_NOT_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                              else {
                                  sendEvent("GENERAL_DELETE_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                          }

                          else if(type.equals("expense")) {
                              if(ExpenseCategory.getOne(name, database).getId() == null) {
                                  sendEvent("GENERAL_DELETE_NOT_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                              else {
                                  sendEvent("GENERAL_DELETE_FOUND", "GENERAL_DELETE_FOLLOW");
                              }
                          }
                  }

                  else if(intent.equals("GENERAL Delete found sure")) {
                      if(responseParams.get("yes_no") != null) {
                          if(delete_type.equals("bill")) {
                              if(responseParams.get("yes_no").getAsString().equals("yes")) {
                                  Boolean success = FeeCategory.delete(delete_name, database);
                                  if (success) addMessage("I've removed " + delete_name + " from your bills. Wow! One off your bills to pay! ", "", "", "BOT");
                                  else addMessage("Fee category " + delete_name + " does not exist.", "", "", "BOT");
                              } else if(responseParams.get("yes_no").getAsString().equals("no")){
                                  addMessage("Okay, we won't delete " + delete_name + ". Pay for it monthly, okay!", "", "", "BOT");
                              }
                          }

                          else if(delete_type.equals("folder")) {
                              if(responseParams.get("yes_no").getAsString().equals("yes")) {
                                  Integer id = Folder.getOne(delete_name, database).getId();
                                  Boolean success = Folder.delete(delete_name, database);
                                  if (success) {
                                      File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                              Environment.DIRECTORY_PICTURES), delete_name);
                                      deleteRecursive(mediaStorageDir);
                                      addMessage("Worked on it! I've deleted " + delete_name + " from your folders.", "", "", "BOT");
                                      ArrayList<Photo> photos = Photo.getAllInFolder(id, database);
                                      for(int i=0; i<photos.size(); i++) {
                                          Photo.delete(photos.get(i).getId(), database);
                                      }
                                  }
                                  else addMessage("Oops, you don't have a folder named " + delete_name + ".", "", "", "BOT");
                              } else if(responseParams.get("yes_no").getAsString().equals("no")){
                                  addMessage("Okay, we won't delete your " + delete_name + " folder.", "", "", "BOT");
                              }
                          }

                          else if(delete_type.equals("expense")) {
                              if(responseParams.get("yes_no").getAsString().equals("no")) addMessage("Okay, we won't delete.", "", "", "BOT");
                              else if(responseParams.get("yes_no").getAsString().equals("yes")) {
                                  Boolean success = ExpenseCategory.delete(delete_name, database);
                                  if (success)
                                      addMessage("Successfully deleted " + delete_name + " from expenses.", "", "", "BOT");
                                  else addMessage("Expense " + delete_name + " does not exist.", "", "", "BOT");
                              }
                          }
                      }
                  }


                  else if(intent.equals("GENERAL Add")) {
                      /*if(responseParams.get("name") == null && responseParams.get("type") != null && responseParams.get("type").getAsString().equals("tasks")) {
                          addMessage("Is it a meeting? An exam? An appointment?", "", "", "BOT");
                      }
                      if(responseParams.get("name") == null && responseParams.get("type") != null && responseParams.get("type").getAsString().equals("bills")) {
                          addMessage("Is it water? Rent? Electricity? Bank?", "", "", "BOT");
                      }*/
                      String name = "";
                      String type = "";
                      if(responseParams.get("name") != null && responseParams.get("type") == null && !current.equals("")) {
                          name = responseParams.get("name").getAsString();
                          resendNewRequest(current);
                      }
                      else if(responseParams.get("name") != null & responseParams.get("type") != null) {
                          name = responseParams.get("name").getAsString();
                          type = responseParams.get("type").getAsString();
                      } else if(responseParams.get("name") != null) {
                          addMessage("Where should we add this? To your tasks? Bills? Expenses? Folders?", "", "", "BOT");
                      }

                      if(type.equals("folder")) {
                          Boolean success = Folder.insert(name, database);
                          if(success) {
                              File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                      Environment.DIRECTORY_PICTURES), name);
                              mediaStorageDir.mkdirs();

                              ArrayList<Folder> folders = Folder.getAll(database);
                              addMessage("YOUR FOLDERS", "", "", "SCHEDULE_HEADER");
                              for(int i=0; i<folders.size(); i++) {
                                  addMessage(folders.get(i).getName(), "", "", "FOLDER");
                              }

                              addMessage("Alright, I've added " + name + " to your folders!", "", "", "BOT");
                                /*if(database.getHelpValue("folder_add") == 0) {
                                    addMessage("You can also ask me to show you all of your folders. Just say 'show folders.'", "", "", "BOT");
                                    database.setHelpValue("folder_add");
                                }*/
                          }
                          else addMessage("Oops, you already have a folder named " + name + ". Fyi, folder names must be unique.", "", "", "BOT");

                      }

                      else if(type.equals("expense")) {
                          Boolean success = ExpenseCategory.insert(name, database);
                          if (success) {
                              ArrayList<ExpenseCategory> expense_categories = ExpenseCategory.getAll(database);
                              addMessage("YOUR EXPENSES", "", "", "SCHEDULE_HEADER");

                              for(int i=0; i<expense_categories.size(); i++) {
                                  Float total = 0.0f;
                                  ArrayList<Expense> expenses = Expense.getAll(expense_categories.get(i).getId(), database);
                                  if(expenses.size() > 0) {
                                      for(int j=0; j<expenses.size(); j++) {
                                          total += expenses.get(j).getAmount();
                                      }
                                  }
                                  addMessage(expense_categories.get(i).getName(), "P" + total.toString(), "", "GRADE");
                              }

                              addMessage("Alright, I've added " + name + " to your list of expenses.", "", "", "BOT");
                                /*if(database.getHelpValue("expense_add") == 0) {
                                    addMessage("You can also tell me to show all your expenses. Just say 'show my expenses.'", "", "", "BOT");
                                    database.setHelpValue("expense_add");
                                }*/
                          }
                          else {
                              String expense_add_message = "Oops, you already have " + name + " in your list of expenses.";
                              Integer id = ExpenseCategory.getOne(name, database).getId();
                              ArrayList<Expense> expenses = Expense.getAll(id, database);
                              Float total = (float) 0;
                              if(expenses.size() == 0) expense_add_message += " However, you have no records of your " + name + " expenses yet.";
                              else {
                                  expense_add_message += "\n\n";
                                  for(int i=0; i<expenses.size(); i++) {
                                      expense_add_message += "[" + (i+1) + "]" + " P" + expenses.get(i).getAmount() + "  -  " + expenses.get(i).getDate() + "\n";
                                      total += expenses.get(i).getAmount();
                                  }
                                  expense_add_message += ("\nYou spent a total of P" + total + " on " + name);
                              }
                              addMessage(expense_add_message, "", "", "BOT");


                          }
                      }

                      else if(type.equals("bill")) {
                          sendEvent("GENERAL_ADD_BILLS", "GENERAL_ADD_FOLLOW");
                      }

                      else if(type.equals("task")) {
                          sendEvent("GENERAL_ADD_TASKS", "GENERAL_ADD_FOLLOW");
                      }
                  }


                  //ARE THERE CLASSES
                  else if(intent.equals(ARE_THERE_CLASSES_INTENT)) {
                      current = "";
                      //if(responseParams.get("location") != null && responseParams.get("date") != null) {
                          Log.i(TAG, "params" + responseParams.get("date") + " location" + responseParams.get("location"));
                          String loc = "";
                          String date_text = "";

                          SimpleDateFormat initial = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                          SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");


                          if(responseParams.get("location") == null) loc = "laguna";
                          else loc = responseParams.get("location").getAsString().replaceAll("\\s", "%20");
                          final String location = loc;

                          try {
                              if(responseParams.get("date") == null) date_text = dateParser.format(initial.parse(new Date().toString()));
                              else date_text = responseParams.get("date").getAsString();
                          } catch(Exception e) {

                          }
                          final String date_req = date_text;

                          SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                          SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
                          Calendar cal = Calendar.getInstance();
                          SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                          try {
                              Date date = format1.parse(date_text);
                              String actual_date = format2.format(date);
                              cal.setTime(sdf.parse(actual_date));
                              final String month = new SimpleDateFormat("MMM").format(cal.getTime()).toLowerCase();
                              final String month_full = new SimpleDateFormat("MMMM").format(cal.getTime()).toLowerCase();
                              final String day_text = new SimpleDateFormat("dd").format(cal.getTime());
                              final String day = Integer.valueOf(day_text).toString();
                              final String year = new SimpleDateFormat("yyyy").format(cal.getTime());
                              Log.i(TAG, "DATE!!!" + day + " " + month + " " + month_full + " " + date_text);
                              runOnUiThread(new Runnable() {
                                  public void run() {

                                      String new_loc = "%20OR%20country%20OR%20philippines%20OR%20holiday%20OR%20government%20OR%20" + location;
                                      new DownloadTwitterTask(ChatActivity.this, "", date_req)
                                              .execute("https://api.twitter.com/1.1/search/tweets.json?q=" +
                                                      day + "%20" + month_full + "%20" + new_loc +
                                                      "%20%23walangpasok%20" +
                                                      "from%3Agmanews%20OR%20from%3Aabscbnnews%20OR%20from%3Adzmmteleradyo%20OR%20from%3Aphilippinestar%20OR%20from%3Agovph%20OR%20from%3Auplbofficial%20OR%20from%3Amanilabulletin" +
                                                      "&tweet_mode=extended");
                                  }
                              });
                          } catch (ParseException e) {
                              Log.i(TAG, e.getMessage() + "ERR HERE PARSE");
                          }
                      //}
                  }



                  // fees
                  else if (metadata.getIntentName().equals(FEE_CATEGORY_ADD_INTENT) || metadata.getIntentName().equals("GENERAL Add bills")) {
                      current = "bills";
                      if (responseParams.get(FEE_CATEGORY_COLUMN_NAME) != null &&
                              responseParams.get(FEE_CATEGORY_COLUMN_BILL_DATE) != null &&
                              responseParams.get(FEE_CATEGORY_COLUMN_DUE_DATE) != null) {
                          Integer bill_date = Integer.valueOf(responseParams.get(FEE_CATEGORY_COLUMN_BILL_DATE).getAsString());
                          Integer due_date = Integer.valueOf(responseParams.get(FEE_CATEGORY_COLUMN_DUE_DATE).getAsString());

                          String bill_date_string = responseParams.get(FEE_CATEGORY_COLUMN_BILL_DATE).getAsString();
                          String due_date_string = responseParams.get(FEE_CATEGORY_COLUMN_DUE_DATE).getAsString();

                          if(bill_date > 31) bill_date_string = "31";
                          else if(bill_date < 1) bill_date_string = "1";
                          if(due_date > 31) due_date_string = "31";
                          else if(due_date < 1) due_date_string = "1";

                          Boolean success = FeeCategory.insert(responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(),
                                  bill_date_string,
                                  due_date_string, database);
                          if(success) {
                              ArrayList<FeeCategory> fee_categories = new ArrayList<FeeCategory>();
                              fee_categories.add(FeeCategory.getByName(responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(), database));

                              addMessage("BILLS","","","SCHEDULE_HEADER");

                              stringify(fee_categories);
                              addMessage(responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(),
                                      FeeCategory.getByName(responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(), database).getId().toString(),
                                      "", "FEE_BUTTON");
                              addMessage("Wow! An addition to your bills to pay. I've added " + responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString() + " to your bills. " +
                                      "I will send you a reminder three days before the due date.", "", "", "BOT");

                          }
                          else addMessage("I've failed to add " + responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString() + " to your bills since it already exists in your list. Bills must be unique!", "", "", "BOT");
                          if(database.getHelpValue("ADD_BILLS") == 0) {
                              addMessage("Alright! Moving on, so, you might be looking for events to attend to anytime soon? Just say 'show events' and I'll look for you!", "", "", "BOT");
                              database.revertHelpValue("ADD_BILLS");
                          }
                      }

                  }

                  else if (metadata.getIntentName().equals("FEE CATEGORY Update bill date")) {
                      current = "bills";
                      if(responseParams.get("name") != null && responseParams.get("bill_due") != null) {
                          String name = responseParams.get("name").getAsString();
                          String bill_due = responseParams.get("bill_due").getAsString();

                          if (responseParams.get("date") != null && bill_due.equals("bill date")) {
                              Boolean success = FeeCategory.update(null,
                                      responseParams.get("date"),
                                      null,
                                      responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(), database);
                              if (success)
                                  addMessage("Successfully updated " + name + " from fee categories.", "", "", "BOT");
                              else
                                  addMessage("I've failed to update " + name + ". Category may not exist or the new name already exists.", "", "", "BOT");
                          }

                          else if (responseParams.get("date") != null && bill_due.equals("due date")) {
                              Boolean success = FeeCategory.update(null,
                                      null,
                                      responseParams.get("date"),
                                      responseParams.get(FEE_CATEGORY_COLUMN_NAME).getAsString(), database);
                              if (success)
                                  addMessage("Successfully updated " + name + " from fee categories.", "", "", "BOT");
                              else
                                  addMessage("I've failed to update " + name + ". Category may not exist or the new name already exists.", "", "", "BOT");
                          }
                      }
                  }

                  else if(metadata.getIntentName().equals("FEE Pay")) {
                      current = "bills";
                      if(responseParams.get("name") != null) {
                          String name = responseParams.get("name").getAsString();
                          ArrayList<FeeCategory> view_fee_categories = FeeCategory.getSome(responseParams.get(FEE_CATEGORY_COLUMN_NAME),
                                  responseParams.get(FEE_CATEGORY_COLUMN_BILL_DATE),responseParams.get(FEE_CATEGORY_COLUMN_DUE_DATE), database);
                          Log.i(TAG, "fee_categories" +  view_fee_categories);

                          Integer fee_category_id = null;
                          for(int i=0; i<view_fee_categories.size(); i++) {
                              if(view_fee_categories.get(i).getName().equals(name)) {
                                  fee_category_id = view_fee_categories.get(i).getId();
                                  break;
                              }
                          }
                          if(fee_category_id != null) {
                              view_fees_array = Fee.getUsingCategory(fee_category_id, database);
                              ArrayList<FeeCategory> fee_categories = new ArrayList<FeeCategory>();
                              fee_categories.add(FeeCategory.getByName(name, database));
                              addMessage(name.toUpperCase() + " BILL","","","SCHEDULE_HEADER");
                              stringify(fee_categories);
                              /*if(view_fees_array.size() != 0) */addMessage(name, fee_category_id.toString(), "", "FEE_BUTTON");
                          }
                          else {
                              addMessage("Aw, you don't have " + name + " bills on my list. We can't view that.", "", "", "BOT");
                          }
                      }
                  }

                    //folders and photos
                   else if(metadata.getIntentName().equals(PHOTO_ADD_INTENT)) {
                      current = "folders";
                      if(responseParams.get("name") == null) {
                          ArrayList<Folder> folders = Folder.getAll(database);
                          addMessage("YOUR FOLDERS", "", "", "SCHEDULE_HEADER");
                          for(int i=0; i<folders.size(); i++) {
                              addMessage(folders.get(i).getName(), "", "", "FOLDER");
                          }
                      }
                      if(responseParams.get("name") != null) {
                          add_folder_name = responseParams.get("name").getAsString();
                          Folder folder = Folder.getOne(responseParams.get("name").getAsString(), database);
                          if(folder.getId() != null) {
                              if(database.getHelpValue("CAMERA") == 0) {
                                  addMessage("We're down to the last set of things that I can do! Aside from organizing, I can also " +
                                          "remind you of things!", "", "", "BOT");
                                  addMessage("At this point, you're probably paying your own bills like your rent, " +
                                          "electricity, water. I can also remind you of those so you won't forget. Just say 'add a bill.'", "", "", "BOT");
                                  database.revertHelpValue("CAMERA");
                              }
                              takePicture(folder.getId(), folder.getName());
                          } else {
                              sendEvent("FOLDER_NOT_FOUND", "");
                              addMessage("Oops. Looks like there's no folder named " + responseParams.get("name").getAsString() + ". Do you want to create one? I'll open the camera right after.", "", "", "BOT");
                          }
                      }
                  }
                  else if(metadata.getIntentName().equals("FOLDER Not found sure")) {
                      current = "folders";
                      if(responseParams.get("yes_no") != null) {
                          if(responseParams.get("yes_no").getAsString().equals("yes")) {
                              Boolean success = Folder.insert(add_folder_name, database);
                              if(success) {
                                  addMessage("Alright, I've added " + add_folder_name + " to your folders. Opening camera...", "", "", "BOT");
                                  Integer new_folder_id = Folder.getOne(add_folder_name, database).getId();
                                  takePicture(new_folder_id, add_folder_name);

                              }
                              else addMessage("Oops, you already have a folder named " + responseParams.get("name").getAsString() + ".", "", "", "BOT");
                          } else if (responseParams.get("yes_no").getAsString().equals("no")) {
                              addMessage("Okay, I won't create that folder. However, you can't add a photo.", "", "", "BOT");
                          }

                          if(database.getHelpValue("CAMERA") == 0) {
                              addMessage("We're down to the last set of things that I can do! Aside from organizing, I can also " +
                                      "remind you of things!", "", "", "BOT");
                              addMessage("At this point, you're probably paying your own bills like your rent, " +
                                      "electricity, water. I can also remind you of those so you won't forget. Just say 'add a bill.'", "", "", "BOT");
                              database.revertHelpValue("CAMERA");
                          }
                      }
                  }

                  // where to buy
                  else if(metadata.getIntentName().equals(WHERE_TO_BUY_INTENT)) {
                      current = "";
                      if(responseParams.get("name") != null) {
                          addMessage("Hold on. I'm looking through the nearby places..", "", "", "BOT");
                          Intent i = new Intent(getBaseContext(), MapsActivity.class);
                          Bundle bundle = new Bundle();
                          bundle.putString("keyword", responseParams.get("name").getAsString());
                          i.putExtras(bundle);
                          startActivity(i);
                      }
                  }

                  // grades
                  else if(metadata.getIntentName().equals(SHOW_MY_GRADES_INTENT)) {
                      current = "";
                      ArrayList<Grade> grades = Grade.getAll(database);
                      Float gwa = 0.0f;
                      Float units = 0.0f;
                      if(grades.size() == 0) {
                          addMessage("You don't have grades saved on your local data. I can load it from SAIS though. Is that okay?", "", "", "BOT");
                      } else {
                          String message = "";
                          addMessage("YOUR GRADES", "", "", "SCHEDULE_HEADER");
                          addMessage("SUBJECT", "GRADE", "UNITS", "GRADE_NEW");
                          for(int i=0; i<grades.size(); i++) {
                              message += (grades.get(i).getCode() + "  -  " + grades.get(i).getGrade() + "\n");
                              if(grades.get(i).getGrade() != 7.0f && grades.get(i).getGrade() != 8.0f && grades.get(i).getGrade() != 9.0f && grades.get(i).getGrade() != 10.0f) {
                                  units += grades.get(i).getUnits();
                                  gwa += (grades.get(i).getGrade() * grades.get(i).getUnits());
                              }
                              addMessage(grades.get(i).getCode(), grades.get(i).getGrade().toString(), grades.get(i).getUnits().toString(), "GRADE_NEW");
                          }
                          if(message.equals("")) message = "No grades saved. Do you want to load data from SAIS? Yes or no?";
                          else {
                              message = "Here are your grades.";
                              gwa = gwa/units;
                              Log.i(TAG, "GWA KO!" + gwa);
                              message = message + " Your General Weighted Average is " + gwa + ".\n\nThese are the ones I've saved from the last time. Although do you want to reload new data from SAIS? Yes or no?";
                          }
                          addMessage(message, "", "", "BOT");
                      }
                  }
                  else if(metadata.getIntentName().equals(SHOW_MY_GRADES_YES_INTENT)) {
                      current = "";
                      if(responseParams.get("yes_no") != null && responseParams.get("yes_no").getAsString().equals("yes")) {
                          current = "";
                          if(cookie.equals("")) {
                              final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                              alert.setTitle("UP SAIS");

                              LinearLayout wrapper = new LinearLayout(ChatActivity.this);
                              WebView wv = new WebView(ChatActivity.this);

                              WebSettings webSettings = wv.getSettings();
                              webSettings.setJavaScriptEnabled(true);
                              wv.loadUrl("https://sais.up.edu.ph/");


                              EditText keyboardHack = new EditText(ChatActivity.this);
                              keyboardHack.setVisibility(View.GONE);

                              wrapper.addView(wv);
                              wrapper.addView(keyboardHack);

                              alert.setView(wrapper);
                              alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int id) {
                                      dialog.dismiss();
                                  }
                              });

                              final AlertDialog show = alert.show();

                              wv.setWebViewClient(new WebViewClient() {
                                  @Override
                                  public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                      Log.i(TAG, "URL HERE:" + url);
                                      if (url.equals("https://sais.up.edu.ph/psp/ps/EMPLOYEE/HRMS/h/?tab=DEFAULT")) {
                                          addMessage("I'm getting the data from SAIS already. Please hold on.", "", "", "BOT");
                                          String grades_url = "https://sais.up.edu.ph/psc/ps/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL?FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.HC_SSS_MY_CRSEHIST_GBL&amp;IsFolder=false&amp;IgnoreParamTempl=FolderPath%2cIsFolder&amp;PortalActualURL=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2fEMPLOYEE%2fHRMS%2fc%2fSA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL&amp;PortalContentURL=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2fEMPLOYEE%2fHRMS%2fc%2fSA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=My%20Course%20History&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fsais.up.edu.ph%2fpsp%2fps%2f&amp;PortalURI=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes";
                                          cookie = CookieManager.getInstance().getCookie(url);
                                          new DownloadGradesTask(ChatActivity.this).execute(grades_url, cookie);
                                          view.destroy();
                                          show.dismiss();
                                      }
                                      view.loadUrl(url);
                                      return true;
                                  }
                              });
                          } else {
                              addMessage("I'm getting the data from SAIS already. Please hold on.", "", "", "BOT");
                              String grades_url = "https://sais.up.edu.ph/psc/ps/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL?FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HCCC_ACADEMIC_RECORDS.HC_SSS_MY_CRSEHIST_GBL&amp;IsFolder=false&amp;IgnoreParamTempl=FolderPath%2cIsFolder&amp;PortalActualURL=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2fEMPLOYEE%2fHRMS%2fc%2fSA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL&amp;PortalContentURL=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2fEMPLOYEE%2fHRMS%2fc%2fSA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL&amp;PortalContentProvider=HRMS&amp;PortalCRefLabel=My%20Course%20History&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fsais.up.edu.ph%2fpsp%2fps%2f&amp;PortalURI=https%3a%2f%2fsais.up.edu.ph%2fpsc%2fps%2f&amp;PortalHostNode=HRMS&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes";
                              new DownloadGradesTask(ChatActivity.this).execute(grades_url, cookie);
                          }
                      } else if(responseParams.get("yes_no") != null && responseParams.get("yes_no").getAsString().equals("no")) {
                          addMessage("Alright. We won't load grades from SAIS.", "", "", "BOT");
                          if(database.getHelpValue("GRADES") == 0) {
                              database.setHelpValue("GRADES");
                              addMessage("Well, alright, now let's move on to my organizing skills. First, I can " +
                                      "also help you manage your tasks. For example, tell me to schedule a meeting tomorrow.", "", "", "BOT");
                              database.revertHelpValue("GRADES");
                          }
                      }
                  }

                  //facebook events
                  else if(metadata.getIntentName().equals("VIEW_EVENTS")) {
                      current = "";
                      if((responseParams.get("type") != null || responseParams.get("date") != null
                              || responseParams.get("location") != null) && responseParams.get("from") != null){
                          addMessage("Please hold on for a while, I'm looking for events...", "", "", "BOT");
                          runOnUiThread(new Runnable() {
                                  public void run() {
                                      String type = "";
                                      String date = "";
                                      String location = "";
                                      if (responseParams.get("type") != null)
                                          type = responseParams.get("type").getAsString();
                                      if (responseParams.get("date") != null)
                                          date = responseParams.get("date").getAsString();
                                      if (responseParams.get("location") != null)
                                          location = responseParams.get("location").getAsString();

                                      if (responseParams.get("from").getAsString().equals("eventbrite"))
                                          new DownloadEventbriteTask(ChatActivity.this, location, date, type).execute();
                                      else if (responseParams.get("from").getAsString().equals("facebook")) {
                                          String access_token = "";
                                          if(AccessToken.getCurrentAccessToken() != null) access_token = AccessToken.getCurrentAccessToken().getToken();
                                          new DownloadFacebookTask(ChatActivity.this, access_token, type, date, location).execute();
                                      } else {
                                          new DownloadEventbriteTask(ChatActivity.this, location, date, type).execute();
                                          String access_token = "";
                                          if(AccessToken.getCurrentAccessToken() != null) AccessToken.getCurrentAccessToken().getToken();
                                          new DownloadFacebookTask(ChatActivity.this, access_token, type, date, location).execute();
                                      }
                                  }
                              });
                      }
                  }


                  // expenses

                  else if(metadata.getIntentName().equals(EXPENSE_ADD_INTENT)) {
                      current = "expenses";
                      if(responseParams.get("amount") != null && responseParams.get("name") != null) {
                          expense_name = responseParams.get("name").getAsString();
                          expense_amount = responseParams.get("amount").getAsFloat();
                          if(responseParams.get("date") != null) expense_date = responseParams.get("date").getAsString();
                          else expense_date = null;
                          String name = responseParams.get("name").getAsString();

                          ExpenseCategory expense_category = ExpenseCategory.getOne(responseParams.get("name").getAsString(), database);
                          if(expense_category.getId() == null) {
                              //sendEvent("EXPENSE_CATEGORY_NOT_FOUND", "");
                              // addMessage("Oops, you don't have " + responseParams.get("name").getAsString() + " on your types of expenses yet. Want to add?", "", "", "BOT");

                              ExpenseCategory.insert(name, database);
                          }
                          expense_category = ExpenseCategory.getOne(name, database);

                          String expense_date_else = "";
                          if(responseParams.get("date") == null) expense_date_else = date_only_format.format(new Date()).toString();
                          else {
                              try {
                                  expense_date_else = date_only_format.format(date_pre.parse(responseParams.get("date").getAsString())).toString();
                              } catch(Exception e) {}
                          }
                          Boolean success = Expense.insert(responseParams.get("amount").getAsFloat(), expense_date_else, expense_category.getId(), database);
                          if(success) addMessage("Noted that down! P" + responseParams.get("amount").getAsFloat() + " added to " + expense_category.getName(), "", "", "BOT");
                          else addMessage("Error.", "", "", "BOT");

                          Integer id = ExpenseCategory.getOne(name, database).getId();
                          ArrayList<Expense> expenses = Expense.getAll(id, database);
                          Float total = (float) 0;

                          if(expenses.size() == 0) addMessage("You have no record of expenses under " + name + " yet", "", "", "BOT");
                          else {
                              addMessage(name.toUpperCase() + " EXPENSES","","","SCHEDULE_HEADER");
                              addMessage("Amount", "Date", "", "GRADE");
                              String expense_string = "Here are your records of " + responseParams.get("name").getAsString() + " expenses.";
                              for(int i=0; i<expenses.size(); i++) {
                                  total += expenses.get(i).getAmount();
                                  addMessage("P"+expenses.get(i).getAmount().toString(), expenses.get(i).getDate(), "", "GRADE");
                              }
                              Log.i(TAG, "ID ID ID " + id.toString());
                              if(expenses.size() != 0) addMessage(name, id.toString(), "", "EXPENSES_BUTTON");
                              expense_string += ("\nYou spent a total of P" + total + " for " + name + ".");
                              addMessage(expense_string, "", "", "BOT");
                          }

                          if(database.getHelpValue("ADD_EXPENSES") == 0 ) {
                              addMessage("I have another thing that I can do! You know what, I've heard that students are having " +
                                      "a hard time looking for pictures of notes that they took in class. They also have a hard time" +
                                      " filing it in folders.", "", "", "BOT");
                              addMessage("So I thought, maybe I could help! Just say 'open camera' and I'll file your picture under a folder.", "", "", "BOT");
                              database.revertHelpValue("ADD_EXPENSES");
                          }
                      }
                  }

                  else if(metadata.getIntentName().equals(ATTENDANCE_VIEW_SCHEDULE_INTENT)) {
                      current = "";
                      ArrayList<Subject> subjects = Subject.getAll(database);
                      if(subjects.size() == 0) {
                          addMessage("You do not have your schedule stored in your local data. I can load it from SAIS though. Is that okay?", "", "", "BOT");
                      } else {
                          String message = "";
                          addMessage("CLASS SCHEDULE","","","SCHEDULE_HEADER");
                          for(int i=0; i<subjects.size(); i++) {
                              //message += (subjects.get(i).getName() + "\n\tVenue: " + subjects.get(i).getLocation() + "\n\tSchedule: " + subjects.get(i).getDate_time() + "\n\tAbsences: " + subjects.get(i).getAbsences() + "\n\n");
                              addMessage(subjects.get(i).getName(), subjects.get(i).getLocation() + "\n" + subjects.get(i).getDate_time(), "Absences: " + subjects.get(i).getAbsences(), "TASK");
                          }
                          addMessage("", "", "", "SUBJECTS_BUTTON");
                          message = "Here are your subjects for this semester. Do you want to reload the data from SAIS? Yes or no?";
                          addMessage(message, "", "", "BOT");
                      }
                  }
                  else if(metadata.getIntentName().equals(ATTENDANCE_VIEW_SCHEDULE_YES_NO_INTENT)) {
                      current = "";
                      if(responseParams.get("yes_no") != null) {
                          if(responseParams.get("yes_no").getAsString().equals("yes")) {
                              if(cookie.equals("")) {
                                  current = "";

                                  final AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this);
                                  alert.setTitle("UP SAIS");

                                  LinearLayout wrapper = new LinearLayout(ChatActivity.this);
                                  WebView wv = new WebView(ChatActivity.this);

                                  WebSettings webSettings = wv.getSettings();
                                  webSettings.setJavaScriptEnabled(true);
                                  wv.loadUrl("https://sais.up.edu.ph/");


                                  EditText keyboardHack = new EditText(ChatActivity.this);
                                  keyboardHack.setVisibility(View.GONE);

                                  wrapper.addView(wv);
                                  wrapper.addView(keyboardHack);

                                  alert.setView(wrapper);
                                  alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int id) {
                                          dialog.dismiss();
                                      }
                                  });

                                  final AlertDialog show = alert.show();

                                  wv.setWebViewClient(new WebViewClient() {
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                          Log.i(TAG, "URL HERE:" + url);
                                          if (url.equals("https://sais.up.edu.ph/psp/ps/EMPLOYEE/HRMS/h/?tab=DEFAULT")) {
                                              addMessage("I'm getting your subjects from SAIS. Please hold on.", "", "", "BOT");
                                              String subjects_url = "https://sais.up.edu.ph/psc/ps/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HC_SSS_STUDENT_CENTER&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder";
                                              cookie = CookieManager.getInstance().getCookie(url);
                                              new DownloadSubjectsTask(ChatActivity.this).execute(subjects_url, cookie);
                                              view.destroy();
                                              show.dismiss();
                                          } else view.loadUrl(url);
                                          return true;
                                      }
                                  });
                              }
                              else {
                                  String subjects_url = "https://sais.up.edu.ph/psc/ps/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?FolderPath=PORTAL_ROOT_OBJECT.CO_EMPLOYEE_SELF_SERVICE.HC_SSS_STUDENT_CENTER&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder";
                                  new DownloadSubjectsTask(ChatActivity.this).execute(subjects_url, cookie);
                              }
                          }
                          else {
                              addMessage("Okay. We won't load it then.", "", "", "BOT");

                              if(database.getHelpValue("SHOW_SCHEDULE") == 0) {
                                  addMessage("I hope you're getting the hang of it. I guess sometimes, you might" +
                                          " be too lazy to check SAIS. I can do it " +
                                          "easily for you. Just tell me to show your grades.", "", "", "BOT");
                                  database.revertHelpValue("SHOW_SCHEDULE");
                              }
                          }
                      }
                  } else if(metadata.getIntentName().equals(ATTENDANCE_ADD_ABSENT_INTENT)) {
                      current = "";
                      subjects_update.clear();
                      if(responseParams.get("name") != null) {
                          String name = responseParams.get("name").getAsString().toLowerCase();
                          ArrayList<Subject> subjects = Subject.getAll(database);
                          String subj = "";
                          for(int i=0; i<subjects.size(); i++) {
                              subj = subjects.get(i).getName().replaceAll("\\s","").toLowerCase();
                              name = name.replaceAll("\\s","").toLowerCase();
                              if(subj.startsWith(name)) {
                                  subjects_update.add(subjects.get(i));
                              }
                          }
                          if(subjects_update.size() == 0) {
                              resendNewRequest("0");
                              addMessage("Sorry, you're not taking any subject named " + name + ". I can't mark you absent for that class.", "", "", "BOT");
                          }
                          else if(subjects_update.size() == 1) {
                              resendNewRequest("1");
                          }
                          else {
                              String message = "";
                              message += "Several subjects matched with " + name + ".\n\n";
                              for(int i=0; i<subjects_update.size(); i++) {
                                  message += ("[" + (i+1) + "]" + " " + subjects_update.get(i).getName() + "\n");
                              }
                              message += "\n\nWhich one of these did you not go to? Just type the number.";
                              addMessage(message, "", "", "BOT");
                          }
                      }
                  } else if (metadata.getIntentName().equals(ATTENDANCE_ADD_ABSENT_SELECT_INTENT)) {
                      current = "";
                      if(Subject.getAll(database).size() == 0) addMessage("I don't have your subjects yet. Try saying \"Get my schedule\" to load your subjects from SAIS.", "", "", "BOT");
                      else if(responseParams.get("number").getAsJsonArray().size() != 0) {
                          String message = "";
                          Boolean flag = false;
                          Iterator iterator = responseParams.get("number").getAsJsonArray().iterator();
                          while (iterator.hasNext()) {
                              Integer num_update = Integer.valueOf(iterator.next().toString());
                              if (num_update > 0 && num_update <= subjects_update.size()) {
                                  Integer id_update = subjects_update.get(num_update - 1).getId();
                                  Integer absences = subjects_update.get(num_update - 1).getAbsences();
                                  Subject.addAbsence(id_update, absences, database);
                                  message += ((absences + 1) + " absences in " + subjects_update.get(num_update - 1).getName() + " ");
                                    flag = true;
                              }
                          }
                          if(flag) {
                              addMessage("Updated. In my records, you now have " + message + "Don't do this again!", "", "", "BOT");
                          }

                          if(database.getHelpValue("ADD_ABSENCE") == 0) {
                              addMessage("Let's proceed to the next. We're still talking about school. I guess sometimes, you might" +
                                      " be too lazy to check SAIS. I can do it " +
                                      "easily for you. Just tell me to show your grades.", "", "", "BOT");
                              database.revertHelpValue("ADD_ABSENCE");
                          }
                      }
                  }

                  //TASK
                  //add task
                  else if (intent.equals(ADD_TASK_INTENT) || intent.equals("GENERAL Add task")) {
                      current = "tasks";
                      if (responseParams.get(TASKS_TABLE_NAME) != null &&
                              responseParams.get(TASKS_COLUMN_DATE) != null &&
                              responseParams.get(TASKS_COLUMN_TIME_START) != null &&
                              responseParams.get(TASKS_COLUMN_TIME_END) != null) {

                          try {
                              Calendar cal = Calendar.getInstance();
                              SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                              cal.setTime(f.parse(responseParams.get(TASKS_COLUMN_DATE).getAsString() + responseParams.get(TASKS_COLUMN_TIME_START).getAsString()));

                              Intent cal_intent = new Intent(Intent.ACTION_EDIT);
                              cal_intent.setType("vnd.android.cursor.item/event");
                              cal_intent.putExtra("beginTime", cal.getTimeInMillis());

                              cal.setTime(f.parse(responseParams.get(TASKS_COLUMN_DATE).getAsString() + responseParams.get(TASKS_COLUMN_TIME_END).getAsString()));

                              cal_intent.putExtra("endTime", cal.getTimeInMillis());
                              cal_intent.putExtra("title", responseParams.get(TASKS_TABLE_NAME).getAsString());

                              new_id = getNewEventId(getContentResolver());

                              startActivityForResult(cal_intent, 200);
                          } catch (Exception e) {
                              Log.i(TAG, e.getMessage());
                          }

                          if(database.getHelpValue("ADD_TASK") == 0) {
                              addMessage("On to the next! I can also record your own expenses so you won't wonder where " +
                                      "your money magically goes to. For example, say 'I spent 100 on food today!'", "", "", "BOT");
                              database.revertHelpValue("ADD_TASK");
                          }

                      }

                  }

                  else if (intent.equals(SHOW_SOME_TASKS_INTENT)) {
                      current = "tasks";
                        /*if(responseParams.get("task") != null &&
                                (responseParams.get("task").getAsString().equals("task") ||
                                        responseParams.get("task").getAsString().equals("tasks")))
                            responseParams.put("task", null);*/

                      ArrayList<Long[]> id = parseTask(responseParams);
                      if(id.size() != 0) addMessage("Here's what I found.", "", "", "BOT");
                  }

                  //delete tasks
                  else if (metadata.getIntentName().equals(DELETE_TASK_INTENT)) {
                      current = "tasks";
                      responseParams.put("date_start", responseParams.get("date"));
                      delete_tasks_ids = parseTask(responseParams);
                      if(delete_tasks_ids.size() != 0 && delete_tasks_ids.size() != 1) {
                          sendEvent("TASK_FOUND", "");
                          addMessage("Here are your tasks that matched. Which numbers would you like to delete?", "", "", "BOT");
                      }
                      else if(delete_tasks_ids.size() == 1) {
                          addMessage("Here's the task that I found.", "", "", "");
                          sendEvent("DELETE_TASK_FOUND", "");
                          resendNewRequest("1");
                      } else {
                          sendEvent("TASK_NOT_FOUND", "");
                      }
                  }
                  else if (metadata.getIntentName().equals(WHICH_TASKS_INTENT)) {
                      current = "tasks";
                      delete_itr = responseParams.get("number").getAsJsonArray().iterator();
                  } else if (metadata.getIntentName().equals(SURE_DELETE_TASKS_INTENT)) {
                      Boolean flag = false;
                      String invalids = "";
                      if(responseParams.get("yes_no") != null) {
                          if(responseParams.get("yes_no").getAsString().equals("yes")) {
                              while(delete_itr.hasNext()) {
                                  Log.i(TAG, "1");
                                  Integer num_delete = Integer.valueOf(delete_itr.next().toString());
                                  if(num_delete > 0 && num_delete <= delete_tasks_ids.size()) {
                                      Long inst_id = delete_tasks_ids.get(num_delete-1)[0];
                                      Long event_id = delete_tasks_ids.get(num_delete-1)[1];
                                      String mSelectionClause = CalendarContract.Events._ID + " = ?";
                                      String[] mSelectionArgs = { event_id.toString() };
                                      int updCount = getContentResolver().delete(CalendarContract.Events.CONTENT_URI,mSelectionClause,mSelectionArgs);
                                      flag = true;
                                  } else {
                                      Log.i(TAG, "3");
                                      invalids += (num_delete + " ");
                                  }
                              }
                              if(!invalids.equals("")) {
                                  invalids = " However, there's no " + invalids + " on the list.";
                              }

                              if(flag) addMessage("I've successfully deleted these tasks!" + invalids, "", "", "BOT");
                              else addMessage("Hold on, that's even on the list. I can't delete that.", "", "", "BOT");

                          } else {
                              addMessage("Okay. I won't delete any of your tasks.", "", "", "BOT");
                          }
                      }
                  }


                  else if(metadata.getIntentName().equals("TASK Update")) {
                      current = "tasks";
                      responseParams.put("time_start", responseParams.get("old_time_start"));
                      responseParams.put("time_end", responseParams.get("old_time_end"));
                      responseParams.put("task", responseParams.get("name"));
                      update_params = responseParams;
                      update_tasks_ids = parseTask(update_params);
                      if(update_tasks_ids.size() != 0 && update_tasks_ids.size() != 1) {
                          addMessage("Apparently, I found more than one. Which numbers would you like to update?", "", "", "BOT");
                          sendEvent("UPDATE_TASK_FOUND", "");
                      }
                      else if(update_tasks_ids.size() == 1) {
                          addMessage("Here's the task that I found.", "", "", "");
                          sendEvent("UPDATE_TASK_FOUND", "");
                          resendNewRequest("1");
                      }/* else {
                          addMessage("Oops, sorry. I found no tasks.", "", "", "");
                      }*/
                  } else if(metadata.getIntentName().equals("TASK Update select")) {
                      current = "tasks";
                      if(responseParams.get("number") != null) {
                          update_itr = responseParams.get("number").getAsJsonArray().iterator();
                          while (update_itr.hasNext()) {
                              Integer num_update = Integer.valueOf(update_itr.next().toString());
                              Long id_update = update_tasks_ids.get(num_update - 1)[1];

                              if (num_update < 0 || num_update > update_tasks_ids.size()) {
                                  continue;
                              }

                              ContentValues contentValues = new ContentValues();
                              SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
                              if (update_params.get("new_time_start") != null) {
                                  try {
                                      Calendar cal = Calendar.getInstance();
                                      Integer hr = f.parse(update_params.get("new_time_start").getAsString()).getHours();
                                      Integer min = f.parse(update_params.get("new_time_start").getAsString()).getMinutes();
                                      cal.set(Calendar.HOUR_OF_DAY, hr);
                                      cal.set(Calendar.MINUTE, min);
                                      contentValues.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
                                  } catch (Exception e) {
                                      Log.i(TAG, e.getMessage());
                                  }
                              }
                              if (update_params.get("new_time_end") != null) {
                                  try {
                                      Calendar cal = Calendar.getInstance();
                                      Integer hr = f.parse(update_params.get("new_time_end").getAsString()).getHours();
                                      Integer min = f.parse(update_params.get("new_time_end").getAsString()).getMinutes();
                                      cal.set(Calendar.HOUR_OF_DAY, hr);
                                      cal.set(Calendar.MINUTE, min);
                                      contentValues.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());
                                  } catch (Exception e) {
                                      Log.i(TAG, e.getMessage());
                                  }
                              }

                              Uri uri = CalendarContract.Events.CONTENT_URI;

                              String mSelectionClause = CalendarContract.Events._ID + " = ?";
                              String[] mSelectionArgs = {id_update.toString()};

                              int updCount = getContentResolver().update(uri, contentValues, mSelectionClause, mSelectionArgs);
                          }

                          update_itr = responseParams.get("number").getAsJsonArray().iterator();
                          String numbers = "";
                          while (update_itr.hasNext()) {
                              Integer num_update = Integer.valueOf(update_itr.next().toString());

                              if (num_update < 0 || num_update > update_tasks_ids.size()) {
                                  numbers += (num_update + " ");
                                  continue;
                              }

                              Long id_update = update_tasks_ids.get(num_update - 1)[1];
                              Log.i(TAG, id_update + " ID");
                              ContentValues contentValues = new ContentValues();
                              SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                              if (update_params.get("new_date") != null) {
                                  try {
                                      Calendar cal = Calendar.getInstance();

                                      String[] mProjection =
                                              {
                                                      "_id",
                                                      CalendarContract.Events.TITLE,
                                                      CalendarContract.Events.EVENT_LOCATION,
                                                      CalendarContract.Events.DTSTART,
                                                      CalendarContract.Events.DTEND,
                                                      CalendarContract.Events.DESCRIPTION
                                              };

                                      Uri uri = CalendarContract.Events.CONTENT_URI;
                                      String selection = CalendarContract.Events._ID + " = ? ";
                                      String[] selectionArgs = new String[]{id_update.toString()};

                                      Cursor cur = getContentResolver().query(uri, mProjection, selection, selectionArgs, null);
                                      cur.moveToFirst();

                                      Integer month = f.parse(update_params.get("new_date").getAsString()).getMonth();
                                      Integer day = f.parse(update_params.get("new_date").getAsString()).getDate();
                                      Integer year = f.parse(update_params.get("new_date").getAsString()).getYear() + 1900;

                                      Long startMillis = cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                                      cal.setTimeInMillis(startMillis);
                                      cal.set(Calendar.MONTH, month);
                                      cal.set(Calendar.DAY_OF_MONTH, day);
                                      cal.set(Calendar.YEAR, year);
                                      contentValues.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());

                                      Long endMillis = cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTEND));
                                      cal = Calendar.getInstance();
                                      cal.setTimeInMillis(endMillis);
                                      cal.set(Calendar.MONTH, month);
                                      cal.set(Calendar.DAY_OF_MONTH, day);
                                      cal.set(Calendar.YEAR, year);
                                      contentValues.put(CalendarContract.Events.DTEND, cal.getTimeInMillis());

                                      uri = CalendarContract.Events.CONTENT_URI;
                                      String mSelectionClause = CalendarContract.Events._ID + " = ?";
                                      String[] mSelectionArgs = {id_update.toString()};

                                      int updCount = getContentResolver().update(uri, contentValues, mSelectionClause, mSelectionArgs);


                                  } catch (Exception e) {
                                      Log.i(TAG, e.getMessage());
                                  }
                              }
                          }


                          addMessage("Updated!", "", "", "BOT");

                          if (!numbers.equals(""))
                              addMessage("But " + numbers + " are not on the list so I can't update that.", "", "", "BOT");
                      }
                  }



                  else if(metadata.getIntentName().equals("TASK Update notes")) {
                      current = "tasks";
                      if(responseParams.get("name") != null && responseParams.get("new_note") != null) {
                          responseParams.put("task", responseParams.get("name"));
                          update_params = responseParams;
                          update_tasks_ids = parseTask(responseParams);
                          if(update_tasks_ids.size() != 0) addMessage("Here are your tasks that matched. Which numbers would you like to delete?", "", "", "BOT");

                      }
                  } else if(metadata.getIntentName().equals("TASK Update notes select")) {
                      current = "tasks";
                      String numbers = "";
                      update_itr = responseParams.get("number").getAsJsonArray().iterator();
                      while(update_itr.hasNext()) {
                          Integer num_update = Integer.valueOf(update_itr.next().toString());

                          if(num_update < 0 || num_update > update_tasks_ids.size()) {
                              numbers += (num_update + " ");
                              continue;
                          }

                          Long id_update = update_tasks_ids.get(num_update-1)[1];

                          ContentValues contentValues = new ContentValues();
                          contentValues.put(CalendarContract.Instances.DESCRIPTION, update_params.get("new_note").getAsString());

                          Uri uri = CalendarContract.Instances.CONTENT_URI;

                          String mSelectionClause = CalendarContract.Events._ID+ " = ?";
                          String[] mSelectionArgs = { id_update.toString() };

                          int updCount = getContentResolver().update(uri, contentValues,mSelectionClause,mSelectionArgs);                    if(!numbers.equals("")) addMessage("But " + numbers + " are not on the list so I can't update that.", "", "", "BOT");
                          addMessage("Alright.", "", "", "BOT");
                      }
                      if(!numbers.equals("")) addMessage("But " + numbers + " are not on the list so I can't update that.", "", "", "BOT");

                  }

                  else if(metadata.getIntentName().equals("HELP")) {
                      //startActivity(HelpActivity2.class);
                      addMessage("","","","MENU");
                  }


                  Boolean def = !(intent.equals("Default Fallback Intent"));
                  Boolean help = !(intent.equals("HELP"));
                  Boolean ok = !(intent.equals("OK"));
                  Boolean thanks = !(intent.equals("THANK YOU")) && !(intent.equals("HELLO"));

              }

          });
      }

    Handler handler = new Handler();
    public void addMessage(final String message, final String image_url, final String post_url, final String sender) {


        if(!sender.equals("ME")) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    Integer botMessage_id = BaseMessage.insert(message, date_format.format(new Date()).toString(), image_url, post_url, sender, database);
                    BaseMessage botMessage = new BaseMessage(botMessage_id, message, date_format.format(new Date()).toString(), image_url, post_url, sender);
                    messageList.add(botMessage);
                    mMessageAdapter.notifyDataSetChanged();
                    //((LinearLayoutManager)mMessageRecycler.getLayoutManager()).scrollToPositionWithOffset(20,messageList.size());

                    mMessageRecycler.smoothScrollToPosition(messageList.size());
                }
            }, 1000);
        } else {
            Integer botMessage_id = BaseMessage.insert(message, date_format.format(new Date()).toString(), image_url, post_url, sender, database);
            BaseMessage botMessage = new BaseMessage(botMessage_id, message, date_format.format(new Date()).toString(), image_url, post_url, sender);
            messageList.add(botMessage);
            mMessageAdapter.notifyDataSetChanged();
            //((LinearLayoutManager)mMessageRecycler.getLayoutManager()).scrollToPositionWithOffset(20,messageList.size());
            mMessageRecycler.smoothScrollToPosition(messageList.size());


        }
    }

    public String getTimeStamp(String ymd){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date date=null;

        try {
            date = (Date) formatter.parse(ymd);
        }catch(ParseException e){
            e.getStackTrace();
        }

        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp_result = Long.parseLong(str);

        return Long.toString(timestamp_result);
    }

    public void stringify(ArrayList<FeeCategory> al) {
        String bill, due = "";
        for(int i=0; i<al.size(); i++) {
            Integer bill_date = Integer.valueOf(al.get(i).getBillDate());
            if((bill_date - 1) % 10 == 0) bill = "st of the month";
            else if((bill_date - 2) % 10 == 0) bill = "nd of the month";
            else if((bill_date - 3) % 10 == 0) bill = "rd of the month";
            else bill = "th of the month";

            Integer due_date = Integer.valueOf(al.get(i).getDueDate());
            if((due_date - 1) % 10 == 0) due = "st of the month";
            else if((due_date - 2) % 10 == 0) due = "nd of the month";
            else if((due_date - 3) % 10 == 0) due = "rd of the month";
            else due = "th of the month";
            ArrayList<Fee> fees = Fee.getUsingCategory(al.get(i).getId(), database);
            String unpaid = stringifee(fees, al.get(i).getName());
            addMessage(al.get(i).getName(),
                    "Bill comes every " + al.get(i).getBillDate() + bill + "\n" +
                            "Due date is every " + al.get(i).getDueDate() + due, unpaid, "TASK");
        }
        /*if(database.getHelpValue("bill_show_all") == 0) {
            addMessage("You can also view the unpaid bills that you have for each category.","","","BOT");
            database.setHelpValue("bill_show_all");
        }*/
    }

    private void allTasks() {
        try {
            Cursor cur = null;
            ContentResolver cr = getContentResolver();

            String[] mProjection =
                    {
                            CalendarContract.Instances._ID,
                            CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.EVENT_LOCATION,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Instances.DESCRIPTION
                    };

            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

            Calendar beginTime = Calendar.getInstance();
            Date date = new Date();
            beginTime.setTime(format.parse(date.toString()));
            beginTime.set(Calendar.HOUR_OF_DAY, 0);
            beginTime.set(Calendar.MINUTE, 0);
            beginTime.set(Calendar.SECOND, 0);
            Long startMillis = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(format.parse(date.toString()));
            endTime.add(Calendar.DATE, 7);
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 60);
            endTime.set(Calendar.SECOND, 60);
            Long endMillis = endTime.getTimeInMillis();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            cur = cr.query(builder.build(), mProjection, null, null, CalendarContract.Events.DTSTART + " ASC");

            if(cur.getCount() == 0) addMessage("Oops, you don't have any tasks yet. You can ask me to add.", "", "", "BOT");
            else {
                addMessage("Here are your tasks for the week.", "", "", "BOT");
                addMessage("TASKS","","","SCHEDULE_HEADER");
            }

            Calendar calendar = Calendar.getInstance();

            while (cur.moveToNext()) {
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));

                Long start = cur.getLong(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(start);
                String startMinString = "";
                int startYear = calendar.get(Calendar.YEAR);
                int startMonth = calendar.get(Calendar.MONTH) + 1;
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startHour = calendar.get(Calendar.HOUR);
                int startMin = calendar.get(Calendar.MINUTE);
                if(startMin < 10) startMinString = "0" + startMin;
                else startMinString = "" + startMin;
                int startAP = calendar.get(Calendar.AM_PM);
                String startAPString = "AM";
                if(startAP == Calendar.PM) startAPString = "PM";

                Long end = cur.getLong(cur.getColumnIndex(CalendarContract.Instances.END));
                calendar.setTimeInMillis(end);
                String endMinString = "";
                int endYear = calendar.get(Calendar.YEAR);
                int endMonth = calendar.get(Calendar.MONTH) + 1;
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endHour = calendar.get(Calendar.HOUR);
                int endMin = calendar.get(Calendar.MINUTE);
                if(endMin < 10) endMinString = "0" + endMin;
                else endMinString = "" + endMin;
                int endAP = calendar.get(Calendar.AM_PM);
                String endAPString = "AM";
                if(endAP == Calendar.PM) endAPString = "PM";

                String description = cur.getString(cur.getColumnIndex(CalendarContract.Instances.DESCRIPTION));
                if(description != null && !description.trim().isEmpty()) {
                    description = description;
                } else {
                    description = "No notes.";
                }
                Log.i(TAG, "description: " + description);
                String id = cur.getString(cur.getColumnIndex("_id"));
                String startDateString = startMonth + "/" + startDay + "/" + startYear;
                String startTimeString = startHour + ":" + startMinString + startAPString;
                String endDateString = endMonth + "/" + endDay + "/" + endYear;
                String endTimeString = endHour + ":" + endMinString + endAPString;
                String dateString = "";
                if(startDateString.equals(endDateString)) dateString = startDateString;
                else dateString = startDateString + " - " + endDateString;
                addMessage(title, dateString + "\n" + startTimeString + "-" + endTimeString  , description, "TASK");
            }
        } catch(Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }




    private ArrayList<Long[]> parseTask(HashMap<String, JsonElement> responseParams) {
        String responseName = "";
        String responseDateStart = "";
        String responseDateEnd = "";
        String responseTimeStart = "";
        String responseTimeEnd = "";

        String responseNameString = " anything ";
        String responseDateStartString = "";
        String responseDateEndString = "";
        String responseTimeStartString = "";
        String responseTimeEndString = "";

        ArrayList<Long[]> ids = new ArrayList<Long[]>();
        try {
            Cursor cur = null;
            ContentResolver cr = getContentResolver();

            String[] mProjection =
                    {
                            CalendarContract.Instances._ID,
                            CalendarContract.Instances.EVENT_ID,
                            CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.EVENT_LOCATION,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Instances.DESCRIPTION
                    };

            SimpleDateFormat initial = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm:ss");

            if(responseParams.get("date_start") != null) responseDateStart = responseParams.get("date_start").getAsString();
            else if(responseParams.get("date_end") != null) responseDateStart = responseParams.get("date_end").getAsString();
            else responseDateStart = dateParser.format(initial.parse(new Date().toString()));

            if(responseParams.get("date_end") != null) responseDateEnd = responseParams.get("date_end").getAsString();
            else if(responseParams.get("date_start") != null) responseDateEnd = responseParams.get("date_start").getAsString();
            else responseDateEnd = dateParser.format(initial.parse(new Date().toString()));

            if(responseParams.get("time_start") != null) responseTimeStart = responseParams.get("time_start").getAsString();
            else responseTimeStart = timeParser.format(initial.parse(new Date().toString()));

            if(responseParams.get("time_end") != null) responseTimeEnd = responseParams.get("time_end").getAsString();
            else responseTimeEnd = timeParser.format(initial.parse(new Date().toString()));

            String startFormat = "";
            String endFormat = "";

            if(!responseDateStart.equals("")) startFormat += "yyyy-MM-dd";
            if(!responseTimeStart.equals("")) startFormat += "HH:mm:ss";
            if(!responseDateEnd.equals("")) endFormat += "yyyy-MM-dd";
            if(!responseTimeEnd.equals("")) endFormat += "HH:mm:ss";

            Log.i(TAG, responseDateStart + responseTimeStart + " " + responseDateEnd + responseTimeEnd);

            SimpleDateFormat start_format = new SimpleDateFormat(startFormat);
            SimpleDateFormat end_format = new SimpleDateFormat(endFormat);

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance();
            if(responseParams.get("time_start") == null) {
                beginTime.setTime(dateParser.parse(responseDateStart));
                beginTime.set(Calendar.HOUR_OF_DAY, 0);
                beginTime.set(Calendar.MINUTE, 0);
                beginTime.set(Calendar.SECOND, 0);
            } else {
                beginTime.setTime(start_format.parse(responseDateStart + responseTimeStart));
            }
            Long startMillis = beginTime.getTimeInMillis();
            ContentUris.appendId(builder, startMillis);

            Calendar endTime = Calendar.getInstance();
            if(responseParams.get("time_start") == null && responseParams.get("time_end") == null) {
                endTime.setTime(dateParser.parse(responseDateEnd));
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 60);
                endTime.set(Calendar.SECOND, 60);
            } else if(responseParams.get("time_start") != null && responseParams.get("time_end") == null) {
                endTime.setTime(start_format.parse(responseDateStart + responseTimeStart));
            }
            else {
                endTime.setTime(end_format.parse(responseDateEnd + responseTimeEnd));
            }

            Long endMillis = endTime.getTimeInMillis();
            ContentUris.appendId(builder, endMillis);

            String selection;
            String[] selectionArgs;

            if(responseParams.get("task") != null && !responseParams.get("task").getAsString().equals("task") &&
                    !responseParams.get("task").getAsString().equals("tasks") && !responseParams.get("task").getAsString().equals("appointment")
                    && !responseParams.get("task").getAsString().equals("appointments")
                    && !responseParams.get("task").getAsString().equals("calendar")) {
                responseName = responseParams.get("task").getAsString();
                selection = CalendarContract.Instances.TITLE + " LIKE ?";
                selectionArgs = new String[]{ "%" + responseName + "%" };
            }
            else {
                selection = null;
                selectionArgs = null;
            }

            cur = cr.query(builder.build(), mProjection, selection, selectionArgs, CalendarContract.Events.DTSTART + " ASC");

            Calendar calendar = Calendar.getInstance();


            if(cur.getCount() == 0) {
                if(responseParams.get("task") != null) responseNameString = responseParams.get("task").getAsString();
                if(responseParams.get("time_start") != null) responseTimeStartString = " at " + time_format.format(time_pre.parse(responseParams.get("time_start").getAsString())) + " ";
                if(responseParams.get("time_end") != null) responseTimeEndString = " until " + time_format.format(time_pre.parse(responseParams.get("time_end").getAsString())) + " ";
                if(responseParams.get("date_start") != null) responseDateStartString = " on " + responseParams.get("date_start").getAsString();
                addMessage("Oops, you don't have" + responseNameString + responseTimeStartString + responseTimeEndString + responseDateStartString + ".", "", "", "BOT");
            }
            else addMessage("TASKS","","","SCHEDULE_HEADER");
            Integer count = 0;
            while (cur.moveToNext()) {
                ids.add( new Long[] {cur.getLong(cur.getColumnIndex(CalendarContract.Instances._ID)),
                        cur.getLong(cur.getColumnIndex(CalendarContract.Instances.EVENT_ID))} );

                String title = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));

                Long start = cur.getLong(cur.getColumnIndex(CalendarContract.Instances.BEGIN));
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(start);
                String startMinString = "";

                int startYear = calendar.get(Calendar.YEAR);
                int startMonth = calendar.get(Calendar.MONTH) + 1;
                int startDay = calendar.get(Calendar.DAY_OF_MONTH);
                int startHour = calendar.get(Calendar.HOUR);
                int startMin = calendar.get(Calendar.MINUTE);
                if(startMin < 10) startMinString = "0" + startMin;
                else startMinString = "" + startMin;
                int startAP = calendar.get(Calendar.AM_PM);
                String startAPString = "AM";
                if(startAP == Calendar.PM) startAPString = "PM";

                Long end = cur.getLong(cur.getColumnIndex(CalendarContract.Instances.END));
                calendar.setTimeInMillis(end);
                String endMinString = "";
                int endYear = calendar.get(Calendar.YEAR);
                int endMonth = calendar.get(Calendar.MONTH) + 1;
                int endDay = calendar.get(Calendar.DAY_OF_MONTH);
                int endHour = calendar.get(Calendar.HOUR);
                int endMin = calendar.get(Calendar.MINUTE);
                if(endMin < 10) endMinString = "0" + endMin;
                else endMinString = "" + endMin;
                int endAP = calendar.get(Calendar.AM_PM);
                String endAPString = "AM";
                if(endAP == Calendar.PM) endAPString = "PM";

                String description = cur.getString(cur.getColumnIndex(CalendarContract.Instances.DESCRIPTION));
                if(description != null && !description.trim().isEmpty()) {
                    description = description;
                } else {
                    description = "No notes.";
                }
                Log.i(TAG, "description: " + description);
                String id = cur.getString(cur.getColumnIndex("_id"));
                String startDateString = startMonth + "/" + startDay + "/" + startYear;
                String startTimeString = startHour + ":" + startMinString + startAPString;
                String endDateString = endMonth + "/" + endDay + "/" + endYear;
                String endTimeString = endHour + ":" + endMinString + endAPString;
                String dateString = "";
                if(startDateString.equals(endDateString)) dateString = startDateString;
                else dateString = startDateString + " - " + endDateString;
                count ++;
                addMessage("[" + count + "] " + title, dateString + "\n" + startTimeString + "-" + endTimeString  , description, "TASK");
            }

        } catch(Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return ids;
    }

    public String stringifee(ArrayList<Fee> al, String fee_category) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String due_date = FeeCategory.getByName(fee_category, database).getDueDate();
        if(al.size() == 0) return "You have no unpaid " + fee_category + " bills. A new record will be added once the bill date comes.";
        String s = "You have unpaid " + fee_category + " bills for the months of:\n";
        for(int i=0; i<al.size(); i++) {
            s += ("\t\t[" +(i+1) + "]\t\t" + monthNames[Integer.valueOf(al.get(i).getBillMonth()) - 1]);
            if(i!=al.size()-1) s += "\n";
        }
        return s;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                /*takePictureButton.setEnabled(true);*/
                //addMessage("Hold on.. I'm loading everything that we need.", "", "", "BOT");
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "CameraDemo");

                Log.i(TAG, "DIR" + mediaStorageDir);

                File[] files = new File("/storage/emulated/0/Pictures").listFiles(new ImageFileFilter());

                Folder.deleteAll(database);

                Log.i(TAG, files + " FILES ");
                if(files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            Log.i(TAG, "FOLDER: " + file.getAbsolutePath());
                            Folder.insert(file.getAbsolutePath().replaceAll("/storage/emulated/0/Pictures/", ""), database);
                        }
                    }
                }
                ArrayList<Folder> folders = Folder.getAll(database);
                for(int i=0; i<folders.size(); i++) {
                    Integer folderid = Folder.getOne(folders.get(i).getName(), database).getId();
                    File[] files_folder = new File("/storage/emulated/0/Pictures/" + folders.get(i).getName()).listFiles(new ImageFileFilter());
                    if(files_folder != null) {
                        for (File file : files_folder) {
                            Log.i(TAG, "PHOTO: " + file.getAbsolutePath());
                            Photo.insert(file.getAbsolutePath(), folderid, database);
                        }
                    }
                }


                addMessage("Hi! How can I help you?\nPress the button below for more information.", "", "", "BOT");
                addMessage("", "", "", "WHAT_CAN_I_DO");

            }
        }
    }

    public void takePicture(Integer id, String name) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        folder_id = id;
        folder_name = name;
        file = Uri.fromFile(getOutputMediaFile(folder_id, folder_name));
        Log.i(TAG, "FOLDER_ID" + folder_id);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    public static long getNewEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        return max_val+1;
    }

    public static long getLastEventId(ContentResolver cr) {
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, new String [] {"MAX(_id) as max_id"}, null, null, "_id");
        cursor.moveToFirst();
        long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
        return max_val;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                //imageView.setImageURI(file);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), folder_name);
                Photo.insert(mediaStorageDir + File.separator +
                        "IMG_"+ time_stamp + ".jpg", folder_id, database);
                Log.i(TAG, "NEW INSERT" + time_stamp + "ID" + folder_id);
                Intent i = new Intent(getBaseContext(), PhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", folder_id);
                i.putExtras(bundle);
                startActivity(i);
            }
        } else if(requestCode == 200) {
            long prev_id = getLastEventId(getContentResolver());

            if (new_id == prev_id) {
                addMessage("Alright, I've added that to your calendar!", "", "", "BOT");
            } else {
                addMessage("Alright, I didn't add that to your calendar.", "", "", "BOT");
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static File getOutputMediaFile(Integer folder_id, String folder_name){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), folder_name);

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }
/*
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Photo.insert(timeStamp, folder_id, database);*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        time_stamp = timeStamp;

        Log.i(TAG, "TIMESTAMP" + mediaStorageDir + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        return new File(mediaStorageDir + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private void removeTyping() {
        for(int i=0; i<messageList.size(); i++) {
            if(messageList.get(i).getSender().equals("TYPING")) messageList.remove(i);
        }
    }



    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(error.toString());
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
        if (aiDialog != null) {
            aiDialog.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (aiDialog != null) {
            aiDialog.resume();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR, Manifest.permission.RECORD_AUDIO }, 0);
        } else {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CameraDemo");

            Log.i(TAG, "DIR" + mediaStorageDir);

            File[] files = new File("/storage/emulated/0/Pictures").listFiles(new ImageFileFilter());
            Folder.deleteAll(database);
            Photo.deleteAll(database);
            Log.i(TAG, files + " FILES ");
            if(files != null && files.length > 0) {
                for (File file : files) {
                    // Add the directories containing images or sub-directories
                    if (file.isDirectory()
                            /*&& file.listFiles(new ImageFileFilter()).length > 0*/) {
                        Log.i(TAG, "FOLDER: " + file.getAbsolutePath());
                        Folder.insert(file.getAbsolutePath().replaceAll("/storage/emulated/0/Pictures/", ""), database);
                    }
                }
            }
            ArrayList<Folder> folders = Folder.getAll(database);
            for(int i=0; i<folders.size(); i++) {
                Integer folderid = Folder.getOne(folders.get(i).getName(), database).getId();
                File[] files_folder = new File("/storage/emulated/0/Pictures/" + folders.get(i).getName()).listFiles(new ImageFileFilter());
                if(files_folder != null) {
                    for (File file : files_folder) {
                        // Add the directories containing images or sub-directories
                        //if (!file.isDirectory()) {
                        Log.i(TAG, "PHOTO: " + file.getAbsolutePath());
                        Photo.insert(file.getAbsolutePath(), folderid, database);
                        //}
                    }
                }
            }
        }
        super.onResume();
    }

    public void buttonListenOnClick(final View view) {
        aiDialog.showAndListen();
    }

    public void sendEvent(String queryString, String contextString) {

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                String name = params[1];
                Log.i(TAG, query);
                //String event = params[1];
                //if (!TextUtils.isEmpty(query))
                AIEvent event = new AIEvent(query);
                AIContext context = new AIContext(name);

                ArrayList<AIContext> contexts = new ArrayList<AIContext>();
                contexts.add(context);

                request.setEvent(event);
                request.setContexts(contexts);

                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    Log.i(TAG, "RESPONSE!!" + response);
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString, contextString);
    }


    public void resendNewRequest(String queryString) {

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                Log.i(TAG, query);
                //String event = params[1];

                //if (!TextUtils.isEmpty(query))
                request.setQuery(query);

                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    Log.i(TAG, "RESPONSE!!" + response);
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString);
    }

    public void sendRequest(final View view) {

        final String queryString = String.valueOf(((AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1)).getText());
        if(queryString.trim().isEmpty()) return;
        ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1)).setText("");

        Integer myMessage_id = BaseMessage.insert(queryString, date_format.format(new Date()).toString(), "", "", "ME", database);
        BaseMessage myMessage = new BaseMessage(myMessage_id, queryString, date_format.format(new Date()).toString(), "", "", "ME");
        messageList.add(myMessage);
        mMessageAdapter.notifyDataSetChanged();
        mMessageRecycler.smoothScrollToPosition(messageList.size());

        if (TextUtils.isEmpty(queryString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];
                //String event = params[1];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);
                RequestExtras requestExtras = null;

                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    addMessage("Uh-oh, I've failed to send that message. Please ensure that you have a stable Internet connection.", "", "", "BOT");
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                ((ProgressBar) findViewById(R.id.progressBar_cyclic)).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                ((ProgressBar) findViewById(R.id.progressBar_cyclic)).setVisibility(View.GONE);
                if (response != null) {
                    Log.i(TAG, "RESPONSE!!" + response);
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(queryString);
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aibutton_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.what_can_i_do) {
            current = "";
            addMessage("","","","MENU");
            return true;
        }
        else if (id == R.id.help) {
            startActivity(HelpActivity2.class);
            return true;
        } else if (id == R.id.action_delete_messages) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            BaseMessage.deleteAll(database);
                            messageList.clear();
                            mMessageAdapter.notifyDataSetChanged();
                            mMessageRecycler.smoothScrollToPosition(messageList.size());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setMessage("Are you sure you'd like to delete all messages?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
}


