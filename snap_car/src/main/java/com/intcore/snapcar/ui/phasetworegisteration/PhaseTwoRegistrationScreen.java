package com.intcore.snapcar.ui.phasetworegisteration;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface PhaseTwoRegistrationScreen extends BaseActivityScreen {

    void setupEditText();

    void showAreaErrorMessage(String areaErrorMsg);

    void showNameErrorMessage(String nameErrorMsg);

    void showEmailErrorMessage(String emailErrorMsg);

    void showPasswordErrorMessage(String passwordErrorMsg);

    void showConfirmPasswordErrorMessage(String confirmPasswordErrorMsg);

    void shouldNavigateToHost(String name, String email, String area, String password, String phone);

    void updateCountry(CountryViewModel countryViewModel);
}