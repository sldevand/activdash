package fr.geringan.activdash.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import fr.geringan.activdash.R;
import fr.geringan.activdash.network.GetHttp;


public class DashWidget extends AppWidgetProvider {
    private static final String INTENT_CLICK1 = "fr.geringan.activdash.activities.click";
    private final static String ACTION_SCENARIO = "fr.geringan.activdashwidget.action.OPEN_TUTO";
    public static String ACTIONURL_EXTRA = "actionUrl";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = DashWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        CharSequence widgetHttp = DashWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(1);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dash_widget);

        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);
        int img = img = R.mipmap.ic_play;
        if (widgetText != null) {
            String text = widgetText.toString();

            switch (text) {
                case "TV":
                    img = R.mipmap.ic_tv;
                    break;
                case "Film":
                    img = R.mipmap.ic_movie;
                    break;
                case "Coucher":
                    img = R.mipmap.ic_bed;
                    break;
                default:
                    img = R.mipmap.ic_play;
                    break;
            }
        }
        remoteViews.setImageViewResource(R.id.appwidget_img, img);

        Intent intent = new Intent(context, DashWidget.class);
        intent.setAction(ACTION_SCENARIO);
        intent.putExtra(ACTIONURL_EXTRA, widgetHttp);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dashWidgetLayout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

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
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            DashWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

