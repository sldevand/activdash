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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.SensorAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

public class SensorsFragment extends CommonNetworkFragment {
    private static final String TAG = "SensorsFragment";
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
                new DividerItemDecoration(this.requireContext(),
                        DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator()))
                .setSupportsChangeAnimations(false);
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

    @Override
    public void onResponseOk(String response, ProgressBar progBar, View v) {
        initializeSocketioListeners();
    }

    @Override
    public void onEmptyResponse(ProgressBar progBar, View v) {
        //intentional empty method
    }

    @Override
    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket == null) return;
        SocketIOHolder.socket
                .on("thermo", this::sensorsDataUpdate)
                .on("teleinfo", this::sensorsDataUpdate)
                .on("chaudiere", this::sensorsDataUpdate);
    }

    public void sensorsDataUpdate(Object... args) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONArray(args).getJSONObject(0);
            SensorDataModel dataModel = new SensorDataModel();
            dataModel.setDataJSON(jsonObject);
            onSocketioUpdate(dataModel);
        } catch (JSONException | IllegalAccessException | DataModelException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
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
                requireActivity().runOnUiThread(() ->
                        adapter.notifyItemChanged(finalIter)
                );
            }
            iter++;
        }
    }

}