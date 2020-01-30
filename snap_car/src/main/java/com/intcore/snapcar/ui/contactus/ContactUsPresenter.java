package com.intcore.snapcar.ui.contactus;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsModel;
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
class ContactUsPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<SettingsModel> settingsModelBehaviorRelay;
    private final BehaviorRelay<TextUtil.Result> messageRelay;
    private final BehaviorRelay<TextUtil.Result> emailRelay;
    private final BehaviorRelay<TextUtil.Result> nameRelay;
    private final BehaviorRelay<Boolean> allInputValidity;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ContactUsScreen screen;
    private final UserRepo userRepo;

    @Inject
    ContactUsPresenter(@IOThread ThreadSchedulers threadSchedulers,
                       @ForActivity CompositeDisposable disposable,
                       ContactUsScreen screen,
                       UserRepo userRepo) {
        super(screen);
        this.messageRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.emailRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.nameRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.allInputValidity = BehaviorRelay.createDefault(false);
        this.settingsModelBehaviorRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        screen.setupEditText();
        fetchPolicyUsData();
        initializeInputsValidityObservable();
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                nameRelay.hide(),
                emailRelay.hide(),
                messageRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputValidity, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result name, TextUtil.Result email, TextUtil.Result message) {
        return name.isValid() && email.isValid() && message.isValid();
    }

    private void fetchPolicyUsData() {
        disposable.add(userRepo.getSittings()
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(settingsModel -> {
                    settingsModelBehaviorRelay.accept(settingsModel);
                    screen.updateUi(settingsModel);
                }, this::processError));
    }

    void onAfterNameChange(TextUtil.Result result) {
        nameRelay.accept(result);
    }

    void onAfterEmailChange(TextUtil.Result result) {
        emailRelay.accept(result);
    }

    void onAfterMessageChange(TextUtil.Result result) {
        messageRelay.accept(result);
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
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

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

    public void onSaveClicked(String name, String email, String message) {
        if (!allInputValidity.getValue()) {
            if (!nameRelay.getValue().isValid()) {
                screen.showNameErrorMsg(nameRelay.getValue().getMessageEn());
            } else {
                screen.showNameErrorMsg(null);
            }
            if (!emailRelay.getValue().isValid()) {
                screen.showEmailErrorMsg(emailRelay.getValue().getMessageEn());
            } else {
                screen.showEmailErrorMsg(null);
            }
            if (!messageRelay.getValue().isValid()) {
                screen.showMessageErrorMsg(messageRelay.getValue().getMessageEn());
            } else {
                screen.showMessageErrorMsg(null);
            }
        } else {
            postMessage(name, email, message);
        }
    }

    private void postMessage(String name, String email, String message) {
        disposable.add(userRepo.postContactUs(name, email, message)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::onSaveSuccessfully, this::processError));
    }

    void onPhonesClicked() {
        screen.onPhoneClicked(settingsModelBehaviorRelay.getValue().getPhones());
    }
}