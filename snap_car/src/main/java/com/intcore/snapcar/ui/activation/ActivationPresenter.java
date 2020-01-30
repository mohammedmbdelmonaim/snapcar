package com.intcore.snapcar.ui.activation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

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
class ActivationPresenter extends BaseActivityPresenter {

    private static final long TIME_OUT = 30000;
    private static final long INTERVAL = 1000;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private final BehaviorRelay<Boolean> canResendRelay;
    private final ActivationScreen activationScreen;
    private final BehaviorRelay<Integer> flagRelay;
    private final BehaviorRelay<String> phoneRelay;
    private final BehaviorRelay<String> codeRelay;
    private final BehaviorRelay<String> tokenRely;
    private final CompositeDisposable disposable;
    private final UserRepo userRepo;
    private final UserManager userManager;
    private final ThreadSchedulers threadSchedulers;
    private CountDownTimer countDownTimer;
    private ActivationActivityArgs activationArgs;
    private BroadcastReceiver receiver;

    @Inject
    ActivationPresenter(@ForActivity CompositeDisposable disposable,
                        ActivationScreen activationScreen,
                        @IOThread ThreadSchedulers threadSchedulers,
                        ActivationScreen screen,
                        @ForActivity Context context,
                        UserManager userManager,
                        CountryMapper countryMapper,
                        UserRepo userRepo) {

        super(activationScreen);
        this.canResendRelay = BehaviorRelay.createDefault(true);
        this.phoneRelay = BehaviorRelay.create();
        this.codeRelay = BehaviorRelay.create();
        this.tokenRely = BehaviorRelay.create();
        this.flagRelay = BehaviorRelay.create();
        this.activationScreen = activationScreen;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.userManager = userManager;
        this.threadSchedulers = threadSchedulers;
    }

    @Override
    protected void onCreate() {
        activationArgs = ActivationActivityArgs.deserializeFrom(getIntent());
        activationScreen.setupEditText();
        flagRelay.accept(activationArgs.getTypeFlag());
        if (activationArgs.getPhoneNumber() != null) {
            phoneRelay.accept(activationArgs.getPhoneNumber());
            activationScreen.updatePhoneField(phoneRelay.getValue());
        }
        if (activationArgs.getApiToken() != null)
            tokenRely.accept(activationArgs.getApiToken());
        codeRelay.accept(activationArgs.getCode());
        Timber.tag("manarDebug").v(codeRelay.getValue());
        startCountDownTimer();

        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SMS_RECEIVED)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        final SmsMessage[] messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++) {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        if (messages.length > -1) {
                            if (messages[0].getMessageBody().contains("Your verification code is ")) {
                                String code = messages[0].getMessageBody().replace("Your verification code is ", "");
                                if (code.length() == 4) {
                                    activationScreen.setPinEntrycode(code);
                                }
                            } else if (messages[0].getMessageBody().contains("Your code reset password code is ")) {
                                String code = messages[0].getMessageBody().replace("Your code reset password code is ", "");
                                if (code.length() == 4) {
                                    activationScreen.setPinEntrycode(code);
                                }
                            }
                        }
                    }
                }
            }
        };
//        activationScreen.registerReciever(receiver, filter);
    }

    void onAfterTextChange(CharSequence activationCode) {
        String value = codeRelay.getValue();
        Boolean valid = replaceArabicNumbers(activationCode.toString()).contentEquals(value);
        activationScreen.setActivationError(valid);
        canResendRelay.accept(!valid);
        activationScreen.setPinEntryEnabled(!valid);
        activationScreen.setCounterVisibility(!valid);
        activationScreen.setDoneButtonEnabled(valid);
    }

    private String replaceArabicNumbers(String original) {
        return original.
                replaceAll("٠", "1")
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9");
    }

    void onActivateClicked() {
        if (!TextUtils.isEmpty(activationArgs.getPreActivity())) {
            if (activationArgs.getPreActivity().equals("updatePhone")) {
                disposable.add(userRepo.updateUserPhone(userManager.getCurrentUser().getApiToken(), activationArgs.getPhoneNumber(), activationArgs.getCode())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> activationScreen.showLoadingAnimation())
                        .doFinally(activationScreen::hideLoadingAnimation)
                        .subscribe(activationScreen::onUpdatedSuccessfully, this::processError));
            }
        } else {
            if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION) {
                activationScreen.onActivationReady(AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION, phoneRelay.getValue(), codeRelay.getValue());
            } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PASSWORD_RESET) {
                activationScreen.onActivationReady(AuthenticationOperationListener.OperationType.PASSWORD_RESET, phoneRelay.getValue(), codeRelay.getValue());
            } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PHONE_VALIDATION) {
                activationScreen.onActivationReady(AuthenticationOperationListener.OperationType.PHONE_VALIDATION, phoneRelay.getValue(), codeRelay.getValue());
            }
        }
    }

    void onResendClicked() {
        if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION) {
            activationScreen.onResendCodeRequested(flagRelay.getValue(), phoneRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PASSWORD_RESET) {
            activationScreen.onResendCodeRequested(flagRelay.getValue(), phoneRelay.getValue());
        } else if (flagRelay.getValue() == AuthenticationOperationListener.OperationType.PHONE_VALIDATION) {
            activationScreen.onResendCodeRequested(flagRelay.getValue(), tokenRely.getValue());
        }
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            activationScreen.showErrorMessage(getHttpErrorMessage((HttpException) t));
        } else if (t instanceof IOException) {
            activationScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            activationScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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
        activationScreen.setRetryButtonEnabled(false);
        countDownTimer = new CountDownTimer(TIME_OUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                activationScreen.updateRetryCounter("00:".concat(String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                activationScreen.updateRetryCounter(getResourcesUtil().getString(R.string.resend));
                if (canResendRelay.getValue()) {
                    activationScreen.setRetryButtonEnabled(true);
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
        activationScreen.unregisterBroadCast(receiver);
    }

    void onCodeResent(String value) {
        codeRelay.accept(value);
        Log.e("activation code" ,codeRelay.getValue());
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