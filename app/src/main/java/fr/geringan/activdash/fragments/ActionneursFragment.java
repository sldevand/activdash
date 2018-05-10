package fr.geringan.activdash.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.geringan.activdash.R;
import fr.geringan.activdash.adapters.DimmerAdapter;
import fr.geringan.activdash.adapters.InterAdapter;
import fr.geringan.activdash.models.DimmerDataModel;
import fr.geringan.activdash.models.InterDataModel;
import fr.geringan.activdash.network.SocketIOHolder;
import fr.geringan.activdash.utils.PrefsManager;
import io.socket.emitter.Emitter;


public class ActionneursFragment extends CommonNetworkFragment {

    public String m_baseAddress = PrefsManager.baseAddress + "/" + PrefsManager.apiDomain + "/actionneurs/";
    public String m_interAddress = m_baseAddress + "inter";
    public String m_dimmerAddress = m_baseAddress + "dimmer";

    RecyclerView interRecyclerView;
    InterAdapter interAdapter;
    RecyclerView dimmerRecyclerView;
    DimmerAdapter dimmerAdapter;
    ProgressBar progressDimmer;
    ProgressBar progressInter;


    public static ActionneursFragment newInstance(int Tag) {
        ActionneursFragment fragment = new ActionneursFragment();
        //fragment.setTargetFragment(fragment,Tag);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actionneurs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        interRecyclerView = view.findViewById(R.id.listInters);
        interRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        interAdapter = new InterAdapter();
        interRecyclerView.setAdapter(interAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        interRecyclerView.addItemDecoration(itemDecoration);

        progressInter = view.findViewById(R.id.progressInter);
        interAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (interAdapter.getItemCount() > 0)
                    progressInter.setVisibility(View.GONE);
                else
                    progressInter.setVisibility(View.VISIBLE);
            }
        });

        dimmerRecyclerView = view.findViewById(R.id.listDimmers);
        dimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        dimmerAdapter = new DimmerAdapter();
        dimmerRecyclerView.setAdapter(dimmerAdapter);
        dimmerRecyclerView.addItemDecoration(itemDecoration);


        progressDimmer = view.findViewById(R.id.progressDimmer);

        dimmerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (dimmerAdapter.getItemCount() > 0)
                    progressDimmer.setVisibility(View.GONE);
                else
                    progressDimmer.setVisibility(View.VISIBLE);
            }
        });

        execGetData(m_dimmerAddress, dimmerAdapter, progressDimmer);
        execGetData(m_interAddress, interAdapter, progressInter);
    }

    public void initializeSocketioListeners() {

        if (SocketIOHolder.socket != null) {


            SocketIOHolder.socket.on(SocketIOHolder.EVENT_INTER, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    JSONObject jsonObject;
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            jsonObject = new JSONArray(args).getJSONObject(0);
                            final InterDataModel dataModel = new InterDataModel();
                            dataModel.setDataJSON(jsonObject);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            interAdapter.setEtat(dataModel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });

            SocketIOHolder.socket.on(SocketIOHolder.EVENT_DIMMER, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    JSONObject jsonObject;
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            jsonObject = new JSONArray(args).getJSONObject(0);
                            final DimmerDataModel dataModel = new DimmerDataModel();
                            dataModel.setDataJSON(jsonObject);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            dimmerAdapter.setEtat(dataModel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

}