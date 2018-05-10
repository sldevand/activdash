package fr.geringan.activdash.models;


import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ScenarioDataModel extends DataModel implements Comparable<ScenarioDataModel> {

    protected String nom;
    private String scenarioid;

    public ScenarioDataModel() {
        super();
    }

    public ScenarioDataModel(JSONObject dataJSON) throws JSONException, IllegalAccessException {
        super(dataJSON);
    }

    //GETTERS
    public String getNom() {
        return this.nom;
    }

    //SETTERS
    public void setNom(String nom) {
        this.nom = nom;
    }


    @Override
    public int compareTo(@NonNull ScenarioDataModel dm) {
        return this.scenarioid.compareTo(dm.scenarioid);
    }
}
