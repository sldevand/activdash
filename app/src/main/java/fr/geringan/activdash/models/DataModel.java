package fr.geringan.activdash.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public abstract class DataModel {

    @SuppressWarnings("WeakerAccess")
    protected JSONObject dataJSON;

    public DataModel() {
    }

    protected DataModel(JSONObject data) throws JSONException, IllegalAccessException {
        setDataJSON(data);
    }

    protected void hydrateFromJSON() throws JSONException, IllegalAccessException {
        Class c = this.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            String type = field.getType().getSimpleName();
            if ("CREATOR".equals(name)) {
                continue;
            }
            if (type.equalsIgnoreCase("int") && dataJSON.has(name)) {
                field.setInt(this, dataJSON.getInt(name));
            } else if (dataJSON.has(name)) {
                field.set(this, dataJSON.get(name));
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

    public JSONObject getDataJSON() throws JSONException, IllegalAccessException {
        writeToJSON();

        return this.dataJSON;
    }

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
                if (null != field.get(this)) {
                    value = field.get(this).toString();
                }
                if ("JSONArray".equals(type)) value = "JSONArray [...]";
                descriptif.append(type).append(" ").append(name).append(" = ").append(value).append("\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return descriptif.toString();
    }
}
