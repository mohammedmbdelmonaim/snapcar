package com.intcore.snapcar.ui.login;

import android.content.Context;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.rilixtech.CountryCodePicker;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class LoginPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> passwordTextValidityRelay;
    private final BehaviorRelay<TextUtil.Result> phoneTextValidityRelay;
    private final BehaviorRelay<List<CountryViewModel>> countryRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final LoginScreen loginScreen;
    private final UserRepo userRepo;
    private final Context context;

    @Inject
    LoginPresenter(@IOThread ThreadSchedulers threadSchedulers,
                   @ForActivity CompositeDisposable disposable,
                   CountryMapper countryMapper,
                   LoginScreen loginScreen,
                   UserRepo userRepo,
                   @ForActivity Context context) {
        super(loginScreen);
        this.passwordTextValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.phoneTextValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countryRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.loginScreen = loginScreen;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.context = context;
    }

    @Override
    protected void onCreate() {
        initializeInputsValidityObservable();
        fetchCountryData();
    }

    private void fetchCountryData() {
        disposable.add(userRepo.fetchCountryList()
                .map(countryMapper::toCountryViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(countryViewModels -> {
                    processDefaultCountryCode(countryViewModels);
                    countryRelay.accept(countryViewModels);
                }, this::processError));
    }

    private void processDefaultCountryCode(List<CountryViewModel> countryViewModels) {
        CountryCodePicker countryCodePicker = new CountryCodePicker(context);
        for (int i = 0; i < countryViewModels.size(); i++) {
            if (countryViewModels.get(i).getPhoneCode()
                    .equalsIgnoreCase(countryCodePicker.getDefaultCountryCode())) {
                loginScreen.setupEditText(countryViewModels.get(i).getPhoneRegex());
                loginScreen.updateCountry(countryViewModels.get(i));
                break;
            } else {
                loginScreen.setupEditText(countryViewModels.get(0).getPhoneRegex());
                loginScreen.updateCountry(countryViewModels.get(0));
            }
        }
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                passwordTextValidityRelay.hide(),
                phoneTextValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result password, TextUtil.Result phone) {
        return password.isValid() && phone.isValid();
    }

    void onAfterPhoneChange(TextUtil.Result result) {

        phoneTextValidityRelay.accept(result);
    }

    void onAfterPasswordChange(TextUtil.Result result) {
        passwordTextValidityRelay.accept(result);
    }

    void onSignInClicked(String phone, String password) {
        if (!allInputsValidityRelay.getValue()) {
            if (!phoneTextValidityRelay.getValue().isValid()) {
                loginScreen.showPhoneErrorMsg(phoneTextValidityRelay.getValue().getMessageEn());
            } else {
                loginScreen.showPhoneErrorMsg(null);
            }
            if (!passwordTextValidityRelay.getValue().isValid()) {
                loginScreen.showPasswordErrorMsg(passwordTextValidityRelay.getValue().getMessageEn());
            } else {
                loginScreen.showPasswordErrorMsg(null);
            }
            if (!phoneTextValidityRelay.getValue().isValid() || !passwordTextValidityRelay.getValue().isValid()) {
                if (getResourcesUtil().isEnglish()) {
                    loginScreen.showErrorMessage("The Password is Invalid or the User is not registered");
                } else {
                    loginScreen.showErrorMessage("كلمة المرور خاطئة أو لم يتم التسجيل");
                }
            }
        } else {
            signIn(phone, password);
        }
    }

    private void signIn(String phone, String password) {
        loginScreen.shouldNavigateToHome(phone, password);
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            loginScreen.showErrorMessage(getHttpErrorMessage((HttpException) t));
        } else if (t instanceof IOException) {
            loginScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            loginScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        if (httpException.code() == 422) {
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
        } else {
            return getResourcesUtil().getString(R.string.error_communicating_with_server);
        }
    }

    List<CountryViewModel> getCountryList() {
        return countryRelay.getValue();
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}