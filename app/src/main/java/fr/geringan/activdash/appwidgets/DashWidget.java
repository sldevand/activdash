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

public class DashWidget extends AppWidgetProvider {
    private final static String ACTION_SCENARIO = "fr.geringan.activdashwidget.action.LAUNCH_SCENARIO";
    public static String ACTIONURL_EXTRA = "actionUrl";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        CharSequence widgetText = DashWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dash_widget);
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);

        if (widgetText != null) {
            remoteViews.setImageViewResource(R.id.appwidget_img, IconProvider.getIconFromName(widgetText.toString()));
        }

        CharSequence widgetHttp = DashWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(1);
        Intent intent = new Intent(context, DashWidget.class);
        intent.setAction(ACTION_SCENARIO);
        intent.putExtra(ACTIONURL_EXTRA, widgetHttp);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dashWidgetLayout, pendingIntent);

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
        if (ACTION_SCENARIO.equals(intent.getAction())) {
            String url = intent.getStringExtra(ACTIONURL_EXTRA);
            GetHttp getData = new GetHttp();
            getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            DashWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }
}
