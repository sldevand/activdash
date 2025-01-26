package fr.geringan.activdash.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class GraphsDataModel extends DataModel implements Parcelable {
    private static final String TAG = "GraphsDataModel";
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
    private Integer id;
    private String nom;
    private JSONArray data;

    public GraphsDataModel(JSONObject data) throws JSONException, IllegalAccessException {
        super(data);
    }

    private GraphsDataModel(Parcel in) {
        try {
            data = new JSONArray(in.readString());
            hydrateFromJSON();
        } catch (JSONException | IllegalAccessException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    //GETTERS
    public String getSensorId() {
        return sensor_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        } catch (JSONException | IllegalAccessException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }
}
