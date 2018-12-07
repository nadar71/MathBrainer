package eu.indiewalkabout.mathbrainer;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainSettingsActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainSettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static class MainPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // show and keep update the preferences
            addPreferencesFromResource(R.xml.main_settings);

            // bind prefs on changes
            Preference someValue = findPreference(getString(R.string.settings_some_value_key));
            bindPreferenceSummaryToValue(someValue);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }


        /**
         * Bind prefs text shown below label on prefs changes
         * @param preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);  // bind

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // get new value to use for replacing old
            String sPreference = sharedPreferences.getString(preference.getKey(),"");

            // callback invokation on preference param
            onPreferenceChange(preference,sPreference);


        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String sValue = newValue.toString();

            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefindex = ((ListPreference) preference).findIndexOfValue(sValue);
                if(prefindex >= 0){
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefindex]);
                }

            }else{

                preference.setSummary(sValue);

            }
            return true;
        }
    }


    /**
     * Support for èressingback/up buttonin action bar and on screen
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to the VisualizerActivity
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
