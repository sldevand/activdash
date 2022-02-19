package fr.geringan.activdash.fragments;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import fr.geringan.activdash.adapters.ScenarioAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

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
                new DividerItemDecoration(this.requireContext(),
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
        if (SocketIOHolder.socket == null) return;
        SocketIOHolder.socket
                .on("scenarioFeedback", this::scenarioFeedback);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResponseOk(String response, ProgressBar progBar, View v) {
        initializeSocketioListeners();
    }

    @Override
    public void onEmptyResponse(ProgressBar progBar, View v) {
        //intentional empty method
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void scenarioFeedback(Object... args) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONArray(args).getJSONObject(0);
            ScenarioDataModel dataModel = new ScenarioDataModel();
            dataModel.setDataJSON(jsonObject);
            onSocketioUpdate(dataModel);
        } catch (JSONException | IllegalAccessException | DataModelException e) {
            e.printStackTrace();
        }
    }

    public void onSocketioUpdate(ScenarioDataModel socketioScenario)
            throws JSONException, IllegalAccessException, DataModelException {

        if (getActivity() == null) throw new DataModelException("Activity is null");
        List<ScenarioDataModel> scenarios = adapter.getDataSet();
        if (scenarios == null) throw new DataModelException("Scenario dataModels is null");
        updateItemFromList(scenarios, socketioScenario);
    }

    private void updateItemFromList(List<ScenarioDataModel> scenarios, ScenarioDataModel socketioScenario)
            throws JSONException, IllegalAccessException {
        Integer idToChange = socketioScenario.getDataJSON().getInt("id");
        int iter = 0;
        for (ScenarioDataModel scenario : scenarios) {
            Integer id = scenario.getDataJSON().getInt("id");
            if (id.equals(idToChange)) {
                scenario.setDataJSON(socketioScenario.getDataJSON());
                final int finalIter = iter;
                Objects.requireNonNull(getActivity()).runOnUiThread(() ->
                        adapter.notifyItemChanged(finalIter)
                );
            }
            iter++;
        }
    }
}