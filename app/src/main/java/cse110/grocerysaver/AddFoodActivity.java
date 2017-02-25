package cse110.grocerysaver;

import android.content.Intent;
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
import java.util.Locale;

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
                Intent intent = new Intent();

                long expDate;
                try {
                    expDate = new SimpleDateFormat("MM / dd / yyyy", Locale.US)
                            .parse(expDateFld.getText().toString()).getTime();
                }
                catch ( ParseException e) {
                    expDate = 0;
                }

                intent.putExtra("foodName", nameFld.getText().toString());
                intent.putExtra("foodExpDate", expDate);
                intent.putExtra("foodNotes", notesFld.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
