package com.lock.and.lock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lock.and.lock.handler.AccessibilityService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SettingsFragment.PERMISSIONS_REQUEST_CODE) {
            SettingsFragment.checkPermissions("");
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static class SettingsFragment extends PreferenceFragmentCompat {
        private static Activity mContext;
        private static SharedPreferences prefs;

        private static Preference LowApi;
        private static Preference Permission;
        private static Preference SelectMethod;
        private static Preference IntervalTime;

        public final static int PERMISSIONS_REQUEST_CODE = 1001;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            if(context instanceof Activity) mContext = (Activity) context;
            else throw new RuntimeException("Context isn't instanceof Activity!!!");
        }

        public static boolean checkRootPermission() throws IOException, InterruptedException {
            Process process = Runtime.getRuntime().exec("su -c id");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("uid=0") && line.contains("gid=0") && line.contains("root")) return true;
            }
            process.waitFor();
            return false;
        }

        public static void checkPermissions(String value) {
            if(value.equals("")) value = prefs.getString("SelectMethod","T");
            String Summary;
            switch (value) {
                case "A":
                    Summary = "Accessibility method";
                    if(Build.VERSION.SDK_INT < 28) {
                        LowApi.setVisible(true);
                        Permission.setVisible(false);
                    } else {
                        LowApi.setVisible(false);
                        Permission.setVisible(!AccessibilityService.isServiceRunning);
                    }
                    break;

                case "D":
                    Summary = "Device Admin method";
                    LowApi.setVisible(false);
                    Permission.setVisible(!prefs.getBoolean("isDeviceAdminOn",false));
                    break;

                case "R":
                    Summary = "Root method";
                    LowApi.setVisible(false);
                    try {
                        Permission.setVisible(!checkRootPermission());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Permission.setVisible(true);
                    }
                    break;

                default:
                    Summary = "Timeout method (Default)";
                    if(Build.VERSION.SDK_INT < 23) {
                        LowApi.setVisible(true);
                        Permission.setVisible(false);
                    } else {
                        LowApi.setVisible(false);
                        Permission.setVisible(!Settings.System.canWrite(mContext));
                    }
                    break;
            }
            SelectMethod.setSummary("Now : " + Summary);
        }

        private void performPermissions(String value) {
            switch (value) {
                case "A":
                    if(Build.VERSION.SDK_INT > 27) {
                        mContext.startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), PERMISSIONS_REQUEST_CODE);
                    } else Toast.makeText(mContext, "android version's too low", Toast.LENGTH_SHORT).show();
                    break;

                case "D":
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(mContext, com.lock.and.lock.handler.DeviceAdminReceiver.class));
                    mContext.startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
                    break;

                case "R":
                    try {
                        checkRootPermission();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "can't get root permission!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    if(Build.VERSION.SDK_INT > 22) {
                        Intent intent2 = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent2.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivityForResult(intent2, PERMISSIONS_REQUEST_CODE);
                    } else Toast.makeText(mContext, "android version's too low", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            prefs = mContext.getSharedPreferences(mContext.getPackageName() + "_preferences",MODE_PRIVATE);

            LowApi = findPreference("LowApi");
            Permission = findPreference("Permission");
            SelectMethod = findPreference("SelectMethod");
            IntervalTime = findPreference("IntervalTime");

            checkPermissions(prefs.getString("SelectMethod","T"));
            IntervalTime.setSummary("Now : " + prefs.getInt("IntervalTime",500) + " ms (Default)");
            SelectMethod.setOnPreferenceChangeListener((preference, newValue) -> {
                checkPermissions(newValue + "");
                return true;
            });
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            switch (preference.getKey()) {
                case "Permission":
                    performPermissions(prefs.getString("SelectMethod","T"));
                    break;

                case "IntervalTime":
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setCancelable(true);
                    dialog.setTitle("Input Value");
                    dialog.setMessage("The interval time maximum limit is 2147483647 ms.");

                    EditText editText = new EditText(mContext);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setHint("Input Limit Value");
                    editText.setGravity(Gravity.START);
                    editText.setText(String.valueOf(prefs.getInt("IntervalTime", 500)));

                    LinearLayout parentLayout = new LinearLayout(mContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 16, 30, 16);
                    editText.setLayoutParams(layoutParams);
                    parentLayout.addView(editText);
                    dialog.setView(parentLayout);

                    dialog.setPositiveButton("Apply", (d, w) -> {
                        String value = editText.getText().toString();
                        if (value.equals("")) {
                            Toast.makeText(mContext, "Please Input Value", Toast.LENGTH_SHORT).show();
                        } else {
                            int IntValue = Integer.parseInt(value);
                            if (IntValue > 0x7FFFFFFF - 1) {
                                Toast.makeText(mContext, "Value must be lower than 2147483647", Toast.LENGTH_SHORT).show();
                            } else {
                                prefs.edit().putInt("IntervalTime", IntValue).apply();
                                IntervalTime.setSummary("Now : " + IntValue + (IntValue == 500 ? " ms (Default)" : " ms"));
                            }
                        }
                    });
                    dialog.setNeutralButton("Reset Default", (d, w) -> {
                        prefs.edit().putInt("IntervalTime", 500).apply();
                        IntervalTime.setSummary("Now : " + 500 + " ms (Default)");
                    });
                    dialog.setNegativeButton("Cancel", (d, w) -> { });
                    dialog.show();
                    break;
            }
            return super.onPreferenceTreeClick(preference);
        }
    }
}