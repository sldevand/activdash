package fr.geringan.activdash.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.GetHttp;

public class ScenariosService extends AbstractService<ScenarioDataModel> {

    private static final String SCENARIOS_GET_PREFILL = "scenarios/";
    private static final String TAG = "ScenariosService";

    public void get() {
        try {
            GetHttp getData = new GetHttp();
            getData.setOnResponseListener(this::onResponse);
            getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buildUrl());
        } catch (Exception e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    protected void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<ScenarioDataModel> scenarioDataModels = populateList(jsonArray);
            if (null != onGetListResponseListener) {
                onGetListResponseListener.onSuccess(scenarioDataModels);
            }
        } catch (Exception e) {
            if (null != onGetListResponseListener) {
                onGetListResponseListener.onError("Error in Scenarios Service " + e.getMessage());
            }
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
