package com.iacn.falsebattery;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by iAcn on 2016/8/4
 * Emali iAcn0301@foxmail.com
 */
public class MainActivity extends PreferenceActivity {

    private Preference description;
    private ListPreference runningModeList;
    private MultiClickCheckBoxPreference batteryDisguiseCheckBox;
    private Preference realBattery;
    private MultiClickCheckBoxPreference dynamicBatteryDisguiseCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_main);
        findPreference();
        initRunningMode();
    }

    private void findPreference() {
        description = findPreference("description");
        runningModeList = (ListPreference) findPreference("running_mode");
        batteryDisguiseCheckBox = (MultiClickCheckBoxPreference) findPreference("battery_disguise");
        realBattery = findPreference("real_battery");
        dynamicBatteryDisguiseCheckBox = (MultiClickCheckBoxPreference) findPreference("dynamic_battery_disguise");
    }

    private void initRunningMode() {
        String value = runningModeList.getValue();
        if ("0".equals(value)) {
            runningModeList.setSummary(R.string.xposed_mode);
            description.setSummary(R.string.xposed_mode_description);
        } else {
            runningModeList.setSummary(R.string.root_mode);
            description.setSummary(R.string.root_mode_description);
        }
    }
}