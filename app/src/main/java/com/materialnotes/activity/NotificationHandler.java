package com.materialnotes.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.materialnotes.R;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationHandler{

    Context context;

    NotificationHandler(Context context){
        this.context = context;
    }

    /**
     * Function which shows a notification at 9 am the day before a task is due.
     * @param date the date the task being investigated is due
     * @param description description of task due
     */
    public void showNotification(final String id, final String date, final String time, final String description)
    {
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
        thatDay.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)));
        thatDay.set(Calendar.YEAR, Integer.parseInt(date.substring(6, 10)));
        thatDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
        thatDay.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
        thatDay.set(Calendar.MILLISECOND, 0);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent notificationIntent = new Intent(context, ToDoActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(context,
                        (int) System.currentTimeMillis(), notificationIntent, 0);

                Notification.Builder builder = new Notification.Builder(context);

                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Напоминание MyNotes")
                        .setContentText(description);

                Notification notification = builder.build();
                notification.defaults = Notification.DEFAULT_SOUND;

                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.parseInt(id), notification);
            }
        };
        timer.schedule(task, new Date());
    }
}