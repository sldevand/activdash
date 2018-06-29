package fr.geringan.activdash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class ScenarioAdapter extends CommonNetworkAdapter<ScenarioAdapter.ViewHolder> {
    private ArrayList<ScenarioDataModel> dataSet;

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else
            return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.setData(dataSet.get(i));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_scenario, parent, false);
        context = view.getContext();

        return new ViewHolder(view);
    }


    @Override
    public int httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if (response.equals("404")) {
            return 404;
        }

        JSONObject scenarios = new JSONObject(response);

        dataSet = new ArrayList<>();

        Iterator itr = scenarios.keys();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            ScenarioDataModel dm = new ScenarioDataModel((JSONObject) scenarios.get(key));
            dataSet.add(dm);
        }
        // Sorting
        Collections.sort(dataSet);

        return 200;
    }

    public class ViewHolder extends CommonViewHolder<ScenarioDataModel> {
        private TextView txtName;
        private ImageView img;
        private ScenarioDataModel currentDataModel;


        ViewHolder(final View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.textScenario);


            img = itemView.findViewById(R.id.imageScenario);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SocketIOHolder.emit(SocketIOHolder.EMIT_SCENARIO, currentDataModel);
                }
            });
        }

        @Override
        public void setData(ScenarioDataModel scenario) {
            String text = scenario.getNom();
            txtName.setText(text);

            int imgRes;
            switch (text.toLowerCase()) {
                case "tv":
                    imgRes = R.mipmap.ic_tv;
                    break;
                case "film":
                    imgRes = R.mipmap.ic_movie;
                    break;
                case "coucher":
                    imgRes = R.mipmap.ic_bed;
                    break;
                default:
                    imgRes = R.mipmap.ic_play;
                    break;
            }

            img.setImageResource(imgRes);

            currentDataModel = scenario;
        }
    }
}
