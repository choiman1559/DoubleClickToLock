package com.lock.and.lock;

import android.accessibilityservice.AccessibilityService;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.lock.and.lock.handler.SleepTimeoutActivity;

import static com.lock.and.lock.handler.AccessibilityService.accessibilityService;

public class OnTouchReceiver extends BroadcastReceiver {
    private static Long syncTime = 0L;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() + "_preferences",Context.MODE_PRIVATE);
        Long time = System.currentTimeMillis();
        int IntervalInMillis = prefs.getInt("IntervalTime", 200);
        if (syncTime != 0L && time - syncTime < IntervalInMillis) {
            switch (prefs.getString("SelectMethod", "T")) {
                case "A":
                    if(Build.VERSION.SDK_INT > 27) {
                        if(accessibilityService != null && com.lock.and.lock.handler.AccessibilityService.isServiceRunning) {
                            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
                        } else Toast.makeText(context,"AccessibilityService isn't running.\nPlease retry after granting accessibility permission.",Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(context, "android version's too low\nthis method needs api 28 at least", Toast.LENGTH_SHORT).show();
                    break;

                case "D":
                    if(prefs.getBoolean("isDeviceAdminOn",false)) {
                        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                        devicePolicyManager.lockNow();
                    } else Toast.makeText(context,"Device admin receiver isn't running.\nPlease retry after granting Device admin permission.",Toast.LENGTH_SHORT).show();
                    break;

                case "R":
                    try {
                        if(SettingsActivity.SettingsFragment.checkRootPermission()) {
                            Runtime.getRuntime().exec("su -c input keyevent KEYCODE_POWER").waitFor();
                        } else Toast.makeText(context, "Root permission isn't granted.\nPlease retry after granting Root permission.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Error occurred while performing su command.", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    if(Build.VERSION.SDK_INT > 22) {
                        if (Settings.System.canWrite(context)) {
                            context.startActivity(new Intent(context, SleepTimeoutActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else Toast.makeText(context,"WRITE_SETTING permission isn't granted.\nPlease retry after granting WRITE_SETTING permission.",Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(context, "android version's too low\nthis method needs api 23 at least", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        syncTime = time;
    }
}
