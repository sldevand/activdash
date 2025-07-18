package fr.geringan.activdash.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.GraphsDataModel;
import fr.geringan.activdash.network.CommonGetHttp;
import fr.geringan.activdash.viewholders.CommonViewHolder;

public class GraphsAdapter extends CommonNetworkAdapter<GraphsAdapter.ViewHolder> {
    private final ArrayList<GraphsDataModel> dataSet = new ArrayList<>();

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void clearDataSet() {
        dataSet.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_graph, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(this.dataSet.get(position));
    }

    @Override
    public void httpToDataModel(String response) throws IllegalAccessException, JSONException {
        if (CommonGetHttp.HTTP_NOT_FOUND.equals(response)) {
            return;
        }

        JSONObject graph = new JSONObject(response);
        GraphsDataModel dm = new GraphsDataModel(graph);

        if (dm.getId() > 0) {
            dataSet.add(dm);
        }
    }

    public static class ViewHolder extends CommonViewHolder<GraphsDataModel> implements OnChartValueSelectedListener {
        public static final String HORODATAGE = "horodatage";
        public static final String TEMPERATURE = "temperature";
        private final AppCompatTextView title;
        private final AppCompatTextView radioId;
        private final LineChart chart;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleGraph);
            radioId = itemView.findViewById(R.id.radioidGraph);
            chart = itemView.findViewById(R.id.chart);
            chart.setVisibility(View.GONE);
        }

        @Override
        public void setData(GraphsDataModel graphDM) {
            title.setText(graphDM.getNom());
            radioId.setText(graphDM.getSensorId());
            chartPopulate(graphDM);
        }

        private void chartPopulate(GraphsDataModel graphDM) {
            LineData lineData;
            LineDataSet lineDataSet;

            try {
                lineDataSet = fromJSONArrayToLineDataSet(graphDM.getData());
                lineDataSet.setDrawCircles(false);
                lineDataSet.setLineWidth(4);
                lineDataSet.setColor(itemView.getResources().getColor(R.color.colorAccent, null));
                lineDataSet.setDrawValues(false);

                lineData = new LineData(lineDataSet);

                int axisTextColor = itemView.getResources().getColor(R.color.white, null);
                float axisTextSize = 12f;
                YAxis axisLeft = chart.getAxisLeft();
                axisLeft.setTextSize(axisTextSize);
                axisLeft.setTextColor(axisTextColor);

                XAxis xAxis = chart.getXAxis();
                xAxis.setTextSize(axisTextSize);
                xAxis.setTextColor(axisTextColor);

                chart.setData(lineData);
                chart.setHighlightPerTapEnabled(true);
                chart.getAxisRight().setEnabled(false);
                chartSetLimits(lineDataSet);
                chart.setVisibility(View.VISIBLE);
                chart.invalidate();

            } catch (Exception e) {
                Snackbar.make(itemView, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            }
        }

        private void chartSetLimits(LineDataSet lineDataSet) {
            float minTemp = 10.0f;
            float maxTemp = 30.0f;
            float yMin = lineDataSet.getYMin();
            float yMax = lineDataSet.getYMax();
            if (yMin < minTemp) {
                minTemp = yMin - 10.0f;
            }
            if (yMax > maxTemp) {
                maxTemp = yMax + 10.0f;
            }

            chart.getAxisLeft().setAxisMinimum(minTemp);
            chart.getAxisLeft().setAxisMaximum(maxTemp);
        }

        private LineDataSet fromJSONArrayToLineDataSet(JSONArray datas) throws JSONException, ParseException {
            List<Entry> entries = new ArrayList<>();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            JSONObject premierObj = datas.getJSONObject(0);
            String depart = premierObj.getString(HORODATAGE);

            long timeDepart = formatDateFromStringToLong(depart, dateFormat);

            for (int i = 0; i < datas.length(); i++) {
                JSONObject obj = datas.getJSONObject(i);

                String heure = obj.getString(HORODATAGE);
                float time = (formatDateFromStringToLong(heure, dateFormat) - timeDepart) / 3600000f;

                entries.add(new Entry(time, Float.parseFloat(obj.getString(TEMPERATURE))));
            }

            return new LineDataSet(entries, "Températures");
        }

        private long formatDateFromStringToLong(String date, SimpleDateFormat dateFormat) throws ParseException {
            Date dateDepart = dateFormat.parse(date);
            return Objects.requireNonNull(dateDepart).getTime();
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            e.describeContents();
        }

        @Override
        public void onNothingSelected() {
            //intentional empty body
        }
    }
}
