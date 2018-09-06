package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.ScenarioAdapter;
import fr.geringan.activdash.helpers.PrefsManager;

public class ScenariosFragment extends CommonNetworkFragment {

    public String m_address = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/scenarios/";
    private ScenarioAdapter adapter;

    public static ScenariosFragment newInstance() {
        return new ScenariosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scenarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.listScenarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ScenarioAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(  Objects.requireNonNull(this.getContext()),
                                            DividerItemDecoration.VERTICAL);
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
        //intentional empty method
    }

    @Override
    public void onResponseOk(String response) {
        //intentional empty method
    }
}