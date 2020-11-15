package fr.geringan.activdash.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ThermostatDataModel extends DataModel {

    private String nom;

    private String planningName;
    private double consigne;
    private double delta;
    private int etat;
    private int pwr;

    private ModeDataModel mode;

    public ThermostatDataModel() {
        super();
    }

    public ThermostatDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    @Override
    protected void hydrateFromJSON() throws JSONException, IllegalAccessException {
        this.consigne = this.dataJSON.getDouble("consigne");
        this.delta = this.dataJSON.getDouble("delta");
        this.etat = this.dataJSON.getInt("etat");
        this.nom = this.dataJSON.getString("nom");
        this.pwr = this.dataJSON.getInt("pwr");

        if (dataJSON.has("planningName")) {
            if (dataJSON.get("planningName").equals("Aucun")) {

                this.planningName = "Aucun";
            } else {
                JSONObject objPlanningName = dataJSON.getJSONObject("planningName");
                if (objPlanningName.has("nom")) {
                    this.planningName = objPlanningName.getString("nom");
                }
            }

        }

        if (dataJSON.has("mode")) {
            JSONObject objMode = dataJSON.getJSONObject("mode");
            this.mode = new ModeDataModel(objMode);
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public ModeDataModel getMode() {
        return mode;
    }

    public void setMode(ModeDataModel mode) {
        this.mode = mode;
    }

    public String getPlanningName() {
        return planningName;
    }

    public void setPlanningName(String planningName) {
        this.planningName = planningName;
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

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public int getPwr() {
        return pwr;
    }

    public void setPwr(int pwr) {
        this.pwr = pwr;
    }
}
