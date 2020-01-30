package com.intcore.snapcar.ui.search;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class SearchPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<List<CarViewModel>> featureCarList;
    private final BehaviorRelay<SearchRequestModel> searchRelay;
    private final BehaviorRelay<List<CarViewModel>> carList;
    private final BehaviorRelay<Boolean> isFromBasicRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final SearchScreen searchScreen;
    private final UserManager userManager;
    private final CarMapper carMapper;
    private final HomeRepo homeRepo;

    @Inject
    SearchPresenter(@IOThread ThreadSchedulers threadSchedulers,
                    @ForActivity CompositeDisposable disposable,
                    SearchScreen searchScreen,
                    UserManager userManager,
                    CarMapper carMapper,
                    HomeRepo homeRepo) {
        super(searchScreen);
        this.featureCarList = BehaviorRelay.createDefault(Collections.emptyList());
        this.carList = BehaviorRelay.createDefault(Collections.emptyList());
        this.isFromBasicRelay = BehaviorRelay.createDefault(false);
        this.searchRelay = BehaviorRelay.create();
        this.threadSchedulers = threadSchedulers;
        this.searchScreen = searchScreen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.carMapper = carMapper;
        this.homeRepo = homeRepo;
    }

    @Override
    protected void onCreate() {
        searchScreen.setupRecyclerView();
        searchScreen.setupFeatureRecyclerView();
        observeFeatureCarList();
        SearchActivityArgs searchActivityArgs = SearchActivityArgs.deserializeFrom(getIntent());
        searchRelay.accept(searchActivityArgs.getSearchRequestModel());
        if (searchActivityArgs.isBasicSearch()) {
            fetchBasicSearchData();
        } else {
            fetchAdvancedSearchData();
        }
    }

    private void observeFeatureCarList() {
        disposable.add(homeRepo.observeFeatureCars()
                .map(carMapper::toCarModels)
                .map(carMapper::toCarViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(featureCar -> {
                    featureCarList.accept(featureCar);
                    searchScreen.updateFeatureCars(featureCar);
                }, Timber::e));
    }

    private void fetchAdvancedSearchData() {
        homeRepo.tearDown();
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(homeRepo.fetchAdvancedSearchCars(apiToken,
                searchRelay.getValue().getBrand_id(),
                searchRelay.getValue().getModel_id(),
                searchRelay.getValue().getCategory_id(),
                searchRelay.getValue().getGender(),
                searchRelay.getValue().getYear_form(),
                searchRelay.getValue().getYear_to(),
                searchRelay.getValue().getPrice_from(),
                searchRelay.getValue().getPrice_to(),
                searchRelay.getValue().getLongitude(),
                searchRelay.getValue().getLatitude(),
                searchRelay.getValue().getWarranty(),
                searchRelay.getValue().getEngineCapacity(),
                searchRelay.getValue().getColor_id(),
                searchRelay.getValue().getKilometer_from(),
                searchRelay.getValue().getKilometer_to(),
                searchRelay.getValue().getGear_type(),
                searchRelay.getValue().getCar_status(),
                searchRelay.getValue().getTracked(),
                searchRelay.getValue().getPostType(),
                searchRelay.getValue().getIsInstallment(),
                searchRelay.getValue().getIsBigSale())
                .map(carMapper::toCarViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
//                .doOnSubscribe(ignored -> searchScreen.showLoadingAnimation())
                .doFinally(searchScreen::hideLoadingAnimation)
                .subscribe(carViewModels -> {
                    if (featureCarList.getValue().size() > 0 || carViewModels.size() > 0) {
                        searchScreen.updateUi(carViewModels);
                        carList.accept(carViewModels);
                    } else {
                        searchScreen.showPopUpInterestMessage(searchRelay.getValue());
                    }
                }, this::processError));
    }

    private void fetchBasicSearchData() {
        homeRepo.tearDown();
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(homeRepo.fetchCarSearch(apiToken,
                searchRelay.getValue().getBrand_id(),
                searchRelay.getValue().getModel_id(),
                searchRelay.getValue().getCategory_id(),
                searchRelay.getValue().getGender(),
                searchRelay.getValue().getYear_form(),
                searchRelay.getValue().getYear_to(),
                searchRelay.getValue().getPrice_from(),
                searchRelay.getValue().getPrice_to(),
                searchRelay.getValue().getLongitude(),
                searchRelay.getValue().getLatitude())
                .map(carMapper::toCarViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
//                .doOnSubscribe(ignored -> searchScreen.showLoadingAnimation())
                .doFinally(searchScreen::hideLoadingAnimation)
                .subscribe(carViewModels -> {
                    if (featureCarList.getValue().size() > 0 || carViewModels.size() > 0) {
                        searchScreen.updateUi(carViewModels);
                        carList.accept(carViewModels);
                    } else {
                        searchScreen.showPopUpInterestMessage(searchRelay.getValue());
                    }
                }, this::processError));
    }

    void fetchSearchResult() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        if (isFromBasicRelay.getValue()) {
            disposable.add(homeRepo.fetchCarSearch(apiToken,
                    searchRelay.getValue().getBrand_id(),
                    searchRelay.getValue().getModel_id(),
                    searchRelay.getValue().getCategory_id(),
                    searchRelay.getValue().getGender(),
                    searchRelay.getValue().getYear_form(),
                    searchRelay.getValue().getYear_to(),
                    searchRelay.getValue().getPrice_from(),
                    searchRelay.getValue().getPrice_to(),
                    searchRelay.getValue().getLongitude(),
                    searchRelay.getValue().getLatitude())
                    .filter(carModels -> !carModels.isEmpty())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(carViewModels -> {
                        searchScreen.appendSearchResultToBottom(carViewModels);
                        List<CarViewModel> tempCarViewModels = carList.getValue();
                        tempCarViewModels.addAll(carViewModels);
                        carList.accept(tempCarViewModels);
                    }, this::processError));
        } else {
            disposable.add(homeRepo.fetchAdvancedSearchCars(apiToken,
                    searchRelay.getValue().getBrand_id(),
                    searchRelay.getValue().getModel_id(),
                    searchRelay.getValue().getCategory_id(),
                    searchRelay.getValue().getGender(),
                    searchRelay.getValue().getYear_form(),
                    searchRelay.getValue().getYear_to(),
                    searchRelay.getValue().getPrice_from(),
                    searchRelay.getValue().getPrice_to(),
                    searchRelay.getValue().getLongitude(),
                    searchRelay.getValue().getLatitude(),
                    searchRelay.getValue().getWarranty(),
                    searchRelay.getValue().getEngineCapacity(),
                    searchRelay.getValue().getColor_id(),
                    searchRelay.getValue().getKilometer_from(),
                    searchRelay.getValue().getKilometer_to(),
                    searchRelay.getValue().getGear_type(),
                    searchRelay.getValue().getCar_status(),
                    searchRelay.getValue().getTracked(),
                    searchRelay.getValue().getPostType(),
                    searchRelay.getValue().getIsInstallment(),
                    searchRelay.getValue().getIsBigSale())
                    .filter(carModels -> !carModels.isEmpty())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(carViewModels -> {
                        searchScreen.appendSearchResultToBottom(carViewModels);
                        List<CarViewModel> tempCarViewModels = carList.getValue();
                        tempCarViewModels.addAll(carViewModels);
                        carList.accept(tempCarViewModels);
                    }, this::processError));
        }
    }

    void onPriceSortClicked() {
        List<CarViewModel> temp = carList.getValue();
        Collections.sort(temp, (o1, o2) -> {
            if (o1.getPriceType() == 1) {
                if (!TextUtils.isEmpty(o1.getPrice()) && !TextUtils.isEmpty(o2.getPrice())) {
                    return Integer.valueOf(o2.getPrice()).compareTo(Integer.valueOf(o1.getPrice()));
                } else {
                    return 0;
                }
            } else if (o1.getPriceType() == 4) {
                if (!TextUtils.isEmpty(o1.getPriceAfter()) && !TextUtils.isEmpty(o2.getPriceAfter())) {
                    return Integer.valueOf(o2.getPriceAfter()).compareTo(Integer.valueOf(o1.getPriceAfter()));
                } else {
                    return 0;
                }
            } else if (o1.getPriceType() == 5) {
                if (!TextUtils.isEmpty(o1.getInstallmentPriceFrom()) && !TextUtils.isEmpty(o2.getInstallmentPriceFrom())) {
                    return Integer.valueOf(o2.getInstallmentPriceFrom()).compareTo(Integer.valueOf(o1.getInstallmentPriceFrom()));
                } else {
                    return 0;
                }
            }
            return 0;
        });
        searchScreen.updateUi(temp);
    }

    void onPriceSortLowClicked() {
        List<CarViewModel> temp = carList.getValue();
        Collections.sort(temp, (o1, o2) -> {
            if (o1.getPriceType() == 5) {
                if (!TextUtils.isEmpty(o1.getInstallmentPriceFrom()) && !TextUtils.isEmpty(o2.getInstallmentPriceFrom())) {
                    return Integer.valueOf(o2.getInstallmentPriceFrom()).compareTo(Integer.valueOf(o1.getInstallmentPriceFrom()));
                } else {
                    return 0;
                }
            } else if (o1.getPriceType() == 4) {
                if (!TextUtils.isEmpty(o1.getPriceAfter()) && !TextUtils.isEmpty(o2.getPriceAfter())) {
                    return Integer.valueOf(o2.getPriceAfter()).compareTo(Integer.valueOf(o1.getPriceAfter()));
                } else {
                    return 0;
                }
            } else if (o1.getPriceType() == 1) {
                if (!TextUtils.isEmpty(o1.getPrice()) && !TextUtils.isEmpty(o2.getPrice())) {
                    return Integer.valueOf(o2.getPrice()).compareTo(Integer.valueOf(o1.getPrice()));
                } else {
                    return 0;
                }
            }
            return 0;
        });
        Collections.reverse(temp);
        List<CarViewModel> undetermined = new ArrayList<>();
        List<CarViewModel> sorted = new ArrayList<>();
        for (CarViewModel carViewModel : temp) {
            if (carViewModel.getPriceType() != 1) {
                undetermined.add(carViewModel);
            }else {
                sorted.add(carViewModel);
            }
        }
        sorted.addAll(undetermined);
        searchScreen.updateUi(sorted);
    }

    void onNewestSortClicked() {
        List<CarViewModel> temp = carList.getValue();
        Collections.sort(temp, (o1, o2) -> {
            if (!TextUtils.isEmpty(o1.getCreatedAt()) && !TextUtils.isEmpty(o2.getCreatedAt())) {
                return o1.getCreatedAt().compareTo(o2.getCreatedAt());
            }
            return 0;
        });
        Collections.reverse(temp);
        searchScreen.updateUi(temp);
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            searchScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                searchScreen.processLogout();
            }
        } else if (t instanceof IOException) {
            searchScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            searchScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    public void onFavoriteClicked(String carId, String
            userId, OperationListener<Void> operationListener) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(homeRepo.addItemToFavorite(userManager.getCurrentUser().getApiToken(), carId, null)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(v -> {
                        // sam
                        operationListener.onSuccess(null);
                        refreshBasicSearchData();
                    }, operationListener::onError));
        } else {
            searchScreen.showErrorMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void refreshBasicSearchData() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        if (isFromBasicRelay.getValue()) {
            disposable.add(homeRepo.fetchCarSearch(apiToken,
                    searchRelay.getValue().getBrand_id(),
                    searchRelay.getValue().getModel_id(),
                    searchRelay.getValue().getCategory_id(),
                    searchRelay.getValue().getGender(),
                    searchRelay.getValue().getYear_form(),
                    searchRelay.getValue().getYear_to(),
                    searchRelay.getValue().getPrice_from(),
                    searchRelay.getValue().getPrice_to(),
                    searchRelay.getValue().getLongitude(),
                    searchRelay.getValue().getLatitude())
                    .filter(carModels -> !carModels.isEmpty())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(carViewModels -> {
//                        searchScreen.appendSearchResultToBottom(carViewModels);
                        List<CarViewModel> tempCarViewModels = carList.getValue();
                        tempCarViewModels.addAll(carViewModels);
                        carList.accept(tempCarViewModels);
                    }, this::processError));
        } else {
            disposable.add(homeRepo.fetchAdvancedSearchCars(apiToken,
                    searchRelay.getValue().getBrand_id(),
                    searchRelay.getValue().getModel_id(),
                    searchRelay.getValue().getCategory_id(),
                    searchRelay.getValue().getGender(),
                    searchRelay.getValue().getYear_form(),
                    searchRelay.getValue().getYear_to(),
                    searchRelay.getValue().getPrice_from(),
                    searchRelay.getValue().getPrice_to(),
                    searchRelay.getValue().getLongitude(),
                    searchRelay.getValue().getLatitude(),
                    searchRelay.getValue().getWarranty(),
                    searchRelay.getValue().getEngineCapacity(),
                    searchRelay.getValue().getColor_id(),
                    searchRelay.getValue().getKilometer_from(),
                    searchRelay.getValue().getKilometer_to(),
                    searchRelay.getValue().getGear_type(),
                    searchRelay.getValue().getCar_status(),
                    searchRelay.getValue().getTracked(),
                    searchRelay.getValue().getPostType(),
                    searchRelay.getValue().getIsInstallment(),
                    searchRelay.getValue().getIsBigSale())
                    .filter(carModels -> !carModels.isEmpty())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(carViewModels -> {
//                        searchScreen.appendSearchResultToBottom(carViewModels);
                        List<CarViewModel> tempCarViewModels = carList.getValue();
                        tempCarViewModels.addAll(carViewModels);
                        carList.accept(tempCarViewModels);
                    }, this::processError));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}