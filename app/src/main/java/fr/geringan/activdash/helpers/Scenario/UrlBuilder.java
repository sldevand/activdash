package fr.geringan.activdash.helpers.Scenario;

import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ScenarioDataModel;

public class UrlBuilder {

    private static final String SCENARIOS_GET_PREFILL = "scenarios/";
    private static final String SCENARIOS_GET_COMMAND_PREFILL = "scenarios/command/";

    public static String buildUrl(ScenarioDataModel scenarioDataModel) {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SCENARIOS_GET_PREFILL + scenarioDataModel.getId();
    }

    public static String buildCommandUrl(ScenarioDataModel scenarioDataModel) {
        return PrefsManager.baseAddress + "/"
                + PrefsManager.entryPointAddress + "/"
                + SCENARIOS_GET_COMMAND_PREFILL + scenarioDataModel.getId();
    }
}
