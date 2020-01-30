package com.intcore.snapcar.ui.editinterest;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.InterestRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.constant.PaymentType;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
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
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class EditInterestPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<CarViewModel> searchRequestRelay;
    private final BehaviorRelay<CarColorViewModel> selectedCarColorModel;
    private final BehaviorRelay<ImporterViewModel> selectedImporterModel;
    private final BehaviorRelay<CountryViewModel> selectedCountryModel;
    private final BehaviorRelay<CountryViewModel> selectedCityModel;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<TextUtil.Result> carConditionRelay;
    private final InterestRequiredMapper interestRequiredMapper;
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
    private final EditInterestScreen addInterestScreen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final InterestRepo interestRepo;
    private final UserManager userManager;

    @Inject
    EditInterestPresenter(InterestRequiredMapper interestRequiredMapper, EditInterestScreen addInterestScreen,
                          @IOThread ThreadSchedulers threadSchedulers,
                          @ForActivity CompositeDisposable disposable,
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
        fetchRequiredDataForAddInterest();
        EditInterestActivityArgs editInterestActivityArgs = EditInterestActivityArgs.desrilizeFrom(getIntent());
        if (editInterestActivityArgs.getCarViewModel() != null) {
            searchRequestRelay.accept(editInterestActivityArgs.getCarViewModel());
            addInterestScreen.updateUi(editInterestActivityArgs.getCarViewModel());
        }
        addInterestScreen.setupEditText();
        initializeInputsValidityObservable();
        initializeInputsValidityObservable2();
    }

    private void processDefaultBrandModelCategory() {
        int modelId = 0;
        int brandId = 0;
        int categoryId = 0;
        if (searchRequestRelay.getValue().getBrandId() != null) {
            brandId = searchRequestRelay.getValue().getBrandId();
        }
        if (searchRequestRelay.getValue().getCarModelId() != null) {
            modelId = searchRequestRelay.getValue().getCarModelId();
        }
        if (searchRequestRelay.getValue().getCategoryId() != null) {
            categoryId = searchRequestRelay.getValue().getCategoryId();
        }
        for (int i = 0; i < getBrandsList().size(); i++) {
            if (getBrandsList().get(i).getId() == brandId) {
                setSelectedBrandModel(getBrandsList().get(i));
                addInterestScreen.updateBrand(getBrandsList().get(i));
                for (int j = 0; j < getSelectedBrandModel().getBrandsModels().size(); j++) {
                    if (getSelectedBrandModel().getBrandsModels().get(j).getId() == modelId) {
                        setSelectedModelModel(getSelectedBrandModel().getBrandsModels().get(j));
                        addInterestScreen.updateModel(getSelectedBrandModel().getBrandsModels().get(j));
                        for (int k = 0; k < getSelectedModelModel().getCategoryViewModels().size(); k++) {
                            if (getSelectedModelModel().getCategoryViewModels().get(k).getId() == categoryId) {
                                setSelectedCategoryModel(getSelectedModelModel().getCategoryViewModels().get(k));
                                addInterestScreen.updateCategory(getSelectedModelModel().getCategoryViewModels().get(k));
                            }
                        }
                    }
                }
            }
        }
        if (brandId == 0) {
            setSelectedBrandModel(BrandsViewModel.createDefault());
            addInterestScreen.updateBrand(BrandsViewModel.createDefault());
        }
        if (modelId == 0) {
            setSelectedModelModel(ModelViewModel.createDefault());
            addInterestScreen.updateModel(ModelViewModel.createDefault());
        }
        if (categoryId == 0) {
            setSelectedCategoryModel(CategoryViewModel.createDefault());
            addInterestScreen.updateCategory(CategoryViewModel.createDefault());
        }
    }

    private void processCountryCityColor() {
        int colorId = 0, countryId = 0, cityId = 0, importerId = 0;
        if (searchRequestRelay.getValue().getCountryViewModel() != null) {
            countryId = searchRequestRelay.getValue().getCountryViewModel().getId();
        }
        if (searchRequestRelay.getValue().getCityViewModel() != null) {
            cityId = searchRequestRelay.getValue().getCityViewModel().getId();
        }
        if (searchRequestRelay.getValue().getImporterViewModel() != null) {
            importerId = searchRequestRelay.getValue().getImporterViewModel().getId();
        }
        if (searchRequestRelay.getValue().getCarColorId() != null) {
            colorId = searchRequestRelay.getValue().getCarColorId();
        }
        for (int i = 0; i < getCountryList().size(); i++) {
            if (getCountryList().get(i).getId() == countryId) {
                setSelectedCountryModel(getCountryList().get(i));
                addInterestScreen.updateCountry(getCountryList().get(i));
                for (int j = 0; j < getCityList().size(); j++) {
                    if (getCityList().get(j).getId() == cityId) {
                        setSelectedCityModel(getCityList().get(j));
                        addInterestScreen.updateCity(getCityList().get(j));
                    }
                }
            }
        }
        for (int i = 0; i < getCarColorList().size(); i++) {
            if (getCarColorList().get(i).getId() == colorId) {
                setSelectedCarColorModel(getCarColorList().get(i));
                addInterestScreen.updateColor(getCarColorList().get(i));
            }
        }
        for (int i = 0; i < getImporterList().size(); i++) {
            if (getImporterList().get(i).getId() == importerId) {
                setSelectedImporterModel(getImporterList().get(i));
                addInterestScreen.updateImporter(getImporterList().get(i));
            }
        }

        if (colorId == 0) {
            setSelectedCarColorModel(CarColorViewModel.createDefault());
            addInterestScreen.updateColor(CarColorViewModel.createDefault());
        }
        if (countryId == 0) {
            setSelectedCountryModel(CountryViewModel.createDefaultWithList());
            addInterestScreen.updateCountry(CountryViewModel.createDefaultWithList());
        }
        if (cityId == 0) {
            setSelectedCityModel(CountryViewModel.createDefault());
            addInterestScreen.updateCity(CountryViewModel.createDefault());
        }
        if (importerId == 0) {
            setSelectedImporterModel(ImporterViewModel.createDefault());
            addInterestScreen.updateImporter(ImporterViewModel.createDefault());
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
                        processDefaultBrandModelCategory();
                        processCountryCityColor();
                    }
                }, this::processError));
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                brandRelay.hide(),
                modelRelay.hide(),
                yearRelay.hide(),
                priceToRelay.hide(),
                priceFromRelay.hide(),
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
                kmFromRelay.hide(),
                kmToRelay.hide(),
                countryRelay.hide(),
                cityRelay.hide(),
                yearToRelay.hide(),
                categoryRelay.hide(),
                this::areAllInputsValid2)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay2, Timber::e));
    }

    private Boolean areAllInputsValid(TextUtil.Result brand, TextUtil.Result model, TextUtil.Result year,
                                      TextUtil.Result priceTo, TextUtil.Result priceFrom,
                                      TextUtil.Result color, TextUtil.Result payment, TextUtil.Result carCondition) {
        return carCondition.isValid() && brand.isValid() && model.isValid() && year.isValid() && priceTo.isValid()
                && priceFrom.isValid() && color.isValid() && payment.isValid();
    }

    private Boolean areAllInputsValid2(TextUtil.Result sellerType, TextUtil.Result importer,
                                       TextUtil.Result gear, TextUtil.Result kmFrom, TextUtil.Result kmTo,
                                       TextUtil.Result country, TextUtil.Result city,
                                       TextUtil.Result yearTo, TextUtil.Result category) {
        return sellerType.isValid() && importer.isValid() && gear.isValid() && kmFrom.isValid()
                && kmTo.isValid() && country.isValid() && city.isValid() && yearTo.isValid() && category.isValid();
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            addInterestScreen.showErrorMessage(getHttpErrorMessage(
                    HttpException.wrapJakewhartonException(
                            (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                addInterestScreen.processLogout();
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

    CarColorViewModel getSelectedCarColorModel() {
        return selectedCarColorModel.getValue();
    }

    void setSelectedCarColorModel(CarColorViewModel carColorModel) {
        selectedCarColorModel.accept(carColorModel);
    }

    ImporterViewModel getSelectedImporterModel() {
        return selectedImporterModel.getValue();
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

    CategoryViewModel getSelectedCategoryModel() {
        return selectedCategoryModel.getValue();
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

    List<CountryViewModel> getCityList() {
        List<CountryViewModel> countryModels = new ArrayList<>();
        if (getSelectedCountryModel() != null) {
            countryModels.add(CountryViewModel.createDefault());
            countryModels.addAll(getSelectedCountryModel().getCountryViewModels());
        }
        return countryModels;
    }

    CountryViewModel getSelectedCityModel() {
        return selectedCityModel.getValue();
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

    public void onAfterModelChange(TextUtil.Result result) {
        modelRelay.accept(result);
    }

    public void onAfterColorChange(TextUtil.Result result) {
        colorRelay.accept(result);
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

    public void onAfterYearToChange(TextUtil.Result result) {
        yearToRelay.accept(result);
    }

    public void onAfterCategoryChange(TextUtil.Result result) {
        categoryRelay.accept(result);
    }

    public void onAfterCityChange(TextUtil.Result result) {
        cityRelay.accept(result);
    }

    public void onSaveClicked(@GearType int gearType, @CarCondition int carCondition,
                              @Gender int sellerType,
                              @PaymentType int paymentType, String priceFrom, String priceTo, String kmFrom,
                              String kmTo, String year, String yearTo, int mvpi, int nearby, int vehicle) {
        if (!allInputsValidityRelay.getValue() || !allInputsValidityRelay2.getValue()) {
            if (!modelRelay.getValue().isValid()) {
                addInterestScreen.showModelErrorMessage(modelRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showModelErrorMessage(null);
            }
            if (!brandRelay.getValue().isValid()) {
                addInterestScreen.showBrandErrorMessage(modelRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showBrandErrorMessage(null);
            }
            if (!yearRelay.getValue().isValid()) {
                addInterestScreen.showYearFromErrorMessage(yearRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showYearFromErrorMessage(null);
            }
            if (!yearToRelay.getValue().isValid()) {
                addInterestScreen.showYearToErrorMessage(yearToRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showYearToErrorMessage(null);
            }
            if (!priceFromRelay.getValue().isValid()) {
                addInterestScreen.showPriceFromErrorMessage(priceFromRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showPriceFromErrorMessage(null);
            }
            if (!priceToRelay.getValue().isValid()) {
                addInterestScreen.showPriceToErrorMessage(priceToRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showPriceToErrorMessage(null);
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
            if (!kmToRelay.getValue().isValid()) {
                addInterestScreen.showKmToErrorMessage(kmToRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showKmToErrorMessage(null);
            }
            if (!kmFromRelay.getValue().isValid()) {
                addInterestScreen.showkmFromErrorMessage(kmFromRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showkmFromErrorMessage(null);
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
            if (!categoryRelay.getValue().isValid()) {
                addInterestScreen.showCategoryErrorMessage(categoryRelay.getValue().getMessageEn());
            } else {
                addInterestScreen.showCategoryErrorMessage(null);
            }
        } else {
            if (userManager.sessionManager().isSessionActive()) {
                disposable.add(interestRepo.updateInterest(userManager.getCurrentUser().getApiToken(),
                        searchRequestRelay.getValue().getId(),
                        gearType,
                        selectedImporterModel.getValue().getId(),
                        paymentType,
                        selectedCarColorModel.getValue().getId(),
                        mvpi,
                        selectedBrandModel.getValue().getId(),
                        selectedModelModel.getValue().getId(),
                        priceFrom,
                        priceTo,
                        year,
                        yearTo,
                        carCondition,
                        sellerType,
                        nearby,
                        selectedCityModel.getValue().getId(),
                        selectedCountryModel.getValue().getId(),
                        kmFrom,
                        kmTo,
                        selectedCategoryModel.getValue().getId(),
                        vehicle)
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> addInterestScreen.showLoadingAnimation())
                        .doFinally(addInterestScreen::hideLoadingAnimation)
                        .subscribe(addInterestScreen::addedSuccessfully, this::processError));
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}