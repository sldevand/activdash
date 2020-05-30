package fr.geringan.activdash.services;

import android.os.AsyncTask;

import fr.geringan.activdash.interfaces.OnGetListResponseListener;
import fr.geringan.activdash.interfaces.OnGetResponseListener;
import fr.geringan.activdash.models.DataModel;
import fr.geringan.activdash.network.GetHttp;

abstract public class AbstractService<T extends DataModel> {
    OnGetResponseListener<T> onGetResponseListener;
    OnGetListResponseListener<T> onGetListResponseListener;

    protected String url;

    public void get() {
        GetHttp getData = new GetHttp();
        getData.setOnResponseListener(this::onResponse);
        getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buildUrl());
    }

    abstract void onResponse(String response);

    abstract String buildUrl();

    public void setOnGetResponseListener(OnGetResponseListener<T> onGetResponseListener) {
        this.onGetResponseListener = onGetResponseListener;
    }

    public void setOnGetListResponseListener(OnGetListResponseListener<T> onGetListResponseListener) {
        this.onGetListResponseListener = onGetListResponseListener;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
