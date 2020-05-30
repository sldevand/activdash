package fr.geringan.activdash.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import fr.geringan.activdash.interfaces.HttpResponse;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public abstract class CommonNetworkAdapter<V extends CommonViewHolder> extends RecyclerView.Adapter<V> implements HttpResponse {

    public Context context;
    private ArrayList<? extends DataModel> dataSet = null;

    @NonNull
    @Override
    public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int i) {
        Objects.requireNonNull(holder).setData(dataSet.get(i));
    }

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else
            return 0;
    }

    @Override
    public void setHttpResponse(String response) throws IllegalAccessException, JSONException {
        httpToDataModel(response);
        notifyDataSetChanged();
    }

    protected abstract void httpToDataModel(String response) throws IllegalAccessException, JSONException;
}
