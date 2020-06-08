package fr.geringan.activdash.interfaces;

import java.util.List;

import fr.geringan.activdash.models.DataModel;

public interface OnGetListResponseListener<T extends DataModel> {
    void onSuccess(List<T> dataList);
    void onError(String error);
}
