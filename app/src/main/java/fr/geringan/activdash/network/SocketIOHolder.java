package fr.geringan.activdash.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.net.URISyntaxException;

import fr.geringan.activdash.interfaces.SocketIOEventsListener;
import fr.geringan.activdash.models.DataModel;
import io.socket.client.IO;
import io.socket.client.Socket;

import static fr.geringan.activdash.utils.PrefsManager.baseAddress;
import static fr.geringan.activdash.utils.PrefsManager.nodePort;

public final class SocketIOHolder {

    public static final String EVENT_INTER = "inter";
    public static final String EVENT_DIMMER = "dimmer";
    public static final String EVENT_THERMOMETER = "thermo";
    public static final String EVENT_THERMOSTAT = "thermostat";
    public static final String EVENT_BOILER = "chaudiere";
    public static final String EMIT_INTER = "updateInter";
    public static final String EMIT_DIMMER = "updateDimmer";
    public static final String EMIT_DIMMERPERSIST = "updateDimmerPersist";
    public static final String EMIT_SCENARIO = "updateScenario";
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
            e.printStackTrace();
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

    public static void emit(String event, String data) {

        if (_task == null) _task = new SocketEmitTask(event);

        if (_task.getStatus() != AsyncTask.Status.RUNNING) {
            _task = new SocketEmitTask(event);
            _task.execute(data);

        } else {
            Log.e("emit", "A task is running");
        }
    }

    public static void emit(String event, DataModel dm) {

        if (_task == null) _task = new SocketEmitTask(event);

        if (_task.getStatus() != AsyncTask.Status.RUNNING) {
            try {
                _task = new SocketEmitTask(event);
                _task.execute(dm.getDataJSON());
            } catch (JSONException | IllegalAccessException | IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("emit", "A task is running");
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
