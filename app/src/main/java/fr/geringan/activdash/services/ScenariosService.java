package fr.geringan.activdash.services;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.GetHttp;

public class ScenariosService extends AbstractService<ScenarioDataModel> {

    private static final String SCENARIOS_GET_PREFILL = "scenarios/";

    public void get() {
        try {
            GetHttp getData = new GetHttp();
            getData.setOnResponseListener(this::onResponse);
            getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buildUrl());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<ScenarioDataModel> scenarioDataModels = populateList(jsonArray);
            if (null != onGetListResponseListener) {
                onGetListResponseListener.onSuccess(scenarioDataModels);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private List<ScenarioDataModel> populateList(JSONArray jsonArray) throws JSONException, IllegalAccessException {
        List<ScenarioDataModel> scenarioDataModels = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            ScenarioDataModel scenario = new ScenarioDataModel(obj);
            scenarioDataModels.add(scenario);
        }

        return scenarioDataModels;
    }

    protected String buildUrl() {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SCENARIOS_GET_PREFILL;
    }
}
