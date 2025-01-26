package fr.geringan.activdash.adapters;


import static java.lang.Float.parseFloat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.activities.GraphsActivity;
import fr.geringan.activdash.models.SensorDataModel;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class SensorAdapter extends CommonNetworkAdapter<SensorAdapter.ViewHolder> {

    private final List<SensorDataModel> dataSet = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_sensor, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    @Override
    public void httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if ("404".equals(response)) return;

        dataSet.clear();
        JSONArray jsonArray = new JSONArray(response);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            dataSet.add(new SensorDataModel(json));
        }
    }
    public List<SensorDataModel> getDataSet() {
        return dataSet;
    }

    public class ViewHolder extends CommonViewHolder<SensorDataModel> {
        public static final int EMPTY_DOUBLE = -255;
        private final TextView txtStatus;
        private final TextView txtName;
        private final TextView txtValue1;
        private final TextView txtValue2;
        private final TextView txtDate;
        private final ImageView img;

        private ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textNameCapteur);
            txtValue1 = itemView.findViewById(R.id.textValue1Capteur);
            txtValue2 = itemView.findViewById(R.id.textValue2Capteur);
            txtStatus = itemView.findViewById(R.id.textStatusCapteur);
            txtDate = itemView.findViewById(R.id.textLastmeasureCapteur);
            img = itemView.findViewById(R.id.imageCapteur);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, GraphsActivity.class);
                context.startActivity(intent);
            });
        }

        @Override
        public void setData(SensorDataModel capteur) {
            txtName.setText(capteur.getNom());
            displayActif(capteur.getActif());
            String valeur2 = capteur.getValeur2();
            Double value2 = valeur2.isEmpty() ? EMPTY_DOUBLE : Double.parseDouble(valeur2);
            displayIconAndValues(capteur.getRadioid(), capteur.getValeur1(), value2);
            txtDate.setText(capteur.getReleve());

            img.animate().rotationBy(360).setDuration(500).setStartDelay(300);
        }

        private void displayActif(int actif) {
            String txtActif="KO";
            txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            if (actif == 1) {
                txtActif = "OK";
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorOk));
            }
            txtStatus.setText(txtActif);
        }

        private void displayIconAndValues(String radioId, Double valeur1, Double valeur2) {

            String value1Units = "°C";
            String value2Units = "";

            if (radioId.contains("dht")) value2Units = "%RH";
            if (radioId.contains("tinfo")) {
                img.setImageResource(R.mipmap.ic_electricity);
                value1Units = "kWH";
                value2Units = "W";
            } else {
                img.setImageResource(R.mipmap.ic_thermometer);
            }

            DecimalFormat df = new DecimalFormat("0.#");
            String v1 = df.format(valeur1);
            String v2 = "";
            if (valeur2 != EMPTY_DOUBLE) {
                v2 = df.format(valeur2);
            }

            String val1 = v1 + " " + value1Units;
            String val2 = v2 + " " + value2Units;

            txtValue1.setText(val1);
            txtValue2.setText(val2);
        }
    }
}
