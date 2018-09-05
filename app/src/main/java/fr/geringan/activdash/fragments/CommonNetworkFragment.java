package fr.geringan.activdash.fragments;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.util.Objects;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.CommonNetworkAdapter;
import fr.geringan.activdash.exceptions.DataModelException;
import fr.geringan.activdash.network.CommonGetHttp;


public abstract class CommonNetworkFragment extends Fragment {

    public abstract void initializeSocketioListeners() throws IllegalAccessException, DataModelException, JSONException;

    public void execGetData(String address, CommonNetworkAdapter<?> adapter, ProgressBar pBar) {
        CommonGetHttp get_dat = new CommonGetHttp(adapter);
        final ProgressBar progBar = pBar;

        final View v = Objects.requireNonNull(getActivity()).findViewById(android.R.id.content);

        get_dat.setOnResponseListener(response -> {

            switch (response) {
                case "404":
                    onBadResponse(response, progBar, v);
                    break;
                default:
                    onResponseOk(response);
                    break;
            }
        });
        get_dat.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address);
    }

    public abstract void onResponseOk(String response) throws IllegalAccessException, DataModelException, JSONException;

    public void onBadResponse(String response, ProgressBar progBar, View v) {


        String resp = v.getResources().getString(R.string.http_bad_response_error, response);

        Snackbar.make(v, resp, Snackbar.LENGTH_SHORT).show();
        progBar.setVisibility(View.GONE);

    }
}
