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
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.network.GetHttp;

public class SensorsService extends AbstractService<SensorDataModel>{
    private static final String SENSORS_GET_PREFILL = "mesures/get-sensors";
    private static final String TAG = "SensorsService";

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
            List<SensorDataModel> sensorDataList = populateList(jsonArray);
            if (null != onGetListResponseListener) {
                onGetListResponseListener.onSuccess(sensorDataList);
            }
        } catch (JSONException | IllegalAccessException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
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

    protected String buildUrl() {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SENSORS_GET_PREFILL;
    }
}
