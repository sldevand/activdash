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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.DimmerAdapter;
import fr.geringan.activdash.adapters.InterAdapter;
import fr.geringan.activdash.interfaces.IActionneurAdapter;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;


public class ActuatorsFragment extends CommonNetworkFragment {

    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/actionneurs/";
    public String m_interAddress = m_baseAddress + "inter";
    public String m_dimmerAddress = m_baseAddress + "dimmer";

    private InterAdapter interAdapter;
    private DimmerAdapter dimmerAdapter;
    private ProgressBar progressDimmer;
    private ProgressBar progressInter;


    public static ActuatorsFragment newInstance() {
        return new ActuatorsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_actuators, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView interRecyclerView = view.findViewById(R.id.listInters);
        interRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (null == interAdapter) interAdapter = new InterAdapter();
        interRecyclerView.setAdapter(interAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(Objects.requireNonNull(this.getContext()),
                        DividerItemDecoration.VERTICAL);
        interRecyclerView.addItemDecoration(itemDecoration);

        progressInter = view.findViewById(R.id.progressInter);
        interAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (interAdapter.getItemCount() > 0)
                    progressInter.setVisibility(View.GONE);
                else
                    progressInter.setVisibility(View.VISIBLE);
            }
        });

        RecyclerView dimmerRecyclerView = view.findViewById(R.id.listDimmers);
        dimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dimmerAdapter = new DimmerAdapter();
        dimmerRecyclerView.setAdapter(dimmerAdapter);
        dimmerRecyclerView.addItemDecoration(itemDecoration);


        progressDimmer = view.findViewById(R.id.progressDimmer);

        dimmerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {

                if (dimmerAdapter.getItemCount() > 0)
                    progressDimmer.setVisibility(View.GONE);
                else
                    progressDimmer.setVisibility(View.VISIBLE);
            }
        });

        execGetData(m_dimmerAddress, dimmerAdapter, progressDimmer);
        execGetData(m_interAddress, interAdapter, progressInter);

    }

    public void initializeSocketioListeners() {
        if (null == SocketIOHolder.socket) return;

        SocketIOHolder.socket.on(SocketIOHolder.EVENT_INTER, args -> {
            final InterDataModel dataModel = new InterDataModel();
            adapterHydrate(dataModel,interAdapter,args);
        });

        SocketIOHolder.socket.on(SocketIOHolder.EVENT_DIMMER, args -> {
            final DimmerDataModel dataModel = new DimmerDataModel();
            adapterHydrate(dataModel,dimmerAdapter,args);
        });

    }

    private void adapterHydrate(final DataModel dataModel, IActionneurAdapter adapter , Object args){
        if (null == getActivity()) return;

        JSONObject jsonObject;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                jsonObject = new JSONArray(args).getJSONObject(0);
                dataModel.setDataJSON(jsonObject);
                getActivity().runOnUiThread(() -> {
                    try {
                        adapter.setEtat(dataModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (JSONException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onResponseOk(String response) {
        initializeSocketioListeners();
    }
}