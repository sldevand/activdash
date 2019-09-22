package fr.geringan.activdash.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SocketIOService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("SocketIOService","Started" + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
