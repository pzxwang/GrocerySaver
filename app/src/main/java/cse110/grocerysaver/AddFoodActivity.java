package cse110.grocerysaver;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.InventoryItem;
import cse110.grocerysaver.database.PersistableManager;

public class AddFoodActivity extends AppCompatActivity {

    private PersistableManager persistableManager;
    private FridgeItem fridgeItem = new FridgeItem();
    private InventoryItem inventoryItem = new InventoryItem();

    private AutoCompleteTextView nameFld;
    private EditText expDateFld;
    private EditText notesFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        persistableManager = new PersistableManager(this);
        ArrayList<String> autoCompleteList = persistableManager.populateInventory(InventoryItem.class);

        nameFld = (AutoCompleteTextView) findViewById(R.id.nameField);
        expDateFld = (EditText) findViewById(R.id.expDateField);
        notesFld = (EditText) findViewById(R.id.notesField);

        ArrayAdapter<String> autoCompleteAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        nameFld.setAdapter(autoCompleteAdapter);

        Long id = getIntent().getLongExtra("EXTRA_FRIDGE_ITEM_ID", -1);
        if (id != -1) {
            setTitle("Edit fridge item");

            findViewById(R.id.remove).setVisibility(View.VISIBLE);
            findViewById(R.id.favorite).setVisibility(View.VISIBLE);

            fridgeItem = (FridgeItem) persistableManager.findByID(FridgeItem.class, id);
            DateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

            nameFld.setText(fridgeItem.getName());
            expDateFld.setText(format.format(fridgeItem.getExpirationDate().getTime()));
            notesFld.setText(fridgeItem.getNotes());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        nameFld.requestFocus();
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
                if (!isDataValid()) {
                    return false;
                }

                SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                Calendar expiration = Calendar.getInstance();
                try {
                    expiration.setTime(format.parse(expDateFld.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                fridgeItem.setName(nameFld.getText().toString());
                fridgeItem.setDateAdded(Calendar.getInstance());
                fridgeItem.setExpirationDate(expiration);
                fridgeItem.setNotes(notesFld.getText().toString());

                PersistableManager pm = new PersistableManager(this);
                pm.save(fridgeItem);

                // check if item exists in inventory table, add it if not add it
                boolean inInventory =
                        pm.isFoodItemInInventoryDb(InventoryItem.class, nameFld.getText().toString());

                if (!inInventory) {
                    inventoryItem = new InventoryItem(nameFld.getText().toString());
                    pm.save(inventoryItem);
                }

                finish();
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private boolean validateFoodName() {
        return nameFld.getText().toString().isEmpty();
    }

    private  boolean validateExpirationDate() {
        return expDateFld.getText().toString().isEmpty();
    }

    private boolean isDataValid() {
        if (nameFld.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter food name.", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (expDateFld.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an expiration date.", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
