package cse110.grocerysaver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cse110.grocerysaver.database.DatabaseContract;
import cse110.grocerysaver.database.DatabaseContract.FridgeItem;
import cse110.grocerysaver.database.ProviderContract;

public class AddFoodActivity extends AppCompatActivity {

    private EditText nameFld;
    private EditText expDateFld;
    private EditText notesFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        nameFld = (EditText) findViewById(R.id.nameField);
        expDateFld = (EditText) findViewById(R.id.expDateField);
        notesFld = (EditText) findViewById(R.id.notesField);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_food_done:
                ProviderContract.uriForTable(FridgeItem.TABLE);
                ContentValues values = new ContentValues();
                Date today = new Date();
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
                Date expiration = null;
                try {
                    expiration = format.parse(expDateFld.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long midnight = c.getTimeInMillis();

                values.put(FridgeItem.COLUMN_NAME, nameFld.getText().toString());
                values.put(FridgeItem.COLUMN_DATE_ADDED, today.getTime() / 1000);
                values.put(FridgeItem.COLUMN_SHELF_LIFE, (expiration.getTime() - midnight) / 1000);
                values.put(FridgeItem.COLUMN_NOTES, notesFld.getText().toString());

                getContentResolver().insert(ProviderContract.uriForTable(FridgeItem.TABLE), values);
                finish();
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
