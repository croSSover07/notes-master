package com.materialnotes.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import com.materialnotes.R;

public class NotificationHandler extends Service {

@Override
    public IBinder onBind(Intent intent)
{
    return null;
}

@Override
    public void onCreate(){
    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    Intent intent1 = new Intent(this.getApplicationContext(),ToDoActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(this,0,intent1,0);

    Notification mNotify = new Notification.Builder(this)
            .setContentTitle("MyNotes")
            .setContentText("Шото мав зробити")
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pIntent)
            .setSound(sound)
            .addAction(0,"Shototam",pIntent)
            .build();

    mNM.notify(1,mNotify);
}

//NotificationManager nm ;
  // ToDoActivity todo;

    /*Context context = new ToDoActivity();
    MakeNotification n = new MakeNotification(context);

    NotificationHandler(Context context){
        this.context = context;
    }*/
/*@Override
public void onReceive(Context context , Intent intent)
{
    this.todo = todo;
    nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    Notification notification = new Notification(R.drawable.ic_launcher, "MyNotes", System.currentTimeMillis());
    //Интент для активити, которую мы хотим запускать при нажатии на уведомление
    Intent intentTL = new Intent(this.todo, MainActivity.class);
    notification.setLatestEventInfo(context, "Test", "Do something!",
            PendingIntent.getActivity(context, 0, intentTL,
                    PendingIntent.FLAG_CANCEL_CURRENT));
    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
    nm.notify(1, notification);
    // Установим следующее напоминание.
    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT);
    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

    // showNotification(" ", " " , " " ," ");
}
 /*   NotificationHandler(Context context){
        this.context = context;
    }*/

    /**
     * Function which shows a notification at 9 am the day before a task is due.
     * @param date the date the task being investigated is due
     * @param description description of task due
     */
  /*  public void showNotification(final String id, final String date, final String time, final String description)
    {
     /*   Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                thatDay.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
                thatDay.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)));
                thatDay.set(Calendar.YEAR, Integer.parseInt(date.substring(6,10)));
                thatDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
                thatDay.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
*/
     /*   Intent intent = new Intent(context, ToDoActivity.class);
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, ToDoActivity.class), 0);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ToDoActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);


        Notification.Builder mBuilder = new Notification.Builder(context)
                .setContentIntent(notificIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Напоминание MyNotes")
                .setContentText(description);

        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
       /* mBuilder.setContentIntent(notificIntent)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Напоминание MyNotes")
                        .setContentText(description);
        */
                /*TaskStackBuilder tStackBuilder = TaskStackBuilder.create(context);
                tStackBuilder.addParentStack(ToDoActivity.class);
                tStackBuilder.addNextIntent(notificationIntent);
                builder.setContentIntent(contentIntent);
*/
        //Notification notification = builder.build();
          //      notification.defaults = Notification.DEFAULT_SOUND;

   /*             NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.parseInt(id), builder.build());
      //   *   }
       // };
        //timer.schedule(task, thatDay.getTime());*/
        /*final String fDate = date;
        final String fDescription = description;

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2))); //Not setting
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5))); //Not setting
        calendar.set(Calendar.SECOND, 0); //Not setting
        final Calendar fCalendar = calendar;
        final PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                n.createNotification(pi, fDate, fDescription, fCalendar);
            }
        };
        timer.schedule(task, calendar.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
*/
 //   }
}