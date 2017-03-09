package cse110.grocerysaver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;


// Preferences Class
public class PreferencesFragment extends PreferenceFragmentCompat {

    // Required empty public constructor
    public PreferencesFragment() {}


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.settings);
    }

}


