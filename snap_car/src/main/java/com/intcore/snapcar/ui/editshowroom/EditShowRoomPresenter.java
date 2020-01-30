package com.intcore.snapcar.ui.editshowroom;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@ActivityScope
class EditShowRoomPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> emailValidityRelay;
    private final BehaviorRelay<TextUtil.Result> nameValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final EditShowRoomScreen screen;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    EditShowRoomPresenter(@IOThread ThreadSchedulers threadSchedulers,
                          @ForActivity CompositeDisposable disposable,
                          EditShowRoomScreen screen,
                          UserManager userManager,
                          UserRepo userRepo) {
        super(screen);
        this.emailValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.nameValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }


    @Override
    protected void onCreate() {
        screen.setupEditText();
        fetchUserData();
        initializeInputsValidityObservable();
        fetchUserData();
    }

    private void fetchUserData() {
        screen.updateUi(userManager.getCurrentUser());
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                nameValidityRelay.hide(),
                emailValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result name, TextUtil.Result email) {
        return name.isValid() && email.isValid();
    }

    public void saveIsClicked(String apiToken,
                              String name,
                              String email,
                              String from,
                              String to,
                              String fromTwo,
                              String toTwo,
                              String image,
                              String dealingValue) {
        if (!nameValidityRelay.getValue().isValid()) {
            screen.showNameErrorMessage(nameValidityRelay.getValue().getMessageEn());
        } else {
            disposable.add(userRepo.updateShowRoom(apiToken, name, email, from, to, fromTwo, toTwo, image, dealingValue)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::onUpdatedSuccessfully, this::processError));
        }
    }

    void openGallery(RxPaparazzo.SingleSelectionBuilder<EditShowRoomActivity> pickedImageObservable) {
        disposable.add(
                pickedImageObservable.usingGallery()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::uploadFile, Timber::e));
    }

    private void uploadFile(File file) {
        screen.setSelectedImage(file);
        disposable.add(userRepo.uploadFile(userManager.getCurrentUser().getApiToken(), file, new RequestBodyProgress.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {

            }
        })
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::setNewImagePath, this::processError));
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            }
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

    void onAfterNameChange(TextUtil.Result result) {
        nameValidityRelay.accept(result);
    }

    void onAfterEmailChange(TextUtil.Result result) {
        emailValidityRelay.accept(result);
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}