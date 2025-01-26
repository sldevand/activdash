package fr.geringan.activdash.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.Arrays;

import fr.geringan.activdash.interfaces.SocketIOEventsListener;
import fr.geringan.activdash.models.DataModel;
import io.socket.client.IO;
import io.socket.client.Socket;

import static fr.geringan.activdash.helpers.PrefsManager.baseAddress;
import static fr.geringan.activdash.helpers.PrefsManager.nodePort;

public final class SocketIOHolder {
    public static final String EVENT_INTER = "inter";
    public static final String EVENT_DIMMER = "dimmer";
    public static final String EVENT_THERMOMETER = "thermo";
    public static final String EVENT_THERMOSTAT = "thermostat";
    public static final String EVENT_BOILER = "chaudiere";
    public static final String EVENT_PLAN_SAVE = "therplansave";
    public static final String EVENT_MODE_SAVE = "thermodesave";
    public static final String EVENT_THER_GET_PWR = "therpowget";
    public static final String EVENT_MESSAGE_CONSOLE = "messageConsole";
    public static final String EMIT_INTER = "updateInter";
    public static final String EMIT_DIMMER = "updateDimmer";
    public static final String EMIT_DIMMERPERSIST = "updateDimmerPersist";
    public static final String EMIT_SCENARIO = "updateScenario";
    public static final String EMIT_SCENARIO_STOP = "stopScenario";
    public static final String EMIT_SCENARIO_WATCH = "watchScenario";
    public static final String EMIT_THT_CONS = "updateTherCons";
    public static final String EMIT_THT_DELTA = "updateTherDelta";
    public static final String EMIT_THT_TEMPEXT = "updateTherTempext";
    public static final String EMIT_THT_INTERNE = "updateTherInterne";
    public static final String EMIT_THT_MODE = "updateTherMode";
    public static final String EMIT_THT_SYNC_MODES = "syncTherModes";
    public static final String EMIT_THT_UPDATE_PLAN = "updateTherPlan";
    public static final String EMIT_THT_REFRESH = "refreshTher";
    public static final String EMIT_THT_UPDATE_CLOCK = "refreshTher";
    public static final String EMIT_THT_GET_CLOCK = "refreshTher";
    public static final String EMIT_THT_SET_PWR = "setTherPwr";
    public static final String EMIT_SERIAL_PORT_RESET = "serialportReset";
    private static final String TAG = "SocketIOHolder";
    public static Socket socket;
    private static SocketEmitTask _task;
    private static SocketIOEventsListener mEventsListener;

    public static void launch() {
        try {

            String socketIOUrl = baseAddress + ":" + nodePort;
            if (null == socket) {
                socket = IO.socket(socketIOUrl);
                socket.io().timeout(5000);
            }
            start();

        } catch (URISyntaxException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    private static void start() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public static void stop() {
        if (socket != null && socket.connected()) {
            socket.off();
            socket.close();
        }
    }

    public static void emitNoControl(String event, String data) {
        SocketEmitTask task = new SocketEmitTask(event);
        task.execute(data);
    }

    public static void emit(String event, String data) {

        if (null == _task){
            _task = new SocketEmitTask(event);
        }

        if (_task.getStatus() != AsyncTask.Status.RUNNING) {
            _task = new SocketEmitTask(event);
            _task.execute(data);
        }
    }

    public static void emit(String event, DataModel dm) {

        if (null == _task) {
            _task = new SocketEmitTask(event);
        }

        if (_task.getStatus() != AsyncTask.Status.RUNNING) {
            try {
                _task = new SocketEmitTask(event);
                _task.execute(dm.getDataJSON());
            } catch (JSONException | IllegalAccessException | IllegalStateException e) {
                Log.e(TAG, Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public static void initEventListeners() {

        socket
                .on(Socket.EVENT_CONNECT, args -> {
                    if (null != mEventsListener) mEventsListener.onSocketIOConnect();
                })
                .on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                    if (null != mEventsListener) mEventsListener.onSocketIOTimeout();
                })
                .on(Socket.EVENT_DISCONNECT, args -> {
                    if (null != mEventsListener) mEventsListener.onSocketIODisconnect();
                })
                .on("message", args -> {
                    if (null != mEventsListener) mEventsListener.onSocketIOMessage(args);
                });
    }

    public static void setEventsListener(SocketIOEventsListener mEventsListener) {
        SocketIOHolder.mEventsListener = mEventsListener;
    }
}
