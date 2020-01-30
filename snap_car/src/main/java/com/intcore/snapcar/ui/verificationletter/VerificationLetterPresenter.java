package com.intcore.snapcar.ui.verificationletter;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@ActivityScope
class VerificationLetterPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final VerificationLetterScreen screen;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    VerificationLetterPresenter(UserManager userManager,
                                @IOThread ThreadSchedulers threadSchedulers,
                                @ForActivity CompositeDisposable disposable,
                                VerificationLetterScreen screen,
                                ResourcesUtil resourcesUtil,
                                UserRepo userRepo) {
        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        fetchUserData();
    }

    private void fetchUserData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::updateUi, this::processError));
        }
    }

    void sendVerifyLetter(File file) {
        disposable.add(userRepo.sendVerfiyLetter(userManager.getCurrentUser().getApiToken(), file)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::sendSuccessfully, this::processError));
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
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    void openGallery(RxPaparazzo.SingleSelectionBuilder<VerificationLetterActivity> pickedImageObservable) {
        disposable.add(
                pickedImageObservable.usingFiles()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(screen::selectedFile, Timber::e));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}