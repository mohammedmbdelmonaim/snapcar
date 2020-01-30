package com.intcore.snapcar.ui.editUser;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class EditUserPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<List<CountryViewModel>> countriesRelay;
    private final BehaviorRelay<TextUtil.Result> emailValidityRelay;
    private final BehaviorRelay<TextUtil.Result> nameValidityRelay;
    private final BehaviorRelay<TextUtil.Result> areaValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final BehaviorRelay<CountryViewModel> countryRelay;
    private final BehaviorRelay<CountryViewModel> cityRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final CountryMapper countryMapper;
    private final EditUserScreen editScreen;
    private final UserManager userManager;
    private final UserRepo userRepo;
    private String emailText;

    @Inject
    EditUserPresenter(UserManager userManager, EditUserScreen editScreen,
                      @ForActivity CompositeDisposable disposable,
                      @IOThread ThreadSchedulers threadSchedulers,
                      CountryMapper countryMapper,
                      UserRepo userRepo) {
        super(editScreen);
        this.emailValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.nameValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.areaValidityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countriesRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.countryRelay = BehaviorRelay.create();
        this.cityRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.countryMapper = countryMapper;
        this.userManager = userManager;
        this.disposable = disposable;
        this.editScreen = editScreen;
        this.userRepo = userRepo;
    }

    @Override
    protected void onCreate() {
        editScreen.setupEditText();
        fetchUserData();
        initializeInputsValidityObservable();
        fetchCountryData();
    }

    private void fetchUserData() {
        editScreen.updateUi(userManager.getCurrentUser());
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
        for (int i = 0; i < countryViewModels.size(); i++) {
            if (countryViewModels.get(i).getId() == userManager.getCurrentUser().getCountryModel().getId()) {
                setSelectedCountry(countryViewModels.get(i));
                for (int j = 0; j < countryViewModels.get(i).getCountryViewModels().size(); j++) {
                    if (countryViewModels.get(i).getCountryViewModels().get(j).getId() == userManager.getCurrentUser().getCityModel().getId())
                        setSelectedCity(countryViewModels.get(i).getCountryViewModels().get(j));
                }
            }
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            editScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                editScreen.processLogout();
            }
        }else if (t instanceof IOException) {
            editScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            editScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    void onAfterNameChange(TextUtil.Result result) {
        nameValidityRelay.accept(result);
    }

    void onAfterEmailChange(TextUtil.Result result) {
        emailValidityRelay.accept(result);
    }

    void onAfterAreaChange(TextUtil.Result result) {
        areaValidityRelay.accept(result);
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                nameValidityRelay.hide(),
                areaValidityRelay.hide(),
                emailValidityRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result name, TextUtil.Result area, TextUtil.Result email) {
        return name.isValid() && email.isValid() && area.isValid();
    }

    public void saveIsClicked(String name, String email, int country, int city, String area, String image) {

        if (!allInputsValidityRelay.getValue() && !email.isEmpty()) {
            if (!nameValidityRelay.getValue().isValid()) {
                editScreen.showNameErrorMessage(nameValidityRelay.getValue().getMessageEn());
            }

            if (!emailValidityRelay.getValue().isValid() && !email.isEmpty()) {
                editScreen.showEmailErrorMessage(emailValidityRelay.getValue().getMessageEn());
            }
            if (!areaValidityRelay.getValue().isValid()) {
                editScreen.showAreaErrorMessage(areaValidityRelay.getValue().getMessageEn());
            }

        } else {
            disposable.add(userRepo.updateUser(userManager.getCurrentUser().getApiToken(), name, email, country, city, area, image)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> editScreen.showLoadingAnimation())
                    .doFinally(editScreen::hideLoadingAnimation)
                    .subscribe(editScreen::onUpdatedSuccessfully, this::processError));
        }
    }

    void openGallery(RxPaparazzo.SingleSelectionBuilder<EditUserActivity> pickedImageObservable) {
        disposable.add(
                pickedImageObservable.usingGallery()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::uploadFile, Timber::e));
    }

    private void uploadFile(File file) {
        editScreen.setSelectedImage(file);
        disposable.add(userRepo.uploadFile(userManager.getCurrentUser().getApiToken(), file, new RequestBodyProgress.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {

            }
        })
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> editScreen.showLoadingAnimation())
                .doFinally(editScreen::hideLoadingAnimation)
                .subscribe(editScreen::setNewImagePath, this::processError));
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}