package fr.geringan.activdash.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.utils.Utils;

import java.util.Arrays;

import fr.geringan.activdash.R;
import fr.geringan.activdash.fragments.ThermostatFragment;
import fr.geringan.activdash.network.SocketIOHolder;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ThermostatControllerActivity extends AppCompatActivity {

    protected ConnectivityManager cm;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat_controller);
        Utils.init(this);

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        createViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_thermostat_controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
/*
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();


            if (isConnected) {

                boolean socketOK = false;

                if (SocketIOHolder.socket == null) {
                    socketOK = SocketIOHolder.launch();
                } else {
                    socketOK = SocketIOHolder.start();
                }
                if (socketOK) {

                    initializeSocketioListeners();
                } else {

                   // progressBar.dismiss();
                    Snackbar.make(mViewPager, getResources().getString(R.string.node_connect_error)
                                    + PrefsManager.baseAddress + ":" + PrefsManager.nodePort,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            } else {
                setContentView(R.layout.activity_error);
                Toolbar toolbar = findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
            }
*/

    }

    public void createViewPager() {
        setContentView(R.layout.activity_thermostat_controller);

        Toolbar toolbar = findViewById(R.id.toolbarThermostat);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.containerThermostat);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tabsThermostat);
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
                    // if (progressBar != null) progressBar.dismiss();
                }
            });

            SocketIOHolder.socket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, "Node Timeout!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //if (progressBar != null) progressBar.dismiss();
                }
            });

            SocketIOHolder.socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Snackbar.make(mViewPager, "Node Déconnecté!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //if (progressBar != null) progressBar.dismiss();
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


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_thermostat_display, container, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return ThermostatFragment.newInstance(position);
                case 1:
                    return ThermostatFragment.newInstance(position);
                case 2:
                    return ThermostatFragment.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Thermostat";
                case 1:
                    return "Graphs";
                case 2:
                    return "Planification";
            }
            return null;
        }
    }
}
