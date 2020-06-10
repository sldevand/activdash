package fr.geringan.activdash.helpers.Sensor;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.SensorDataModel;

public class UrlBuilder {

    private static final String SENSOR_GET_PREFILL = "mesures/get-";

    public static String buildUrl(SensorDataModel sensorDataModel) {
        return PrefsManager.baseAddress + "/" + PrefsManager.entryPointAddress + "/" +
                SENSOR_GET_PREFILL + sensorDataModel.getRadioid();
    }
}
