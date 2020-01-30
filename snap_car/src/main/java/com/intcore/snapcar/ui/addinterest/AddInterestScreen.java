package com.intcore.snapcar.ui.addinterest;

import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface AddInterestScreen extends BaseActivityScreen {

    void setupEditText();

    void addedSuccessfully();

    void showModelErrorMessage(String messageEn);

    void showBrandErrorMessage(String brandMsg);

    void showYearFromErrorMessage(String yearMsg);

    void showYearToErrorMessage(String messageEn);

    void showPriceFromErrorMessage(String message);

    void showPriceToErrorMessage(String messageEn);

//    void showTypeErrorMessage(String messageEn);

    void showColorErrorMessage(String messageEn);

    void showPaymentErrorMessage(String messageEn);

    void showCarConditionErrorMessage(String messageEn);

    void showSellerErrorMessage(String messageEn);

    void showImporterErrorMessage(String messageEn);

    void showGearErrorMessage(String messageEn);

    void showKmToErrorMessage(String messageEn);

    void showKmFromErrorMessage(String messageEn);

    void showCountryErrorMessage(String messageEn);

    void showCityErrorMessage(String messageEn);

    void showCategoryErrorMessage(String messageEn);

    void updateUi(SearchRequestModel interestViewModel);

    void updateBrand(BrandsViewModel model);

    void updateModel(ModelViewModel modelModel);

    void updateCategory(CategoryViewModel model);

    void updateColor(CarColorViewModel carColorModel);

    void updateImporter(ImporterViewModel importerViewModel);

    void initializeAllInputs();

    void processLogout();

    void updateCountry(CountryViewModel aDefault);

    void updateCity(CountryViewModel aDefault);
}
