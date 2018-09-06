package fr.geringan.activdash.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.webkit.WebView;

import fr.geringan.activdash.R;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.helpers.PrefsManager;


public class ActivServerActivity extends RootActivity {

    private final static String ON_STATE = "on";
    private final static String OFF_STATE = "off";
    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress;
    public String m_stateAddress = m_baseAddress + "getpiassistnode";
    public String m_switchAddress = m_baseAddress + "piassistnode=";
    public String m_logAddress = m_baseAddress + "log";
    private SwitchCompat activServerSwitch;
    private WebView logWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activ_server);

        activServerSwitch = findViewById(R.id.activ_server_switch);
        activServerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) activServerSwitch(isChecked);
        });
        activServerState();
        logWebView = findViewById(R.id.web_view_log);
        logWebViewRefresh();

        initializeSocketioListeners();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.node_status)
                .setIcon((SocketIOHolder.socket.connected()) ? R.mipmap.ic_node_on : R.mipmap.ic_node_off);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void logWebViewRefresh() {
        logWebView.loadUrl(m_logAddress);
    }

    private void activServerState() {

        GetHttp serverState = new GetHttp();
        serverState.setOnResponseListener(response -> {
            boolean onoff = false;
            if (response.contains(ON_STATE)) onoff = true;
            activServerSwitch.setChecked(onoff);
        });
        serverState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_stateAddress);
    }

    private void activServerSwitch(boolean onoff) {

        String state;

        if (onoff) state = ON_STATE;
        else state = OFF_STATE;

        GetHttp serverSwitch = new GetHttp();
        serverSwitch.setOnResponseListener(response -> activServerState());
        serverSwitch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_switchAddress + state);
    }
}
