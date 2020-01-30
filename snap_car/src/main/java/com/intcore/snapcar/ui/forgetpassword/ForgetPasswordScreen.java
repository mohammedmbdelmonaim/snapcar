package com.intcore.snapcar.ui.forgetpassword;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface ForgetPasswordScreen extends BaseActivityScreen {

    void updateCountry(CountryViewModel countryCode);

    void showPhoneError(String phoneErrorMsg);

    void onActivationNeeded(String phone);

    void setupEditText(String phoneRegex);

    void processLogout();

}