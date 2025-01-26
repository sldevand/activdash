package fr.geringan.activdash.network;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;

public class SocketEmitTask extends AsyncTask<Object, Void, String> {
    private static final String TAG = "SocketEmitTask";
    private final String _event;

    SocketEmitTask(String event) {
        _event = event;
    }

    @Override
    protected String doInBackground(Object... args) {
        String result = "KO";
        try {
            Thread.sleep(60);
            SocketIOHolder.socket.emit(_event, args[0]);
            result = "OK";
        } catch (InterruptedException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //intentional empty method
    }
}