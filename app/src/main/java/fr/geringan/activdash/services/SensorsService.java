package fr.geringan.activdash.services;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.GetHttp;

public class SensorsService {
    private static final String SENSORS_GET_PREFILL = "mesures/get-sensors";

    private OnGetResponseListener onGetResponseListener;

    public void get() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(this::onResponse);
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buildSensorsUrl());
    }

    private void onResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            List<SensorDataModel> sensorDataList = populateList(jsonArray);
            if (null != onGetResponseListener) {
                onGetResponseListener.onSuccess(sensorDataList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private List<SensorDataModel> populateList(JSONArray jsonArray) throws JSONException, IllegalAccessException {
        List<SensorDataModel> sensorDataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            SensorDataModel sensor = new SensorDataModel(obj);
            sensorDataList.add(sensor);
        }

        return sensorDataList;
    }

    private String buildSensorsUrl() {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SENSORS_GET_PREFILL;
    }

    public void setOnGetResponseListener(OnGetResponseListener onGetResponseListener) {
        this.onGetResponseListener = onGetResponseListener;
    }

    public interface OnGetResponseListener {
        void onSuccess(List<SensorDataModel> sensorDataList);
    }
}
