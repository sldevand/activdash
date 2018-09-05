package fr.geringan.activdash.interfaces;

import org.json.JSONException;

import fr.geringan.activdash.models.DataModel;

public interface IActionneurAdapter {
    void setEtat(DataModel dataModel) throws JSONException, IllegalAccessException;
}
