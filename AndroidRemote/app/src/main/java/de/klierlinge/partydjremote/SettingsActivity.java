package de.klierlinge.partydjremote;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public final class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection deprecation
        addPreferencesFromResource(R.xml.settins);
    }
}
