package com.intcore.snapcar.ui.settings;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class SettingsPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final SettingsScreen settingsScreen;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    SettingsPresenter(@IOThread ThreadSchedulers threadSchedulers,
                      @ForActivity CompositeDisposable disposable,
                      SettingsScreen settingsScreen,
                      UserManager userManager,
                      UserRepo userRepo) {
        super(settingsScreen);
        this.threadSchedulers = threadSchedulers;
        this.settingsScreen = settingsScreen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
    }

    void changeLanguage(String lang) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.changeLanguage(userManager.getCurrentUser().getApiToken(), lang)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> settingsScreen.showLoadingAnimation())
                    .doFinally(settingsScreen::hideLoadingAnimation)
                    .subscribe(settingsScreen::changeLanguage, this::processError));
        } else {
            settingsScreen.changeLanguage(null);
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            settingsScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        } else if (t instanceof IOException) {
            settingsScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            settingsScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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
}