package fr.geringan.activdash.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import fr.geringan.activdash.interfaces.HttpResponse;
import fr.geringan.activdash.models.DataModel;

public abstract class CommonNetworkAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> implements HttpResponse {

    public Context context;
    public ArrayList<? extends DataModel> dataSet = null;

    @NonNull
    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int i) {
        holder.setData(dataSet.get(i));
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

    protected abstract int httpToDataModel(String response) throws IllegalAccessException, JSONException;
}
