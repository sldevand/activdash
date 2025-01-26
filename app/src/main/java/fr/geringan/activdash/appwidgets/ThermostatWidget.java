package fr.geringan.activdash.appwidgets;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import androidx.core.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import fr.geringan.activdash.R;
import fr.geringan.activdash.activities.ThermostatControllerActivity;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ModeDataModel;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.models.ThermostatDataModel;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.providers.BoilerState;
import fr.geringan.activdash.providers.BoilerStateProvider;
import fr.geringan.activdash.providers.ModeImage;
import fr.geringan.activdash.providers.ModeImageProvider;

public class ThermostatWidget extends AppWidgetProvider {
    public static final String THERMOSTAT_GET_ENDPOINT = "thermostat";
    public static final String THERMOSTAT_SENSOR_GET_ENDPOINT = "mesures/get-sensors/thermostat";
    public static RemoteViews remoteViews;

    public static void updateAppWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId
    ) {
        PrefsManager.launch(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.thermostat_widget);

        //Call the REST APIs
        String thermostatUrl = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" + THERMOSTAT_GET_ENDPOINT;
        callThermostatApi(appWidgetManager, appWidgetId, thermostatUrl, context);
        String thermostatSensorUrl = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" + THERMOSTAT_SENSOR_GET_ENDPOINT;
        callThermostatSensorApi(appWidgetManager, appWidgetId, thermostatSensorUrl);

        //Refresh the widget informations
        Intent intentUpdate = new Intent(context, ThermostatWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.thermostat_widget_infos_layout, pendingIntent);

        //launch thermostat activity
        Intent thermostatIntent = new Intent(context, ThermostatControllerActivity.class);
        PendingIntent thermostatPendingIntent = PendingIntent.getActivity(context, 0, thermostatIntent, FLAG_MUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.thermostat_widget_title_layout, thermostatPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void callThermostatApi(AppWidgetManager appWidgetManager, int appWidgetId, String thermostatUrl, Context context) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            if (null == remoteViews) {
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ThermostatDataModel thermostat = new ThermostatDataModel(obj);

                    // set consigne text
                    double consigne = thermostat.getConsigne();
                    remoteViews.setTextViewText(R.id.thermostat_widget_consigne_text, String.valueOf(consigne));

                    Resources resources = context.getResources();
                    //setEtatColor
                    BoilerState boiler = BoilerStateProvider.getBoilerState(thermostat.getEtat());
                    int etatColor = ResourcesCompat.getColor(resources, boiler.getColor(), null);
                    remoteViews.setInt(R.id.thermostat_widget_etat_image, "setColorFilter", etatColor);

                    //setModeImage
                    ModeDataModel mode = thermostat.getMode();
                    ModeImage modeImg = ModeImageProvider.getModeImage(mode.getNom());
                    int modeColor = ResourcesCompat.getColor(resources, modeImg.getColor(), null);
                    remoteViews.setImageViewResource(R.id.thermostat_widget_mode_image, modeImg.getImg());
                    remoteViews.setInt(R.id.thermostat_widget_mode_image, "setColorFilter", modeColor);

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            } catch (Exception e) {
                //Intentionnally empty
            }
        });
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thermostatUrl);
    }

    public static void callThermostatSensorApi(
        AppWidgetManager appWidgetManager,
        int appWidgetId,
        String thermostatSensorUrl
    ) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    SensorDataModel sensor = new SensorDataModel(obj);
                    Double temperature = sensor.getValeur1();
                    if (null != remoteViews) {
                        DecimalFormat df = new DecimalFormat("0.#");
                        remoteViews.setTextViewText(R.id.thermostat_widget_sensor_text, df.format(temperature));
                        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    }
                }
            } catch (Exception e) {
                //Intentionnally empty
            }
        });
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thermostatSensorUrl);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
