package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.CapteurAdapter;
import fr.geringan.activdash.models.CapteurDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;
import io.socket.emitter.Emitter;

public class CapteursFragment extends CommonNetworkFragment {

    public String m_address = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/mesures/get-sensors";
    protected CapteurAdapter adapter;
    RecyclerView recyclerView;

    public static CapteursFragment newInstance(int tag) {
        CapteursFragment fragment = new CapteursFragment();


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capteurs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.listCapteurs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CapteurAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        final ProgressBar progress = view.findViewById(R.id.progress);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (adapter.getItemCount() > 0)
                    progress.setVisibility(View.GONE);
                else
                    progress.setVisibility(View.VISIBLE);
            }
        });

        execGetData(m_address, adapter, progress);

    }

    public void updateListView() {
        recyclerView.setAdapter(adapter);
    }

    public void capteursDataUpdate(Object... args) throws IllegalAccessException {
        JSONObject jsonObject;

        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {


                jsonObject = new JSONArray(args).getJSONObject(0);

                CapteurDataModel dataModel = new CapteurDataModel();
                dataModel.setDataJSON(jsonObject);

                onSocketioUpdate(dataModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeSocketioListeners() {
        if (SocketIOHolder.socket != null) {

            SocketIOHolder.socket.on("thermo", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {

                        capteursDataUpdate(args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            SocketIOHolder.socket.on("teleinfo", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        capteursDataUpdate(args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });

            SocketIOHolder.socket.on("chaudiere", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        capteursDataUpdate(args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public void onResponseOk(String response) {
        initializeSocketioListeners();
    }

    public void onSocketioUpdate(CapteurDataModel socketioDataModel) throws JSONException, IllegalAccessException {

        int iter = 0;
        ArrayList<CapteurDataModel> dataModels = adapter.getDataSet();
        if (dataModels != null) {

            for (CapteurDataModel capteurDataModel : dataModels) {

                int id = capteurDataModel.getDataJSON().getInt("id");
                int idToChange = socketioDataModel.getDataJSON().getInt("id");

                if (id == idToChange) {

                    dataModels.get(iter).setDataJSON(socketioDataModel.getDataJSON());
                    final int pos = iter;
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(pos);
                            }
                        });
                    } else {
                        Log.e("onSocketioUpdate", "activity is null!");
                    }
                }
                iter++;
            }
        } else {
            Log.e("onSocketioUpdate", "dataModels is null!");
        }
    }

}