package fr.geringan.activdash.appwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.GetHttp;

public class SensorWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "fr.geringan.activdash.SensorWidget";
    private static final String PREF_PREFIX_KEY = "sensorwidget_";
    private static final String PREF_HTTP_KEY = "http_";
    private static final String SENSOR_GET_PREFILL = "mesures/get-";
    private static final String SENSORS_GET_PREFILL = "mesures/get-sensors";
    protected String selectedUrl;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner spinner;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = SensorWidgetConfigureActivity.this;
            ArrayList<String> prefs = new ArrayList<>();
            prefs.add(selectedUrl);

            savePrefs(context, mAppWidgetId, prefs);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            SensorWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public static void savePrefs(Context context, int appWidgetId, ArrayList<String> prefsArray) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, prefsArray.get(0));
        prefs.apply();
    }

    public static ArrayList<String> loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String httpValue = prefs.getString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, null);

        ArrayList<String> prefsArray = new ArrayList<>();
        prefsArray.add(httpValue);

        return prefsArray;
    }

    public static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId);

        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);
        setContentView(R.layout.sensor_widget_configure);
        PrefsManager.launch(this);

        spinner = findViewById(R.id.sensor_widget_configure_spinner);
        callSensorsApi(buildSensorsUrl());
        findViewById(R.id.sensor_widget_configure_add_button).setOnClickListener(mOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (null != extras) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        loadPrefs(SensorWidgetConfigureActivity.this, mAppWidgetId);
    }

    public void callSensorsApi(String sensorUrl) {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(this::onResponse);
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sensorUrl);
    }

    public List<SensorDataModel> populateSpinnerArray(JSONArray jsonArray) throws JSONException, IllegalAccessException {
        List<SensorDataModel> spinnerArray = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            SensorDataModel sensor = new SensorDataModel(obj);
            spinnerArray.add(sensor);
        }

        return spinnerArray;
    }

    public void setSpinnerAdapter(List<SensorDataModel> spinnerArray) {
        ArrayAdapter<SensorDataModel> adapter = new ArrayAdapter<SensorDataModel>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public String buildSensorUrl(SensorDataModel sensorDataModel) {
        return PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" +
                SENSOR_GET_PREFILL + sensorDataModel.getRadioid();
    }

    public String buildSensorsUrl() {
        return PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" +
                SENSORS_GET_PREFILL;
    }

    private void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<SensorDataModel> spinnerArray = populateSpinnerArray(jsonArray);
            setSpinnerAdapter(spinnerArray);
            setSpinnerListener();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUrl = buildSensorUrl((SensorDataModel) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedUrl = buildSensorUrl((SensorDataModel) adapterView.getSelectedItem());
            }
        });
    }
}
