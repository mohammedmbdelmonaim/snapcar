package com.intcore.snapcar.ui.addcarphotes;

import android.content.Context;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@ActivityScope
class AddCarPhotosPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final AddCarPhotosScreen screen;
    private final UserManager userManager;
    private final UserRepo userRepo;
    private final Context context;
    private int currentImagePos = 0;

    @Inject
    AddCarPhotosPresenter(@IOThread ThreadSchedulers threadSchedulers,
                          @ForActivity CompositeDisposable disposable,
                          @ForActivity Context context,
                          AddCarPhotosScreen screen,
                          UserManager userManager,
                          UserRepo userRepo) {
        super(screen);
        this.threadSchedulers = threadSchedulers;
        this.context = context;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    void openGallery(RxPaparazzo.SingleSelectionBuilder<AddCarPhotosActivity> pickedImageObservable, int pos) {
        this.currentImagePos = pos;
        disposable.add(
                pickedImageObservable.usingGallery()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .map(new Compressor(SnapCarApplication.getContext())::compressToFile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::uploadFile, Timber::e));
    }

    void openCamera(RxPaparazzo.SingleSelectionBuilder<AddCarPhotosActivity> pickedImageObservable, int pos) {
        this.currentImagePos = pos;
        screen.openCamera();
    }

    void uploadFile(File file) {
        screen.setuploadedPosition(currentImagePos, file);
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.uploadFile(apiToken, file,
                new RequestBodyProgress.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {
                        screen.updatePercentage(percentage);
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
                .doOnSubscribe(ignored -> screen.showPercentageLoadingAnimation())
                .doFinally(screen::hidePercentageLoadingAnimation)
                .subscribe(screen::setNewImagePath, this::processError));
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage((HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t))));
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

    private boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}