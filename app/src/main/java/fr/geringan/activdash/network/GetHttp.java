package fr.geringan.activdash.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetHttp extends AsyncTask<String, Void, String> {

    private OnHttpResponseListener responseListener;

    @Override
    protected String doInBackground(String... strings) {


        InputStream in = null;
        String _address = strings[0];

        if (_address != null && !_address.equals("")) {
            Log.e("doInBackground", "BEGIN = " + _address);
            try {
                URL url = new URL(_address);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return "404";
            } finally {
                try {

                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "404";
    }

    public void setOnResponseListener(OnHttpResponseListener rl) {
        this.responseListener = rl;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (this.responseListener != null) responseListener.onResponse(s);

    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public interface OnHttpResponseListener {
        void onResponse(String response);
    }
}
