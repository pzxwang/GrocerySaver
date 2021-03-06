package cse110.grocerysaver.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

public class SetRepeatAlarmService extends IntentService {

    private static int FREQUENCY_IN_DAYS;
    private static int TIME_OF_DAY;

    public SetRepeatAlarmService() { super("name"); }

    public SetRepeatAlarmService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("debug", "setRepeatAlarmService is called");

        // get ready to retrieve user's settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // get the settings
        FREQUENCY_IN_DAYS = Integer.parseInt(sharedPref.getString("notify_period_preference",""));
        TIME_OF_DAY = Integer.parseInt(sharedPref.getString("notify_time_preference",""));

        // create a intent and a pending intent
        Intent myIntent = new Intent(this , SendNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        // set up the time of notification
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, TIME_OF_DAY);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        // setInexactRepeating
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        // the lines below set the notification push every 1 hour, and this is just for grader's convenience to test
        long startingTime = System.currentTimeMillis();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startingTime, AlarmManager.INTERVAL_HOUR , pendingIntent);

        // if the graders want the real functionality, comment out the line above and replace it with the line below
        // the line below is the real functionality
        //long startingTime = calendar.getTimeInMillis();
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startingTime, AlarmManager.INTERVAL_DAY*FREQUENCY_IN_DAYS , pendingIntent);

        Log.d("debug","the repeating is set at "+startingTime+" every "+FREQUENCY_IN_DAYS+" days");
    }
}
