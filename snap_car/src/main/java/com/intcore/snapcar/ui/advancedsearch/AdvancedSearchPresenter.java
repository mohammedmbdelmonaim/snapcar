package com.intcore.snapcar.ui.advancedsearch;

import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class AdvancedSearchPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<CarColorViewModel> selectedCarColorModel;
    private final BehaviorRelay<TextUtil.Result> engineCapacityRelay;
    private final BehaviorRelay<TextUtil.Result> carConditionRelay;
    private final InterestRequiredMapper interestRequiredMapper;
    private final BehaviorRelay<Boolean> allInputsValidityRelay2;
    private final BehaviorRelay<Boolean> allInputsValidityRelay;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final BehaviorRelay<TextUtil.Result> warrantyRelay;
    private final BehaviorRelay<TextUtil.Result> postTypeRelay;
    private final BehaviorRelay<TextUtil.Result> categoryRelay;
    private final BehaviorRelay<TextUtil.Result> trackedRelay;
    private final BehaviorRelay<TextUtil.Result> kmFromRelay;
    private final BehaviorRelay<TextUtil.Result> sellerRelay;
    private final BehaviorRelay<TextUtil.Result> modelRelay;
    private final BehaviorRelay<TextUtil.Result> brandRelay;
    private final BehaviorRelay<TextUtil.Result> colorRelay;
    private final BehaviorRelay<TextUtil.Result> kmToRelay;
    private final BehaviorRelay<TextUtil.Result> gearRelay;
    private final AdvancedSearchScreen advancedSearchScreen;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final HomeRepo homeRepo;
    private LocationCallback locationCallback;
    private Location lastKnownLocation;

    @Inject
    AdvancedSearchPresenter(InterestRequiredMapper interestRequiredMapper, AdvancedSearchScreen advancedSearchScreen,
                            @IOThread ThreadSchedulers threadSchedulers,
                            @ForActivity CompositeDisposable disposable,
                            UserManager userManager,
                            HomeRepo homeRepo) {
        super(advancedSearchScreen);
        this.locationCallback = createLocationCallback();
        this.interestRequiredRelay = BehaviorRelay.createDefault(InterestRequiredViewModel.createDefault());
        this.selectedCarColorModel = BehaviorRelay.createDefault(CarColorViewModel.createDefault());
        this.selectedCategoryModel = BehaviorRelay.createDefault(CategoryViewModel.createDefault());
        this.selectedModelModel = BehaviorRelay.createDefault(ModelViewModel.createDefault());
        this.selectedBrandModel = BehaviorRelay.createDefault(BrandsViewModel.createDefault());
        this.engineCapacityRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.carConditionRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.categoryRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.warrantyRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.postTypeRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.trackedRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.sellerRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.kmFromRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.modelRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.brandRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.colorRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.gearRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.kmToRelay = BehaviorRelay.createDefault(new TextUtil.Result());
        this.allInputsValidityRelay2 = BehaviorRelay.createDefault(false);
        this.allInputsValidityRelay = BehaviorRelay.createDefault(false);
        this.interestRequiredMapper = interestRequiredMapper;
        this.advancedSearchScreen = advancedSearchScreen;
        this.threadSchedulers = threadSchedulers;
        this.userManager = userManager;
        this.disposable = disposable;
        this.homeRepo = homeRepo;
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location result = locationResult.getLastLocation();
                if (result != null) {
                    lastKnownLocation = result;
                }
            }
        };
    }

    @Override
    protected void onCreate() {
        AdvancedSearchActivityArgs advancedSearchActivityArgs = AdvancedSearchActivityArgs.deserializeFrom(getIntent());
        advancedSearchScreen.updatePrice(advancedSearchActivityArgs.getMinPrice(),
                advancedSearchActivityArgs.getMaxPrice());
        fetchRequiredDataForAddInterest();
        advancedSearchScreen.initializeInputs();
        advancedSearchScreen.setupEditText();
        advancedSearchScreen.setupSlider();
        initializeInputsValidityObservable();
        initializeInputsValidityObservable2();
    }

    @Override
    protected void onStart() {
        startReceivingLocationUpdates();
    }

    @Override
    protected void onStop() {
        stopReceivingLocationUpdates();
    }

    private void startReceivingLocationUpdates() {
        if (getPermissionUtil().hasLocationPermission()) {
            if (getPermissionUtil().isGPSEnabled()) {
                advancedSearchScreen.startReceivingLocationUpdates(locationCallback);
            } else {
                advancedSearchScreen.showGPSIsRequiredMessage();
            }
        } else {
            getPermissionUtil().requestLocationPermission();
        }
    }

    private void stopReceivingLocationUpdates() {
        advancedSearchScreen.stopReceivingLocationUpdates(locationCallback);
    }

    private void initializeInputsValidityObservable() {
        disposable.add(Observable.combineLatest(
                brandRelay.hide(),
                modelRelay.hide(),
                warrantyRelay.hide(),
                engineCapacityRelay.hide(),
                sellerRelay.hide(),
                colorRelay.hide(),
                gearRelay.hide(),
                carConditionRelay.hide(),
                categoryRelay.hide(),
                this::areAllInputsValid)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay, Timber::e));
    }

    private void initializeInputsValidityObservable2() {
        disposable.add(Observable.combineLatest(
                trackedRelay.hide(),
                postTypeRelay.hide(),
                this::areAllInputsValid2)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(allInputsValidityRelay2, Timber::e));
    }

    private Boolean areAllInputsValid2(TextUtil.Result tracked, TextUtil.Result postType) {
        return tracked.isValid()
                && postType.isValid();
    }

    private Boolean areAllInputsValid(TextUtil.Result brand,
                                      TextUtil.Result model,
                                      TextUtil.Result warranty,
                                      TextUtil.Result engineCapacity,
                                      TextUtil.Result sellerType,
                                      TextUtil.Result colorRelay,
                                      TextUtil.Result gearRelay,
                                      TextUtil.Result carCondition,
                                      TextUtil.Result category) {
        return brand.isValid()
                && model.isValid()
                && warranty.isValid()
                && engineCapacity.isValid()
                && sellerType.isValid()
                && colorRelay.isValid()
                && gearRelay.isValid()
                && carCondition.isValid()
                && category.isValid();
    }

    private void fetchRequiredDataForAddInterest() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(homeRepo.fetchRequiredDataForInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredViewModel)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> advancedSearchScreen.showLoadingAnimation())
                .doFinally(advancedSearchScreen::hideLoadingAnimation)
                .subscribe(interestRequiredRelay, this::processError));
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            advancedSearchScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                advancedSearchScreen.processLogout();
            }
        } else if (t instanceof IOException) {
            advancedSearchScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            advancedSearchScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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
        List<CarColorViewModel> carColorModels = new ArrayList<>();
        carColorModels.add(CarColorViewModel.createDefault());
        carColorModels.addAll(interestRequiredRelay.getValue().getColorViewModels());
        return carColorModels;
    }

    void setSelectedCarColorModel(CarColorViewModel carColorModel) {
        selectedCarColorModel.accept(carColorModel);
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

    String getMinPrice() {
        return interestRequiredRelay.getValue().getMinPrice();
    }

    String getMaxPrice() {
        return interestRequiredRelay.getValue().getMaxPrice();
    }

    public void onAfterCarConditionChange(TextUtil.Result result) {
        carConditionRelay.accept(result);
    }

    void onAfterEngineCapacityChange(TextUtil.Result result) {
        engineCapacityRelay.accept(result);
    }

    void onAfterSellerChange(TextUtil.Result result) {
        sellerRelay.accept(result);
    }

    void onAfterBrandChange(TextUtil.Result result) {
        brandRelay.accept(result);
    }

    void onAfterModelChange(TextUtil.Result result) {
        modelRelay.accept(result);
    }

    void onAfterColorChange(TextUtil.Result result) {
        colorRelay.accept(result);
    }

    void onAfterGearChange(TextUtil.Result result) {
        gearRelay.accept(result);
    }

    void onAfterCategoryChange(TextUtil.Result result) {
        categoryRelay.accept(result);
    }

    void onAfterWarrantyChange(TextUtil.Result result) {
        warrantyRelay.accept(result);
    }

    public void onAfterKmFromChange(TextUtil.Result result) {
        kmFromRelay.accept(result);
    }

    void onAfterPostTypeChange(TextUtil.Result result) {
        postTypeRelay.accept(result);
    }

    void onAfterTrackedChange(TextUtil.Result result) {
        trackedRelay.accept(result);
    }

    public void onAfterKmToChange(TextUtil.Result result) {
        kmToRelay.accept(result);
    }

    public void onSearchClicked(int gearType, int carCondition, int sellerType,
                                int engineCapacity, int warranty, int tracked, int postType,
                                String minYear, String maxYear, String minPrice, String maxPrice,
                                String fromKm, String toKm, int isNearby, int isInstallment, int isBigSale) {

        String newMinPrice = "0";
        String newMaxPrice = this.getMaxPrice();

        if (!TextUtils.isEmpty(minPrice)) {
            newMinPrice = minPrice;
        }
        if (!TextUtils.isEmpty(maxPrice)) {
            newMaxPrice = maxPrice;
        }

        SearchRequestModel.Builder builder = new SearchRequestModel.Builder()
                .gender(sellerType)
                .yearTo(maxYear)
                .yearFrom(minYear)
                .kilometer_from(fromKm)
                .kilometer_to(toKm)
                .gear_type(gearType)
                .price_to(newMaxPrice)
                .priceFrom(newMinPrice)
                .modelId(selectedModelModel.getValue().getId())
                .brandId(selectedBrandModel.getValue().getId())
                .categoryId(selectedCategoryModel.getValue().getId())
                .colorId(selectedCarColorModel.getValue().getId())
                .carStatus(carCondition)
                .tracked(tracked)
                .postType(postType)
                .engineCapacity(engineCapacity)
                .warranty(warranty)
                .isInstallment(isInstallment)
                .isBigSale(isBigSale);
        if (isNearby == 1) {
            builder.longitude(String.valueOf(lastKnownLocation.getLongitude()))
                    .latitude(String.valueOf(lastKnownLocation.getLatitude()));
        }
        SearchRequestModel searchRequestModel = builder.build();
        advancedSearchScreen.shouldNavigateToSearch(searchRequestModel);
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}