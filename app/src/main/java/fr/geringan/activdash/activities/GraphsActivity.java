package fr.geringan.activdash.activities;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

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

public class GraphsActivity extends RootActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
