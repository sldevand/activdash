package fr.geringan.activdash.services;

import org.json.JSONObject;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;

public class ScenarioService extends AbstractService<ScenarioDataModel> {

    private static final String SCENARIOS_GET_PREFILL = "scenarios/";

    protected String id;

    public ScenarioService(String id) {
        this.id = id;
    }

    protected void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            ScenarioDataModel scenarioDataModel = new ScenarioDataModel(jsonObject);

            if (null != onGetResponseListener) {
                onGetResponseListener.onSuccess(scenarioDataModel);
            }
        } catch (Exception e) {
            if (null != onGetResponseListener) {
                onGetResponseListener.onError("Error in Scenario Service " + e.getMessage());
            }
        }
    }

    protected String buildUrl() {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SCENARIOS_GET_PREFILL + id;
    }
}
