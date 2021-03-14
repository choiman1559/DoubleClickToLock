package com.lock.and.lock.handler;

import android.view.accessibility.AccessibilityEvent;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {
    public static AccessibilityService accessibilityService;
    public static boolean isServiceRunning = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        accessibilityService = this;
        isServiceRunning = true;
    }
}
