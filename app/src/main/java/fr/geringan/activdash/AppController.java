package fr.geringan.activdash;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import fr.geringan.activdash.activities.ActivServerActivity;
import fr.geringan.activdash.activities.RootActivity;
import fr.geringan.activdash.activities.SettingsActivity;
import fr.geringan.activdash.activities.ThermostatControllerActivity;
import fr.geringan.activdash.fragments.ActuatorsFragment;
import fr.geringan.activdash.fragments.ScenariosFragment;
import fr.geringan.activdash.fragments.SensorsFragment;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.helpers.Tools;
import fr.geringan.activdash.menu.OptionsItems;
import fr.geringan.activdash.network.NetworkChangeReceiver;
import fr.geringan.activdash.network.NetworkUtil;
import fr.geringan.activdash.network.SocketIOHolder;

public class AppController extends RootActivity implements NetworkChangeReceiver.OnNetworkChangedListener
{

    private static final Integer MAIN_VIEW = 1;
    private static final Integer NO_PREFS_VIEW = 2;
    private static final Integer ERROR_VIEW = 3;
    protected ProgressDialog progressBar;
    protected DrawerLayout mDrawerLayout;
    protected NetworkChangeReceiver networkChangeReceiver;
    protected IntentFilter intentFilter;
    private Integer selectedView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        checkConnectivity();
        if (null != networkChangeReceiver) {
            return;
        }
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setOnNetworkChangedListener(this);
        registerReceiverWithVersions();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        OptionsItems optionsItems = new OptionsItems(
                getSupportFragmentManager(),
                AppController.this
        );

        try {
            if (optionsItems.onOptionsItemSelected(item)) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Tools.longSnackbar(rootView, R.string.no_version_found);
        } catch (Exception exception) {
            Tools.longSnackbar(rootView, exception.getMessage());
        }

        if (android.R.id.home == item.getItemId()) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (null != networkChangeReceiver) {
            super.unregisterReceiver(networkChangeReceiver);
        }
        if (null != progressBar) {
            progressBar.dismiss();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        SocketIOHolder.stop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (null == networkChangeReceiver) {
            return;
        }
        registerReceiverWithVersions();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void registerReceiverWithVersions()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            super.registerReceiver(networkChangeReceiver, intentFilter);
            return;
        }
        super.registerReceiver(networkChangeReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    public void createNavigationDrawer()
    {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Class<?> activityClass = R.id.nav_activ_server == menuItem.getItemId()
                                     ? ActivServerActivity.class
                                     : R.id.nav_thermostat == menuItem.getItemId()
                                       ? ThermostatControllerActivity.class
                                       : null;

            if (null != activityClass) {
                Intent intent = new Intent(AppController.this, activityClass);
                startActivity(intent);
            }

            return true;
        });
    }

    public void createScrollView()
    {

    }

    public void createToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
    }

    public void showMainView()
    {
        setContentView(R.layout.activity_main);
        createToolbar();
        createScrollView();
        createNavigationDrawer();
        initializeSocketioListeners();
        selectedView = MAIN_VIEW;
    }

    public void showErrorView()
    {
        setContentView(R.layout.activity_error);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatButton refreshButton = findViewById(R.id.refreshBtn);
        refreshButton.setOnClickListener(v -> recreate());
        selectedView = ERROR_VIEW;
    }

    public void showNoPrefsView()
    {
        setContentView(R.layout.activity_no_prefs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button prefsButton = findViewById(R.id.noPrefsButton);
        prefsButton.setVisibility(View.INVISIBLE);
        selectedView = NO_PREFS_VIEW;
    }

    @Override
    public void onChange(Integer conStatus, String conStatusStr)
    {
        if (!routeErrorViews(conStatus) && selectedView.equals(ERROR_VIEW)) {
            Button prefsButton = findViewById(R.id.noPrefsButton);
            prefsButton.setVisibility(View.VISIBLE);
            prefsButton.setOnClickListener(view -> {
                Intent intent = new Intent(AppController.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    public void checkConnectivity()
    {
        Integer conStatus = NetworkUtil.getConnectivityStatus(this);
        routeViews(conStatus);
    }

    public boolean routeErrorViews(Integer state)
    {
        if (!PrefsManager.areTherePrefs()) {
            showNoPrefsView();
            Tools.longSnackbar(rootView, R.string.no_prefs);
            return true;
        }

        if (NetworkUtil.TYPE_NOT_CONNECTED == state) {
            showErrorView();
            if (!selectedView.equals(ERROR_VIEW)) {
                Tools.longSnackbar(rootView, R.string.no_wifi);
            }
            return true;
        }
        return false;
    }

    public void routeViews(Integer state)
    {
        if (routeErrorViews(state)) {
            return;
        }
        if (!selectedView.equals(MAIN_VIEW)) {
            showMainView();
        }
    }
}
