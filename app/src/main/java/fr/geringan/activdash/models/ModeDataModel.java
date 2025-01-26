package fr.geringan.activdash.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ModeDataModel extends DataModel {

    private String nom;
    private double consigne;
    private double delta;

    ModeDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    @Override
    protected void hydrateFromJSON() throws JSONException {
        this.nom = this.dataJSON.getString("nom");
        this.consigne = this.dataJSON.getDouble("consigne");
        this.delta = this.dataJSON.getDouble("delta");
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getConsigne() {
        return consigne;
    }

    public void setConsigne(double consigne) {
        this.consigne = consigne;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }
}
