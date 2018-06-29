package fr.geringan.activdash.network;

import android.os.AsyncTask;

public class SocketEmitTask extends AsyncTask<Object, Void, String> {


    private String _event;

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
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {}

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}