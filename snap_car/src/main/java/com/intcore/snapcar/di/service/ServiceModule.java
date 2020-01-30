package com.intcore.snapcar.di.service;

import android.app.Service;


import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    private final Service service;

    public ServiceModule(Service service) {
        this.service = new WeakReference<>(service).get();
    }

    @Provides
    @ServiceScope
    Service providesBaseActivity() {
        return service;
    }


}