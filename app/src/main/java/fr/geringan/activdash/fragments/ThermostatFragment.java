package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.dialogs.ModeDialogFragment;
import fr.geringan.activdash.dialogs.PlanDialogFragment;
import fr.geringan.activdash.helpers.PrefsManager;
import fr.geringan.activdash.models.ModeDataModel;
import fr.geringan.activdash.models.ThermostatDataModel;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.providers.BoilerState;
import fr.geringan.activdash.providers.BoilerStateProvider;
import fr.geringan.activdash.providers.ModeImage;
import fr.geringan.activdash.providers.ModeImageProvider;

public class ThermostatFragment extends CommonNetworkFragment {

    public static final double THT_CHANGE_VALUE = 0.5;

    protected String m_baseAddress = PrefsManager.apiAdress + "/thermostat";
    protected String m_planifAddress = PrefsManager.apiAdress + "/thermostat/planifname/";
    protected String m_modesAddress = PrefsManager.apiAdress + "/thermostat/mode/";

    protected String m_sensorAddress = PrefsManager.apiAdress + "/mesures/get-sensor24thermid1";
    private AppCompatTextView txtMinus = null;
    private AppCompatTextView txtPlus = null;
    private AppCompatTextView txtConsigne = null;
    private AppCompatTextView txtThermometre = null;
    private AppCompatTextView txtThermostatMode = null;
    private AppCompatTextView txtThermostatPlan = null;
    private LinearLayout llThermostatPlan = null;
    private LinearLayout llThermostatMode = null;
    private AppCompatTextView txtThermostatEtat = null;
    private AppCompatImageView imgThermostatEtat = null;
    private AppCompatImageView imgThermostatPwr = null;
    private AppCompatImageView imgThermostatMode = null;
    private ThermostatDataModel thermostat;
    private CardView cvThermostatEtat;

    private ProgressBar progressBar;

    public static ThermostatFragment newInstance() {
        ThermostatFragment fragment = new ThermostatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void disableControls() {
        progressBar.setVisibility(View.VISIBLE);
        txtMinus.setVisibility(View.GONE);
        txtPlus.setVisibility(View.GONE);
        llThermostatPlan.setVisibility(View.GONE);
        llThermostatMode.setVisibility(View.GONE);
    }

    private boolean enableControls() {
        progressBar.setVisibility(View.GONE);
        txtMinus.setVisibility(View.VISIBLE);
        txtPlus.setVisibility(View.VISIBLE);
        llThermostatPlan.setVisibility(View.VISIBLE);
        llThermostatMode.setVisibility(View.VISIBLE);

        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thermostat_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtConsigne = view.findViewById(R.id.consigne_thermostat);
        txtMinus = view.findViewById(R.id.minus_thermostat);
        txtPlus = view.findViewById(R.id.plus_thermostat);
        txtThermometre = view.findViewById(R.id.value_thermometer);
        txtThermostatEtat = view.findViewById(R.id.txtThermostatEtat);
        txtThermostatPlan = view.findViewById(R.id.txtThermostatPlan);
        llThermostatPlan = view.findViewById(R.id.layoutThermostatPlan);
        txtThermostatMode = view.findViewById(R.id.txtThermostatMode);
        llThermostatMode = view.findViewById(R.id.layoutThermostatMode);
        imgThermostatEtat = view.findViewById(R.id.imgThermostatEtat);
        imgThermostatPwr = view.findViewById(R.id.imgThermostatPwr);
        imgThermostatMode = view.findViewById(R.id.imgThermostatMode);
        cvThermostatEtat = view.findViewById(R.id.cvThermostatEtat);
        progressBar = view.findViewById(R.id.progressTher);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getThermostat();
        getThermostatSensor();
        initializeSocketioListeners();
        initClickEvents();
        getThermostatPlanif();
        getThermostatModes();
    }

    public void getThermostat() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    updateThermostatDisplays(obj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        getData.execute(m_baseAddress);
    }

    public void getThermostatSensor() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    updateSensorDisplay(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        getData.execute(m_sensorAddress);
    }

    public void getThermostatPlanif() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray plannings = new JSONArray(response);
                llThermostatPlan.setOnClickListener(v -> openPlanList(plannings.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        getData.execute(m_planifAddress);
    }

    public void getThermostatModes() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(response -> {
            try {
                JSONArray modes = new JSONArray(response);
                llThermostatMode.setOnClickListener(v -> openModeList(modes.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        getData.execute(m_modesAddress);
    }


    public void updateSensorDisplay(JSONObject obj) throws JSONException {
        if (obj.has("valeur1")) {
            txtThermometre.setText(obj.getString("valeur1"));
        }
    }

    public void updateThermostatDisplays(JSONObject obj) throws JSONException, IllegalAccessException {
        if (obj == null) {
            throw new JSONException("updateThermostatDisplay : JSON Object is null !!");
        }

        createThermostatDataModel(obj);

        ModeDataModel mode = thermostat.getMode();
        BoilerState boiler = BoilerStateProvider.getBoilerState(thermostat.getEtat());


        imgThermostatEtat.setColorFilter(ContextCompat.getColor(getView().getContext(), boiler.getColor()));
        txtThermostatEtat.setText(boiler.getEtat());

        if (thermostat.getPwr() == 0 ) {
            imgThermostatPwr.setColorFilter(ContextCompat.getColor(getView().getContext(), R.color.colorAccent));
            imgThermostatPwr.setVisibility(View.VISIBLE);
        } else {
            imgThermostatPwr.setVisibility(View.INVISIBLE);
        }

        txtConsigne.setText(String.valueOf(thermostat.getConsigne()));
        txtThermostatPlan.setText(thermostat.getPlanningName());

        ModeImage modeImg = ModeImageProvider.getModeImage(mode.getNom());

        imgThermostatMode.setImageResource(modeImg.getImg());
        imgThermostatMode.setColorFilter(ContextCompat.getColor(getView().getContext(), modeImg.getColor()));
        txtThermostatMode.setText(mode.getNom());
    }

    private void createThermostatDataModel(JSONObject obj) throws JSONException, IllegalAccessException {
        if (null == thermostat) {
            thermostat = new ThermostatDataModel(obj);
        } else {
            thermostat.setDataJSON(obj);
        }
    }

    private void initClickEvents() {
        txtPlus.setOnClickListener(v -> setConsigne(THT_CHANGE_VALUE));
        txtConsigne.setOnClickListener(v -> SocketIOHolder.emit(SocketIOHolder.EMIT_THT_REFRESH, ""));
        txtMinus.setOnClickListener(v -> setConsigne(-THT_CHANGE_VALUE));
        cvThermostatEtat.setOnLongClickListener(v -> togglePwr());
    }

    private boolean togglePwr() {
        int pwrStat = thermostat.getPwr() == 1 ? 0 : 1;
        SocketIOHolder.emit(SocketIOHolder.EMIT_THT_SET_PWR, String.valueOf(pwrStat));

        return false;
    }

    private void openModeList(String modes) {
        final FragmentManager fm = getFragmentManager();
        final ModeDialogFragment dialog = ModeDialogFragment.newInstance(modes);

        if (null == fm) return;

        dialog.setSelectionListener(mode -> {
            if (mode.has("id")) {
                SocketIOHolder.emitNoControl(SocketIOHolder.EMIT_THT_UPDATE_PLAN, "0");
                new Handler().postDelayed(
                        () -> {
                            try {
                                SocketIOHolder.emitNoControl(SocketIOHolder.EMIT_THT_MODE, mode.getString("id"));
                                disableControls();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        300);
            }
        });
        dialog.show(fm, "dialog");
    }

    private void openPlanList(String plannings) {
        final FragmentManager fm = getFragmentManager();
        final PlanDialogFragment dialog = PlanDialogFragment.newInstance(plannings);

        if (null == fm || null == dialog) return;

        dialog.setSelectionListener(planning -> {
            if (planning.has("id")) {
                SocketIOHolder.emit(SocketIOHolder.EMIT_THT_UPDATE_PLAN, planning.getString("id"));
                disableControls();
            }
        });
        dialog.show(fm, "dialog");
    }

    private void setConsigne(double changeValue) {
        double consigne = thermostat.getConsigne();
        consigne += changeValue;
        thermostat.setConsigne(consigne);
        SocketIOHolder.emit(SocketIOHolder.EMIT_THT_CONS, thermostat);
    }

    @Override
    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket != null) {

            SocketIOHolder.socket
                    .off(SocketIOHolder.EVENT_THERMOSTAT)
                    .off(SocketIOHolder.EVENT_BOILER)
                    .on(SocketIOHolder.EVENT_THERMOSTAT,
                            args -> onSocketIOReceive(SocketIOHolder.EVENT_THERMOSTAT, args))
                    .on(SocketIOHolder.EVENT_BOILER,
                            args -> onSocketIOReceive(SocketIOHolder.EVENT_BOILER, args))
                    .on(SocketIOHolder.EVENT_PLAN_SAVE,
                            args -> onSocketIOReceive(SocketIOHolder.EVENT_PLAN_SAVE, args))
                    .on(SocketIOHolder.EVENT_MODE_SAVE,
                            args -> onSocketIOReceive(SocketIOHolder.EVENT_MODE_SAVE, args))
                    .on(SocketIOHolder.EVENT_THER_GET_PWR,
                            args -> onSocketIOReceive(SocketIOHolder.EVENT_THER_GET_PWR, args));
        }
    }

    public boolean onSocketIOReceive(final String event, Object... args) {

        if (event.equals(SocketIOHolder.EVENT_MODE_SAVE)
                || event.equals(SocketIOHolder.EVENT_PLAN_SAVE)) {
            getActivity().runOnUiThread(this::enableControls);
            return true;
        }

        try {
            return eventsDispatch(event, args);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean eventsDispatch(String event, Object... args) throws JSONException {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT
                || null == getActivity()) {
            return false;
        }

        JSONArray jsonArray = new JSONArray(args);
        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject obj = jsonArray.getJSONObject(i);
            getActivity().runOnUiThread(() -> {
                try {
                    eventDispatch(event, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        return true;
    }

    private void eventDispatch(String event, JSONObject obj) throws JSONException, IllegalAccessException {
        switch (event) {
            case SocketIOHolder.EVENT_THER_GET_PWR:
            case SocketIOHolder.EVENT_THERMOSTAT:
                updateThermostatDisplays(obj);
                break;
            case SocketIOHolder.EVENT_BOILER:
                updateSensorDisplay(obj);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResponseOk(String response) {
        //intentional empty method
    }
}
