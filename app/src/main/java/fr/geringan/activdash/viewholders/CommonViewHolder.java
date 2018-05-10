package fr.geringan.activdash.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import fr.geringan.activdash.models.DataModel;


public abstract class CommonViewHolder<V extends DataModel> extends RecyclerView.ViewHolder {

    private V currentDataModel;

    public CommonViewHolder(View itemView) {
        super(itemView);
    }

    public void setData(V dm) {
        currentDataModel = dm;
    }
}
