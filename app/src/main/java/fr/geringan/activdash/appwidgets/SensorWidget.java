package fr.geringan.activdash.appwidgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.GetHttp;

public class SensorWidget extends AppWidgetProvider {
    private final static String ACTION_SENSOR = "fr.geringan.activdashwidget.action.REFRESH_SENSOR";
    public static String ACTIONURL_EXTRA = "actionUrl";
    public static CustomRemoteViews remoteViews;

    public static void updateAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {
        remoteViews = new CustomRemoteViews(context.getPackageName(), R.layout.sensor_widget);
        PrefsManager.launch(context);

        CharSequence widgetText = SensorWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        remoteViews.setTextViewText(R.id.sensor_widget_configure_text, widgetText);

        CharSequence widgetHttp = SensorWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(1);

        //Call the REST APIs
        String thermostatUrl = String.valueOf(widgetHttp);
        callSensorApi(appWidgetManager, appWidgetId, thermostatUrl);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callSensorApi(AppWidgetManager appWidgetManager, int appWidgetId, String sensorUrl) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    SensorDataModel sensor = new SensorDataModel(obj);
                    String temperature = sensor.getValeur1();
                    String name = sensor.getNom();
                    if (null != remoteViews) {
                        remoteViews.setTextViewText(R.id.sensor_widget_value, temperature);
                        remoteViews.setTextViewText(R.id.sensor_widget_name, name);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sensorUrl);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
