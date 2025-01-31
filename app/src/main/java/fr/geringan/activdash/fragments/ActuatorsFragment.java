package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.DimmerAdapter;
import fr.geringan.activdash.adapters.InterAdapter;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.interfaces.IActionneurAdapter;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

public class ActuatorsFragment extends CommonNetworkFragment
{
    public static final String TAG = "ActuatorsFragment";
    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/actionneurs/";
    public String m_interAddress = m_baseAddress + "inter";
    public String m_dimmerAddress = m_baseAddress + "dimmer";

    private InterAdapter interAdapter;
    private DimmerAdapter dimmerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_actuators, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
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

    public void initializeSocketioListeners()
    {
        if (null == SocketIOHolder.socket) {
            return;
        }
        SocketIOHolder.socket.on(
                SocketIOHolder.EVENT_INTER,
                args -> adapterHydrate(new InterDataModel(), interAdapter, args)
        );
        SocketIOHolder.socket.on(
                SocketIOHolder.EVENT_DIMMER,
                args -> adapterHydrate(new DimmerDataModel(), dimmerAdapter, args)
        );
    }

    private void adapterHydrate(final DataModel dataModel, IActionneurAdapter adapter, Object args)
    {
        if (null == getActivity()) {
            return;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONArray(args).getJSONObject(0);
            dataModel.setDataJSON(jsonObject);
            getActivity().runOnUiThread(() -> {
                try {
                    adapter.setEtat(dataModel);
                } catch (JSONException | IllegalAccessException e) {
                    Log.e(TAG, Arrays.toString(e.getStackTrace()));
                }
            });
        } catch (JSONException | IllegalAccessException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    protected RecyclerView.Adapter<?> populateRecyclerView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter)
    {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {});
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    @Override
    public void onResponseOk(String response, @NonNull ProgressBar progBar, View v)
    {
        progBar.setVisibility(View.GONE);
        initializeSocketioListeners();
    }

    @Override
    public void onEmptyResponse(@NonNull ProgressBar progBar, View v)
    {
        progBar.setVisibility(View.GONE);
    }
}