package com.intcore.snapcar.ui.forgetpassword;

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

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class ForgetPasswordPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> phoneTextValidityRelay;
    private final BehaviorRelay<List<CountryViewModel>> countryRelay;
    private final ForgetPasswordScreen forgetPasswordScreen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final UserRepo userRepo;
    private final Context context;

    @Inject
    ForgetPasswordPresenter(ForgetPasswordScreen forgetPasswordScreen,
                            @IOThread ThreadSchedulers threadSchedulers,
                            @ForActivity CompositeDisposable disposable,
                            CountryMapper countryMapper,
                            UserRepo userRepo,
                            @ForActivity Context context) {
        super(forgetPasswordScreen);
        this.phoneTextValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countryRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.forgetPasswordScreen = forgetPasswordScreen;
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.context = context;
    }

    @Override
    protected void onCreate() {
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
            if (countryViewModels.get(i).getPhoneCode().equalsIgnoreCase(countryCodePicker.getDefaultCountryCode())) {
                forgetPasswordScreen.setupEditText(countryViewModels.get(i).getPhoneRegex());
                forgetPasswordScreen.updateCountry(countryViewModels.get(i));
                break;
            } else {
                forgetPasswordScreen.setupEditText(countryViewModels.get(0).getPhoneRegex());
                forgetPasswordScreen.updateCountry(countryViewModels.get(0));
            }
        }
    }

    void onAfterPhoneChange(TextUtil.Result result) {
        phoneTextValidityRelay.accept(result);
    }

    void onProceedClicked(String phone) {
        if (!phoneTextValidityRelay.getValue().isValid()) {
            forgetPasswordScreen.showPhoneError(phoneTextValidityRelay.getValue().getMessageEn());
        } else {
            forgetPasswordScreen.onActivationNeeded(phone);
        }
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof HttpException) {
            forgetPasswordScreen.showErrorMessage(getHttpErrorMessage((HttpException) t));
        } else if (t instanceof IOException) {
            forgetPasswordScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            forgetPasswordScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
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

    List<CountryViewModel> getCountryList() {
        return countryRelay.getValue();
    }
}