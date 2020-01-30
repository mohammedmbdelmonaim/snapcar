package com.intcore.snapcar.ui.login;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface LoginScreen extends BaseActivityScreen {

    void shouldNavigateToHome(String phone, String password);

    void updateCountry(CountryViewModel countryCode);

    void showPasswordErrorMsg(String passwordErrorMsg);

    void showPhoneErrorMsg(String phoneErrorMsg);

    void setupEditText(String phoneRegex);
}