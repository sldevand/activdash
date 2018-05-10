package fr.geringan.activdash.models;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public abstract class DataModel {


    JSONObject dataJSON;

    protected DataModel(JSONObject data) throws JSONException, IllegalAccessException {

        setDataJSON(data);
    }

    public DataModel() {
    }

    protected void hydrateFromJSON() throws JSONException, IllegalAccessException {
        Class c = this.getClass();
        Field[] fields = c.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            String type = field.getType().getSimpleName();

            if (!name.equals("CREATOR")) {
                if (type.equalsIgnoreCase("int") && dataJSON.has(name)) {
                    field.setInt(this, dataJSON.getInt(name));
                } else if (dataJSON.has(name)) {
                    field.set(this, dataJSON.get(name));
                }
            }
        }
    }

    protected void writeToJSON() throws JSONException, IllegalAccessException {

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            dataJSON.put(field.getName(), field.get(this));
        }
    }

    //GETTERS
    public JSONObject getDataJSON() throws JSONException, IllegalAccessException {

        writeToJSON();
        return this.dataJSON;
    }

    //SETTERS
    public void setDataJSON(JSONObject dataJSON) throws JSONException, IllegalAccessException {
        this.dataJSON = dataJSON;
        hydrateFromJSON();
    }

    @Override
    public String toString() {

        StringBuilder descriptif = new StringBuilder();

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            String name = field.getName();
            String type = field.getType().getSimpleName();
            String value = null;

            try {
                if (field.get(this) != null) {
                    value = field.get(this).toString();
                }
                if (type.equals("JSONArray")) value = "JSONArray [...]";
                descriptif.append(type).append(" ").append(name).append(" = ").append(value).append("\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
        return descriptif.toString();

    }


}
