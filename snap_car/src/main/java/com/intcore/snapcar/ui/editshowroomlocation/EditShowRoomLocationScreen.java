package com.intcore.snapcar.ui.editshowroomlocation;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface EditShowRoomLocationScreen extends BaseActivityScreen {
    void updateCountryFromDialog(CountryViewModel countryViewModel);

    void onUpdatedSuccessfully(DefaultUserModel defaultUserModel);

    void updateCountry(CountryViewModel countryViewModels);

    void updateCity(CountryViewModel countryViewModel);

    void updateUi(DefaultUserModel defaultUserModel);

    void showNameErrorMessage(String messageEn);

    void updateUi(ResponseBody responseBody);

    void updateArea(DefaultUserModel area);

    void setupEditText();

    void processLogout();
}