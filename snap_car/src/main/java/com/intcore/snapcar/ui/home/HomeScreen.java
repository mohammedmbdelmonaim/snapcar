package com.intcore.snapcar.ui.home;

import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;

public interface HomeScreen {

    void updateUi(FilterApiResponse filterViewModel);

    void showErrorMessage(String errorMsg);

    void showLoadingAnimation();

    void initializeFragment();

    void hideLoadingAnimation();

    void showCarExpiredDialog(CarApiResponse carApiResponse);

    void setSwearData(SettingsModel settingsApiResponse);

    void showSurveyDialog(SurveyApiResponse.Surveies surveies);

    void processLogout();

    void showSuccessMessage(String string);

    void showSwearDialog(SettingsModel settingsModel, int carId);

    void showTermsDialog();

    void huaweiPermission();

    void showWarningMessage(String msg);

    void showInterestPopup();
}