package com.iacn.falsebattery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by iAcn on 2016/8/4
 * Email i@iacn.me
 */
public class MainActivity extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {

    private Preference description;
    private ListPreference runningModeList;
    private MultiClickCheckBoxPreference batteryDisguiseCheckBox;
    private PreferenceCategory advancedFuncCategory;
    private Preference realBattery;
    private MultiClickCheckBoxPreference dynamicBatteryDisguiseCheckBox;

    private SharedPreferences mPrefs;
    private int mBatteryDisguiseValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_main);
        findPreference();
        setListener();
        initData();
    }

    private void findPreference() {
        description = findPreference("description");
        runningModeList = (ListPreference) findPreference("running_mode");
        batteryDisguiseCheckBox = (MultiClickCheckBoxPreference) findPreference("battery_disguise");
        advancedFuncCategory = (PreferenceCategory) findPreference("advanced_function_category");
        realBattery = findPreference("real_battery");
        dynamicBatteryDisguiseCheckBox = (MultiClickCheckBoxPreference) findPreference("dynamic_battery_disguise");
    }

    private void setListener() {
        batteryDisguiseCheckBox.setOnPreferenceClickListener(this);
        dynamicBatteryDisguiseCheckBox.setOnPreferenceClickListener(this);

        runningModeList.setOnPreferenceChangeListener(this);
        batteryDisguiseCheckBox.setOnPreferenceChangeListener(this);
        dynamicBatteryDisguiseCheckBox.setOnPreferenceChangeListener(this);
    }

    private void initData() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mBatteryDisguiseValue = mPrefs.getInt(Constant.BATTERY_DISGUISE_VALUE, 0);

        String runningMode = runningModeList.getValue();
        syncRunningModeSummaryAndState(runningMode);

        batteryDisguiseCheckBox.setSummary(mBatteryDisguiseValue + "%");
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "battery_disguise":
                showSetBatteryDialog();
                break;

            case "dynamic_battery_disguise":
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        switch (key) {
            case "running_mode":
                syncRunningModeSummaryAndState((String) newValue);
                break;

            case "battery_disguise":

                break;

            case "dynamic_battery_disguise":
                break;
        }

        return true;
    }

    private void syncRunningModeSummaryAndState(String value) {
        if ("0".equals(value)) {
            runningModeList.setSummary(R.string.xposed_mode);
            description.setSummary(R.string.xposed_mode_description);
            advancedFuncCategory.setEnabled(true);
        } else {
            runningModeList.setSummary(R.string.root_mode);
            description.setSummary(R.string.root_mode_description);
            advancedFuncCategory.setEnabled(false);
        }
    }

    private void showSetBatteryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_battery_disguise, null);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(mBatteryDisguiseValue);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBatteryDisguiseValue = numberPicker.getValue();
                        mPrefs.edit().putInt(Constant.BATTERY_DISGUISE_VALUE, mBatteryDisguiseValue).apply();
                        batteryDisguiseCheckBox.setSummary(mBatteryDisguiseValue + "%");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}