package com.intcore.snapcar.ui.phaseoneregisteration;

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
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class PhaseOneRegistrationPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> phoneTextValidityRelay;
    private final BehaviorRelay<List<CountryViewModel>> countryRelay;
    private final PhaseOneRegistrationScreen screen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final UserRepo userRepo;
    private final Context context;

    @Inject
    PhaseOneRegistrationPresenter(@IOThread ThreadSchedulers threadSchedulers,
                                  @ForActivity CompositeDisposable disposable,
                                  PhaseOneRegistrationScreen screen,
                                  @ForActivity Context context,
                                  CountryMapper countryMapper,
                                  UserRepo userRepo) {
        super(screen);
        this.phoneTextValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countryRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.context = context;
        this.screen = screen;
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
                screen.setupEditText(countryViewModels.get(i).getPhoneRegex());
                screen.updateCountry(countryViewModels.get(i));
                break;
            } else {
                screen.setupEditText(countryViewModels.get(0).getPhoneRegex());
                screen.updateCountry(countryViewModels.get(0));
            }
        }
    }

    void onAfterPhoneChange(TextUtil.Result result) {
        phoneTextValidityRelay.accept(result);
    }

    void onProceedClicked(String phone, String countryCode) {
        if (!phoneTextValidityRelay.getValue().isValid()) {
            screen.showPhoneError(phoneTextValidityRelay.getValue().getMessageEn());
        } else {
            disposable.add(userRepo.validatePhone(countryCode + "-" + phone, LocaleUtil.getLanguage(context))
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(code -> screen.onActivationNeeded(countryCode + "-" + phone, code), this::processError));

        }
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

    List<CountryViewModel> getCountryList() {
        return countryRelay.getValue();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

}