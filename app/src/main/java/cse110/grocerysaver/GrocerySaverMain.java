package cse110.grocerysaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class GrocerySaverMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_saver_main);

        setupNavigationView();
    }

    private void setupNavigationView () {
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        if (bottomNav != null) {

            // get the list of menu items in bottom bar and display MyFridge(first option)
            Menu menu = bottomNav.getMenu();
            selectFragment(menu.getItem(0));

            // Switch fragments when other options are selected.
            bottomNav.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    // choose the appropriate fragment based on the selected page
    private void selectFragment(MenuItem item) {

        item.setChecked(true);

        // find the correct fragment
        switch(item.getItemId()) {
            case R.id.menu_fridge:
                showFragment(new MyFridgeFragment());
                break;
            case R.id.menu_favorites:
                showFragment(new FavoritesFragment());
                break;
            case R.id.menu_settings:
                showFragment(new PreferencesFragment());
                break;
        }
    }

    // method to display the selected page on bottom navigation
    protected void showFragment(Fragment frag) {

        if (frag != null) {

            FragmentManager fragManager = getSupportFragmentManager();
            if (fragManager != null) {

                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                if (fragTransaction != null) {
                    fragTransaction.replace(R.id.rootLayout, frag);
                    fragTransaction.commit();
                }

            }
        }
    }
}
