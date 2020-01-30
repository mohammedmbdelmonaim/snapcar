package com.intcore.snapcar.ui.privacyactivtiy;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class PrivacyPresenter extends BaseActivityPresenter {
    private final ThreadSchedulers threadSchedulers;
    private final UserRepo userRepo;
    private final CompositeDisposable disposable;
    private final PrivacyScreen screen;
    private final UserManager userManager;
    private final PreferencesUtil preferencesUtil;

    @Inject
    PrivacyPresenter(UserManager userManager, @IOThread ThreadSchedulers threadSchedulers,
                     @ForActivity CompositeDisposable disposable,
                     PrivacyScreen screen,
                     PreferencesUtil preferencesUtil,
                     UserRepo userRepo) {

        super(screen);
        this.screen = screen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.preferencesUtil = preferencesUtil;
        this.userRepo = userRepo;
        this.threadSchedulers = threadSchedulers;
    }

    @Override
    protected void onCreate() {
        fetchPolicyUsData();
    }

    private void fetchPolicyUsData() {
        disposable.add(userRepo.getSittings()
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::policyText, this::processError));
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        }else if (t instanceof IOException) {
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

    public void termsAgreed() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.termsAgreed(userManager.getCurrentUser().getApiToken(), "1")
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(this::termsUpdated, this::processError));
        }
    }

    private void termsUpdated() {
        disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(screen::finishActivity, this::processError));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

    public void processLogout() {
        preferencesUtil.saveOrUpdateInt("interest_count", 0);
        String apiToken = userManager.getCurrentUser().getApiToken();
        disposable.add(userRepo.logout(apiToken)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(() -> {
                    userManager.sessionManager().logout();
                    screen.logoutReady();
                }, this::processError));
    }
}
