package fr.geringan.activdash.appwidgets;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.widget.RemoteViews;

public class CustomRemoteViews extends RemoteViews {
    public CustomRemoteViews(String packageName, int layoutId) {
        super(packageName, layoutId);
    }

    public CustomRemoteViews(Parcel parcel) {
        super(parcel);
    }

    public void changeImageResourceColor(int resourceImageView, int resourceColor) {

        
    }
}
