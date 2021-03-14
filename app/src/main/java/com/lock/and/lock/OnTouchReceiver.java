package com.lock.and.lock;

import android.accessibilityservice.AccessibilityService;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import com.lock.and.lock.handler.SleepTimeoutActivity;

import static com.lock.and.lock.handler.AccessibilityService.accessibilityService;

public class OnTouchReceiver extends BroadcastReceiver {
    private static Long syncTime = 0L;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "_preferences",Context.MODE_PRIVATE);
        Long time = System.currentTimeMillis();
        int IntervalInMillis = prefs.getInt("IntervalTime", 500);
        if (syncTime != 0L && time - syncTime < IntervalInMillis) {
            switch (prefs.getString("SelectMethod", "T")) {
                case "A":
                    if(Build.VERSION.SDK_INT > 27) {
                        accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
                    } else Toast.makeText(context, "android version's too low", Toast.LENGTH_SHORT).show();
                    break;

                case "D":
                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    devicePolicyManager.lockNow();
                    break;

                case "R":
                    try {
                        if(SettingsActivity.SettingsFragment.checkRootPermission()) {
                            Runtime.getRuntime().exec("su -c input keyevent KEYCODE_POWER").waitFor();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "Error occurred while performing su command!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    context.startActivity(new Intent(context, SleepTimeoutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    break;
            }
        }
        syncTime = time;
    }
}
