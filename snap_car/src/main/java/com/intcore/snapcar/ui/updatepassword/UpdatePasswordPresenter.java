package com.intcore.snapcar.ui.updatepassword;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import timber.log.Timber;

@ActivityScope
class UpdatePasswordPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> confirmNewPasswordValidityRelay;
    private final BehaviorRelay<TextUtil.Result> oldPasswordValidityRelay;
    private final BehaviorRelay<TextUtil.Result> newPasswordValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final UpdatePasswordScreen screen;
    private final UserManager userManager;
    private final UserRepo userRepo;
    private String newPassword;

    @Inject
    UpdatePasswordPresenter(@IOThread ThreadSchedulers threadSchedulers,
                            @ForActivity CompositeDisposable disposable,
                            UpdatePasswordScreen screen,
                            UserManager userManager,
                            UserRepo userRepo,
                            ResourcesUtil resourcesUtil) {
        super(screen);
        this.confirmNewPasswordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.oldPasswordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.newPasswordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.threadSchedulers = threadSchedulers;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        screen.setupEditText();
        initializeInputsValidityObservable();
    }

    void onAfterNewPasswordChange(TextUtil.Result result) {
        newPasswordValidityRelay.accept(result);
    }

    void onAfterOldPasswordChange(TextUtil.Result result) {
        oldPasswordValidityRelay.accept(result);
    }

    void observeIfPasswordsMatch(TextUtil.Result result) {
        confirmNewPasswordValidityRelay.accept(result);
    }

    private Boolean areAllInputsValid(TextUtil.Result newPassword, TextUtil.Result confirmNewPassword) {
        return newPassword.isValid() && confirmNewPassword.isValid();
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                confirmNewPasswordValidityRelay.hide(),
                newPasswordValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }


    void updateClicked(String oldPass, String newPass) {
        this.newPassword = newPass;
        if (!allInputsValidityRelay.getValue()) {

            if (!newPasswordValidityRelay.getValue().isValid()) {
                screen.newPasswordErrorMessage(newPasswordValidityRelay.getValue().getMessageEn());
            } else {
                screen.newPasswordErrorMessage("");
            }

            if (!confirmNewPasswordValidityRelay.getValue().isValid()) {
                screen.showConfirmPasswordErrorMessage(confirmNewPasswordValidityRelay.getValue().getMessageEn());
            } else {
                screen.showConfirmPasswordErrorMessage("");
            }


        } else {
            disposable.add(userRepo.updatePassword(userManager.getCurrentUser().getApiToken(), oldPass, newPass)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(() -> screen.hideLoadingAnimation())
                    .subscribe(this::updateFirebase, this::processError));
        }
    }

    private void updateFirebase(ResponseBody responseBody) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        user.updatePassword(newPassword)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        screen.hideLoadingAnimation();
        screen.passwordUpdated(responseBody);
//                    }else{
////                        screen.hideLoadingAnimation();
//                        screen.passwordUpdated(responseBody);
//                    }
//                });
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

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}