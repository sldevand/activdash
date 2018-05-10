package fr.geringan.activdash.adapters;


import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.CapteurDataModel;
import fr.geringan.activdash.viewholders.CommonViewHolder;

import static java.lang.Float.parseFloat;

public class CapteurAdapter extends CommonNetworkAdapter<CapteurAdapter.ViewHolder> {


    private ArrayList<CapteurDataModel> dataSet = null;

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else
            return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_capteur, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));

    }

    @Override
    public int httpToDataModel(String response) throws IllegalAccessException {
        if (response.equals("404")) {
            return 404;
        }
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(response);
            dataSet = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    dataSet.add(new CapteurDataModel(json));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return 200;
    }

    public ArrayList<CapteurDataModel> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<CapteurDataModel> dataSet) {
        this.dataSet = dataSet;
    }

    public class ViewHolder extends CommonViewHolder<CapteurDataModel> {
        public TextView txtStatus;
        TextView txtName;
        TextView txtValue1;
        TextView txtValue2;
        TextView txtDate;
        ImageView img;
        CapteurDataModel _currentCapteur;


        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textNameCapteur);
            txtValue1 = itemView.findViewById(R.id.textValue1Capteur);
            txtValue2 = itemView.findViewById(R.id.textValue2Capteur);
            txtStatus = itemView.findViewById(R.id.textStatusCapteur);
            txtDate = itemView.findViewById(R.id.textLastmeasureCapteur);
            img = itemView.findViewById(R.id.imageCapteur);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, _currentCapteur.getNom(), Snackbar.LENGTH_SHORT).show();
                }
            });


        }

        @Override
        public void setData(CapteurDataModel capteur) {
            txtName.setText(capteur.getNom());
            displayActif(capteur.getActif());
            displayIconAndValues(capteur.getRadioid(), capteur.getValeur1(), capteur.getValeur2());
            txtDate.setText(capteur.getReleve());

            img.animate().rotationBy(360).setDuration(500).setStartDelay(300);
            _currentCapteur = capteur;
        }

        public void displayActif(int actif) {
            String txtActif = "";
            if (actif == 1) {
                txtActif = "OK";
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorOk));
            } else {
                txtActif = "KO";
                txtStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
            txtStatus.setText(txtActif);
        }

        public void displayIconAndValues(String radioId, String valeur1, String valeur2) {

            String value1Units = "°C";
            String value2Units = "";
            if (radioId.contains("dht11")) value2Units = "%RH";
            if (radioId.contains("tinfo")) {
                img.setImageResource(R.mipmap.ic_electricity);
                value1Units = "kWH";
                value2Units = "W";
            } else {

                img.setImageResource(R.mipmap.ic_thermometer);
            }

            DecimalFormat df = new DecimalFormat("0.#");

            if (!valeur1.isEmpty()) {
                float val1 = parseFloat(valeur1);
                valeur1 = df.format(val1);
            }

            if (!valeur2.isEmpty()) {
                float val2 = parseFloat(valeur2);
                valeur2 = df.format(val2);
            }

            String val1 = valeur1 + " " + value1Units;
            String val2 = valeur2 + " " + value2Units;

            txtValue1.setText(val1);
            txtValue2.setText(val2);
        }


    }


}
