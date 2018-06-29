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

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;


public class DimmerAdapter extends CommonNetworkAdapter<DimmerAdapter.ViewHolder> {
    private ArrayList<DimmerDataModel> dataSet = null;

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else
            return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_dimmer, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DimmerAdapter.ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    protected void httpToDataModel(String response) throws IllegalAccessException {

        if ("404".equals(response)) {
            return;
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataSet = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    dataSet.add(new DimmerDataModel(json));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setEtat(DimmerDataModel dataModel) throws JSONException, IllegalAccessException {
        JSONObject obj = dataModel.getDataJSON();
        int iter = 0;
        try {
            String id = obj.getString("id");
            for (DimmerDataModel dm : dataSet) {

                if (dm.getDataJSON().getString("id").equalsIgnoreCase(id)) {

                    dataSet.get(iter).etat = obj.getInt("etat");

                    notifyDataSetChanged();
                    return;

                }
                iter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            dimmer.setProgress(dimmerData.etat);

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
