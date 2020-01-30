package com.intcore.snapcar.ui.notification;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.NotificationRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.notification.NotificationMapper;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@FragmentScope
class NotificationPresenter {

    private final NotificationScreen notificationScreen;
    private final NotificationMapper notificationMapper;
    private final ThreadSchedulers threadSchedulers;
    private final NotificationRepo notificationRepo;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;

    @Inject
    NotificationPresenter(@IOThread ThreadSchedulers threadSchedulers,
                          @ForFragment CompositeDisposable disposable,
                          NotificationScreen notificationScreen,
                          NotificationMapper notificationMapper,
                          NotificationRepo notificationRepo,
                          ResourcesUtil resourcesUtil,
                          UserManager userManager) {
        this.notificationScreen = notificationScreen;
        this.notificationMapper = notificationMapper;
        this.threadSchedulers = threadSchedulers;
        this.notificationRepo = notificationRepo;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
    }

    void onViewCreated() {
        notificationScreen.setupRecyclerView();
        notificationScreen.setupRefreshLayout();
        fetchData();
    }

    public void fetchData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(notificationRepo.fetchNotificationList(userManager.getCurrentUser().getApiToken())
                    .map(notificationMapper::toNotificationViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> notificationScreen.showLoadingAnimation())
                    .doFinally(notificationScreen::hideLoadingAnimation)
                    .subscribe(notificationScreen::updateUi, this::processError));
        } else {
            notificationScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    void onNotificationClicked(String notificationViewModelId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(notificationRepo.updateSeen(userManager.getCurrentUser().getApiToken(),
                    notificationViewModelId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(this::fetchData, this::processError));
        } else {
            notificationScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            notificationScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        } else if (t instanceof IOException) {
            notificationScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            notificationScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
        }
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(resourcesUtil.getContext()).equals("en");
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

    void shouldNavigateToProfile() {
        notificationScreen.shouldNavigateToProfile();
    }

    void shouldNavigateToHome() {
        notificationScreen.shouldNavigateToHome();
    }

    void onDestroy() {
        disposable.clear();
    }
}