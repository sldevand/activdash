package fr.geringan.activdash.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.interfaces.IActionneurAdapter;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;


public class DimmerAdapter extends CommonNetworkAdapter<DimmerAdapter.ViewHolder> implements IActionneurAdapter {
    private List<DimmerDataModel> dataSet = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_dimmer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DimmerAdapter.ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    @Override
    protected void httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if ("404".equals(response)) return;

        JSONArray jsonArray = new JSONArray(response);
        dataSet.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            dataSet.add(new DimmerDataModel(json));
        }
    }

    @Override
    public void setEtat(DataModel dataModel) throws JSONException, IllegalAccessException {
        JSONObject obj = dataModel.getDataJSON();
        int iter = 0;
        String id = obj.getString("id");
        for (DimmerDataModel dm : dataSet) {
            if (dm.getDataJSON().getString("id").equalsIgnoreCase(id)) {
                dataSet.get(iter).setEtat( obj.getInt("etat"));
                notifyItemChanged(iter);
                return;
            }
            iter++;
        }
    }

    private void onDimmerChanged(final int progress, final DimmerDataModel dataModel, final String event) {
        dataModel.changeEtat(progress);
        SocketIOHolder.emit(event, dataModel);
    }

    public class ViewHolder extends CommonViewHolder<DimmerDataModel> {

        private TextView txtName;
        private SeekBar dimmer;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textDimmer);
            dimmer = itemView.findViewById(R.id.seekbarDimmer);
        }

        public void setData(final DimmerDataModel dimmerData) {

            txtName.setText(dimmerData.nom);
            dimmer.setProgress(dimmerData.getEtat());
            dimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        onDimmerChanged(seekBar.getProgress(), dimmerData, SocketIOHolder.EMIT_DIMMER);
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    onDimmerChanged(seekBar.getProgress(), dimmerData, SocketIOHolder.EMIT_DIMMERPERSIST);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //intentional empty body
                }
            });
        }
    }

}
