package com.iacn.falsebattery;

/**
 * Created by iAcn on 2016/8/4
 * Emali iAcn0301@foxmail.com
 */

import android.content.Intent;
import android.os.BatteryManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("android")) return;

        findAndHookMethod("android.app.ActivityManagerNative", loadPackageParam.classLoader, "broadcastStickyIntent",
                Intent.class, String.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[0];

                        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                            XSharedPreferences xPref = new XSharedPreferences(MainActivity.class.getPackage().getName(), "setting");
                            int battery = xPref.getInt("battery", -1);

                            if (battery != -1) {
                                intent.putExtra(BatteryManager.EXTRA_LEVEL, battery);
                            }
                        }
                    }
                });
    }
}