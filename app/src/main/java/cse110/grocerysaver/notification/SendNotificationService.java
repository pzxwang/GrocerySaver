package cse110.grocerysaver.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import cse110.grocerysaver.GrocerySaverMain;
import cse110.grocerysaver.R;


public class SendNotificationService extends IntentService {

    public SendNotificationService() { super("name"); }

    public SendNotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        // below are nothing but a simple notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noun_864097_cc)
                        .setContentTitle("Food is expiring!!!")
                        .setContentText("Following food is expring: ...");
        // TODO: need to include some expiring items in the notification

        Intent resultIntent = new Intent(this, GrocerySaverMain.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // this code makes the click on the notification lead users to the Main page
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
        Log.d("debug","pushing notification");

        // if need to send email, send.
        if (sharedPref.getBoolean("email_onoff_preference",false)) {
            final String emailAddress = sharedPref.getString("email_address_preference","");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        GMailSender sender = new GMailSender(
                                "xuzepei19950617@gmail.com",
                                "Xzp8587668067!");

                        sender.sendMail("Your Food Is Expiring!!!", "The following food is expiring soon: "+"",
                                "GrocerySaver",
                                emailAddress);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    }
}
