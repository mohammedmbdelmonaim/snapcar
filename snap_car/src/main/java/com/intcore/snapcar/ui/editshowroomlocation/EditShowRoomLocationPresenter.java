package com.intcore.snapcar.ui.editshowroomlocation;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
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
class EditShowRoomLocationPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<List<CountryViewModel>> countriesRelay;
    private final BehaviorRelay<TextUtil.Result> areaValidityRelay;
    private final BehaviorRelay<CountryViewModel> countryRelay;
    private final BehaviorRelay<CountryViewModel> cityRelay;
    private final EditShowRoomLocationScreen screen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    EditShowRoomLocationPresenter(
            @IOThread ThreadSchedulers threadSchedulers,
            @ForActivity CompositeDisposable disposable,
            EditShowRoomLocationScreen screen,
            UserManager userManager,
            UserRepo userRepo,
            CountryMapper countryMapper) {
        super(screen);
        this.areaValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countriesRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.countryRelay = BehaviorRelay.create();
        this.cityRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = screen;
    }

    @Override
    protected void onCreate() {
        screen.setupEditText();
        fetchCountryData();
        fetchUserData();
    }

    private void fetchUserData() {
        if (userManager.sessionManager().isSessionActive())
            screen.updateArea(userManager.getCurrentUser());
    }

    List<CountryViewModel> getCountryList() {
        return countriesRelay.getValue();
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
        if (userManager.getCurrentUser().getCountryModel() != null) {
            for (int i = 0; i < countryViewModels.size(); i++) {
                if (countryViewModels.get(i).getId() == userManager.getCurrentUser().getCountryModel().getId()) {
                    setSelectedCountry(countryViewModels.get(i));
                    screen.updateCountry(countryViewModels.get(i));
                    for (int j = 0; j < countryViewModels.get(i).getCountryViewModels().size(); j++) {
                        if (userManager.getCurrentUser().getCityModel() != null) {
                            if (countryViewModels.get(i).getCountryViewModels().get(j).getId() == userManager.getCurrentUser().getCityModel().getId()) {
                                setSelectedCity(countryViewModels.get(i).getCountryViewModels().get(j));
                                screen.updateCity(countryViewModels.get(i).getCountryViewModels().get(j));
                            }
                        }
                    }
                }
            }
        }
    }

    CountryViewModel getSelectedCountry() {
        return countryRelay.getValue();
    }

    void setSelectedCountry(CountryViewModel countryViewModel) {
        countryRelay.accept(countryViewModel);
    }

    void setSelectedCity(CountryViewModel countryViewModel) {
        cityRelay.accept(countryViewModel);
    }

    public void saveIsClicked(String area, String lon, String lat) {
        if (!areaValidityRelay.getValue().isValid()) {
            screen.showErrorMessage(areaValidityRelay.getValue().getMessageEn());
        } else if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lon)) {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.you_should_pick_your_location));
        } else if (countryRelay.getValue() != null && cityRelay.getValue() != null) {
            disposable.add(userRepo.updateLocation(userManager.getCurrentUser().getApiToken(),
                    countryRelay.getValue().getId(),
                    cityRelay.getValue().getId(),
                    area,
                    lon,
                    lat)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .subscribe(this::updatedUser, this::processError));
        }
    }

    private void updatedUser(DefaultUserModel defaultUserModel) {
        disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::onUpdatedSuccessfully, this::processError));
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage((HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t))));
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

    void onAfterNameChange(TextUtil.Result result) {
        areaValidityRelay.accept(result);
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

    public void onAreaChange(TextUtil.Result result) {
        areaValidityRelay.accept(result);
    }
}