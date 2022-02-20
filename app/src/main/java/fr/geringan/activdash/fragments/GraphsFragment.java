package fr.geringan.activdash.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.GraphsAdapter;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.network.GetHttp;

public class GraphsFragment extends CommonNetworkFragment {

    public final static String PERIOD_TODAY = "today";
    public final static String PERIOD_YESTERDAY = "yesterday";
    public final static String PERIOD_WEEK = "week";
    public final static String PERIOD_MONTH = "month";
    public ProgressBar progressBar;
    protected String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/mesures/";
    protected String m_thermoAddress = m_baseAddress + "get-sensors/thermo";
    protected GraphsAdapter adapter;

    public static GraphsFragment newInstance(int position) {
        GraphsFragment fragment = new GraphsFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graphs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.list_graphs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new GraphsAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                this.requireContext(),
                DividerItemDecoration.VERTICAL
        );

        recyclerView.addItemDecoration(itemDecoration);
        progressBar = view.findViewById(R.id.progressGraph);

        FloatingActionButton fabToday = view.findViewById(R.id.fabToday);
        FloatingActionButton fabYesterday = view.findViewById(R.id.fabYesterday);
        FloatingActionButton fabWeek = view.findViewById(R.id.fabWeek);
        FloatingActionButton fabMonth = view.findViewById(R.id.fabMonth);

        fabToday.setOnClickListener(fabClickListener(PERIOD_TODAY));
        fabYesterday.setOnClickListener(fabClickListener(PERIOD_YESTERDAY));
        fabWeek.setOnClickListener(fabClickListener(PERIOD_WEEK));
        fabMonth.setOnClickListener(fabClickListener(PERIOD_MONTH));

        getCharts(PERIOD_TODAY);
    }

    public View.OnClickListener fabClickListener(final String period) {
        return v -> getCharts(period);
    }

    public void getCharts(final String period) {
        GetHttp getSensors = new GetHttp();

        getSensors.setOnResponseListener(response -> {
            adapter.clearDataSet();

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String sensorid = obj.getString("radioid");
                    callGraph(sensorid, period);
                }
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        });

        getSensors.execute(m_thermoAddress);

    }

    public void callGraph(String sensorid, String period) {
        String address = m_baseAddress + sensorid + "-" + period;
        execGetData(address, adapter, progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void initializeSocketioListeners() {
        //intentional empty method
    }

    @Override
    public void onResponseOk(String response, ProgressBar progBar, View v) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyResponse(ProgressBar progBar, View v) {
        progressBar.setVisibility(View.GONE);
    }
}
