package fr.geringan.activdash.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.models.ThermostatDataModel;
import fr.geringan.activdash.network.GetHttp;

public class ThermostatWidget extends AppWidgetProvider {
    public static String thermostatUrl = "http://192.168.1.52/activapi/api/thermostat";
    public static String thermostatSensorUrl = "http://192.168.1.52/activapi/api/mesures/get-sensors/thermostat";
    public static RemoteViews remoteViews;

    public static void updateAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.thermostat_widget);
        callThermostatApi(appWidgetManager, appWidgetId);
        callThermostatSensorApi(appWidgetManager, appWidgetId);

        Intent intentUpdate = new Intent(context, ThermostatWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.thermostat_widget_layout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void callThermostatApi(AppWidgetManager appWidgetManager,int  appWidgetId) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ThermostatDataModel thermostat = new ThermostatDataModel(obj);
                    double consigne = thermostat.getConsigne();

                    if (null != remoteViews) {
                        remoteViews.setTextViewText(R.id.thermostat_widget_consigne_text, String.valueOf(consigne));
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thermostatUrl);
    }

    public static void callThermostatSensorApi(AppWidgetManager appWidgetManager,int  appWidgetId) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    SensorDataModel sensor = new SensorDataModel(obj);
                    Log.d("inside sensor",String.valueOf(sensor.getValeur1()));
                    String temperature = sensor.getValeur1();
                    if (null != remoteViews) {
                        remoteViews.setTextViewText(R.id.thermostat_widget_sensor_text, temperature);
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thermostatSensorUrl);
    }
}
