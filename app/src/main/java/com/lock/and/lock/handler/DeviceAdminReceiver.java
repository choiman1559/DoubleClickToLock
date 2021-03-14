package com.lock.and.lock.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    private void editSharedPreferences(Context context,boolean value) {
        context.getSharedPreferences(context.getPackageName() + "_preferences",Context.MODE_PRIVATE).edit().putBoolean("isDeviceAdminOn",value).apply();
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        super.onEnabled(context, intent);
        editSharedPreferences(context,true);
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        super.onDisabled(context, intent);
        editSharedPreferences(context,false);
    }

    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        return "Are you sure to disable admin?";
    }
}
