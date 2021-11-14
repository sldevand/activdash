package fr.geringan.activdash.viewholders;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import fr.geringan.activdash.models.DataModel;

public abstract class CommonViewHolder<V extends DataModel> extends RecyclerView.ViewHolder {
    public CommonViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void setData(V dm);
}
