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
import java.util.Arrays;

import fr.geringan.activdash.adapters.CommonNetworkAdapter;
import fr.geringan.activdash.exceptions.DataModelException;

public class CommonGetHttp extends AsyncTask<String, Void, String> {

    public static final String HTTP_NOT_FOUND = String.valueOf(HttpURLConnection.HTTP_NOT_FOUND);
    private static final String TAG = "CommonGetHttp";
    private OnHttpResponseListener responseListener;
    private final CommonNetworkAdapter<?> _adapter;

    public CommonGetHttp(CommonNetworkAdapter<?> adapter) {
        _adapter = adapter;
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream in = null;
        String _address = params[0];

        try {
            URL url = new URL(_address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);

        } catch (MalformedURLException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
            return HTTP_NOT_FOUND;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e(TAG, Arrays.toString(e.getStackTrace()));
            }
        }
        return HTTP_NOT_FOUND;
    }

    @Override
    protected void onPostExecute(String result) {
        if (null == result || HTTP_NOT_FOUND.equals(result)) return;

        try {
            if (this.responseListener != null) responseListener.onResponse(result);
            _adapter.setHttpResponse(result);
        } catch (IllegalAccessException | JSONException | DataModelException e) {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
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
        void onResponse(String response) throws IllegalAccessException, DataModelException, JSONException;
    }
}