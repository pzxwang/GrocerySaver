package cse110.grocerysaver;

import android.os.Bundle;
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


