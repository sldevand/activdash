package fr.geringan.activdash.interfaces;

import fr.geringan.activdash.models.DataModel;

public interface OnGetResponseListener<T extends DataModel> {
    void onSuccess(T dataModel);
    void onError(String error);
}
