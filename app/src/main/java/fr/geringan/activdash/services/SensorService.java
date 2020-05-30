package fr.geringan.activdash.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.models.SensorDataModel;

public class SensorService extends AbstractService<SensorDataModel> {
    public SensorService(String url) {
        this.url = url;
    }

    protected void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            SensorDataModel sensorDataModel = new SensorDataModel(jsonObject);

            if (null != onGetResponseListener) {
                onGetResponseListener.onSuccess(sensorDataModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    String buildUrl() {
        return url;
    }
}
