package cse110.grocerysaver.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

import cse110.grocerysaver.GrocerySaverMain;
import cse110.grocerysaver.R;
import cse110.grocerysaver.database.DatabaseContract;
import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.Persistable;
import cse110.grocerysaver.database.PersistableManager;


public class SendNotificationService extends IntentService {

    public SendNotificationService() { super("name"); }

    public SendNotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 3);

        String[] col = new String[]{
                DatabaseContract.FridgeItem.COLUMN_NAME,
                DatabaseContract.FridgeItem.COLUMN_DATE_ADDED,
                DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE
        };
        String sel = DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE + " > ? AND " +
                DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE + " < ? ";
        String[] args = new String[]{
                String.valueOf(Calendar.getInstance().getTimeInMillis()),
                String.valueOf(c.getTimeInMillis())
        };

        PersistableManager persistableManager = new PersistableManager(this);
        final ArrayList<Persistable> fridgeItems = persistableManager.query(FridgeItem.class, col, sel, args, null);

        if (fridgeItems.isEmpty()) {
            return;
        }

        SharedPreferences sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        final String contentText;
        if (fridgeItems.size() == 1) {
            contentText = "1 item is about to expire";
        } else {
            contentText = fridgeItems.size() + " items are about to expire";
        }
        // below are nothing but a simple notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noun_864097_cc)
                        .setContentTitle("My Fridge")
                        .setContentText(contentText);
        // TODO: need to include some expiring items in the notification

        Intent resultIntent = new Intent(this, GrocerySaverMain.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // this code makes the click on the notification lead users to the Main page
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
        Log.d("debug","pushing notification at "+Calendar.getInstance().getTimeInMillis());

        // if need to send email, send.
        if (sharedPref.getBoolean("email_onoff_preference",false)) {
            final String emailAddress = sharedPref.getString("email_address_preference","");
            // only when the emailAddress is not equal to empty string will the email notification send
            if (!(emailAddress.equals(""))) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String list = "";
                            for ( Persistable item : fridgeItems) {
                                list = list + ((FridgeItem) item).getName() + "\n";
                            }

                            GMailSender sender = new GMailSender(
                                    "xuzepei19950617@gmail.com",
                                    "Xzp8587668067!");

                            sender.sendMail("My Fridge notification", contentText + ":\n" + list,
                                    "GrocerySaver",
                                    emailAddress);
                            Log.d("debug","Sending an email");

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        }
    }
}
