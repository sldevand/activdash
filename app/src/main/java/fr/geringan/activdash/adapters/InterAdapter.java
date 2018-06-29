package fr.geringan.activdash.adapters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class InterAdapter extends CommonNetworkAdapter<InterAdapter.ViewHolder> {
    private ArrayList<InterDataModel> dataSet = null;

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
        View view = inflater.inflate(R.layout.item_inter, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterAdapter.ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    @Override
    public void httpToDataModel(String response) throws IllegalAccessException {
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
                    dataSet.add(new InterDataModel(json));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setEtat(InterDataModel dataModel) throws JSONException, IllegalAccessException {
        JSONObject obj = dataModel.getDataJSON();
        int iter = 0;
        try {
            String id = obj.getString("id");
            for (InterDataModel dm : dataSet) {

                if (dm.getDataJSON().getString("id").equalsIgnoreCase(id)) {

                    dataSet.get(iter).setEtat(obj.getInt("etat"));

                    notifyItemChanged(iter);

                }
                iter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends CommonViewHolder<InterDataModel> {

        private TextView txtName;
        private ImageView img;
        private InterDataModel currentDataModel;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int etat;
                    if (currentDataModel.etat == 1)
                        etat = 0;
                    else
                        etat = 1;

                    try {
                        currentDataModel.changeEtat(etat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    SocketIOHolder.emit(SocketIOHolder.EMIT_INTER, currentDataModel);

                }
            });

            txtName = itemView.findViewById(R.id.textInter);
            img = itemView.findViewById(R.id.imageInter);

        }

        @Override
        public void setData(InterDataModel interData) {
            txtName.setText(interData.nom);
            setEtat(interData);
            currentDataModel = interData;
        }

        void setEtat(InterDataModel interData) {
            int resource = R.mipmap.ic_power_off;
            if (interData.etat == 1) resource = R.mipmap.ic_power_on;
            img.setImageResource(resource);
        }
    }
}
