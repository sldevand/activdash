package fr.geringan.activdash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.utils.Utils;

import java.util.Arrays;

import fr.geringan.activdash.activities.ActivServerActivity;
import fr.geringan.activdash.activities.SettingsActivity;
import fr.geringan.activdash.activities.ThermostatControllerActivity;
import fr.geringan.activdash.fragments.ActionneursFragment;
import fr.geringan.activdash.fragments.CapteursFragment;
import fr.geringan.activdash.fragments.GraphsFragment;
import fr.geringan.activdash.fragments.ScenariosFragment;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AppController extends AppCompatActivity {

    protected SectionsPagerAdapter mSectionsPagerAdapter;
    protected ViewPager mViewPager;
    protected ProgressDialog progressBar;
    protected ConnectivityManager cm;
    protected boolean isConnected;
    protected DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.init(this);

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected) {
            createViewPager();
            createNavigationDrawer();
        } else {
            createErrorView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        // SocketIOHolder.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketIOHolder.stop();
    }

    @Override
    public void onResume() {
        super.onResume();

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        PrefsManager.launch(this);

        if (PrefsManager.areTherePrefs()) {

            if (isConnected) {
                progressBar = new ProgressDialog(this);
                progressBar.setCancelable(true);
                progressBar.setMessage("Connection à node...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setIndeterminate(true);
                progressBar.show();

                boolean socketOK = false;

                if (SocketIOHolder.socket == null) {
                    socketOK = SocketIOHolder.launch();
                } else {
                    socketOK = SocketIOHolder.start();
                }

                //  progressBar.dismiss();
                initializeSocketioListeners();
                /*} else {

                    progressBar.dismiss();
                    Snackbar.make(mViewPager, getResources().getString(R.string.node_connect_error)
                                    + PrefsManager.baseAddress + ":" + PrefsManager.nodePort,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }*/

            } else {
                setContentView(R.layout.activity_error);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
            }

        } else {

            setContentView(R.layout.activity_no_prefs);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            Button prefsButton = findViewById(R.id.noPrefsButton);

            prefsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AppController.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void createNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent intent = null;
                        switch (menuItem.getItemId()) {

                            case R.id.nav_activ_server:
                                intent = new Intent(AppController.this, ActivServerActivity.class);
                                startActivity(intent);
                                return true;

                            case R.id.nav_thermostat:
                                intent = new Intent(AppController.this, ThermostatControllerActivity.class);
                                startActivity(intent);
                                return true;
                        }

                        return true;
                    }
                });
    }

    public void createViewPager() {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);

        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_drawer);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void createErrorView() {
        setContentView(R.layout.activity_error);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket != null) {


            SocketIOHolder.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, "Node Connecté!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (progressBar != null) progressBar.dismiss();
                }
            });

            SocketIOHolder.socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, "Node Timeout!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (progressBar != null) progressBar.dismiss();
                }
            });

            SocketIOHolder.socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, "Node Déconnecté!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (progressBar != null) progressBar.dismiss();
                }
            });

            SocketIOHolder.socket.on("message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, Arrays.toString(args), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
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
                    return ActionneursFragment.newInstance(position);
                case 2:
                    return CapteursFragment.newInstance();
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
                    return "Scenarios";
                case 1:
                    return "Actionneurs";
                case 2:
                    return "Capteurs";
                case 3:
                    return "Graphs";
                default:
                    return null;
            }

        }
    }
}
