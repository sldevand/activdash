package fr.geringan.activdash.models;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorDataModel extends DataModel {

    private String nom;
    private Double valeur1;
    private String valeur2;
    private String radioid;
    private String releve;
    private int actif;

    public SensorDataModel() {
        super();
    }

    public SensorDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    //GETTERS
    public String getNom() {
        return nom;
    }
    public Double getValeur1() {
        return valeur1;
    }
    public String getValeur2() {
        return valeur2;
    }
    public String getRadioid() {
        return radioid;
    }
    public int getActif() {
        return actif;
    }
    public String getReleve() {
        return releve;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getNom();
    }
}
