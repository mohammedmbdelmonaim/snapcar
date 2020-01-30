package com.intcore.snapcar.backgroundServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.intcore.snapcar.store.UserUpdateLocationRepo;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class BackgroundService extends Service {

    @Inject
    UserUpdateLocationRepo updateLocationRepo;
    @Inject
    UserManager sessionManager;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }
}
