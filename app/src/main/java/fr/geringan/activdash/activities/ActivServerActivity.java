package fr.geringan.activdash.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.webkit.WebView;
import android.widget.CompoundButton;

import fr.geringan.activdash.R;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.utils.PrefsManager;


public class ActivServerActivity extends AppCompatActivity {

    private final static String ON_STATE = "on";
    private final static String OFF_STATE = "off";
    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress;
    public String m_stateAddress = m_baseAddress + "getpiassistnode";
    public String m_switchAddress = m_baseAddress + "piassistnode=";
    public String m_logAddress = m_baseAddress + "log";
    SwitchCompat activServerSwitch;
    WebView logWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activ_server);

        //ACTIV SERVER SWITCH
        activServerSwitch = findViewById(R.id.activ_server_switch);
        activServerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) activServerSwitch(isChecked);
            }

        });
        activServerState();

        //LOG WEBBVIEW
        logWebView = findViewById(R.id.web_view_log);

        logWebViewRefresh();

    }

    private void logWebViewRefresh() {

        logWebView.loadUrl(m_logAddress);


    }

    private void activServerState() {

        GetHttp serverState = new GetHttp();
        serverState.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                boolean onoff = false;
                if (response.contains(ON_STATE)) onoff = true;
                activServerSwitch.setChecked(onoff);
            }
        });
        serverState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_stateAddress);
    }

    private void activServerSwitch(boolean onoff) {

        String state;

        if (onoff) state = ON_STATE;
        else state = OFF_STATE;

        GetHttp serverSwitch = new GetHttp();
        serverSwitch.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                activServerState();
            }
        });
        serverSwitch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_switchAddress + state);
    }


}
