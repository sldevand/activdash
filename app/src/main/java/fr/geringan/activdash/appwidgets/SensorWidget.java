package fr.geringan.activdash.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import fr.geringan.activdash.R;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.providers.IconProvider;

public class SensorWidget extends AppWidgetProvider {
    private final static String ACTION_SENSOR = "fr.geringan.activdashwidget.action.REFRESH_SENSOR";
    public static String ACTIONURL_EXTRA = "actionUrl";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        CharSequence widgetText = SensorWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sensor_widget);
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);

        CharSequence widgetHttp = DashWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(1);
        Intent intent = new Intent(context, SensorWidget.class);
        intent.setAction(ACTION_SENSOR);
        intent.putExtra(ACTIONURL_EXTRA, widgetHttp);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.sensor_widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_SENSOR.equals(intent.getAction())) {
            String url = intent.getStringExtra(ACTIONURL_EXTRA);
            GetHttp getData = new GetHttp();
            getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SensorWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }
}
