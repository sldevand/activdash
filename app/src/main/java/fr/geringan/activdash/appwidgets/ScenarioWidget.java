package fr.geringan.activdash.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.OnGetResponseListener;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.providers.IconProvider;
import fr.geringan.activdash.services.ScenarioCommandService;
import fr.geringan.activdash.services.ScenarioService;

public class ScenarioWidget extends AppWidgetProvider {
    private final static String ACTION_SCENARIO = "fr.geringan.activscenariowidget.action.LAUNCH_SCENARIO";
    public static String ACTION_SCENARIO_COMMAND_URL_EXTRA = "actionScenarioCommandUrl";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.scenario_widget);
        String scenarioUrl = ScenarioWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        if (null != scenarioUrl) {
            callScenarioApi(appWidgetManager, appWidgetId, context, scenarioUrl, remoteViews);
        }

        String scenarioCommandUrl = ScenarioWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(1);
        Intent intent = new Intent(context, ScenarioWidget.class);
        intent.setAction(ACTION_SCENARIO);
        intent.putExtra(ACTION_SCENARIO_COMMAND_URL_EXTRA, scenarioCommandUrl);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.scenario_widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callScenarioApi(AppWidgetManager appWidgetManager, int appWidgetId, Context context, String scenarioUrl, RemoteViews remoteViews) {
        ScenarioService scenarioService = new ScenarioService(scenarioUrl);
        scenarioService.setOnGetResponseListener(new OnGetResponseListener<ScenarioDataModel>() {
            @Override
            public void onSuccess(ScenarioDataModel dataModel) {
                if (null != remoteViews) {
                    String name = dataModel.getNom();
                    remoteViews.setTextViewText(R.id.scenario_widget_text, name);
                    if (name != null) {
                        Integer icon = IconProvider.getIconFromName(name);
                        remoteViews.setImageViewResource(R.id.scenario_widget_img, icon);
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            }

            @Override
            public void onError(String error) {
                remoteViews.setTextViewText(R.id.scenario_widget_text, context.getString(R.string.undefined));
            }
        });
        scenarioService.get();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PrefsManager.launch(context);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        PrefsManager.launch(context);
        if (ACTION_SCENARIO.equals(intent.getAction())) {
            String url = intent.getStringExtra(ACTION_SCENARIO_COMMAND_URL_EXTRA);
            ScenarioCommandService scenarioCommandService = new ScenarioCommandService(url);
            scenarioCommandService.setOnGetResponseListener(new OnGetResponseListener<ScenarioDataModel>() {
                @Override
                public void onSuccess(ScenarioDataModel dataModel) {
                    //Intentionnally empty
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            });
            scenarioCommandService.get();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            ScenarioWidgetConfigureActivity.deletePrefs(context, appWidgetId);
        }
    }
}
