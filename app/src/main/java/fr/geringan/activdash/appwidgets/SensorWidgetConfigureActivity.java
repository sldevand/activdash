package fr.geringan.activdash.appwidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;

public class SensorWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "fr.geringan.activdash.SensorWidget";
    private static final String PREF_PREFIX_KEY = "sensorwidget_";
    private static final String PREF_TITLE_KEY = "title_";
    private static final String PREF_HTTP_KEY = "http_";
    //Example get-sensor24ctn10id4
    private static final String SENSOR_GET_PREFILL = "mesures/get-";

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText mAppWidgetText, mAppWidgetHttp;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = SensorWidgetConfigureActivity.this;
            String title = mAppWidgetText.getText().toString();
            String HttpQuery = mAppWidgetHttp.getText().toString();
            ArrayList<String> prefs = new ArrayList<>();
            prefs.add(title);
            prefs.add(HttpQuery);

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
        prefs.putString(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId, prefsArray.get(0));
        prefs.putString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, prefsArray.get(1));
        prefs.apply();
    }

    public static ArrayList<String> loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId, null);
        String httpValue = prefs.getString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, null);

        ArrayList<String> prefsArray = new ArrayList<>();
        prefsArray.add(titleValue);
        prefsArray.add(httpValue);

        return prefsArray;
    }

    public static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId);

        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);
        setContentView(R.layout.sensor_widget_configure);
        PrefsManager.launch(this);

        mAppWidgetText = findViewById(R.id.sensor_widget_configure_text);
        mAppWidgetHttp = findViewById(R.id.sensor_widget_configure_http);
        findViewById(R.id.sensor_widget_configure_add_button).setOnClickListener(mOnClickListener);

        String prefill = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" + SENSOR_GET_PREFILL;
        mAppWidgetHttp.setText(prefill);

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

        ArrayList<String> prefs = loadPrefs(SensorWidgetConfigureActivity.this, mAppWidgetId);
        mAppWidgetText.setText(prefs.get(0));
    }
}

