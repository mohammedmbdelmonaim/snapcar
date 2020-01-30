package com.intcore.snapcar.ui.mycars;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class MyCarsPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final ResourcesUtil resources;
    private final MyCarsScreen screen;
    private final UserRepo userRepo;
    @Inject
    JopDispatcher jopDispatcher;

    @Inject
    MyCarsPresenter(@IOThread ThreadSchedulers threadSchedulers,
                    @ForActivity CompositeDisposable disposable,
                    ResourcesUtil resourcesUtil,
                    UserManager userManager,
                    MyCarsScreen screen,
                    UserRepo userRepo) {
        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.resources = resourcesUtil;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onResume() {
        fetchCarsData();
    }

    public void fetchCarsData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchMyCars(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
//                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::updateUi, this::processError));
        }
    }

    public void fetchSwearData(int carId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getSittings()
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::openSwearDialog, this::processError));
            screen.setCarId(carId);
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(
                    getHttpErrorMessage(HttpException.wrapJakewhartonException((
                            com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            }
        } else if (t instanceof IOException) {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return resources.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resources.getString(R.string.error_communicating_with_server);
        }
    }

    public void deleteCar(int carId, int isTracked, String deleteReson) {
        if (deleteReson.isEmpty()) {
            screen.showWarningMessage(resources.getString(R.string.please_type_delete_reason));
            return;
        }
        disposable.add(userRepo.deleteCar(carId, deleteReson, userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::carRemoved, this::processError));
        if (isTracked == 1) {
            FirebaseJobDispatcher dispatcher = jopDispatcher.getInstance();
            dispatcher.cancelAll();
        }
        disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(this::update, this::processError));
    }

    private void update(DefaultUserModel defaultUserModel) {
    }

    public void deleteSold(int carId) {
//        userRepo.deleteCar(carId, userManager.getCurrentUser().getApiToken());
//        disposable.add(userRepo.deleteCar(carId, userManager.getCurrentUser().getApiToken())
//                .subscribeOn(threadSchedulers.workerThread())
//                .observeOn(threadSchedulers.mainThread())
//                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
//                .doFinally(screen::hideLoadingAnimation)
//                .subscribe(screen::carRemoved, this::processError));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}