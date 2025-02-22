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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.ScenarioAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.SocketIOHolder;

public class ScenariosFragment extends CommonNetworkFragment
{

    private static final String TAG = "ScenariosFragment";
    public String m_address = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/scenarios/";
    private ScenarioAdapter adapter;

    public static ScenariosFragment newInstance()
    {
        return new ScenariosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_scenarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.listScenarios);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ScenarioAdapter();
        recyclerView.setAdapter(adapter);
        final ProgressBar progress = view.findViewById(R.id.progressScenario);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                progress.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });

        execGetData(m_address, adapter, progress);
    }

    @Override
    public void initializeSocketioListeners()
    {
        if (SocketIOHolder.socket == null) {
            return;
        }
        SocketIOHolder.socket
                .on("scenarioFeedback", this::scenarioFeedback);
    }

    @Override
    public void onResponseOk(String response, ProgressBar progBar, View v)
    {
        initializeSocketioListeners();
    }

    @Override
    public void onEmptyResponse(ProgressBar progBar, View v)
    {
        //intentional empty method
    }

    public void scenarioFeedback(Object... args)
    {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONArray(args).getJSONObject(0);
            ScenarioDataModel dataModel = new ScenarioDataModel();
            dataModel.setDataJSON(jsonObject);
            onSocketioUpdate(dataModel);
        } catch (JSONException | IllegalAccessException | DataModelException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    public void onSocketioUpdate(ScenarioDataModel socketioScenario)
            throws JSONException, IllegalAccessException, DataModelException
    {

        if (getActivity() == null) {
            throw new DataModelException("Activity is null");
        }
        List<ScenarioDataModel> scenarios = adapter.getDataSet();
        if (scenarios == null) {
            throw new DataModelException("Scenario dataModels is null");
        }
        updateItemFromList(scenarios, socketioScenario);
    }

    private void updateItemFromList(List<ScenarioDataModel> scenarios, ScenarioDataModel socketioScenario) throws JSONException, IllegalAccessException
    {
        Integer idToChange = socketioScenario.getDataJSON().getInt("id");
        int iter = 0;
        for (ScenarioDataModel scenario : scenarios) {
            Integer id = scenario.getDataJSON().getInt("id");
            if (id.equals(idToChange)) {
                scenario.setDataJSON(socketioScenario.getDataJSON());
                final int finalIter = iter;
                requireActivity().runOnUiThread(() ->
                        adapter.notifyItemChanged(finalIter)
                );
            }
            iter++;
        }
    }
}