package nonameyetsoft.com.torch;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(Flashlight.LOG_TAG, "Widget tapped.");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.neon_widget);
        Intent serviceIntent = new Intent(context, FlashlightService.class);

        if (Flashlight.isOn()) {
            Log.i(Flashlight.LOG_TAG, "turn off code.");
            views.setImageViewResource(R.id.NeonWidget, R.drawable.button_widget_on);
            context.stopService(serviceIntent);
            Flashlight.setInUseByWidget(false);
            Flashlight.setIsOn(false);
        } else {
            Log.i(Flashlight.LOG_TAG, "turn on code.");
            if (!FlashlightService.isRunning()) {
                Log.i(Flashlight.LOG_TAG, "Starting service from the widget");
                serviceIntent.putExtra("command", "turnOn");
                context.startService(serviceIntent);
            }
            views.setImageViewResource(R.id.NeonWidget, R.drawable.button_widget_off);
            Flashlight.setInUseByWidget(true);
            Flashlight.setIsOn(true);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class),
                views);
    }
}
