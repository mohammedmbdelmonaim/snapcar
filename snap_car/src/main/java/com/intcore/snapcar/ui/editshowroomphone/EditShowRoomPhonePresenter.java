package com.intcore.snapcar.ui.editshowroomphone;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
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
class EditShowRoomPhonePresenter extends BaseActivityPresenter {

    private final BehaviorRelay<TextUtil.Result> phoneTextValidityRelay;
    private final BehaviorRelay<List<CountryViewModel>> countryRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final EditShowRoomPhoneScreen screen;
    private final CountryMapper countryMapper;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    EditShowRoomPhonePresenter(@IOThread ThreadSchedulers threadSchedulers,
                               @ForActivity CompositeDisposable disposable,
                               EditShowRoomPhoneScreen screen,
                               CountryMapper countryMapper,
                               UserManager userManager,
                               UserRepo userRepo) {
        super(screen);
        this.phoneTextValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countryRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        fetchUserData();
    }

    private void fetchUserData() {
        disposable.add(userRepo.fetchCountryList()
                .map(countryMapper::toCountryViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(countryViewModels -> {
                    processDefaultCountryCode(countryViewModels);
                    countryRelay.accept(countryViewModels);
                }, this::processError));
        screen.updateUi(userManager.getCurrentUser());
    }

    public void saveIsClicked(String mainPhone, List<String> phones) {
        Boolean isValid;
        if (!phoneTextValidityRelay.getValue().isValid()) {
            screen.showPhoneErrorMessage(phoneTextValidityRelay.getValue().getMessageEn());
            isValid = false;
        } else {
            screen.showPhoneErrorMessage(null);
            isValid = true;
        }
        for (int i = 0; i < phones.size(); i++) {
            if (!TextUtils.isEmpty(phones.get(i).replaceAll("'", ""))) {
                if (!TextUtils.isEmpty(phones.get(i).split("-")[1].replaceAll("'", ""))) {
                    if (phones.get(i).split("-")[1].replaceAll("'", "").length() < 9 ||
                            phones.get(i).split("-")[1].replaceAll("'", "").length() > 10) {
                        screen.showphoneError(getResourcesUtil().getString(R.string.invalid_phone_number), i);
                        isValid = false;
                    }
                }
            }
        }
        if (isValid) updatePhone(mainPhone, phones.toString());
    }

    private void updatePhone(String mainPhone, String phones) {
        disposable.add(userRepo.updateShowRoomPhones(userManager.getCurrentUser().getApiToken(), mainPhone, phones)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::onUpdatedSuccessfully, this::processError));
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
                return getResourcesUtil().getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return getResourcesUtil().getString(R.string.error_communicating_with_server);
        }
    }

    private void processDefaultCountryCode(List<CountryViewModel> countryViewModels) {
        for (int i = 0; i < countryViewModels.size(); i++) {
            if (countryViewModels.get(i).getPhoneCode().equals(userManager.getCurrentUser().getPhone().split("-")[0])) {
                screen.setupEditText(countryViewModels.get(i).getPhoneRegex());
                screen.updateCountry(countryViewModels.get(i));
                break;
            } else {
                screen.setupEditText(countryViewModels.get(0).getPhoneRegex());
                screen.updateCountry(countryViewModels.get(0));
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

    void onAfterPhoneChange(TextUtil.Result result) {
        phoneTextValidityRelay.accept(result);
    }

    List<CountryViewModel> getCountryList() {
        return countryRelay.getValue();
    }
}