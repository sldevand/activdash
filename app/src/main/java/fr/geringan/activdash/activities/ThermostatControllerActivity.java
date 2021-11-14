package fr.geringan.activdash.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.Map;

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
        ViewPager2 viewPager = createViewPager();
        Map<Integer, String> tabTitlesMap = this.getTabTitlesMap();
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String text = null != tabTitlesMap.get(position)
                    ? tabTitlesMap.get(position)
                    : "Tab " + position;
            tab.setText(text);
        }).attach();

        initializeSocketioListeners();
    }

    protected Map<Integer, String> getTabTitlesMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, getString(R.string.title_activity_thermostat_controller));

        return map;
    }

    public void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarThermostat);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public ViewPager2 createViewPager() {
        ThermostatControllerActivity.SectionsPagerAdapter mSectionsPagerAdapter =
                new ThermostatControllerActivity.SectionsPagerAdapter(
                        getSupportFragmentManager(),
                        getLifecycle()
                );
        ViewPager2 viewPager = findViewById(R.id.containerThermostat);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        return viewPager;
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

    public static class SectionsPagerAdapter extends FragmentStateAdapter {
        private SectionsPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return ThermostatFragment.newInstance();
            }
            return new Fragment();
        }

        @Nullable
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Thermostat";
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}
