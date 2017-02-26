package cse110.grocerysaver;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cse110.grocerysaver.database.ProviderContract;
import cse110.grocerysaver.database.FridgeItem;

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
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
                Calendar expiration = Calendar.getInstance();
                try {
                    expiration.setTime(format.parse(expDateFld.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                FridgeItem fridgeItem = new FridgeItem(this);
                fridgeItem.setName(nameFld.getText().toString());
                fridgeItem.setDateAdded(Calendar.getInstance());
                fridgeItem.setExpirationDate(expiration);
                fridgeItem.setNotes(notesFld.getText().toString());
                fridgeItem.insert();

                finish();
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
