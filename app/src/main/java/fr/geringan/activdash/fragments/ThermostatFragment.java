package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.models.ModeDataModel;
import fr.geringan.activdash.models.ThermostatDataModel;
import fr.geringan.activdash.network.GetHttp;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;
import io.socket.emitter.Emitter;

public class ThermostatFragment extends CommonNetworkFragment {

    public static final double THT_CHANGE_VALUE = 0.5;

    protected String m_baseAddress = PrefsManager.apiAdress + "/thermostat";
    protected String m_sensorAddress = PrefsManager.apiAdress + "/mesures/get-sensor24thermid1";

    AppCompatTextView txtMinus = null;
    AppCompatTextView txtPlus = null;
    AppCompatTextView txtConsigne = null;
    AppCompatTextView txtThermometre = null;
    AppCompatTextView txtThermostatMode = null;
    AppCompatTextView txtThermostatPlan = null;
    AppCompatTextView txtThermostatEtat = null;

    AppCompatImageView imgThermostatEtat = null;
    AppCompatImageView imgThermostatMode = null;

    ThermostatDataModel thermostat;
    ModeDataModel mode;
    BoilerState boiler;

    public static ThermostatFragment newInstance(int position) {
        ThermostatFragment fragment = new ThermostatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thermostat_display, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtConsigne = view.findViewById(R.id.consigne_thermostat);
        txtMinus = view.findViewById(R.id.minus_thermostat);
        txtPlus = view.findViewById(R.id.plus_thermostat);
        txtThermometre = view.findViewById(R.id.value_thermometer);
        txtThermostatEtat = view.findViewById(R.id.txtThermostatEtat);
        txtThermostatPlan = view.findViewById(R.id.txtThermostatPlan);
        txtThermostatMode = view.findViewById(R.id.txtThermostatMode);

        imgThermostatEtat = view.findViewById(R.id.imgThermostatEtat);
        imgThermostatMode = view.findViewById(R.id.imgThermostatMode);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getThermostat();
        getThermostatSensor();
        initializeSocketioListeners();
        initClickEvents();
    }

    public void getThermostat() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
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
            }
        });

        getData.execute(m_baseAddress);
    }

    public void getThermostatSensor() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        updateSensorDisplay(obj);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getData.execute(m_sensorAddress);
    }

    public void updateSensorDisplay(JSONObject obj) throws JSONException {
        if (obj.has("valeur1")) {
            txtThermometre.setText(obj.getString("valeur1"));
        }
    }

    public void updateThermostatDisplays(JSONObject obj) throws JSONException, IllegalAccessException {
        if (obj == null)
            throw new RuntimeException("updateThermostatDisplay : JSON Object is null !!");

        thermostat = new ThermostatDataModel(obj);
        mode = thermostat.getMode();
        boiler = getBoilerState(thermostat.getEtat());

        imgThermostatEtat.setColorFilter(ContextCompat.getColor(getView().getContext(), boiler.getColor()));
        txtThermostatEtat.setText(boiler.getEtat());
        txtConsigne.setText(String.valueOf(thermostat.getConsigne()));
        txtThermostatPlan.setText(thermostat.getPlanningName());


        ModeImage modeImg = getModeImage(mode.getNom());

        imgThermostatMode.setImageResource(modeImg.getImg());
        imgThermostatMode.setColorFilter(ContextCompat.getColor(getView().getContext(), modeImg.getColor()));
        txtThermostatMode.setText(mode.getNom());
    }

    private void initClickEvents() {
        txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsigne(THT_CHANGE_VALUE);
            }
        });

        txtConsigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketIOHolder.emit(SocketIOHolder.EMIT_THT_REFRESH, "");
            }
        });

        txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConsigne(-THT_CHANGE_VALUE);
            }
        });
    }

    private void setConsigne(double changeValue) {
        double consigne = thermostat.getConsigne();
        consigne += changeValue;
        thermostat.setConsigne(consigne);
        SocketIOHolder.emit(SocketIOHolder.EMIT_THT_CONS, thermostat);
    }

    private BoilerState getBoilerState(int etat) {
        String etatStr;
        int color;

        switch (etat) {
            case BoilerState.BOILER_STATE_OFF:
                color = R.color.colorAccent;
                etatStr = BoilerState.BOILER_OFF;
                break;
            case BoilerState.BOILER_STATE_ON:
                color = R.color.colorOk;
                etatStr = BoilerState.BOILER_ON;
                break;
            default:
                color = R.color.almostBlack;
                etatStr = BoilerState.BOILER_UNDEFINED;
        }
        return new BoilerState(etatStr, color);
    }

    private ModeImage getModeImage(String nom) {

        int modeImg;
        int color;

        switch (nom) {
            case ModeImage.MODE_NIGHT:
                modeImg = R.drawable.ic_mode_nuit;
                color = R.color.colorPrimaryDark;
                break;
            case ModeImage.MODE_COMFORT:
                modeImg = R.drawable.ic_mode_confort;
                color = R.color.amber;
                break;
            case ModeImage.MODE_ECO:
                modeImg = R.drawable.ic_mode_eco;
                color = R.color.colorOk;
                break;
            case ModeImage.MODE_NO_FREEZE:
                modeImg = R.drawable.ic_hors_gel;
                color = R.color.coldBlue;
                break;
            default:
                modeImg = android.R.drawable.ic_menu_close_clear_cancel;
                color = R.color.almostBlack;

        }
        return new ModeImage(modeImg, color);
    }

    @Override
    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket != null) {

            SocketIOHolder.socket.off(SocketIOHolder.EVENT_THERMOSTAT);
            SocketIOHolder.socket.off(SocketIOHolder.EVENT_BOILER);

            SocketIOHolder.socket.on(SocketIOHolder.EVENT_THERMOSTAT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onSocketIOReceive(SocketIOHolder.EVENT_THERMOSTAT, args);
                }
            });

            SocketIOHolder.socket.on(SocketIOHolder.EVENT_BOILER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    onSocketIOReceive(SocketIOHolder.EVENT_BOILER, args);
                }
            });
        }
    }

    public void onSocketIOReceive(final String event, Object... args) {

        try {

            JSONArray jsonArray = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                jsonArray = new JSONArray(args);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject obj = jsonArray.getJSONObject(i);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    eventDispatch(event, obj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void eventDispatch(String event, JSONObject obj) throws JSONException, IllegalAccessException {
        switch (event) {
            case SocketIOHolder.EVENT_THERMOSTAT:
                updateThermostatDisplays(obj);
                break;

            case SocketIOHolder.EVENT_BOILER:
                updateSensorDisplay(obj);
                break;
        }
    }

    @Override
    public void onResponseOk(String response) {

    }

    private class BoilerState {
        public static final String BOILER_ON = "On";
        public static final String BOILER_OFF = "Off";
        public static final String BOILER_UNDEFINED = "?";
        public static final int BOILER_STATE_ON = 1;
        public static final int BOILER_STATE_OFF = 0;
        private String etat;
        private int color;

        public BoilerState(String etat, int color) {
            this.etat = etat;
            this.color = color;
        }

        public String getEtat() {
            return etat;
        }

        public void setEtat(String etat) {
            this.etat = etat;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    private class ModeImage {
        public static final String MODE_NIGHT = "Nuit";
        public static final String MODE_ECO = "Eco";
        public static final String MODE_COMFORT = "Confort";
        public static final String MODE_NO_FREEZE = "Hors Gel";
        private int img;
        private int color;

        public ModeImage(int img, int color) {
            this.img = img;
            this.color = color;
        }

        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
