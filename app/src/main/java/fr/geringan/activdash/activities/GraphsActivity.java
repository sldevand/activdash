package fr.geringan.activdash.activities;

import android.os.Bundle;
import android.view.Menu;

import fr.geringan.activdash.R;
import fr.geringan.activdash.network.SocketIOHolder;

public class GraphsActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int nodeIconResource = SocketIOHolder.socket.connected()
                ? R.mipmap.ic_node_on
                : R.mipmap.ic_node_off;

        menu.findItem(R.id.node_status).setIcon(nodeIconResource);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
