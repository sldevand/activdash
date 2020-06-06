package fr.geringan.activdash.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.providers.IconProvider;
import fr.geringan.activdash.services.ScenarioCommandService;
import fr.geringan.activdash.services.ScenarioService;

public class ScenarioWidget extends AppWidgetProvider {
    private final static String ACTION_SCENARIO = "fr.geringan.activscenariowidget.action.LAUNCH_SCENARIO";
    public static String ACTIONURL_EXTRA = "actionUrl";
    public static RemoteViews remoteViews;
    protected static ScenarioService scenarioService;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        PrefsManager.launch(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.scenario_widget);
        String scenarioId = ScenarioWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        if (null != scenarioId) {
            scenarioService = new ScenarioService(scenarioId);
            callScenarioApi(appWidgetManager, appWidgetId);
        }

        Intent intent = new Intent(context, ScenarioWidget.class);
        intent.setAction(ACTION_SCENARIO);
        intent.putExtra(ACTIONURL_EXTRA, scenarioId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.scenario_widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callScenarioApi(AppWidgetManager appWidgetManager, int appWidgetId) {
        scenarioService.setOnGetResponseListener(dataModel -> {
            if (null != remoteViews) {
                String name = dataModel.getNom();
                remoteViews.setTextViewText(R.id.scenario_widget_text, name);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                if (name != null) {
                    Integer icon = IconProvider.getIconFromName(name);
                    remoteViews.setImageViewResource(R.id.scenario_widget_img, icon);
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        });
        scenarioService.get();
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
            String id = intent.getStringExtra(ACTIONURL_EXTRA);
            ScenarioCommandService scenarioCommandService = new ScenarioCommandService(id);
            scenarioCommandService.get();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            ScenarioWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }
}
