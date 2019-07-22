package fr.geringan.activdash.models;


import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ScenarioDataModel extends DataModel implements Comparable<ScenarioDataModel> {

    protected String nom;
    private String id;
    protected String status;
    protected Integer remainingTime;

    public ScenarioDataModel() {
        super();
    }

    public ScenarioDataModel(JSONObject dataJSON) throws JSONException, IllegalAccessException {
        super(dataJSON);
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    @Override
    public int compareTo(@NonNull ScenarioDataModel dm) {
        return this.id.compareTo(dm.id);
    }
}
