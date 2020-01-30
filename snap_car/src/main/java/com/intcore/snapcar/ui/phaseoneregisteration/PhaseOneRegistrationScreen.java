package com.intcore.snapcar.ui.phaseoneregisteration;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface PhaseOneRegistrationScreen extends BaseActivityScreen {

    void updateCountry(CountryViewModel countryViewModel);

    void onActivationNeeded(String phone, String code);

    void showPhoneError(String phoneErrorMsg);

    void setupEditText(String phoneRegex);
}