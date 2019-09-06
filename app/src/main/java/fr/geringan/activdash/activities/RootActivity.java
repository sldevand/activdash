package fr.geringan.activdash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import java.util.Arrays;

import fr.geringan.activdash.R;
import fr.geringan.activdash.helpers.Tools;
import fr.geringan.activdash.interfaces.SocketIOEventsListener;
import fr.geringan.activdash.network.SocketIOHolder;

public abstract class RootActivity extends AppCompatActivity implements SocketIOEventsListener {


    protected View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = findViewById(android.R.id.content);
    }

    public void initializeSocketioListeners() {
        SocketIOHolder.launch();
        if (null == SocketIOHolder.socket) return;
        SocketIOHolder.initEventListeners();
        SocketIOHolder.setEventsListener(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (null != SocketIOHolder.socket)
            menu.findItem(R.id.node_status).setIcon((SocketIOHolder.socket.connected()) ? R.mipmap.ic_node_on : R.mipmap.ic_node_off);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSocketIOConnect() {
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onSocketIOTimeout() {
        Tools.shortSnackbar(rootView, R.string.node_timeout);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onSocketIODisconnect() {
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onSocketIOMessage(Object... args) {
        Tools.shortSnackbar(rootView, Arrays.toString(args));
    }
}
