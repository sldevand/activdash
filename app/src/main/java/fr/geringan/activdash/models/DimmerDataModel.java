package fr.geringan.activdash.models;

import org.json.JSONException;
import org.json.JSONObject;

public class DimmerDataModel extends DataModel {

    private int etat;
    public String nom;

    public DimmerDataModel() {
        super();
    }

    public DimmerDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    @Override
    protected void hydrateFromJSON() throws JSONException {
        this.etat = this.dataJSON.getInt("etat");
        this.nom = this.dataJSON.getString("nom");
    }

    @Override
    protected void writeToJSON() {
        //intentional empty method
    }

    public void changeEtat(int etat) {
        try {
            this.etat = etat;
            dataJSON.put("etat", etat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }
}


