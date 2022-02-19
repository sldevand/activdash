package fr.geringan.activdash.adapters;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.interfaces.IActionneurAdapter;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class InterAdapter extends CommonNetworkAdapter<InterAdapter.ViewHolder> implements IActionneurAdapter {
    private List<InterDataModel> dataSet = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_inter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterAdapter.ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    @Override
    public void httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if ("404".equals(response)) return;

        JSONArray jsonArray = new JSONArray(response);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            dataSet.add(new InterDataModel(json));
        }
    }

    @Override
    public void setEtat(DataModel dataModel) throws JSONException, IllegalAccessException {
        JSONObject obj = dataModel.getDataJSON();
        int iter = 0;
        String id = obj.getString("id");
        for (InterDataModel dm : dataSet) {
            if (dm.getDataJSON().getString("id").equalsIgnoreCase(id)) {
                dataSet.get(iter).setEtat(obj.getInt("etat"));
                notifyItemChanged(iter);
            }
            iter++;
        }
    }

    public static class ViewHolder extends CommonViewHolder<InterDataModel> {

        private TextView txtName;
        private ImageView img;
        private InterDataModel currentDataModel;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textInter);
            img = itemView.findViewById(R.id.imageInter);

            itemView.setOnClickListener(view -> {
                int state = 1 == currentDataModel.etat ? 0 : 1;
                try {
                    currentDataModel.changeEtat(state);
                    SocketIOHolder.emit(SocketIOHolder.EMIT_INTER, currentDataModel);
                } catch (JSONException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void setData(InterDataModel interData) {
            txtName.setText(interData.nom);
            setEtat(interData);
            currentDataModel = interData;
        }

        private void setEtat(InterDataModel interData) {
            int resource = R.mipmap.ic_power_off;
            if (interData.etat == 1) resource = R.mipmap.ic_power_on;
            img.setImageResource(resource);
        }
    }
}
