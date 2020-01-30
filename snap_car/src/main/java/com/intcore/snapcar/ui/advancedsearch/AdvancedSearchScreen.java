package com.intcore.snapcar.ui.advancedsearch;

import com.google.android.gms.location.LocationCallback;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface AdvancedSearchScreen extends BaseActivityScreen {

    void setupEditText();

    void showBrandErrorMessage(String messageEn);

    void showModelErrorMessage(String messageEn);

    void showEngineCapacityErrorMessage(String messageEn);

    void showSellerTypeErrorMessage(String messageEn);

    void showColorErrorMessage(String messageEn);

    void showGearErrorMessage(String messageEn);

    void showCarConditionErrorMessage(String messageEn);

    void showCategoryErrorMessage(String messageEn);

    void updatePrice(String minPrice, String maxPrice);

    void setupSlider();

    void startReceivingLocationUpdates(LocationCallback locationCallback);

    void showGPSIsRequiredMessage();

    void stopReceivingLocationUpdates(LocationCallback locationCallback);

    void shouldNavigateToSearch(SearchRequestModel searchRequestModel);

    void showWarrantyErrorMessage(String messageEn);

    void processLogout();

    void initializeInputs();
}