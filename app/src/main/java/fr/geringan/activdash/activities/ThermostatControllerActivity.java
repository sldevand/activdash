package fr.geringan.activdash.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.utils.Utils;

import fr.geringan.activdash.R;
import fr.geringan.activdash.fragments.ThermostatFragment;

public class ThermostatControllerActivity extends RootActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat_controller);
        Utils.init(this);
        createMainView();
    }

    public void createMainView() {
        createToolbar();
        TabLayout tabLayout = findViewById(R.id.tabsThermostat);
        tabLayout.setupWithViewPager(createViewPager());
        initializeSocketioListeners();
    }

    public void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarThermostat);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public ViewPager createViewPager(){
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.containerThermostat);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        return mViewPager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_thermostat_display, container, false);
        }
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                case 1:
                case 2:
                    return ThermostatFragment.newInstance();
                default:
                    return null;
            }
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
                default:
                    return null;
            }
        }
    }
}
