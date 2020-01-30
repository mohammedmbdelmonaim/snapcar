package com.intcore.snapcar.ui.addinterest;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.InterestRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.constant.PaymentType;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class AddInterestPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<SearchRequestModel> searchRequestRelay;
    private final BehaviorRelay<CarColorViewModel> selectedCarColorModel;
    private final BehaviorRelay<ImporterViewModel> selectedImporterModel;
    private final BehaviorRelay<CountryViewModel> selectedCountryModel;
    private final BehaviorRelay<CountryViewModel> selectedCityModel;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<TextUtil.Result> carConditionRelay;
    private final BehaviorRelay<TextUtil.Result> priceFromRelay;
    private final BehaviorRelay<TextUtil.Result> importerRelay;
    private final BehaviorRelay<TextUtil.Result> priceToRelay;
    private final BehaviorRelay<TextUtil.Result> paymentRelay;
    private final BehaviorRelay<TextUtil.Result> countryRelay;
    private final BehaviorRelay<TextUtil.Result> categoryRelay;
    private final BehaviorRelay<TextUtil.Result> genderRelay;
    private final BehaviorRelay<TextUtil.Result> sellerRelay;
    private final BehaviorRelay<TextUtil.Result> kmFromRelay;
    private final BehaviorRelay<TextUtil.Result> modelRelay;
    private final BehaviorRelay<TextUtil.Result> brandRelay;
    private final BehaviorRelay<TextUtil.Result> colorRelay;
    private final BehaviorRelay<TextUtil.Result> cityRelay;
    private final BehaviorRelay<TextUtil.Result> gearRelay;
    private final BehaviorRelay<TextUtil.Result> kmToRelay;
    private final BehaviorRelay<TextUtil.Result> typeRelay;
    private final BehaviorRelay<TextUtil.Result> yearRelay;
    private final BehaviorRelay<TextUtil.Result> yearToRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final BehaviorRelay<Boolean> allInputsValidityRelay2;
    private final InterestRequiredMapper interestRequiredMapper;
    private final AddInterestScreen addInterestScreen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final InterestRepo interestRepo;
    private final UserManager userManager;

    @Inject
    AddInterestPresenter(InterestRequiredMapper interestRequiredMapper,
                         @IOThread ThreadSchedulers threadSchedulers,
                         @ForActivity CompositeDisposable disposable,
                         AddInterestScreen addInterestScreen,
                         InterestRepo interestRepo,
                         UserManager userManager) {
        super(addInterestScreen);
        this.interestRequiredRelay = BehaviorRelay.createDefault(InterestRequiredViewModel.createDefault());
        this.selectedCategoryModel = BehaviorRelay.create();
        this.selectedCarColorModel = BehaviorRelay.create();
        this.selectedImporterModel = BehaviorRelay.create();
        this.selectedCountryModel = BehaviorRelay.create();
        this.selectedModelModel = BehaviorRelay.create();
        this.selectedBrandModel = BehaviorRelay.create();
        this.searchRequestRelay = BehaviorRelay.create();
        this.selectedCityModel = BehaviorRelay.create();
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.allInputsValidityRelay2 = BehaviorRelay.createDefault(false);
        this.carConditionRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.priceFromRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.categoryRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.importerRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.countryRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.priceToRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.paymentRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.genderRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.sellerRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.kmFromRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.modelRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.brandRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.colorRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.typeRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.gearRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.kmToRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.yearRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.yearToRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.cityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.interestRequiredMapper = interestRequiredMapper;
        this.addInterestScreen = addInterestScreen;
        this.threadSchedulers = threadSchedulers;
        this.interestRepo = interestRepo;
        this.userManager = userManager;
        this.disposable = disposable;
    }

    @Override
    protected void onCreate() {
        AddInterestActivityArgs addInterestActivityArgs = AddInterestActivityArgs.desrilizeFrom(getIntent());
        addInterestScreen.setupEditText();
        initializeInputsValidityObservable();
        initializeInputsValidityObservable2();
        if (addInterestActivityArgs.getSearchRequestModel() != null) {
            searchRequestRelay.accept(addInterestActivityArgs.getSearchRequestModel());
            addInterestScreen.updateUi(addInterestActivityArgs.getSearchRequestModel());
        } else {
            addInterestScreen.initializeAllInputs();
        }
        fetchRequiredDataForAddInterest();
    }

    private void processDefaultBrandModelCategoryColor() {

        Timber.d("selected brand"+searchRequestRelay.getValue().getBrand_id());
        Timber.d("selected model"+searchRequestRelay.getValue().getModel_id());
        Timber.d("selected categ"+searchRequestRelay.getValue().getCategory_id());
        for (int i = 0; i < getBrandsList().size(); i++) {
            if (getBrandsList().get(i).getId() == searchRequestRelay.getValue().getBrand_id()) {
                setSelectedBrandModel(getBrandsList().get(i));
                addInterestScreen.updateBrand(getBrandsList().get(i));
                for (int j = 0; j < getSelectedBrandModel().getBrandsModels().size(); j++) {
                    if (getSelectedBrandModel().getBrandsModels().get(j).getId() == searchRequestRelay.getValue().getModel_id()) {
                        setSelectedModelModel(getSelectedBrandModel().getBrandsModels().get(j));
                        addInterestScreen.updateModel(getSelectedBrandModel().getBrandsModels().get(j));
                        for (int k = 0; k < getSelectedModelModel().getCategoryViewModels().size(); k++) {
                            if (getSelectedModelModel().getCategoryViewModels().get(k).getId() == searchRequestRelay.getValue().getCategory_id()) {
                                setSelectedCategoryModel(getSelectedModelModel().getCategoryViewModels().get(k));
                                addInterestScreen.updateCategory(getSelectedModelModel().getCategoryViewModels().get(k));
                            }
                        }
                    }
                }
            }
        }

        if (searchRequestRelay.getValue().getModel_id() == 0) {
            setSelectedModelModel(ModelViewModel.createDefault());
            addInterestScreen.updateModel(ModelViewModel.createDefault());
        }

        if (searchRequestRelay.getValue().getCategory_id() == 0) {
            setSelectedCategoryModel(CategoryViewModel.createDefault());
            addInterestScreen.updateCategory(CategoryViewModel.createDefault());
        }
        if (searchRequestRelay.getValue().getCar_specification_id() == 0) {
            setSelectedImporterModel(ImporterViewModel.createDefault());
            addInterestScreen.updateImporter(ImporterViewModel.createDefault());
        }
        if (selectedCountryModel.getValue() == null) {
            setSelectedCountryModel(CountryViewModel.createDefault());
            selectedCountryModel.accept(CountryViewModel.createDefault());
            addInterestScreen.updateCountry(CountryViewModel.createDefault());
        }

        if (selectedCityModel.getValue() == null) {
            setSelectedCityModel(CountryViewModel.createDefault());
            selectedCityModel.accept(CountryViewModel.createDefault());
            addInterestScreen.updateCity(CountryViewModel.createDefault());
        }
        for (int i = 0; i < getCarColorList().size(); i++) {
            if (getCarColorList().get(i).getId() == searchRequestRelay.getValue().getColor_id()) {
                setSelectedCarColorModel(getCarColorList().get(i));
                addInterestScreen.updateColor(getCarColorList().get(i));
            }
        }
    }

    private void fetchRequiredDataForAddInterest() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(interestRepo.fetchRequiredDataForInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredViewModel)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> addInterestScreen.showLoadingAnimation())
                .doFinally(addInterestScreen::hideLoadingAnimation)
                .subscribe(interestRequiredModel -> {
                    interestRequiredRelay.accept(interestRequiredModel);
                    if (searchRequestRelay.getValue() != null) {
                        processDefaultBrandModelCategoryColor();
                    }
                }, this::processError));
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                brandRelay.hide(),
                modelRelay.hide(),
                colorRelay.hide(),
                paymentRelay.hide(),
                carConditionRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private void initializeInputsValidityObservable2() {
        disposable.add(Observable.combineLatest(
                sellerRelay.hide(),
                importerRelay.hide(),
                gearRelay.hide(),
                countryRelay.hide(),
                cityRelay.hide(),
                categoryRelay.hide(),
                this::areAllInputsValid2)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay2, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result brand,
                                      TextUtil.Result model,
                                      TextUtil.Result color,
                                      TextUtil.Result payment,
                                      TextUtil.Result carCondition) {
        return carCondition.isValid() && brand.isValid() && model.isValid()
                && color.isValid() && payment.isValid();
    }

    private Boolean areAllInputsValid2(TextUtil.Result sellerType,
                                       TextUtil.Result importer,
                                       TextUtil.Result gear,
                                       TextUtil.Result country,
                                       TextUtil.Result city,
                                       TextUtil.Result category) {
        return sellerType.isValid() && importer.isValid() && gear.isValid()
                && country.isValid() && city.isValid() && category.isValid();
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                addInterestScreen.processLogout();
            } else {
                addInterestScreen.showErrorMessage(getHttpErrorMessage(
                        HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            }
        } else if (throwable instanceof IOException) {
            addInterestScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            addInterestScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    List<CarColorViewModel> getCarColorList() {
        List<CarColorViewModel> colorModels = new ArrayList<>();
        colorModels.add(CarColorViewModel.createDefault());
        colorModels.addAll(interestRequiredRelay.getValue().getColorViewModels());
        return colorModels;
    }

    List<CountryViewModel> getCountryList() {
        List<CountryViewModel> countryModels = new ArrayList<>();
        countryModels.add(CountryViewModel.createDefaultWithList());
        countryModels.addAll(interestRequiredRelay.getValue().getCountryViewModels());
        return countryModels;
    }

    List<ImporterViewModel> getImporterList() {
        List<ImporterViewModel> importerModels = new ArrayList<>();
        importerModels.add(ImporterViewModel.createDefault());
        importerModels.addAll(interestRequiredRelay.getValue().getImporterViewModels());
        return importerModels;
    }

    void setSelectedCarColorModel(CarColorViewModel carColorModel) {
        selectedCarColorModel.accept(carColorModel);
    }

    void setSelectedImporterModel(ImporterViewModel carColorModel) {
        selectedImporterModel.accept(carColorModel);
    }

    List<BrandsViewModel> getBrandsList() {
        List<BrandsViewModel> brandsModels = new ArrayList<>();
        brandsModels.add(BrandsViewModel.createDefault());
        brandsModels.addAll(interestRequiredRelay.getValue().getBrandsViewModels());
        return brandsModels;
    }

    BrandsViewModel getSelectedBrandModel() {
        return selectedBrandModel.getValue();
    }

    void setSelectedBrandModel(BrandsViewModel brandModel) {
        selectedBrandModel.accept(brandModel);
    }

    ModelViewModel getSelectedModelModel() {
        return selectedModelModel.getValue();
    }

    void setSelectedModelModel(ModelViewModel brandModel) {
        selectedModelModel.accept(brandModel);
    }

    void setSelectedCategoryModel(CategoryViewModel brandModel) {
        selectedCategoryModel.accept(brandModel);
    }

    CountryViewModel getSelectedCountryModel() {
        return selectedCountryModel.getValue();
    }

    void setSelectedCountryModel(CountryViewModel countryModel) {
        selectedCountryModel.accept(countryModel);
    }

    void setSelectedCityModel(CountryViewModel cityModel) {
        selectedCityModel.accept(cityModel);
    }

    public void onAfterCarConditionChange(TextUtil.Result result) {
        carConditionRelay.accept(result);
    }

    public void onAfterPriceFromChange(TextUtil.Result result) {
        priceFromRelay.accept(result);
    }

    public void onAfterImporterChange(TextUtil.Result result) {
        importerRelay.accept(result);
    }

    public void onAfterPriceToChange(TextUtil.Result result) {
        priceToRelay.accept(result);
    }

    public void onAfterPaymentChange(TextUtil.Result result) {
        paymentRelay.accept(result);
    }

    public void onAfterCountryChange(TextUtil.Result result) {
        countryRelay.accept(result);
    }

    public void onAfterGenderChange(TextUtil.Result result) {
        genderRelay.accept(result);
    }

    public void onAfterSellerChange(TextUtil.Result result) {
        sellerRelay.accept(result);
    }

    public void onAfterKmFromChange(TextUtil.Result result) {
        kmFromRelay.accept(result);
    }

    public void onAfterBrandChange(TextUtil.Result result) {
        brandRelay.accept(result);
    }

    public void onAfterYearChange(TextUtil.Result result) {
        yearRelay.accept(result);
    }

    public void onAfterTypeChange(TextUtil.Result result) {
        typeRelay.accept(result);
    }

    public void onAfterGearChange(TextUtil.Result result) {
        gearRelay.accept(result);
    }

    public void onAfterKmToChange(TextUtil.Result result) {
        kmToRelay.accept(result);
    }

    public void onAfterCityChange(TextUtil.Result result) {
        cityRelay.accept(result);
    }

    public void onAfterModelChange(TextUtil.Result result) {
        modelRelay.accept(result);
    }

    public void onAfterColorChange(TextUtil.Result result) {
        colorRelay.accept(result);
    }

    public void onAfterYearToChange(TextUtil.Result result) {
        yearToRelay.accept(result);
    }

    public void onAfterCategoryChange(TextUtil.Result result) {
        categoryRelay.accept(result);
    }

    public void onSaveClicked(@GearType int gearType, @CarCondition int carCondition,
                              @Gender int sellerType, @PaymentType int paymentType, String priceFrom,
                              String priceTo, String kmFrom,
                              String kmTo, String year, String yearTo,
                              int mvpi, int nearby, int vehicle) {
        if (!allInputsValidityRelay.getValue() || !allInputsValidityRelay2.getValue()) {
            if (!brandRelay.getValue().isValid()) {
                addInterestScreen.showBrandErrorMessage(modelRelay.getValue().getMessageEn());
                addInterestScreen.showWarningMessage(getResourcesUtil().getString(R.string.interest_select_brand));
                return;
            } else {
                addInterestScreen.showBrandErrorMessage(null);
            }
            if (!modelRelay.getValue().isValid()) {
                addInterestScreen.showModelErrorMessage(modelRelay.getValue().getMessageEn());
                addInterestScreen.showWarningMessage(getResourcesUtil().getString(R.string.interest_select_model));
                return;
            } else {
                addInterestScreen.showModelErrorMessage(null);
            }
//            if (!typeRelay.getValue().isValid()) {
//                addInterestScreen.showTypeErrorMessage(typeRelay.getValue().getMessageEn());
//            } else {
//                addInterestScreen.showTypeErrorMessage(null);
//            }
            if (!colorRelay.getValue().isValid()) {
                addInterestScreen.showColorErrorMessage(colorRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showColorErrorMessage(null);
            }
            if (!paymentRelay.getValue().isValid()) {
                addInterestScreen.showPaymentErrorMessage(paymentRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showPaymentErrorMessage(null);
            }
            if (!carConditionRelay.getValue().isValid()) {
                addInterestScreen.showCarConditionErrorMessage(carConditionRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showCarConditionErrorMessage(null);
            }
            if (!sellerRelay.getValue().isValid()) {
                addInterestScreen.showSellerErrorMessage(sellerRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showSellerErrorMessage(null);
            }
            if (!importerRelay.getValue().isValid()) {
                addInterestScreen.showImporterErrorMessage(importerRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showImporterErrorMessage(null);
            }
            if (!gearRelay.getValue().isValid()) {
                addInterestScreen.showGearErrorMessage(gearRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showGearErrorMessage(null);
            }
            if (!countryRelay.getValue().isValid()) {
                addInterestScreen.showCountryErrorMessage(countryRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showCountryErrorMessage(null);
            }
            if (!cityRelay.getValue().isValid()) {
                addInterestScreen.showCityErrorMessage(cityRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showCityErrorMessage(null);
            }
        } else {
            if (userManager.sessionManager().isSessionActive()) {
                String finalPriceFrom = priceFrom, finalPriceTo = priceTo, finalYearFrom = year,
                        finalYearTo = yearTo, finalKmFrom = kmFrom, finalKmTo = kmTo;
                if (TextUtils.isEmpty(priceFrom)) {
                    finalPriceFrom = "0";
                }
                if (TextUtils.isEmpty(priceTo)) {
                    finalPriceTo = "9999999";
                }
                if (TextUtils.isEmpty(year)) {
                    finalYearFrom = "1950";
                }
                if (TextUtils.isEmpty(yearTo)) {
                    finalYearTo = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)+1);
                }
                if (TextUtils.isEmpty(kmFrom)) {
                    finalKmFrom = "0";
                }
                if (TextUtils.isEmpty(kmTo)) {
                    finalKmTo = "999999";
                }
                disposable.add(interestRepo.addInterest(userManager.getCurrentUser().getApiToken(),
                        gearType,
                        selectedImporterModel.getValue().getId(),
                        paymentType,
                        selectedCarColorModel.getValue().getId(),
                        mvpi,
                        selectedBrandModel.getValue().getId(),
                        selectedModelModel.getValue().getId(),
                        finalPriceFrom,
                        finalPriceTo,
                        finalYearFrom,
                        finalYearTo,
                        carCondition,
                        sellerType,
                        nearby,
                        selectedCityModel.getValue().getId(),
                        selectedCountryModel.getValue().getId(),
                        finalKmFrom,
                        finalKmTo,
                        selectedCategoryModel.getValue().getId(),
                        vehicle)
//                        ,sale
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> addInterestScreen.showLoadingAnimation())
                        .doFinally(addInterestScreen::hideLoadingAnimation)
                        .subscribe(addInterestScreen::addedSuccessfully, this::processError));
            } else {
                addInterestScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}