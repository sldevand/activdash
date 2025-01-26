package fr.geringan.activdash.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;

import fr.geringan.activdash.interfaces.HttpResponse;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public abstract class CommonNetworkAdapter<V extends CommonViewHolder> extends RecyclerView.Adapter<V> implements HttpResponse {

    public Context context;
    protected final ArrayList<? extends DataModel> dataSet = new ArrayList<>();

    @NonNull
    @Override
    abstract public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull V holder, int i) {
        holder.setData(dataSet.get(i));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void setHttpResponse(String response) throws IllegalAccessException, JSONException {
        httpToDataModel(response);
        notifyDataSetChanged();
    }

    protected abstract void httpToDataModel(String response) throws IllegalAccessException, JSONException;
}
