package com.intcore.snapcar.store;

import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@ApplicationScope
public class UserUpdateLocationRepo {

    private final WebServiceStore webServiceStore;
    private final CompositeDisposable disposable;

    @Inject
    UserUpdateLocationRepo(WebServiceStore webServiceStore) {
        this.webServiceStore = webServiceStore;
        this.disposable = new CompositeDisposable();
    }

    public void updateCarLocation(String apiToken, String carId, String lon, String lat) {
        disposable.add(webServiceStore
                .updateCarLocation(apiToken, carId, lon, lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Functions.EMPTY_ACTION, Timber::e));
    }

    public void updateUserLocation(String apiToken, String lon, String lat) {
        disposable.add(webServiceStore
                .updateUserLocation(apiToken, lon, lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Functions.EMPTY_ACTION, Timber::e));
    }
}