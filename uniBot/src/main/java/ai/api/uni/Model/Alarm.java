package ai.api.uni.Model;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ai.api.uni.Activity.ChatActivity;
import ai.api.uni.Database.DBHelper;
import ai.api.uni.R;

/**
 * Created by Lenovo-G4030 on 2/24/2018.
 */
/* REFERENCES:
    https://github.com/dialogflow/dialogflow-android-client/
    Created this new file
*/

public class Alarm extends BroadcastReceiver
{
    private DBHelper database;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        DateFormat date_format = new SimpleDateFormat("dd");
        DateFormat month_format = new SimpleDateFormat("MM");
        Date date = new Date();
        Integer current_date = Integer.valueOf(date_format.format(date));
        Integer current_month = Integer.valueOf(month_format.format(date));

        DBHelper db = new DBHelper(context);
        ArrayList<FeeCategory> fee_categories = db.getAllFeeCategories();
        ArrayList<Fee> fees = db.getAllFees();
        ArrayList<Subject> subjects = db.getAllSubjects();

        NotificationManager NM;
        NM=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent new_intent = new Intent(context, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new_intent, 0);

        ArrayList<Notification> notifications = new ArrayList<>();
        ArrayList<String> notificationsString = new ArrayList<>();



        for(int i=0; i<fee_categories.size(); i++) {
            if(Integer.valueOf(fee_categories.get(i).getBillDate()) == current_date - 3) {
                Fee.insert(fee_categories.get(i).getId(), current_month, current_date, db);
                notifications.add(
                        new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("UNI")
                            .setContentText(fee_categories.get(i).getName() + "   |   bill date today")
                            .setGroup("1")
                            .build()
                );
                notificationsString.add(fee_categories.get(i).getName() + "   |   bill date today");

            }
        }

        for(int i=0; i<fee_categories.size(); i++) {
            if(Integer.valueOf(fee_categories.get(i).getDueDate()) == current_date - 3) {
                notifications.add(
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("UNI")
                                .setContentText(fee_categories.get(i).getName() + "   |   due date today")
                                .setGroup("1")
                                .build()
                );
                notificationsString.add(fee_categories.get(i).getName() + "   |   due date today");

            }
        }

        for(int i=0; i<subjects.size(); i++) {
            if(subjects.get(i).getAbsences() > 6) {
                notifications.add(
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("UNI")
                                .setContentText(subjects.get(i).getName() + " | Nearing maximum absences!")
                                .setGroup("1")
                                .build()
                );
                notificationsString.add(subjects.get(i).getName() + " | Nearing maximum absences!");
            }
        }

        if(notifications.size() > 0) {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            for(int i=0; i<notifications.size(); i++) {
                style.addLine(notificationsString.get(i));
            }
            Notification summaryNotification =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("UNI")
                            .setContentText(notifications.size() + " new messages")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setGroup("1")
                            .setGroupSummary(true)
                            .setStyle(style)
                            .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            for(int i=0; i<notifications.size(); i++) {
                notificationManager.notify(i+1, notifications.get(i));
            }
            notificationManager.notify(0, summaryNotification);

        }
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("ai.api.sample.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000*60*60*24, 1000*60*60*24, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}