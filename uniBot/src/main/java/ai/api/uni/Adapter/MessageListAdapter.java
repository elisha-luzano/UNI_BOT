package ai.api.uni.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ai.api.uni.Activity.AttendanceUpdateActivity;
import ai.api.uni.Activity.ChatActivity;
import ai.api.uni.Activity.ExpensesUpdateActivity;
import ai.api.uni.Activity.FeeUpdateActivity;
import ai.api.uni.Model.ExpenseCategory;
import ai.api.uni.Model.FeeCategory;
import ai.api.uni.Model.BaseMessage;
import ai.api.uni.R;

import static ai.api.uni.Activity.ChatActivity.database;
import static ai.api.uni.Activity.ChatActivity.current;
import static ai.api.uni.Activity.ChatActivity.TAG;

/**
 * Created by Lenovo-G4030 on 3/14/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_FACEBOOK_EVENT = 3;
    private static final int VIEW_TYPE_TASK = 4;
    private static final int VIEW_TYPE_SCHEDULE = 5;
    private static final int VIEW_TYPE_SCHEDULE_HEADER = 6;
    private static final int VIEW_TYPE_GRADE = 7;
    private static final int VIEW_TYPE_FOLDER = 8;
    private static final int VIEW_TYPE_FEE = 9;
    private static final int VIEW_TYPE_FEE_BUTTON = 10;
    private static final int VIEW_TYPE_EXPENSES_BUTTON = 11;
    private static final int VIEW_TYPE_TWITTER = 12;
    private static final int VIEW_TYPE_SUBJECTS_BUTTON = 13;
    private static final int VIEW_TYPE_WHAT_CAN_I_DO = 14;
    private static final int VIEW_TYPE_MENU = 15;
    private static final int VIEW_TYPE_TYPING = 16;
    private static final int VIEW_TYPE_GRADE_NEW = 17;

    private ChatActivity mActivity;
    private Context mContext;
    private ArrayList<BaseMessage> mMessageList;

    public MessageListAdapter(ChatActivity activity, Context context, ArrayList<BaseMessage> messageList) {
        mActivity = activity;
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);

        if (message.getSender().equals("ME")) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else if (message.getSender().equals("FACEBOOK")){
            // If some other user sent the message
            return VIEW_TYPE_FACEBOOK_EVENT;
        } else if (message.getSender().equals("TASK")){
            // If some other user sent the message
            return VIEW_TYPE_TASK;
        } else if (message.getSender().equals("SCHEDULE")){
            // If some other user sent the message
            return VIEW_TYPE_SCHEDULE;
        } else if (message.getSender().equals("SCHEDULE_HEADER")){
            // If some other user sent the message
            return VIEW_TYPE_SCHEDULE_HEADER;
        } else if (message.getSender().equals("GRADE")){
            // If some other user sent the message
            return VIEW_TYPE_GRADE;
        } else if (message.getSender().equals("FOLDER")){
            // If some other user sent the message
            return VIEW_TYPE_FOLDER;
        } else if (message.getSender().equals("FEE")){
            // If some other user sent the message
            return VIEW_TYPE_FEE;
        } else if (message.getSender().equals("FEE_BUTTON")){
            // If some other user sent the message
            return VIEW_TYPE_FEE_BUTTON;
        } else if (message.getSender().equals("EXPENSES_BUTTON")){
            // If some other user sent the message
            return VIEW_TYPE_EXPENSES_BUTTON;
        } else if (message.getSender().equals("TWITTER")){
            // If some other user sent the message
            return VIEW_TYPE_TWITTER;
        } else if (message.getSender().equals("SUBJECTS_BUTTON")){
            // If some other user sent the message
            return VIEW_TYPE_SUBJECTS_BUTTON;
        } else if (message.getSender().equals("WHAT_CAN_I_DO")){
            // If some other user sent the message
            return VIEW_TYPE_WHAT_CAN_I_DO;
        } else if (message.getSender().equals("MENU")){
            // If some other user sent the message
            return VIEW_TYPE_MENU;
        } else if (message.getSender().equals("TYPING")){
            // If some other user sent the message
            return VIEW_TYPE_TYPING;
        } else if (message.getSender().equals("GRADE_NEW")){
            // If some other user sent the message
            return VIEW_TYPE_GRADE_NEW;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_FACEBOOK_EVENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.facebook_event, parent, false);
            return new FacebookEventMessageHolder(view);
        } else if (viewType == VIEW_TYPE_TASK) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_layout, parent, false);
            return new TaskMessageHolder(view);
        } else if (viewType == VIEW_TYPE_SCHEDULE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subject_layout, parent, false);
            return new ScheduleMessageHolder(view);
        } else if (viewType == VIEW_TYPE_SCHEDULE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule_header, parent, false);
            return new ScheduleHeaderHolder(view);
        } else if (viewType == VIEW_TYPE_GRADE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grade_layout, parent, false);
            return new GradeMessageHolder(view);
        } else if (viewType == VIEW_TYPE_FOLDER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.folder_layout, parent, false);
            return new FolderMessageHolder(view);
        } else if (viewType == VIEW_TYPE_FEE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fee_layout, parent, false);
            return new FeeMessageHolder(view);
        } else if (viewType == VIEW_TYPE_FEE_BUTTON) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fee_edit_button, parent, false);
            return new FeeButtonMessageHolder(view);
        } else if (viewType == VIEW_TYPE_EXPENSES_BUTTON) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.expenses_edit_button, parent, false);
            return new ExpensesButtonMessageHolder(view);
        } else if (viewType == VIEW_TYPE_TWITTER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.twitter, parent, false);
            return new TwitterHolder(view);
        } else if (viewType == VIEW_TYPE_SUBJECTS_BUTTON) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subjects_edit_button, parent, false);
            return new SubjectsButtonHolder(view);
        } else if (viewType == VIEW_TYPE_WHAT_CAN_I_DO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.what_can_i_do_button, parent, false);
            return new WhatCanIDoButtonHolder(view);
        } else if (viewType == VIEW_TYPE_MENU) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.menu_layout, parent, false);
            return new MenuButtonHolder(view);
        } else if (viewType == VIEW_TYPE_TYPING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_typing, parent, false);
            return new TypingButtonHolder(view);
        } else if (viewType == VIEW_TYPE_GRADE_NEW) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grade_layout_new, parent, false);
            return new GradeNewMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_FACEBOOK_EVENT:
                ((FacebookEventMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_TASK:
                ((TaskMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_SCHEDULE:
                ((ScheduleMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_SCHEDULE_HEADER:
                ((ScheduleHeaderHolder) holder).bind(message);
                break;
            case VIEW_TYPE_GRADE:
                ((GradeMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_FOLDER:
                ((FolderMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_FEE:
                ((FeeMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_FEE_BUTTON:
                ((FeeButtonMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_EXPENSES_BUTTON:
                ((ExpensesButtonMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_TWITTER:
                ((TwitterHolder) holder).bind(message);
                break;
            case VIEW_TYPE_SUBJECTS_BUTTON:
                ((SubjectsButtonHolder) holder).bind(message);
                break;
            case VIEW_TYPE_WHAT_CAN_I_DO:
                ((WhatCanIDoButtonHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MENU:
                ((MenuButtonHolder) holder).bind(message);
                break;
            case VIEW_TYPE_GRADE_NEW:
                ((GradeNewMessageHolder) holder).bind(message);
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;


        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(BaseMessage message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getDate());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(BaseMessage message) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getDate());
        }
    }

    private class FacebookEventMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout eventContainer = (LinearLayout) itemView.findViewById(R.id.text_message_body);
        TextView nameText, venueText, timeStartText, timeEndText;
        ImageButton remind;
        ImageView coverImage;

        FacebookEventMessageHolder(View itemView) {
            super(itemView);

            coverImage = (ImageView) itemView.findViewById(R.id.imageView2);
            nameText = (TextView) itemView.findViewById(R.id.name);
            venueText = (TextView) itemView.findViewById(R.id.venue);
            timeStartText = (TextView) itemView.findViewById(R.id.time_start);
            timeEndText = (TextView) itemView.findViewById(R.id.time_end);
            remind = (ImageButton) itemView.findViewById(R.id.remind);
        }

        void bind(BaseMessage message) {
            final SimpleDateFormat outgoingFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", java.util.Locale.getDefault());
            String[] details = message.getMessage().split("\n");
            final String name = details[0];
            String venue = details[1];
            String start_time = details[2];
            String end_time = details[3];

            nameText.setText(name);
            venueText.setText(venue);
            timeStartText.setText(start_time);
            timeEndText.setText(end_time);

            String[] url = message.getImage_url().split(" ");
            final String event = url[0];
            String img = url[1];

            final String time = message.getPost_url();

            Picasso.with(mContext).load(img).into(coverImage);
            final BaseMessage message_obj = message;

            remind.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    try {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                        cal.setTime(outgoingFormat.parse(time));

                        Intent cal_intent = new Intent(Intent.ACTION_EDIT);
                        cal_intent.setType("vnd.android.cursor.item/event");
                        cal_intent.putExtra("beginTime", cal.getTimeInMillis());
                        cal_intent.putExtra("allDay", true);
                        cal_intent.putExtra("title", name);
                        mContext.startActivity(cal_intent);
                    } catch (Exception e) {
                        Log.i(TAG, "ERR: " + e.getMessage());
                    }
                }
            });

            coverImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(message_obj.getSender().equals("TWITTER")) {
                        Uri uri = Uri.parse(event); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                    else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("EVENTS");

                        LinearLayout wrapper = new LinearLayout(mContext);
                        WebView wv = new WebView(mContext);

                        WebSettings webSettings = wv.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        wv.loadUrl(event);


                        EditText keyboardHack = new EditText(mContext);
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
                                view.loadUrl(url);
                                return true;
                            }
                        });
                    }
                }
            });

            eventContainer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(message_obj.getSender().equals("TWITTER")) {
                        Uri uri = Uri.parse(message_obj.getPost_url()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                    else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("EVENTS");

                        LinearLayout wrapper = new LinearLayout(mContext);
                        WebView wv = new WebView(mContext);

                        WebSettings webSettings = wv.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        wv.loadUrl(event);


                        EditText keyboardHack = new EditText(mContext);
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
                                view.loadUrl(url);
                                return true;
                            }
                        });
                    }
                }
            });
            // Format the stored timestamp into a readable String using method.
        }
    }


    private class TwitterHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView coverImage;

        TwitterHolder(View itemView) {
            super(itemView);

            coverImage = (ImageView) itemView.findViewById(R.id.imageView2);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        }

        void bind(BaseMessage message) {

            Picasso.with(mContext).load(message.getImage_url()).into(coverImage);
            messageText.setText(message.getMessage());
            final BaseMessage message_obj = message;

            coverImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(message_obj.getSender().equals("TWITTER")) {
                        Uri uri = Uri.parse(message_obj.getPost_url()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                    else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("EVENTS");

                        LinearLayout wrapper = new LinearLayout(mContext);
                        WebView wv = new WebView(mContext);

                        WebSettings webSettings = wv.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        wv.loadUrl(message_obj.getPost_url());


                        EditText keyboardHack = new EditText(mContext);
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
                                view.loadUrl(url);
                                return true;
                            }
                        });
                    }
                }
            });

            messageText.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(message_obj.getSender().equals("TWITTER")) {
                        Uri uri = Uri.parse(message_obj.getPost_url()); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                    else {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle("EVENTS");

                        LinearLayout wrapper = new LinearLayout(mContext);
                        WebView wv = new WebView(mContext);

                        WebSettings webSettings = wv.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        wv.loadUrl(message_obj.getPost_url());


                        EditText keyboardHack = new EditText(mContext);
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
                                view.loadUrl(url);
                                return true;
                            }
                        });
                    }
                }
            });
            // Format the stored timestamp into a readable String using method.
        }
    }

    private class TaskMessageHolder extends RecyclerView.ViewHolder {
        TextView nameText, contentsText, priorityText;
        Button delete;

        TaskMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.task_name);
            contentsText = (TextView) itemView.findViewById(R.id.contents);
            priorityText = (TextView) itemView.findViewById(R.id.priority);
            delete = (Button) itemView.findViewById(R.id.delete);
        }

        void bind(final BaseMessage message) {
            nameText.setText(message.getMessage());
            contentsText.setText(message.getImage_url());
            priorityText.setText(message.getPost_url());

            /*if(message.getPost_url().toLowerCase().equals("priority: high")) {
                nameText.setBackgroundColor(Color.parseColor("#bf4b37"));
            } else if(message.getPost_url().toLowerCase().equals("priority: medium")) {
                nameText.setBackgroundColor(Color.parseColor("#e07f1f"));
            } else {
                nameText.setBackgroundColor(Color.parseColor("#edd465"));
            }*/
        }
    }

    private class ScheduleMessageHolder extends RecyclerView.ViewHolder {
        TextView nameText, contentsText, absencesText;

        ScheduleMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.subject);
            contentsText = (TextView) itemView.findViewById(R.id.contents);
            absencesText = (TextView) itemView.findViewById(R.id.absences);
        }

        void bind(BaseMessage message) {
            nameText.setText(message.getMessage());
            contentsText.setText(message.getImage_url());
            absencesText.setText(message.getPost_url());

        }
    }

    private class ScheduleHeaderHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        ScheduleHeaderHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.class_schedule);
        }

        void bind(BaseMessage message) {
            headerText.setText(message.getMessage());
        }
    }

    private class GradeMessageHolder extends RecyclerView.ViewHolder {
        TextView nameText, gradesText;

        GradeMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.subject);
            gradesText = (TextView) itemView.findViewById(R.id.grade);
        }

        void bind(BaseMessage message) {
            nameText.setText(message.getMessage());
            gradesText.setText(message.getImage_url());

            /*if(message.getImage_url().equals("1.0"))
                gradesText.setBackgroundColor(Color.parseColor("#D4F4FF"));
            else if (message.getImage_url().equals("5.0"))
                gradesText.setBackgroundColor(Color.parseColor("#FFB1B1"));
            else */if (message.getImage_url().equals("7.0")){
                gradesText.setText("S");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("8.0")){
                gradesText.setText("U");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("9.0")){
                gradesText.setText("INC");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("10.0")){
                gradesText.setText("DRP");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
        }
    }

    private class GradeNewMessageHolder extends RecyclerView.ViewHolder {
        TextView nameText, gradesText,unitsText;

        GradeNewMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.subject);
            gradesText = (TextView) itemView.findViewById(R.id.grade);
            unitsText = (TextView) itemView.findViewById(R.id.units);
        }

        void bind(BaseMessage message) {
            nameText.setText(message.getMessage());
            gradesText.setText(message.getImage_url());
            unitsText.setText(message.getPost_url());

            /*if(message.getImage_url().equals("1.0"))
                gradesText.setBackgroundColor(Color.parseColor("#D4F4FF"));
            else if (message.getImage_url().equals("5.0"))
                gradesText.setBackgroundColor(Color.parseColor("#FFB1B1"));
            else */if (message.getImage_url().equals("7.0")){
                gradesText.setText("S");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("8.0")){
                gradesText.setText("U");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("9.0")){
                gradesText.setText("INC");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
            else if (message.getImage_url().equals("10.0")){
                gradesText.setText("DRP");
                //gradesText.setBackgroundColor(Color.parseColor("#FF9F9F9F"));
            }
        }
    }

    private class FolderMessageHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        FolderMessageHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name);
        }

        void bind(BaseMessage message) {
            nameText.setText(message.getMessage());
        }
    }

    private class FeeMessageHolder extends RecyclerView.ViewHolder {
        TextView contentText;

        FeeMessageHolder(View itemView) {
            super(itemView);

            contentText = (TextView) itemView.findViewById(R.id.content);
        }

        void bind(BaseMessage message) {
            contentText.setText(message.getMessage());
        }
    }

    private class FeeButtonMessageHolder extends RecyclerView.ViewHolder {
        Button update_fees_button;

        FeeButtonMessageHolder(View itemView) {
            super(itemView);

            update_fees_button = (Button) itemView.findViewById(R.id.update_expenses_button);
        }

        void bind(final BaseMessage message) {

            update_fees_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ExpenseCategory.getOne(message.getMessage(), database) != null) {
                        Intent intent = new Intent(mContext, FeeUpdateActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", message.getMessage());
                        bundle.putString("id", message.getImage_url());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Oops, " + message.getMessage() + " is not in your expenses anymore. Maybe you've deleted it already.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private class ExpensesButtonMessageHolder extends RecyclerView.ViewHolder {
        Button update_expenses_button;

        ExpensesButtonMessageHolder(View itemView) {
            super(itemView);

            update_expenses_button = (Button) itemView.findViewById(R.id.update_expenses_button);
        }

        void bind(final BaseMessage message) {

            update_expenses_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(FeeCategory.getByName(message.getMessage(), database) != null) {
                        Intent intent = new Intent(mContext, ExpensesUpdateActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", message.getMessage());
                        bundle.putString("id", message.getImage_url());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Oops, " + message.getMessage() + " is not in your bills anymore. Maybe you've deleted it already.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private class SubjectsButtonHolder extends RecyclerView.ViewHolder {
        Button update_subjects_button;

        SubjectsButtonHolder(View itemView) {
            super(itemView);

            update_subjects_button = (Button) itemView.findViewById(R.id.update_subjects_button);
        }

        void bind(final BaseMessage message) {

            update_subjects_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AttendanceUpdateActivity.class);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    private class WhatCanIDoButtonHolder extends RecyclerView.ViewHolder {

        Button what_can_i_do;

        WhatCanIDoButtonHolder(View itemView) {
            super(itemView);

            what_can_i_do = itemView.findViewById(R.id.what_can_i_do);
        }

        void bind(final BaseMessage message) {
            what_can_i_do.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                    mActivity.addMessage("", "", "", "MENU");
                }
            });
        }
    }

    private class MenuButtonHolder extends RecyclerView.ViewHolder {
        Button suspensions, absences, grades,
        tasks, expenses, photos, bills, wtb, events;

        MenuButtonHolder(View itemView) {
            super(itemView);

            suspensions = itemView.findViewById(R.id.suspensions);
            absences = itemView.findViewById(R.id.absences);
            grades = itemView.findViewById(R.id.grades);

            tasks = itemView.findViewById(R.id.tasks);
            expenses = itemView.findViewById(R.id.expenses);
            photos = itemView.findViewById(R.id.photos);

            bills = itemView.findViewById(R.id.bills);
            wtb = itemView.findViewById(R.id.wtb);
            events = itemView.findViewById(R.id.events);
        }

        void bind(final BaseMessage message) {
            suspensions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                 mActivity.addMessage("Try saying \"Are there classes?\" and I will check for class suspensions from Twitter.\n" +
                         "Also, you can check for a specific date and place. For example, \"Check for class suspensions in Manila on May 14.\"", "", "", "BOT");
                }
            });

            absences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                    mActivity.addMessage("Try saying \"Show my schedule\"  to see if you have any absences on your subjects.\n" +
                            "To add to your number of absences, you can try saying \"I missed a class.\"", "", "", "BOT");
                }
            });

            grades.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                    mActivity.addMessage("Try saying \"Show my grades\"  to see your grades from SAIS.", "", "", "BOT");
                }
            });

            tasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "tasks";
                    mActivity.addMessage("Try saying \"Show my tasks\"  to see your upcoming tasks for the week.\n" +
                            "To schedule a new task, just try saying something like \"I have a meeting tomorrow.\"", "", "", "BOT");
                }
            });

            expenses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "expenses";
                    mActivity.addMessage("Try saying \"Show my expenses\"  to see your past record of expenses.\n" +
                            "To add a new one, try saying something like \"I spent 100 on food today.\"", "", "", "BOT");
                }
            });

            photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "folders";
                    mActivity.addMessage("Try saying \"Show my folders\"  to see your folders of pictures.\n" +
                            "To open each folder, you can also say something like \"Open my acads folder.\"\n" +
                            "To add a new photo, try saying something like \"Open camera.\"", "", "", "BOT");
                }
            });

            bills.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "bills";
                    mActivity.addMessage("Try saying \"Show my bills\"  to see your bills, paid or unpaid.\n" +
                            "To view each one, try saying something like \"View my water bills\".\n" +
                            "To schedule add a new one, you can say something like \"Add rent to my bills\".", "", "", "BOT");
                }
            });

            wtb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                    mActivity.addMessage("Try saying something like \"Where to buy chicken?\" to view all the places" +
                            " selling chicken according to Google places.", "", "", "BOT");
                }
            });

            events.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = "";
                    mActivity.addMessage("Try saying something like \"Show nearby events\" to view all the events near UPLB.", "", "", "BOT");
                }
            });
        }
    }

    private class TypingButtonHolder extends RecyclerView.ViewHolder {

        TypingButtonHolder(View itemView) {
            super(itemView);
        }
    }
}