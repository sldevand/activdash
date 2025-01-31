package fr.geringan.activdash.appwidgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DecimalFormat;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.OnGetResponseListener;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.services.SensorService;

public class SensorWidget extends AppWidgetProvider {
    public static void updateAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {
        PrefsManager.launch(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sensor_widget);

        String sensorUrl = SensorWidgetConfigureActivity.loadPrefs(context, appWidgetId).get(0);
        if (null != sensorUrl) {
            callSensorApi(context, appWidgetManager, appWidgetId, sensorUrl, remoteViews);
        }

        //Refresh the widget informations
        Intent intentUpdate = new Intent(context, SensorWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_MUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.sensor_widget_value, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callSensorApi(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String sensorUrl, RemoteViews remoteViews) {
        SensorService sensorService = new SensorService(sensorUrl);
        sensorService.setOnGetResponseListener(new OnGetResponseListener<SensorDataModel>() {
            @Override
            public void onSuccess(SensorDataModel dataModel) {
                if (null == remoteViews) {
                    return;
                }

                remoteViews.setTextViewText(R.id.sensor_widget_name, dataModel.getNom());

                if (dataModel.getActif() == 0) {
                    remoteViews.setViewVisibility(R.id.sensor_widget_value_2, View.GONE);
                    remoteViews.setTextViewText(
                            R.id.sensor_widget_value,
                            context.getString(R.string.thermometer_value)
                    );

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    return;
                }

                DecimalFormat df = new DecimalFormat("##.#");
                String value1 = df.format(dataModel.getValeur1());
                remoteViews.setTextViewText(R.id.sensor_widget_value, value1);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

                String value2 = dataModel.getValeur2();
                if (null != value2 && !value2.isEmpty()) {
                    String formattedValue2 = df.format(Double.valueOf(value2));
                    remoteViews.setViewVisibility(R.id.sensor_widget_value_2, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.sensor_widget_value_2, formattedValue2);
                }
            }

            @Override
            public void onError(String error) {
                //Intentionnally empty
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
