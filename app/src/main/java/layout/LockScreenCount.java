package layout;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import hu.elte.ordogfioka.screenlockcounter.R;

/**
 * Implementation of App Widget functionality.
 */
public class LockScreenCount extends AppWidgetProvider {
    private BroadcastReceiver mScreenOnReceiver = null;
    private static Integer numberOfLocks = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = numberOfLocks.toString();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lock_screen_count);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);

        mScreenOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                numberOfLocks++;
                Toast.makeText(context.getApplicationContext(),"lock" + numberOfLocks.toString(),Toast.LENGTH_LONG).show();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), LockScreenCount.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    onUpdate(context, appWidgetManager, appWidgetIds);
                }
            }
        };

        context.getApplicationContext().registerReceiver(mScreenOnReceiver, theFilter);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 7) {
            try {
                context.getApplicationContext().unregisterReceiver(mScreenOnReceiver);
            }
            catch (IllegalArgumentException e) {
                mScreenOnReceiver = null;
            }
        }
        else {
            context.getApplicationContext().unregisterReceiver(mScreenOnReceiver);
            mScreenOnReceiver = null;
        }
    }
}

