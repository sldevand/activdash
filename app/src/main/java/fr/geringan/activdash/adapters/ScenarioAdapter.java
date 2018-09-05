package fr.geringan.activdash.adapters;

import android.support.annotation.NonNull;
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
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class ScenarioAdapter extends CommonNetworkAdapter<ScenarioAdapter.ViewHolder> {
    private List<ScenarioDataModel> dataSet = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.setData(dataSet.get(i));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_scenario, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }


    @Override
    public void httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if ("404".equals(response)) return;

        JSONObject scenarios = new JSONObject(response);
        dataSet.clear();
        Iterator itr = scenarios.keys();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            ScenarioDataModel dm = new ScenarioDataModel((JSONObject) scenarios.get(key));
            dataSet.add(dm);
        }
        Collections.sort(dataSet);
    }

    public class ViewHolder extends CommonViewHolder<ScenarioDataModel> {
        private TextView txtName;
        private ImageView img;
        private ScenarioDataModel currentDataModel;

        ViewHolder(final View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textScenario);
            img = itemView.findViewById(R.id.imageScenario);
            itemView.setOnClickListener(view ->
                    SocketIOHolder.emit(SocketIOHolder.EMIT_SCENARIO, currentDataModel));
        }

        @Override
        public void setData(ScenarioDataModel scenario) {
            String text = scenario.getNom();
            txtName.setText(text);
            img.setImageResource(getImgRes(text));
            currentDataModel = scenario;
        }

        private Integer getImgRes(final String text){
            switch (text.toLowerCase()) {
                case "tv":
                    return R.mipmap.ic_tv;
                case "film":
                    return R.mipmap.ic_movie;
                case "coucher":
                    return R.mipmap.ic_bed;
                default:
                    return R.mipmap.ic_play;
            }
        }
    }
}
