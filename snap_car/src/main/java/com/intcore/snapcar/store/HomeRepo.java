package com.intcore.snapcar.store;

import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.car.CarSearchDTO;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.filter.FilterMapper;
import com.intcore.snapcar.store.model.filter.FilterModel;
import com.intcore.snapcar.store.model.messageresponse.MessageApiResponse;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import kotlin.Triple;

@ActivityScope
public class HomeRepo {

    private final BehaviorRelay<List<CarApiResponse>> featuredCarList;
    private final InterestRequiredMapper interestRequiredMapper;
    private final WebServiceStore webServiceStore;
    private final FilterMapper filterMapper;
    private final CarMapper carMapper;
    private boolean canLoadMoreSearchResult = true;
    private int currentSearchResultListPage = 0;

    @Inject
    HomeRepo(InterestRequiredMapper interestRequiredMapper,
             WebServiceStore webServiceStore,
             FilterMapper filterMapper,
             CarMapper carMapper) {
        this.featuredCarList = BehaviorRelay.createDefault(Collections.emptyList());
        this.interestRequiredMapper = interestRequiredMapper;
        this.webServiceStore = webServiceStore;
        this.filterMapper = filterMapper;
        this.carMapper = carMapper;
    }

    public Single<InterestRequiredModel> fetchRequiredDataForInterest(String apiToken) {
        return webServiceStore.getRequiredDataFroInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredModel);
    }

    public Single<FilterApiResponse> fetchFilterData(String apiToken, String latitude, String longitude) {
        return webServiceStore.fetchFilterData(apiToken, latitude, longitude);
    }

    public Single<MessageApiResponse> addItemToFavorite(String apiToken, String carId, String userId) {
        return webServiceStore.addItemToFavorite(apiToken, carId, userId);
    }


    public Completable hotzoneLocationClicked(String apiToken, int hotzoneId) {
        return webServiceStore.hotzoneLocationClicked(apiToken, hotzoneId);
    }

    public Single<FilterApiResponse>
    filterHome(String apiToken,
               int brandId,
               int carModelId,
               int categoryId,
               int showAll,
               int menCar,
               int womenCar,
               int damagedCar,
               int vipShowRoom,
               int showRoom,
               int vipHotZone,
               int hotZone,
               double longitude,
               double latitude) {

        return webServiceStore.filterHome(apiToken, carModelId, brandId, categoryId, showAll, menCar, womenCar,
                damagedCar, vipShowRoom, showRoom, vipHotZone, hotZone, longitude, latitude);
    }

    public Single<FilterModel>
    filterExploreList(String apiToken,
                      int carModelId,
                      int brandId,
                      int categoryId,
                      int showAll,
                      int menCar,
                      int womenCar,
                      int damagedCar,
                      int vipShowRoom,
                      int showRoom,
                      int vipHotZone,
                      int hotZone,
                      double longitude,
                      double latitude) {
        return webServiceStore.filterExploreList(apiToken,
                carModelId, brandId, categoryId,
                showAll, menCar, womenCar,
                damagedCar, vipShowRoom,
                showRoom, vipHotZone,
                hotZone, longitude, latitude)
                .map(filterMapper::toFilterModel);
    }

    public Single<FilterModel> fetchUntrackedCars(String apiToken) {
        return webServiceStore.fetchUntrackedCars(apiToken)
                .map(filterMapper::toFilterModel);
    }

    public Single<List<CarModel>> fetchCarSearch(String apiToken,
                                                 int brandId,
                                                 int modelId,
                                                 int categoryId,
                                                 int gender,
                                                 String yearFrom,
                                                 String yearTo,
                                                 String priceFrom,
                                                 String priceTo,
                                                 String longitude,
                                                 String latitude) {
        if (canLoadMoreSearchResult) {
            return webServiceStore.fetchCarSearch(apiToken,
                    brandId, modelId, categoryId, gender, yearFrom, yearTo, priceFrom, priceTo, longitude,
                    latitude, currentSearchResultListPage + 1)
                    .doOnSuccess(this::updateCurrentSearchResultState)
                    .map(Triple::getThird)
                    .map(carSearchDTO -> {
                        featuredCarList.accept(carSearchDTO.getFeaturedCars());
                        return carMapper.toCarModels(carSearchDTO.getCars().getCarApiResponses());
                    });
        } else {
            return Single.just(Collections.emptyList());
        }
    }

    public Single<List<CarModel>>
    fetchAdvancedSearchCars(String apiToken,
                            int brandId,
                            int modelId,
                            int categoryId,
                            int gender,
                            String yearFrom,
                            String yearTo,
                            String priceFrom,
                            String priceTo,
                            String longitude,
                            String latitude,
                            int warranty,
                            int engineCapacity,
                            int color_id,
                            String kilometer_from,
                            String kilometer_to,
                            int gear_type,
                            int car_status,
                            int tracked,
                            int postType,
                            int installment,
                            int bigsale) {
        if (canLoadMoreSearchResult) {
            return webServiceStore.fetchAdvancedSearch(apiToken,
                    brandId, modelId, categoryId,
                    gender, yearFrom, yearTo,
                    priceFrom, priceTo, longitude,
                    latitude, currentSearchResultListPage + 1,
                    warranty, engineCapacity,
                    color_id, kilometer_from,
                    kilometer_to, gear_type, car_status,
                    tracked, postType, installment, bigsale)
                    .doOnSuccess(this::updateCurrentSearchResultState)
                    .map(Triple::getThird)
                    .map(carSearchDTO -> {
                        featuredCarList.accept(carSearchDTO.getFeaturedCars());
                        return carMapper.toCarModels(carSearchDTO.getCars().getCarApiResponses());
                    });

        } else {
            return Single.just(Collections.emptyList());
        }
    }

    public Completable getDirection(String apiToken, int hotZoneId) {
        return webServiceStore.getDirection(apiToken, hotZoneId);
    }

    private void updateCurrentSearchResultState(Triple<Integer, Boolean, CarSearchDTO> currentPageCanLoadMoreTriple) {
        if (currentPageCanLoadMoreTriple.getSecond()) {
            currentSearchResultListPage = currentPageCanLoadMoreTriple.getFirst();
        } else {
            canLoadMoreSearchResult = false;
        }
    }

    public Observable<List<CarApiResponse>> observeFeatureCars() {
        return featuredCarList.hide();
    }

    public void tearDown() {
        currentSearchResultListPage = 0;
        canLoadMoreSearchResult = true;
    }
}