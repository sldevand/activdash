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
import fr.geringan.activdash.helpers.Scenario.UrlBuilder;
import fr.geringan.activdash.interfaces.OnGetListResponseListener;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.services.ScenariosService;

public class ScenarioWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "fr.geringan.activdash.ScenarioWidget";
    private static final String PREF_PREFIX_KEY = "scenariowidget_";
    private static final String PREF_URL_KEY = "http_url";
    private static final String PREF_COMMAND_URL_KEY = "http_command_url";
    protected String selectedUrl;
    protected String selectedCommandUrl;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner spinner;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ScenarioWidgetConfigureActivity.this;
            ArrayList<String> prefs = new ArrayList<>();
            prefs.add(selectedUrl);
            prefs.add(selectedCommandUrl);

            savePrefs(context, mAppWidgetId, prefs);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ScenarioWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public static void savePrefs(Context context, int appWidgetId, ArrayList<String> prefsArray) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_URL_KEY + appWidgetId, prefsArray.get(0));
        prefs.putString(PREF_PREFIX_KEY + PREF_COMMAND_URL_KEY + appWidgetId, prefsArray.get(1));
        prefs.apply();
    }

    public static ArrayList<String> loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String urlPref = prefs.getString(PREF_PREFIX_KEY + PREF_URL_KEY + appWidgetId, null);
        String commandUrlPref = prefs.getString(PREF_PREFIX_KEY + PREF_COMMAND_URL_KEY + appWidgetId, null);

        ArrayList<String> prefsArray = new ArrayList<>();
        prefsArray.add(urlPref);
        prefsArray.add(commandUrlPref);

        return prefsArray;
    }

    public static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + PREF_URL_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY + PREF_COMMAND_URL_KEY + appWidgetId);

        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (null != extras) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        PrefsManager.launch(this);
        setContentView(R.layout.scenario_widget_configure);

        spinner = findViewById(R.id.scenario_widget_configure_spinner);
        findViewById(R.id.scenario_widget_configure_add_button).setOnClickListener(mOnClickListener);
        callScenariosApi();
    }

    public void callScenariosApi() {
        ScenariosService scenariosService = new ScenariosService();
        scenariosService.setOnGetListResponseListener(new OnGetListResponseListener<ScenarioDataModel>() {
            @Override
            public void onSuccess(List<ScenarioDataModel> dataList) {
                setSpinnerAdapter(dataList);
                setSpinnerListener();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        scenariosService.get();
    }

    public void setSpinnerAdapter(List<ScenarioDataModel> spinnerArray) {
        ArrayAdapter<ScenarioDataModel> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUrl = UrlBuilder.buildUrl((ScenarioDataModel) adapterView.getSelectedItem());
                selectedCommandUrl = UrlBuilder.buildCommandUrl((ScenarioDataModel) adapterView.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedUrl = UrlBuilder.buildUrl((ScenarioDataModel) adapterView.getSelectedItem());
                selectedCommandUrl = UrlBuilder.buildCommandUrl((ScenarioDataModel) adapterView.getSelectedItem());
            }
        });
    }
}

