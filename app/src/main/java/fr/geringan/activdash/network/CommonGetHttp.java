package fr.geringan.activdash.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.geringan.activdash.adapters.CommonNetworkAdapter;


public class CommonGetHttp extends AsyncTask<String, Void, String> {

    OnHttpResponseListener responseListener;
    private CommonNetworkAdapter<?> _adapter;
    private String _address;

    public CommonGetHttp(CommonNetworkAdapter<?> adapter) {
        _adapter = adapter;
    }

    @Override
    protected String doInBackground(String... params) {

        InputStream in = null;
        _address = params[0];
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
        return "404";
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("onPostExecute", "FINISHED! = " + _address);
        if (this.responseListener != null) responseListener.onResponse(result);
        if (result != null && !result.equals("404")) {
            try {
                _adapter.setHttpResponse(result);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnResponseListener(OnHttpResponseListener rl) {
        this.responseListener = rl;
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