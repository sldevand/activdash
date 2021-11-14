package fr.geringan.activdash.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.network.SocketIOHolder;


public class ActivServerActivity extends RootActivity {

    private final static String ON_STATE = "on";
    private final static String OFF_STATE = "off";
    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress;
    public String m_stateAddress = m_baseAddress + "/node/status";
    public String m_switchAddress = m_baseAddress + "/node/toggle/";
    public String m_logAddress = m_baseAddress + "/node/log";
    private SwitchCompat activServerSwitch;
    private AppCompatImageView serialPortReset;
    private TextView tvLog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activ_server);

        activServerSwitch = findViewById(R.id.activ_server_switch);
        activServerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) activServerSwitch(isChecked);
        });

        serialPortReset = findViewById(R.id.seriaport_reset);
        serialPortReset.setOnClickListener(view -> {
            serialPortReset();
        });

        activServerState();
        tvLog = findViewById(R.id.tvLog);
        logTextViewRefresh();
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

    private void logTextViewRefresh() {
        GetHttp serverState = new GetHttp();
        serverState.setOnResponseListener(response -> {
            try {
                tvLog.setText(prepareMessage(response), TextView.BufferType.SPANNABLE);
            } catch (JSONException e) {
                e.printStackTrace();
                tvLog.setText(e.getMessage());
            }
        });
        serverState.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_logAddress);
    }

    protected void prependLogText(String text) {
        Spannable currentText = (Spannable) tvLog.getText();
        CharSequence indexedText = TextUtils.concat(text + "\n", currentText);
        tvLog.setText(indexedText);
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
        serverSwitch.setOnResponseListener(response -> {
            new android.os.Handler().postDelayed(
                    this::activServerState,
                    300
            );
        });
        serverSwitch.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m_switchAddress + state);
    }

    private void serialPortReset() {
        SocketIOHolder.socket.emit(SocketIOHolder.EMIT_SERIAL_PORT_RESET);
    }

    protected String prepareMessage(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (!jsonObject.has("messages")) {
            throw new JSONException("JSONObject has no 'message' key");
        }

        return buildLogsString(jsonObject.getJSONArray("messages"));
    }

    protected String buildLogsString(JSONArray logs) throws JSONException {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < logs.length(); i++) {
            long createdAt = logs.getJSONObject(i).getLong("createdAt");
            stringBuilder.append(sDateFormat.format(new Date(createdAt * 1000)));
            stringBuilder.append(" ");
            stringBuilder.append(logs.getJSONObject(i).getString("content"));
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    @Override
    public void initializeSocketioListeners() {
        super.initializeSocketioListeners();
        SocketIOHolder.socket.on(SocketIOHolder.EVENT_MESSAGE_CONSOLE, args -> {
            if (args.length < 1) {
                return;
            }
            runOnUiThread(() ->
                    prependLogText((String) args[0])
            );
        });
    }
}
