package com.intcore.snapcar.core.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Intent;

import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import javax.inject.Inject;

import timber.log.Timber;

public class BaseActivityPresenter implements LifecycleObserver {

    private final BaseActivityScreen baseActivityScreen;

    @Inject
    protected BaseActivityPresenter(BaseActivityScreen baseActivityScreen) {
        this.baseActivityScreen = baseActivityScreen;
        baseActivityScreen.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected void onCreate() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void onStart() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_RESUME");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_STOP");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void onDestroy() {
        Timber.tag("Muhammad").d("Lifecycle presenter: ON_DESTROY");
    }

    protected Intent getIntent() {
        return baseActivityScreen.getIntent();
    }

    protected Lifecycle getLifeCycle() {
        return baseActivityScreen.getLifecycle();
    }

    protected ResourcesUtil getResourcesUtil() {
        return baseActivityScreen.getResourcesUtil();
    }

    protected PermissionUtil getPermissionUtil() {
        return baseActivityScreen.getPermissionUtil();
    }
}
