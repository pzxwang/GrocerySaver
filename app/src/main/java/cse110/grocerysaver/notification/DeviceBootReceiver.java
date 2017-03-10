package cse110.grocerysaver.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DeviceBootReceiver extends BroadcastReceiver {

    public DeviceBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("debug","the device is booted");

        // after android is booted, try to set up an alarm if necessary

        // get the default shared preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // check if the notification checkbox is checked
        boolean setAlarm = sharedPref.getBoolean("notification_onoff_preference",false);


        // if it is checked, set up an alarm that call SendNotificationService repeatedly
        if (setAlarm) {
            Log.d("debug","the notification is on, so the application start the service 'SetRepeatAlarmService' ");
            setOnAlarm(context);
        } else {
            Log.d("debug","the notification is off, so the application don't need to start the service at boot time");
        }
    }

    public void setOnAlarm(Context context) {
        Intent setAlarmService = new Intent(context,SetRepeatAlarmService.class);
        context.startService(setAlarmService);
    }
}
