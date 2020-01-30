package com.intcore.snapcar.ui.viewcar;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import timber.log.Timber;

@ActivityScope
class viewCarPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<Integer> userIdRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final ViewCarScreen screen;
    private final UserRepo userRepo;

    @Inject
    viewCarPresenter(@IOThread ThreadSchedulers threadSchedulers,
                     @ForActivity CompositeDisposable disposable,
                     UserManager userManager,
                     ViewCarScreen screen,
                     UserRepo userRepo) {
        super(screen);
        this.userIdRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    void fetchCarData(int id) {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            //apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.getCar(id, apiToken)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(carDTO -> {
                    if (carDTO.getCar() == null) {
                        screen.carDeleted();
                    } else {
                        screen.updateUi(carDTO);
                        userIdRelay.accept(carDTO.getCar().getUserId());
                    }
                }, this::processError));
    }

    public void addFav(int carId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.deleteCarFavorite(userManager.getCurrentUser().getApiToken(), carId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(this::favAction, this::processError));
        }
    }

    public void removeFav(int carId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.deleteCarFavorite(userManager.getCurrentUser().getApiToken(), carId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(this::favAction, this::processError));
        }
    }

    private void favAction(ResponseBody responseBody) {
    }

    public int currentUserId() {
        if (userManager.sessionManager().isSessionActive()) {
            return (int) userManager.getCurrentUser().getId();
        } else return 0;
    }

    public int getUserType() {
        if (userManager.sessionManager().isSessionActive()) {
            return userManager.getCurrentUser().getType();
        } else return 0;
    }

    void onChatClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            if (userManager.getCurrentUser().getId() != userIdRelay.getValue()) {
                disposable.add(userRepo.startChat(userManager.getCurrentUser().getApiToken(),
                        userIdRelay.getValue())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                        .doFinally(screen::hideLoadingAnimation)
                        .subscribe(screen::shouldNavigateToChatThread, this::processError));
            }
        } else {
            screen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            }
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 404) {
                screen.carDeleted();
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
                return getResourcesUtil().getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return getResourcesUtil().getString(R.string.error_communicating_with_server);
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}
