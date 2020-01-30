package com.intcore.snapcar.ui.paymenthistory;

import android.content.Context;

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
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class PaymentHistoryPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final PaymentHistoryScreen scrren;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    PaymentHistoryPresenter(@IOThread ThreadSchedulers threadSchedulers,
                            @ForActivity CompositeDisposable disposable,
                            ResourcesUtil resourcesUtil,
                            @ForActivity Context context,
                            UserManager userManager,
                            PaymentHistoryScreen screen,
                            UserRepo userRepo) {
        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.scrren = screen;
    }

    @Override
    protected void onCreate() {
        fetchHistoryData();
    }

    private void fetchHistoryData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getPaymentHistory(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> scrren.showLoadingAnimation())
                    .doFinally(scrren::hideLoadingAnimation)
                    .subscribe(scrren::updateUi, this::processError));
        } else {
            scrren.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            scrren.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                scrren.processLogout();
            }
        } else if (t instanceof IOException) {
            scrren.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            scrren.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}