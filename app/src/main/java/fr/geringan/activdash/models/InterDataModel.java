package fr.geringan.activdash.models;


import org.json.JSONException;
import org.json.JSONObject;


public class InterDataModel extends DataModel {

    public String nom;
    public int etat;
    protected String id;
    protected String module;
    protected String protocole;
    protected String adresse;
    protected String type;
    protected String radioid;
    protected String categorie;


    public InterDataModel() {
        super();
    }

    public InterDataModel(JSONObject dataJSON) throws JSONException, IllegalAccessException {
        super(dataJSON);
    }

    public void changeEtat(int etat) throws JSONException, IllegalAccessException {
        setEtat(etat);
        this.writeToJSON();
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

}
