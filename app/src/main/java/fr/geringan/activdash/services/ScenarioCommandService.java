package fr.geringan.activdash.services;

import org.json.JSONObject;

import fr.geringan.activdash.models.ScenarioDataModel;

public class ScenarioCommandService extends AbstractService<ScenarioDataModel> {
    public ScenarioCommandService(String url) {
        this.url = url;
    }

    protected void onResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (null != onGetResponseListener && jsonObject.has("error")) {
                onGetResponseListener.onError(jsonObject.getString("error"));
                return;
            }
            ScenarioDataModel scenarioDataModel = new ScenarioDataModel(jsonObject);

            if (null != onGetResponseListener) {
                onGetResponseListener.onSuccess(scenarioDataModel);
            }
        } catch (Exception e) {
            if (null != onGetResponseListener) {
                onGetResponseListener.onError("Error in Scenario Command Service " + e.getMessage());
            }
        }
    }

    protected String buildUrl() {
        return url;
    }
}
