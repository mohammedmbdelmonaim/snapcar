package com.intcore.snapcar.store;

import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import kotlin.Triple;

@ActivityScope
public class InterestRepo {

    private final InterestRequiredMapper interestRequiredMapper;
    private final WebServiceStore webServiceStore;
    private final CarMapper carMapper;
    private boolean canLoadMoreInterestList = true;
    private int currentInterestListPage = 0;

    @Inject
    InterestRepo(InterestRequiredMapper interestRequiredMapper, WebServiceStore webServiceStore, CarMapper carMapper) {
        this.interestRequiredMapper = interestRequiredMapper;
        this.webServiceStore = webServiceStore;
        this.carMapper = carMapper;
    }

    public Single<InterestRequiredModel> fetchRequiredDataForInterest(String apiToken) {
        return webServiceStore.getRequiredDataFroInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredModel);
    }

    public Completable addInterest(String apiToken, int gearType, int specificationId, int paymentType,
                                   int colorId, int mvpi, int brandId, int modelId, String priceFrom, String priceTo,
                                   String year, String yearTo, int carCondition, int sellerType,
                                   int nearby, int cityId, int countryId, String kmFrom, String kmTo,
                                   int categoryId, int vehicle) {
//                                   int bigSale
        return webServiceStore.addInterest(apiToken, gearType, specificationId, paymentType, colorId, mvpi,
                brandId, modelId, priceFrom, priceTo, year, yearTo, carCondition, sellerType,
                nearby, cityId, countryId, kmFrom, kmTo, categoryId, vehicle);
//                bigSale

    }

    public Completable updateInterest(String apiToken, int interestId, int gearType, int specificationId, int paymentType,
                                      int colorId, int mvpi, int brandId, int modelId, String priceFrom, String priceTo,
                                      String year, String yearTo, int carCondition, int sellerType,
                                      int nearby, int cityId, int countryId, String kmFrom, String kmTo,
                                      int categoryId, int vehicle) {
        return webServiceStore.updateInterest(apiToken, interestId, gearType, specificationId,
                paymentType, colorId, mvpi,
                brandId, modelId, priceFrom, priceTo, year, yearTo, carCondition, sellerType,
                nearby, cityId, countryId, kmFrom, kmTo, categoryId, vehicle);
    }


    public Single<List<CarModel>> fetchMyInterest(String apiToken) {
        if (canLoadMoreInterestList) {
            return webServiceStore.fetchMyInterest(apiToken, currentInterestListPage + 1)
                    .doOnSuccess(this::updateCurrentInterestsState)
                    .map(Triple::getThird)
                    .map(carMapper::toCarModels);
        } else {
            return Single.just(Collections.emptyList());
        }
    }

    private void updateCurrentInterestsState(Triple<Integer, Boolean, List<CarApiResponse>> currentPageCanLoadMoreTriple) {
        if (currentPageCanLoadMoreTriple.getSecond()) {
            currentInterestListPage = currentPageCanLoadMoreTriple.getFirst();
        } else {
            canLoadMoreInterestList = false;
        }
    }

    public Completable removeInterestFromList(String apiToken, int interestId) {
        return webServiceStore.removeUserFromInterest(apiToken, interestId);
    }

    public void tearDown() {
        currentInterestListPage = 0;
        canLoadMoreInterestList = true;
    }
}