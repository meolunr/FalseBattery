package com.iacn.falsebattery;

/**
 * Created by iAcn on 2016/8/4
 * Emali iAcn0301@foxmail.com
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class MainHook implements IXposedHookLoadPackage {

    private boolean mFirstRun = true;
    private int mBatteryValue;
    private Context mContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("android")) return;

        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        mContext = (Context) callMethod(activityThread, "getSystemContext");

        findAndHookMethod("android.app.ActivityManagerNative", loadPackageParam.classLoader, "broadcastStickyIntent",
                Intent.class, String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (mFirstRun) {
                            // 被 Hook 的方法第一次执行
                            registerDataChange();

                            XSharedPreferences xPref = new XSharedPreferences(Constant.PACKAGE_NAME, Constant.FILE_NAME_SETTING);
                            mBatteryValue = xPref.getInt(Constant.SETTING_KEY, -1);

                            mFirstRun = false;
                        }

                        Intent intent = (Intent) param.args[0];

                        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) && mBatteryValue != -1) {
                            intent.putExtra(BatteryManager.EXTRA_LEVEL, mBatteryValue);
                        }
                    }
                });
    }

    private void registerDataChange() {
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 更新内存中的电量值
                mBatteryValue = intent.getIntExtra("value", -1);
                XposedBridge.log("Update battery in memory = " + mBatteryValue);
            }
        }, new IntentFilter(Constant.INTENT_DATA_CHANGED));
    }
}