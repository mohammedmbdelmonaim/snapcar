package com.intcore.snapcar.backgroundServices.di;

import android.content.Context;


import com.intcore.snapcar.backgroundServices.ScheduledJobService;

import java.lang.ref.WeakReference;

import dagger.Module;
import dagger.Provides;

@Module
public class JopDispatcherModule {

    private final ScheduledJobService scheduledJobService;

    public JopDispatcherModule(ScheduledJobService scheduledJobService) {
        this.scheduledJobService = new WeakReference<>(scheduledJobService).get();
    }

    @Provides
    @JopDispatcherScope
    ScheduledJobService providesBaseActivity() {
        return scheduledJobService;
    }

    @Provides
    @JopDispatcherScope
    Context provideContext() {
        return scheduledJobService.getApplicationContext();
    }

}