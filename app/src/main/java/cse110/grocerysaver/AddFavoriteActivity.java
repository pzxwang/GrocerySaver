package cse110.grocerysaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cse110.grocerysaver.database.Favorite;
import cse110.grocerysaver.database.PersistableManager;

public class AddFavoriteActivity extends AppCompatActivity {

    private PersistableManager persistableManager;
    private Favorite favorite = new Favorite();

    private AutoCompleteTextView nameFld;
    private EditText shelfLifeFld;
    private EditText notesFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite);

        nameFld = (AutoCompleteTextView) findViewById(R.id.favNameField);
        shelfLifeFld = (EditText) findViewById(R.id.shelfLifeField);
        notesFld = (EditText) findViewById(R.id.favNotesField);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_food, menu);
        setTitle("Add Favorite");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_food_done:
                if (!isDataValid()) {
                    return false;
                }

                int numDays = Integer.parseInt(shelfLifeFld.getText().toString());

                favorite.setName(nameFld.getText().toString());
                favorite.setShelfLife(TimeUnit.DAYS.toMillis(numDays));
                favorite.setNotes(notesFld.getText().toString());

                PersistableManager pm = new PersistableManager(this);
                pm.save(favorite);

                finish();
        }
        return false;
    }

    private boolean isDataValid() {
        if (nameFld.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter favorite name.", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (shelfLifeFld.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a shelf life.", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }















}
