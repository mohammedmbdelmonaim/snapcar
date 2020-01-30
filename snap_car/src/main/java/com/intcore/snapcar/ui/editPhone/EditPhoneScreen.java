package com.intcore.snapcar.ui.editPhone;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface EditPhoneScreen  extends BaseActivityScreen{
    void updateUi(DefaultUserModel currentUser);

    void updateCountry(CountryViewModel countryViewModel);

    void setupEditText(String phoneRegex);

    void showPhoneError(String messageEn);

    void onActivationNeeded(String s, DefaultUserModel code);

    void processLogout();

    void finishScreen();
}
