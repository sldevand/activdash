package fr.geringan.activdash.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DecimalFormat;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.OnGetResponseListener;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.services.SensorService;

public class SensorWidget extends AppWidgetProvider {
    public static RemoteViews remoteViews;

    public static void updateAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {
        PrefsManager.launch(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.sensor_widget);

        String sensorUrl = String.valueOf(SensorWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0));
        callSensorApi(context, appWidgetManager, appWidgetId, sensorUrl);

        //Refresh the widget informations
        Intent intentUpdate = new Intent(context, SensorWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.sensor_widget_value, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callSensorApi(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String sensorUrl) {
        SensorService sensorService = new SensorService(sensorUrl);
        sensorService.setOnGetResponseListener(new OnGetResponseListener<SensorDataModel>() {
            @Override
            public void onSuccess(SensorDataModel dataModel) {
                DecimalFormat df = new DecimalFormat("##.#");
                String temperature = df.format(Double.valueOf(dataModel.getValeur1()));
                if (dataModel.getActif() == 0) {
                    temperature = context.getString(R.string.thermometer_value);
                }
                String name = dataModel.getNom();
                if (null != remoteViews) {
                    remoteViews.setTextViewText(R.id.sensor_widget_value, temperature);
                    remoteViews.setTextViewText(R.id.sensor_widget_name, name);
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
        sensorService.get();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            SensorWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }
}
