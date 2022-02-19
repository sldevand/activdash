package fr.geringan.activdash.fragments;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.CommonNetworkAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.network.CommonGetHttp;

public abstract class CommonNetworkFragment extends Fragment {

    public abstract void initializeSocketioListeners() throws IllegalAccessException, DataModelException, JSONException;

    public void execGetData(String address, CommonNetworkAdapter<?> adapter, ProgressBar pBar) {
        CommonGetHttp get_dat = new CommonGetHttp(adapter);
        final ProgressBar progBar = pBar;

        final View v = requireActivity().findViewById(android.R.id.content);

        get_dat.setOnResponseListener(response -> {
            if ("404".equals(response)) {
                onBadResponse(response, progBar, v);
                return;
            }

            try {
                JSONArray responseArray = new JSONArray(response);
                if (responseArray.length() == 0) {
                    onEmptyResponse(progBar, v);
                    return;
                }
            } catch (JSONException jsonException) {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.length() == 0) {
                    onEmptyResponse(progBar, v);
                    return;
                }
            } catch (Exception exception) {
                return;
            }

            onResponseOk(response, progBar, v);
        });
        get_dat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address);
    }

    public abstract void onResponseOk(String response, ProgressBar progBar, View v) throws IllegalAccessException, DataModelException, JSONException;

    public abstract void onEmptyResponse(ProgressBar progBar, View v);

    public void onBadResponse(String response, ProgressBar progBar, View v) {
        String resp = v.getResources().getString(R.string.http_bad_response_error, response);
        Snackbar.make(v, resp, Snackbar.LENGTH_SHORT).show();
        progBar.setVisibility(View.GONE);
    }
}
