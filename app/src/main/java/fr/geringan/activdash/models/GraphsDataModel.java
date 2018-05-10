package fr.geringan.activdash.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GraphsDataModel extends DataModel implements Parcelable {


    //PARCELABLE STUFF
    public static final Parcelable.Creator<GraphsDataModel> CREATOR
            = new Parcelable.Creator<GraphsDataModel>() {
        public GraphsDataModel createFromParcel(Parcel in) {
            return new GraphsDataModel(in);
        }

        public GraphsDataModel[] newArray(int size) {
            return new GraphsDataModel[size];
        }
    };
    private String sensor_id;
    private String id;
    private String nom;
    private JSONArray data;

    GraphsDataModel() {
        super();
    }

    public GraphsDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    private GraphsDataModel(Parcel in) {
        try {
            data = new JSONArray(in.readString());
            hydrateFromJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //GETTERS
    public String getSensor_id() {
        return sensor_id;
    }

    //SETTERS
    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        try {
            writeToJSON();
            parcel.writeString(dataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
