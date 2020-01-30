package com.intcore.snapcar.ui.resetpassword;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class ResetPasswordPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> confirmPasswordValidityRelay;
    private final BehaviorRelay<TextUtil.Result> passwordValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final ResetPasswordScreen resetPasswordScreen;
    private final ThreadSchedulers threadSchedulers;
    private final BehaviorRelay<String> phoneRelay;
    private final BehaviorRelay<String> codeRelay;
    private final CompositeDisposable disposable;

    @Inject
    ResetPasswordPresenter(@IOThread ThreadSchedulers threadSchedulers,
                           @ForActivity CompositeDisposable disposable,
                           ResetPasswordScreen resetPasswordScreen) {
        super(resetPasswordScreen);
        this.confirmPasswordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.passwordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.resetPasswordScreen = resetPasswordScreen;
        this.threadSchedulers = threadSchedulers;
        this.phoneRelay = BehaviorRelay.create();
        this.codeRelay = BehaviorRelay.create();
        this.disposable = disposable;
    }

    @Override
    protected void onCreate() {
        resetPasswordScreen.setupEditText();
        PasswordResetActivityArgs activityArgs = PasswordResetActivityArgs.deserializeFrom(getIntent());
        if (activityArgs.getPhoneNumber() != null){
            phoneRelay.accept(activityArgs.getPhoneNumber());
        }
        if (codeRelay != null) {
            codeRelay.accept(activityArgs.getCode());
        }
        initializeInputsValidityObservable();
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                passwordValidityRelay.hide(),
                confirmPasswordValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result password, TextUtil.Result confirmPassword) {
        return password.isValid() && confirmPassword.isValid();
    }

    void onAfterPasswordChange(TextUtil.Result result) {
        passwordValidityRelay.accept(result);
    }

    void observeIfPasswordsMatch(TextUtil.Result result) {
        confirmPasswordValidityRelay.accept(result);
    }

    void onResetPasswordClicked(String password) {
        if (!allInputsValidityRelay.getValue()) {
            if (!passwordValidityRelay.getValue().isValid()) {
                resetPasswordScreen.showPasswordErrorMessage(passwordValidityRelay.getValue().getMessageEn());
            }
            if (!confirmPasswordValidityRelay.getValue().isValid()) {
                resetPasswordScreen.showConfirmPasswordErrorMessage(confirmPasswordValidityRelay.getValue().getMessageEn());
            }
        } else {
            resetPasswordScreen.onReadyToResetPassword(phoneRelay.getValue(), codeRelay.getValue());
        }
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            resetPasswordScreen.showErrorMessage(getHttpErrorMessage((HttpException) t));
        }else if (t instanceof IOException) {
            resetPasswordScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            resetPasswordScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}