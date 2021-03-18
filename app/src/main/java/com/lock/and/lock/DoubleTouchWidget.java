package com.lock.and.lock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DoubleTouchWidget extends AppWidgetProvider {

    public static void requestPinAppWidget(Context context) {
        if(Build.VERSION.SDK_INT > 25) {
            AppWidgetManager mAppWidgetManager = context.getSystemService(AppWidgetManager.class);
            mAppWidgetManager.requestPinAppWidget(new ComponentName(context,DoubleTouchWidget.class),null,null);
        } else {
            Toast.makeText(context, "Because the Android version is low,\nyou can't add widgets automatically.\nPlease add the widget manually.", Toast.LENGTH_SHORT).show();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            context.startActivity(startMain);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.double_touch_widget);
        Intent intent = new Intent("com.lock.and.lock.OnTouchReceiver").setComponent(new ComponentName(context,OnTouchReceiver.class));
        views.setOnClickPendingIntent(R.id.touchView, PendingIntent.getBroadcast(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.double_touch_widget);
        Intent intent = new Intent("com.lock.and.lock.OnTouchReceiver").setComponent(new ComponentName(context,OnTouchReceiver.class));
        views.setOnClickPendingIntent(R.id.touchView, PendingIntent.getBroadcast(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}