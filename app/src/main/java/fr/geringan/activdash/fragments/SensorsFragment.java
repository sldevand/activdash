package fr.geringan.activdash.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.SensorAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

public class SensorsFragment extends CommonNetworkFragment {

    public String m_address = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/mesures/get-sensors";
    private SensorAdapter adapter;

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        ProgressBar progress = initProgressBar(view);
        execGetData(m_address, adapter, progress);
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.listSensors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SensorAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(Objects.requireNonNull(this.getContext()),
                        DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private ProgressBar initProgressBar(View view) {
        final ProgressBar progress = view.findViewById(R.id.progress);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (adapter.getItemCount() > 0) {
                    progress.setVisibility(View.GONE);
                } else {
                    progress.setVisibility(View.VISIBLE);
                }
            }
        });
        return progress;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResponseOk(String response) {
        initializeSocketioListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket == null) return;
        SocketIOHolder.socket
                .on("thermo", this::sensorsDataUpdate)
                .on("teleinfo", this::sensorsDataUpdate)
                .on("chaudiere", this::sensorsDataUpdate);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sensorsDataUpdate(Object... args) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONArray(args).getJSONObject(0);
            SensorDataModel dataModel = new SensorDataModel();
            dataModel.setDataJSON(jsonObject);
            onSocketioUpdate(dataModel);
        } catch (JSONException | IllegalAccessException | DataModelException e) {
            e.printStackTrace();
        }
    }

    public void onSocketioUpdate(SensorDataModel socketioSensor)
            throws JSONException, IllegalAccessException, DataModelException {
        if (getActivity() == null) throw new DataModelException("Activity is null");
        List<SensorDataModel> sensors = adapter.getDataSet();
        if (sensors == null) throw new DataModelException("Sensor dataModels is null");
        updateItemFromList(sensors, socketioSensor);
    }

    private void updateItemFromList(List<SensorDataModel> sensors, SensorDataModel socketioSensor)
            throws JSONException, IllegalAccessException {
        Integer idToChange = socketioSensor.getDataJSON().getInt("id");
        int iter = 0;
        for (SensorDataModel sensor : sensors) {
            Integer id = sensor.getDataJSON().getInt("id");
            if (id.equals(idToChange)) {
                sensor.setDataJSON(socketioSensor.getDataJSON());
                final int finalIter = iter;
                Objects.requireNonNull(getActivity()).runOnUiThread(() ->
                        adapter.notifyItemChanged(finalIter)
                );
            }
            iter++;
        }
    }

}