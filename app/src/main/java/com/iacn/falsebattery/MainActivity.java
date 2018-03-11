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
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

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

        String runningMode = runningModeList.getValue();
        syncRunningModeSummaryAndState("0".equals(runningMode));

        mBatteryDisguiseValue = mPrefs.getInt(Constant.BATTERY_DISGUISE_VALUE, -1);
        if (mBatteryDisguiseValue != -1) {
            batteryDisguiseCheckBox.setSummary(mBatteryDisguiseValue + "%");
        }

        int realBatteryValue = mPrefs.getInt("real_battery", -1);
        if (realBatteryValue != -1) {
            realBattery.setSummary(realBatteryValue + "%");
        }

        int value = mPrefs.getInt(Constant.DYNAMIC_BATTERY_DISGUISE_VALUE, -1);
        if (value != -1) {
            String savedAction = mPrefs.getString(Constant.DYNAMIC_BATTERY_DISGUISE_ACTION, null);
            dynamicBatteryDisguiseCheckBox.setSummary(savedAction + value + "%");
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        switch (key) {
            case "battery_disguise":
                showSetBatteryDialog();
                break;

            case "dynamic_battery_disguise":
                showSetDynamicBatteryDialog();
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        switch (key) {
            case "running_mode":
                syncRunningModeSummaryAndState("0".equals(newValue));
                break;

            case "battery_disguise":
                break;

            case "dynamic_battery_disguise":
                break;
        }

        return true;
    }

    private void syncRunningModeSummaryAndState(boolean isXposedMode) {
        if (isXposedMode) {
            runningModeList.setSummary(R.string.xposed_mode);
            description.setSummary(R.string.xposed_mode_description);
        } else {
            runningModeList.setSummary(R.string.root_mode);
            description.setSummary(R.string.root_mode_description);
        }

        // 高级功能仅在 Xposed 模式下可用
        for (int i = 0; i < advancedFuncCategory.getPreferenceCount(); i++) {
            advancedFuncCategory.getPreference(i).setEnabled(isXposedMode);
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

    private void showSetDynamicBatteryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_dynamic_battery_disguise, null);

        final Spinner spinner = dialogView.findViewById(R.id.spinner);
        initDynamicBatterySpinner(spinner);

        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);

        String savedAction = mPrefs.getString(Constant.DYNAMIC_BATTERY_DISGUISE_ACTION, "+");
        spinner.setSelection("+".equals(savedAction) ? 0 : 1);
        numberPicker.setValue(mPrefs.getInt(Constant.DYNAMIC_BATTERY_DISGUISE_VALUE, 1));

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = mPrefs.edit();
                        // 保存动态伪装操作方式
                        String action = (String) spinner.getSelectedItem();
                        editor.putString(Constant.DYNAMIC_BATTERY_DISGUISE_ACTION, action);
                        // 保存动态伪装值
                        int value = numberPicker.getValue();
                        editor.putInt(Constant.DYNAMIC_BATTERY_DISGUISE_VALUE, value);
                        editor.apply();

                        dynamicBatteryDisguiseCheckBox.setSummary(action + value + "%");
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void initDynamicBatterySpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.actions, R.layout.item_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void handleStaticDisguise() {
        // TODO: 检查 Running Mode，默认 Xposed 模式
    }
}