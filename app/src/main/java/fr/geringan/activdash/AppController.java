package fr.geringan.activdash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.utils.Utils;

import java.util.Arrays;

import fr.geringan.activdash.activities.ActivServerActivity;
import fr.geringan.activdash.activities.RootActivity;
import fr.geringan.activdash.activities.SettingsActivity;
import fr.geringan.activdash.activities.ThermostatControllerActivity;
import fr.geringan.activdash.fragments.ActuatorsFragment;
import fr.geringan.activdash.fragments.GraphsFragment;
import fr.geringan.activdash.fragments.ScenariosFragment;
import fr.geringan.activdash.fragments.SensorsFragment;
import fr.geringan.activdash.interfaces.SocketIOEventsListener;
import fr.geringan.activdash.network.NetworkChangeReceiver;
import fr.geringan.activdash.network.NetworkUtil;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;
import fr.geringan.activdash.utils.Tools;
import io.socket.client.Socket;

public class AppController extends RootActivity implements NetworkChangeReceiver.OnNetworkChangedListener {

    private static final Integer MAIN_VIEW = 1;
    private static final Integer NO_PREFS_VIEW = 2;
    private static final Integer ERROR_VIEW = 3;
    protected ProgressDialog progressBar;
    protected DrawerLayout mDrawerLayout;
    protected NetworkChangeReceiver networkChangeReceiver;
    protected IntentFilter intentFilter;

    private Integer selectedView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.init(this);
        selectedView = 0;
        checkConnectivity();
        if (null == networkChangeReceiver) {
            intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.wifi.STATE_CHANGE");
            networkChangeReceiver = new NetworkChangeReceiver();
            networkChangeReceiver.setOnNetworkChangedListener(this);
            super.registerReceiver(networkChangeReceiver, intentFilter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(AppController.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != networkChangeReceiver) super.unregisterReceiver(networkChangeReceiver);
        if (null != progressBar) progressBar.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketIOHolder.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != networkChangeReceiver)
            super.registerReceiver(networkChangeReceiver, intentFilter);
    }



    public void createNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Intent intent;
            switch (menuItem.getItemId()) {
                case R.id.nav_activ_server:
                    intent = new Intent(AppController.this, ActivServerActivity.class);
                    startActivity(intent);
                    break;

                case R.id.nav_thermostat:
                    intent = new Intent(AppController.this, ThermostatControllerActivity.class);
                    startActivity(intent);
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    public void createViewPager() {
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

    }

    public void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer);
    }

    public void showMainView() {
        setContentView(R.layout.activity_main);
        createToolbar();
        createViewPager();
        createNavigationDrawer();
        createProgressBar();

        initializeSocketioListeners();
        selectedView = MAIN_VIEW;
    }

    public void showErrorView() {
        setContentView(R.layout.activity_error);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppCompatButton refreshButton = findViewById(R.id.refreshBtn);
        refreshButton.setOnClickListener(v -> recreate());

        selectedView = ERROR_VIEW;
    }

    public void showNoPrefsView() {
        setContentView(R.layout.activity_no_prefs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button prefsButton = findViewById(R.id.noPrefsButton);
        prefsButton.setVisibility(View.INVISIBLE);

        selectedView = NO_PREFS_VIEW;
    }

    @Override
    public void onChange(Integer conStatus, String conStatusStr) {
        if (!routeErrorViews(conStatus) && selectedView.equals(ERROR_VIEW)) {
            Button prefsButton = findViewById(R.id.noPrefsButton);
            prefsButton.setVisibility(View.VISIBLE);
            prefsButton.setOnClickListener(view -> {
                Intent intent = new Intent(AppController.this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    public void checkConnectivity() {
        Integer conStatus = NetworkUtil.getConnectivityStatus(this);
        routeViews(conStatus);
    }

    public boolean routeErrorViews(Integer state) {
        PrefsManager.launch(this);
        if (!PrefsManager.areTherePrefs()) {
            showNoPrefsView();
            Tools.longSnackbar(rootView, R.string.no_prefs);
            return true;
        }

        if (NetworkUtil.TYPE_NOT_CONNECTED == state) {
            showErrorView();
            if (!selectedView.equals(ERROR_VIEW))
                Tools.longSnackbar(rootView, R.string.no_wifi);
            return true;
        }
        return false;
    }

    public void routeViews(Integer state) {

        if (routeErrorViews(state)) return;
        if (!selectedView.equals(MAIN_VIEW)) {
            showMainView();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return ScenariosFragment.newInstance();
                case 1:
                    return ActuatorsFragment.newInstance();
                case 2:
                    return SensorsFragment.newInstance();
                case 3:
                    return GraphsFragment.newInstance(position);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.scenarios);
                case 1:
                    return getString(R.string.actuators);
                case 2:
                    return getString(R.string.sensors);
                case 3:
                    return getString(R.string.graphs);
                default:
                    return null;
            }
        }
    }
}
