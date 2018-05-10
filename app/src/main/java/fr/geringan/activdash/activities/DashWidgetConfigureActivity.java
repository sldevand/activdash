package fr.geringan.activdash.activities;

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
import fr.geringan.activdash.utils.PrefsManager;

public class DashWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "fr.geringan.activdash.DashWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_TITLE_KEY = "title_";
    private static final String PREF_HTTP_KEY = "http_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText, mAppWidgetHttp;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = DashWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String title = mAppWidgetText.getText().toString();
            String HttpQuery = mAppWidgetHttp.getText().toString();

            ArrayList<String> prefs = new ArrayList<>();
            prefs.add(title);
            prefs.add(HttpQuery);

            savePrefs(context, mAppWidgetId, prefs);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            DashWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public DashWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePrefs(Context context, int appWidgetId, ArrayList<String> prefsArray) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();


        prefs.putString(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId, prefsArray.get(0));
        prefs.putString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, prefsArray.get(1));
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static ArrayList<String> loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId, null);
        String httpValue = prefs.getString(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId, null);

        ArrayList<String> prefsArray = new ArrayList<>();
        prefsArray.add(titleValue);
        prefsArray.add(httpValue);

        return prefsArray;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + PREF_TITLE_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY + PREF_HTTP_KEY + appWidgetId);

        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.dash_widget_configure);
        mAppWidgetText = findViewById(R.id.appwidget_text);
        mAppWidgetHttp = findViewById(R.id.appwidget_http);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);
        PrefsManager.launch(this);
        String prefill = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress;

        mAppWidgetHttp.setText(prefill);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            //  Log.e("mAppWidgetId", String.valueOf(mAppWidgetId));

        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        ArrayList<String> prefs = loadPrefs(DashWidgetConfigureActivity.this, mAppWidgetId);

        mAppWidgetText.setText(prefs.get(0));
    }
}

