package com.intcore.snapcar.ui.feedback;

import android.content.Context;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.util.ImageFileFilter;
import com.intcore.snapcar.util.UserManager;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@ActivityScope
class FeedbackPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final FeedbackScreen screen;
    private final UserRepo userRepo;
    private final Context context;
    private FeedbackApiResponse feedbackApiResponse;

    @Inject
    FeedbackPresenter(UserManager userManager, @IOThread ThreadSchedulers threadSchedulers,
                      @ForActivity CompositeDisposable disposable,
                      FeedbackScreen screen,
                      UserRepo userRepo,
                      @ForActivity Context context) {
        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.context = context;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        fetchSubjects();
    }

    private void fetchSubjects() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.getFeedbacksubjects(apiToken)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(this::setSubjects, this::processError));
    }

    List<FeedbackApiResponse.FeedBackData> getSubjects() {
        return feedbackApiResponse.getFeedBackData();
    }

    private void setSubjects(FeedbackApiResponse feedbackApiResponse) {
        this.feedbackApiResponse = feedbackApiResponse;
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

    void openGallery(RxPaparazzo.SingleSelectionBuilder<FeedbackActivity> pickedImageObservable) {
        disposable.add(
                pickedImageObservable.usingGallery()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::imageUploaded, Timber::e));
    }

    private void imageUploaded(File file) {
        if (!new ImageFileFilter(file).accept(file)) {
            screen.showWarningMessage(context.getString(R.string.attachment_must_be_image));
            return;
        }
        screen.setAttachment(file);
    }

    public void postFeedback(String categoryId, String details, File attachment, String subject) {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.postFeedback(apiToken, categoryId, details, attachment, subject)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::feedbackSent, this::processError));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}