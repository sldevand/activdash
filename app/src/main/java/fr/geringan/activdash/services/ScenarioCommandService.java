package fr.geringan.activdash.services;

import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;

public class ScenarioCommandService extends AbstractService<ScenarioDataModel> {
    private static final String SCENARIOS_GET_COMMAND_PREFILL = "scenarios/command/";

    protected String id;

    public ScenarioCommandService(String id) {
        this.id = id;
    }

    protected void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            ScenarioDataModel scenarioDataModel = new ScenarioDataModel(jsonObject);

            if (null != onGetResponseListener) {
                onGetResponseListener.onSuccess(scenarioDataModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected String buildUrl() {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SCENARIOS_GET_COMMAND_PREFILL + id;
    }

}
