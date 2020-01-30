package com.intcore.snapcar.ui.survey;

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
class SurveyPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final SurveyScreen screen;
    private final UserRepo userRepo;

    @Inject
    SurveyPresenter(UserManager userManager,
                    @IOThread ThreadSchedulers threadSchedulers,
                    @ForActivity CompositeDisposable disposable,
                    SurveyScreen screen,
                    UserRepo userRepo) {

        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        fetchSurvies();
    }

    private void fetchSurvies() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchSurvies(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::updateUi, this::processError));
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

    public void postSurvey(String surveyId, String json) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.postSurvey(userManager.getCurrentUser().getApiToken(), surveyId, json)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::surveyIsSent, this::processError));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}