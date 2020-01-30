package com.intcore.snapcar.ui.editshowroomphone;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface EditShowRoomPhoneScreen extends BaseActivityScreen {
    void updateUi(DefaultUserModel currentUser);

    void onUpdatedSuccessfully(DefaultUserModel defaultUserModel);

    void setupEditText(String phoneRegex);

    void updateCountry(CountryViewModel countryViewModel);

    void showPhoneErrorMessage(String messageEn);

    void showphoneError(String invalid_phone_number, int i);

    void processLogout();
}
