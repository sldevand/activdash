package fr.geringan.activdash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import fr.geringan.activdash.viewholders.CommonViewHolder;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.interfaces.HttpResponse;

public abstract class CommonNetworkAdapter<V extends CommonViewHolder> extends RecyclerView.Adapter<V> implements HttpResponse {

    public Context context;
    private ArrayList<? extends DataModel> dataSet=null;


    @Override
    public V onCreateViewHolder( ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int i){
        holder.setData(dataSet.get(i));
    }

    @Override
    public int getItemCount() {
        if(dataSet != null){
            return dataSet.size();
        }else
            return 0;
    }

    @Override
    public void setHttpResponse(String response) throws IllegalAccessException, JSONException {
        httpToDataModel(response);
        notifyDataSetChanged();
    }

    protected abstract void httpToDataModel(String response) throws IllegalAccessException, JSONException;
}
