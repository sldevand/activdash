package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.DimmerAdapter;
import fr.geringan.activdash.adapters.InterAdapter;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.IActionneurAdapter;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

public class ActuatorsFragment extends CommonNetworkFragment {
    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/actionneurs/";
    public String m_interAddress = m_baseAddress + "inter";
    public String m_dimmerAddress = m_baseAddress + "dimmer";

    private InterAdapter interAdapter;
    private DimmerAdapter dimmerAdapter;


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
        ProgressBar progressInter = view.findViewById(R.id.progressInter);
        if (null == interAdapter) {
            interAdapter = (InterAdapter) populateRecyclerView(interRecyclerView, new InterAdapter());
        }
        execGetData(m_interAddress, interAdapter, progressInter);

        RecyclerView dimRecyclerView = view.findViewById(R.id.listDimmers);
        ProgressBar progressDimmer = view.findViewById(R.id.progressDimmer);
        if (null == dimmerAdapter) {
            dimmerAdapter = (DimmerAdapter) populateRecyclerView(dimRecyclerView, new DimmerAdapter());
        }
        execGetData(m_dimmerAddress, dimmerAdapter, progressDimmer);
    }

    public void initializeSocketioListeners() {
        if (null == SocketIOHolder.socket) return;

        SocketIOHolder.socket.on(SocketIOHolder.EVENT_INTER, args -> {
            adapterHydrate(new InterDataModel(), interAdapter, args);
        });

        SocketIOHolder.socket.on(SocketIOHolder.EVENT_DIMMER, args -> {
            adapterHydrate(new DimmerDataModel(), dimmerAdapter, args);
        });
    }

    private void adapterHydrate(final DataModel dataModel, IActionneurAdapter adapter, Object args) {
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

    protected RecyclerView.Adapter<?> populateRecyclerView(
            RecyclerView recyclerView,
            RecyclerView.Adapter<?> adapter
    ) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                this.requireContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(itemDecoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        });
        recyclerView.setAdapter(adapter);

        return adapter;
    }

    @Override
    public void onResponseOk(String response, ProgressBar progBar, View v) {
        progBar.setVisibility(View.GONE);
        initializeSocketioListeners();
    }

    @Override
    public void onEmptyResponse(ProgressBar progBar, View v) {
        progBar.setVisibility(View.GONE);
    }
}