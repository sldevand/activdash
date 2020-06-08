package fr.geringan.activdash.appwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.OnGetListResponseListener;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.services.SensorsService;

public class SensorWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "fr.geringan.activdash.SensorWidget";
    private static final String PREF_PREFIX_KEY = "sensorwidget_";
    private static final String PREF_HTTP_KEY = "http_";
    private static final String SENSOR_GET_PREFILL = "mesures/get-";
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
        callSensorsApi();
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

    public void callSensorsApi() {
        SensorsService sensorsService = new SensorsService();
        sensorsService.setOnGetListResponseListener(new OnGetListResponseListener<SensorDataModel>() {
            @Override
            public void onSuccess(List<SensorDataModel> dataList) {
                setSpinnerAdapter(dataList);
                setSpinnerListener();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        sensorsService.get();
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

