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
import java.util.Collections;
import java.util.List;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.ScenarioDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.providers.IconProvider;
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

        JSONArray scenarios = new JSONArray(response);
        dataSet.clear();
        for (int i = 0; i < scenarios.length(); i++) {
            JSONObject json = scenarios.getJSONObject(i);
            dataSet.add(new ScenarioDataModel(json));
        }
        Collections.sort(dataSet);
    }

    public List<ScenarioDataModel> getDataSet() {
        return dataSet;
    }

    public class ViewHolder extends CommonViewHolder<ScenarioDataModel> {
        private TextView txtName;
        private ImageView img;
        private TextView txtRemainingTime;
        private ImageView imgStop;
        private ScenarioDataModel currentDataModel;

        ViewHolder(final View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textScenario);
            img = itemView.findViewById(R.id.imageScenario);
            txtRemainingTime = itemView.findViewById(R.id.textRemaining);
            imgStop = itemView.findViewById(R.id.imageScenarioStop);
            imgStop.setOnClickListener(view ->
                    SocketIOHolder.emit(SocketIOHolder.EMIT_SCENARIO_STOP, currentDataModel));
            itemView.setOnClickListener(view ->
                    SocketIOHolder.emit(SocketIOHolder.EMIT_SCENARIO, currentDataModel));
        }

        @Override
        public void setData(ScenarioDataModel scenario) {
            String text = scenario.getNom();
            txtName.setText(text);
            img.setImageResource(IconProvider.getIconFromName(text));
            toggleStatus(scenario);
            currentDataModel = scenario;
        }

        private void toggleStatus(final ScenarioDataModel scenario) {
            if (scenario.getStatus().equals("play")) {
                if (null != scenario.getRemainingTime()) {
                    int seconds = scenario.getRemainingTime() / 1000;
                    Integer minutes = seconds / 60;
                    Integer hours = minutes / 60;
                    txtRemainingTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds % 60));
                }
                txtRemainingTime.setVisibility(View.VISIBLE);
                imgStop.setVisibility(View.VISIBLE);
            } else {
                txtRemainingTime.setVisibility(View.INVISIBLE);
                imgStop.setVisibility(View.INVISIBLE);
            }
        }
    }
}
