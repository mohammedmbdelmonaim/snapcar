package com.intcore.snapcar.ui.phasetworegisteration;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.rilixtech.CountryCodePicker;
import com.intcore.snapcar.core.chat.model.constants.Gender;
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
class PhaseTwoRegistrationPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> confirmPasswordValidityRelay;
    private final BehaviorRelay<TextUtil.Result> passwordValidityRelay;
    private final BehaviorRelay<List<CountryViewModel>> countriesRelay;
    private final BehaviorRelay<TextUtil.Result> emailValidityRelay;
    private final BehaviorRelay<TextUtil.Result> nameValidityRelay;
    private final BehaviorRelay<TextUtil.Result> areaValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final BehaviorRelay<CountryViewModel> countryRelay;
    private final BehaviorRelay<CountryViewModel> cityRelay;
    private final ThreadSchedulers threadSchedulers;
    private final PhaseTwoRegistrationScreen screen;
    private final BehaviorRelay<String> phoneRelay;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final UserRepo userRepo;
    private final Context context;

    @Inject
    PhaseTwoRegistrationPresenter(@IOThread ThreadSchedulers threadSchedulers,
                                  @ForActivity CompositeDisposable disposable,
                                  PhaseTwoRegistrationScreen screen,
                                  @ForActivity Context context,
                                  CountryMapper countryMapper,
                                  UserRepo userRepo) {
        super(screen);
        this.confirmPasswordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.passwordValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.emailValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.nameValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.areaValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countriesRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.countryRelay = BehaviorRelay.create();
        this.phoneRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.cityRelay = BehaviorRelay.create();
        this.countryMapper = countryMapper;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.context = context;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        PhaseTwoRegistrationActivityArgs phaseTwoRegistrationActivityArgs = PhaseTwoRegistrationActivityArgs.deserializeFrom(getIntent());
        phoneRelay.accept(phaseTwoRegistrationActivityArgs.getPhoneNumber());
        screen.setupEditText();
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
                    countriesRelay.accept(countryViewModels);
                }, this::processError));
    }

    private void processDefaultCountryCode(List<CountryViewModel> countryViewModels) {
        CountryCodePicker countryCodePicker = new CountryCodePicker(context);
        for (int i = 0; i < countryViewModels.size(); i++) {
            if (countryViewModels.get(i).getPhoneCode().equalsIgnoreCase(countryCodePicker.getDefaultCountryCode())) {
                screen.updateCountry(countryViewModels.get(i));
                break;
            } else {
                screen.updateCountry(countryViewModels.get(0));
            }
        }
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                nameValidityRelay.hide(),
                passwordValidityRelay.hide(),
                confirmPasswordValidityRelay.hide(),
                areaValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result name, TextUtil.Result password, TextUtil.Result confirmPassword, TextUtil.Result area) {
        return name.isValid() && password.isValid() && confirmPassword.isValid() && area.isValid();
    }

    void onAfterNameChange(TextUtil.Result result) {
        nameValidityRelay.accept(result);
    }

    void onAfterEmailChange(TextUtil.Result result) {
        emailValidityRelay.accept(result);
    }

    void onAfterPasswordChange(TextUtil.Result result) {
        passwordValidityRelay.accept(result);
    }

    void observeIfPasswordsMatch(TextUtil.Result result) {
        confirmPasswordValidityRelay.accept(result);
    }

    void onAfterAreaChange(TextUtil.Result result) {
        areaValidityRelay.accept(result);
    }

    void onRegisterClicked(String name, String email, String area, String password, @UserType int userType,
                           @Gender int gender) {
        screen.showEmailErrorMessage("");
        screen.showNameErrorMessage("");
        screen.showConfirmPasswordErrorMessage("");
        screen.showAreaErrorMessage("");
        screen.showPasswordErrorMessage("");

        if (!TextUtils.isEmpty(email)) {
            if (!emailValidityRelay.getValue().isValid()) {
                screen.showEmailErrorMessage(emailValidityRelay.getValue().getMessageEn());
                return;
            }
        }
        if (!allInputsValidityRelay.getValue()) {
            if (!nameValidityRelay.getValue().isValid()) {
                screen.showNameErrorMessage(nameValidityRelay.getValue().getMessageEn());
            } else {
                screen.showNameErrorMessage("");
            }

            if (!confirmPasswordValidityRelay.getValue().isValid()) {
                screen.showConfirmPasswordErrorMessage(confirmPasswordValidityRelay.getValue().getMessageEn());
            } else {
                screen.showConfirmPasswordErrorMessage("");
            }
            if (!areaValidityRelay.getValue().isValid()) {
                screen.showAreaErrorMessage(areaValidityRelay.getValue().getMessageEn());
            } else {
                screen.showAreaErrorMessage("");
            }
            if (!passwordValidityRelay.getValue().isValid()) {
                screen.showPasswordErrorMessage(passwordValidityRelay.getValue().getMessageEn());
            } else {
                screen.showPasswordErrorMessage("");
            }
        } else if (userType == 0) {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.you_should_select_user_type));
        } else if (userType == UserType.USER && gender == 0) {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.you_should_select_your_gender));
        } else {
            screen.shouldNavigateToHost(name, email, area, password, phoneRelay.getValue());
        }
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            screen.showErrorMessage(getHttpErrorMessage((HttpException) t));
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

    CountryViewModel getSelectedCountry() {
        return countryRelay.getValue();
    }

    void setSelectedCountry(CountryViewModel countryViewModel) {
        countryRelay.accept(countryViewModel);
    }

    CountryViewModel getSelectedCity() {
        return cityRelay.getValue();
    }

    void setSelectedCity(CountryViewModel countryViewModel) {
        cityRelay.accept(countryViewModel);
    }

    List<CountryViewModel> getCountryList() {
        return countriesRelay.getValue();
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}