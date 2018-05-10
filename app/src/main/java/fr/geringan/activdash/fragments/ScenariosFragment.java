package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.ScenarioAdapter;
import fr.geringan.activdash.utils.PrefsManager;

public class ScenariosFragment extends CommonNetworkFragment {

    public String m_address = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/scenarios/";
    protected ScenarioAdapter adapter;
    RecyclerView recyclerView;

    public static ScenariosFragment newInstance(int tag) {
        ScenariosFragment fragment = new ScenariosFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scenarios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.listScenarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ScenarioAdapter();
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        final ProgressBar progress = view.findViewById(R.id.progressScenario);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (adapter.getItemCount() > 0)
                    progress.setVisibility(View.GONE);
                else
                    progress.setVisibility(View.VISIBLE);
            }
        });

        execGetData(m_address, adapter, progress);
    }

    @Override
    public void initializeSocketioListeners() {

    }

    @Override
    public void onResponseOk(String response) {

    }
}