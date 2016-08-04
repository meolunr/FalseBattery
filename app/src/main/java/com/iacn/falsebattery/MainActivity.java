package com.iacn.falsebattery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by iAcn on 2016/8/4
 * Emali iAcn0301@foxmail.com
 */
public class MainActivity extends Activity {
    private EditText etBattery;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.gray));

        etBattery = (EditText) findViewById(R.id.et_battery);

        mPref = getSharedPreferences(Constant.FILE_NAME_SETTING, MODE_WORLD_READABLE);
        int value = mPref.getInt(Constant.SETTING_KEY, -1);

        if (value != -1) {
            etBattery.setText(String.valueOf(value));
        }
    }

    public void btnOk(View view) {
        String str = etBattery.getText().toString();

        if (!TextUtils.isEmpty(str)) {
            int battery = Integer.parseInt(str);

            if (battery > 100 || battery < 0) {
                Toast.makeText(this, "电量应在0~100之间", Toast.LENGTH_SHORT).show();
            } else {
                sendDataChanged(battery);
                mPref.edit().putInt("battery", battery).apply();
                Toast.makeText(this, "应用成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            sendDataChanged(-1);
            mPref.edit().putInt("battery", -1).apply();
            Toast.makeText(this, "关闭成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDataChanged(int value) {
        Intent intent = new Intent(Constant.INTENT_DATA_CHANGED);
        intent.putExtra("value", value);
        sendBroadcast(intent);
    }
}