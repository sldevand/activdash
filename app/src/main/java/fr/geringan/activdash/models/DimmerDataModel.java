package fr.geringan.activdash.models;

import org.json.JSONException;
import org.json.JSONObject;

public class DimmerDataModel extends DataModel {

    public int etat;
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
    protected void writeToJSON() {}

    public void changeEtat(int etat) {
        try {
            this.etat = etat;
            dataJSON.put("etat", etat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


