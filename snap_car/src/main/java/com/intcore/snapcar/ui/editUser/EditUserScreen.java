package com.intcore.snapcar.ui.editUser;

import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

public interface EditUserScreen extends BaseActivityScreen {

    void updateCountry(CountryViewModel countryViewModels);

    void updateCountryFromDialog(CountryViewModel countryViewModel);

    void updateUi(DefaultUserModel defaultUserModelSingle);

    void showAreaErrorMessage(String areaErrorMsg);

    void showNameErrorMessage(String nameErrorMsg);

    void showEmailErrorMessage(String emailErrorMsg);

    void setupEditText();

    void setSelectedImage(File file);

    void setNewImagePath(String s);

    void onUpdatedSuccessfully();

    void processLogout();
}
