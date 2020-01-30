package com.intcore.snapcar.ui.editphoneactivition;

import android.content.Context;
import android.os.CountDownTimer;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class EditPhoneActivationPresenter extends BaseActivityPresenter {

    private static final long TIME_OUT = 30000;
    private static final long INTERVAL = 1000;
    private final EditPhoneActivationScreen editPhoneActivationScreen;
    private final BehaviorRelay<Boolean> canResendRelay;
    private final ThreadSchedulers threadSchedulers;
    private final BehaviorRelay<Integer> flagRelay;
    private final BehaviorRelay<String> phoneRelay;
    private final BehaviorRelay<String> codeRelay;
    private final BehaviorRelay<String> tokenRely;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final UserRepo userRepo;
    private EditPhoneActivationActivityArgs activationArgs;
    private CountDownTimer countDownTimer;

    @Inject
    EditPhoneActivationPresenter(@ForActivity CompositeDisposable disposable,
                                 EditPhoneActivationScreen editPhoneActivationScreen,
                                 @IOThread ThreadSchedulers threadSchedulers,
                                 EditPhoneActivationScreen screen,
                                 @ForActivity Context context,
                                 CountryMapper countryMapper,
                                 UserManager userManager,
                                 UserRepo userRepo) {
        super(editPhoneActivationScreen);
        this.canResendRelay = BehaviorRelay.createDefault(true);
        this.phoneRelay = BehaviorRelay.create();
        this.codeRelay = BehaviorRelay.create();
        this.tokenRely = BehaviorRelay.create();
        this.flagRelay = BehaviorRelay.create();
        this.editPhoneActivationScreen = editPhoneActivationScreen;
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
    }

    @Override
    protected void onCreate() {
        activationArgs = EditPhoneActivationActivityArgs.deserializeFrom(getIntent());
        editPhoneActivationScreen.setupEditText();
        flagRelay.accept(activationArgs.getTypeFlag());
        if (activationArgs.getPhoneNumber() != null) {
            phoneRelay.accept(activationArgs.getPhoneNumber());
            editPhoneActivationScreen.updatePhoneField(phoneRelay.getValue());
        }
        if (activationArgs.getApiToken() != null)
            tokenRely.accept(activationArgs.getApiToken());
        codeRelay.accept(activationArgs.getCode());
        startCountDownTimer();
    }

    void onAfterTextChange(CharSequence activationCode) {
        String value = codeRelay.getValue();
        Boolean valid = activationCode.toString().contentEquals(value);
        editPhoneActivationScreen.setActivationError(valid);
        canResendRelay.accept(!valid);
        editPhoneActivationScreen.setPinEntryEnabled(!valid);
        editPhoneActivationScreen.setCounterVisibility(!valid);
        editPhoneActivationScreen.setDoneButtonEnabled(valid);
    }

    void onActivateClicked() {
        if (activationArgs.getPreActivity() != null) {
            if (activationArgs.getPreActivity().equals("updatePhone")) {
                disposable.add(userRepo.updateUserPhone(userManager.getCurrentUser().getApiToken(), activationArgs.getPhoneNumber(), activationArgs.getCode())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> editPhoneActivationScreen.showLoadingAnimation())
                        .doFinally(editPhoneActivationScreen::hideLoadingAnimation)
                        .subscribe(editPhoneActivationScreen::onUpdatedSuccessfully, this::processError));
            }
        }
        if (2 == AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION) {
            editPhoneActivationScreen.onActivationReady(AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION, phoneRelay.getValue(), codeRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PASSWORD_RESET) {
            editPhoneActivationScreen.onActivationReady(AuthenticationOperationListener.OperationType.PASSWORD_RESET, phoneRelay.getValue(), codeRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PHONE_VALIDATION) {
            editPhoneActivationScreen.onActivationReady(AuthenticationOperationListener.OperationType.PHONE_VALIDATION, phoneRelay.getValue(), codeRelay.getValue());
        }
    }

    void onResendClicked() {
        if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION) {
            editPhoneActivationScreen.onResendCodeRequested(flagRelay.getValue(), phoneRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PASSWORD_RESET) {
            editPhoneActivationScreen.onResendCodeRequested(flagRelay.getValue(), phoneRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PHONE_VALIDATION) {
            editPhoneActivationScreen.onResendCodeRequested(flagRelay.getValue(), tokenRely.getValue());
        }
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            editPhoneActivationScreen.showErrorMessage(getHttpErrorMessage((HttpException) t));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                editPhoneActivationScreen.processLogout();
            }
        } else if (t instanceof IOException) {
            editPhoneActivationScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            editPhoneActivationScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    private void startCountDownTimer() {
        editPhoneActivationScreen.setRetryButtonEnabled(false);
        countDownTimer = new CountDownTimer(TIME_OUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                editPhoneActivationScreen.updateRetryCounter("00:".concat(String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                editPhoneActivationScreen.updateRetryCounter(getResourcesUtil().getString(R.string.resend));
                if (canResendRelay.getValue()) {
                    editPhoneActivationScreen.setRetryButtonEnabled(true);
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        disposable.clear();
    }

    void onCodeResent(String value) {
        codeRelay.accept(value);
        Timber.tag("manarDebug").v(codeRelay.getValue());
        startCountDownTimer();
    }

    String getCode() {
        return codeRelay.getValue();
    }

    public String getPhone() {
        return phoneRelay.getValue();
    }
}