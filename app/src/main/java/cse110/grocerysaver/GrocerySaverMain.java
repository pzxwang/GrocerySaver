package cse110.grocerysaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.LinkedList;

import cse110.grocerysaver.database.FridgeItem;
import cse110.grocerysaver.database.PersistableManager;

public class GrocerySaverMain extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private enum MainFragment {
        MyFridgeFragment, FavoritesFragment, SettingsFragment
    }

    public MyFridgeFragment myFridgeFragment;
    public FavoritesFragment favoritesFragment;
    public PreferencesFragment settingsFragment;
    BottomNavigationView navigationView;

    private LinkedList<MainFragment> fragmentBackStack = new LinkedList<>();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == fragmentBackStack.peek().ordinal()) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.menu_fridge:
                pushFragment(MainFragment.MyFridgeFragment);
                break;
            case R.id.menu_favorites:
                pushFragment(MainFragment.FavoritesFragment);
                break;
            case R.id.menu_settings:
                pushFragment(MainFragment.SettingsFragment);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (fragmentBackStack.size() == 1) {
            super.onBackPressed();
            return;
        }

        popFragment();
    }

    private void changeFragment(MainFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rootLayout, fragment(fragment));
        transaction.commit();

        navigationView.getMenu().getItem(fragment.ordinal()).setChecked(true);
    }

    private void pushFragment(MainFragment fragment) {
        if (fragmentBackStack.contains(fragment)) {
            fragmentBackStack.remove(fragment);
        }

        fragmentBackStack.push(fragment);
        changeFragment(fragment);
    }

    private void popFragment() {
        fragmentBackStack.pop();
        changeFragment(fragmentBackStack.peek());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_saver_main);

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        pushFragment(MainFragment.MyFridgeFragment);
    }

    @NonNull
    private Fragment fragment(MainFragment fragment) {
        switch (fragment) {
            case FavoritesFragment:
                if (favoritesFragment == null) {
                    favoritesFragment = new FavoritesFragment();
                }
                return favoritesFragment;
            case SettingsFragment:
                if (settingsFragment == null) {
                    settingsFragment = new PreferencesFragment();
                }
                return settingsFragment;
            default:
                if (myFridgeFragment == null) {
                    myFridgeFragment = new MyFridgeFragment();
                }
                return myFridgeFragment;
        }
    }

}
