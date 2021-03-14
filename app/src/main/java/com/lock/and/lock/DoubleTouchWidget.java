package com.lock.and.lock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DoubleTouchWidget extends AppWidgetProvider {

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