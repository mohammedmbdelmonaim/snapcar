package com.intcore.snapcar.ui.notificationsetting;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.NotificationSettingDTO;
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
class NotificationSettingPresenter extends BaseActivityPresenter {

    private final NotificationSettingScreen notificationSettingScreen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    NotificationSettingPresenter(NotificationSettingScreen notificationSettingScreen,
                                 @IOThread ThreadSchedulers threadSchedulers,
                                 @ForActivity CompositeDisposable disposable, UserManager userManager, UserRepo userRepo) {
        super(notificationSettingScreen);
        this.notificationSettingScreen = notificationSettingScreen;
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
    }

    @Override
    protected void onCreate() {
        if (userManager.sessionManager().isSessionActive()) {
            notificationSettingScreen.setDefaultData(userManager.getCurrentUser().getNotificationSettingDTO());
        }
    }

    public void onSaveClicked(boolean isMatchCarChecked, boolean isChatChecked, boolean isOfferChecked) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.manageNotification(userManager.getCurrentUser().getApiToken(),
                    new Gson().toJson(new NotificationSettingDTO(isMatchCarChecked, isChatChecked, isOfferChecked)))
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> notificationSettingScreen.showLoadingAnimation())
                    .doFinally(notificationSettingScreen::hideLoadingAnimation)
                    .subscribe(notificationSettingScreen::onSavedSuccessfully, this::processError));
        } else {
            notificationSettingScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            notificationSettingScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                notificationSettingScreen.processLogout();
            }
        }else if (t instanceof IOException) {
            notificationSettingScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            notificationSettingScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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