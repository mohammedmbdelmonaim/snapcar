package com.intcore.snapcar.ui.editinterest;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SwitchCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.constant.PaymentType;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.ui.addinterest.BrandAdapter;
import com.intcore.snapcar.ui.addinterest.CategoryAdapter;
import com.intcore.snapcar.ui.addinterest.CityAdapter;
import com.intcore.snapcar.ui.addinterest.ColorAdapter;
import com.intcore.snapcar.ui.addinterest.CountryAdapter;
import com.intcore.snapcar.ui.addinterest.ImporterAdapter;
import com.intcore.snapcar.ui.addinterest.ModelAdapter;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.intcore.snapcar.store.model.constant.PaymentType.BIG_SALE;
import static com.intcore.snapcar.store.model.constant.PaymentType.EXCHANGE;
import static com.intcore.snapcar.store.model.constant.PaymentType.FULL_PAYMENT;
import static com.intcore.snapcar.store.model.constant.PaymentType.INSTALLMENT;
import static com.intcore.snapcar.store.model.constant.PaymentType.SHOW_ALL;
import static com.intcore.snapcar.store.model.constant.PaymentType.TRANSFER;

@ActivityScope
public class EditInterestActivity extends BaseActivity implements EditInterestScreen {

    @Inject
    EditInterestPresenter presenter;
    @BindView(R.id.et_brand)
    RxEditText brandEditText;
    @BindView(R.id.et_model)
    RxEditText modelEditText;
    @BindView(R.id.et_year)
    RxEditText yearEditText;
    @BindView(R.id.et_year_to)
    RxEditText yearToEditText;
    @BindView(R.id.et_from)
    RxEditText priceFromEditText;
    @BindView(R.id.et_to)
    RxEditText priceToEditText;
    //    @BindView(R.id.et_type)
//    RxEditText typeEditText;
    //    @BindView(R.id.et_gender)
//    RxEditText genderEditText;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.et_category)
    RxEditText categoryEditText;
    @BindView(R.id.et_color)
    RxEditText colorEditText;
    @BindView(R.id.et_payment)
    RxEditText paymentEditText;
    @BindView(R.id.et_condition)
    RxEditText conditionEditText;
    @BindView(R.id.et_seller)
    RxEditText sellerEditText;
    @BindView(R.id.et_importer)
    RxEditText importerEditText;
    @BindView(R.id.et_gear)
    RxEditText gearEditText;
    @BindView(R.id.et_from_km)
    RxEditText fromKmEditText;
    @BindView(R.id.et_to_km)
    RxEditText toKmEditText;
    @BindView(R.id.et_country)
    RxEditText countryEditText;
    @BindView(R.id.et_city)
    RxEditText cityEditText;
    @BindView(R.id.switch_mvpi)
    SwitchCompat switchMvpi;
    @BindView(R.id.switch_nearby)
    SwitchCompat switchNearby;
    //    @BindView(R.id.switch_sale)
//    SwitchCompat switchSale;
    @BindView(R.id.switch_vachel)
    SwitchCompat switchVachel;
    Calendar limitCalender = Calendar.getInstance();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private MaterialDialog materialDialog;
    @Gender
    private int sellerType;
    @CarCondition
    private int carCondition;
    @PaymentType
    private int paymentType;
    @GearType
    private int gearType;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        limitCalender.set(Calendar.YEAR, limitCalender.get(Calendar.YEAR) + 1);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_interest;
    }

    @Override
    public void setupEditText() {
        priceFromEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceToEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        fromKmEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        toKmEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        conditionEditText.setValidityListener(presenter::onAfterCarConditionChange);
        priceFromEditText.setValidityListener(presenter::onAfterPriceFromChange);
        importerEditText.setValidityListener(presenter::onAfterImporterChange);
        categoryEditText.setValidityListener(presenter::onAfterCategoryChange);
        priceToEditText.setValidityListener(presenter::onAfterPriceToChange);
        paymentEditText.setValidityListener(presenter::onAfterPaymentChange);
        countryEditText.setValidityListener(presenter::onAfterCountryChange);
//        genderEditText.setValidityListener(presenter::onAfterGenderChange);
        sellerEditText.setValidityListener(presenter::onAfterSellerChange);
        fromKmEditText.setValidityListener(presenter::onAfterKmFromChange);
        brandEditText.setValidityListener(presenter::onAfterBrandChange);
        modelEditText.setValidityListener(presenter::onAfterModelChange);
        colorEditText.setValidityListener(presenter::onAfterColorChange);
        yearEditText.setValidityListener(presenter::onAfterYearChange);
        yearToEditText.setValidityListener(presenter::onAfterYearToChange);
//        typeEditText.setValidityListener(presenter::onAfterTypeChange);
        gearEditText.setValidityListener(presenter::onAfterGearChange);
        toKmEditText.setValidityListener(presenter::onAfterKmToChange);
        cityEditText.setValidityListener(presenter::onAfterCityChange);
    }

    @Override
    public void addedSuccessfully() {
        showSuccessMessage(getString(R.string.added_successfully));
        finish();
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showModelErrorMessage(String messageEn) {
        modelEditText.setError(messageEn);
    }

    @Override
    public void showBrandErrorMessage(String brandMsg) {
        brandEditText.setError(brandMsg);
    }

    @Override
    public void showYearFromErrorMessage(String yearMsg) {
        yearEditText.setError(yearMsg);
    }

    @Override
    public void showYearToErrorMessage(String messageEn) {
        yearToEditText.setError(messageEn);
    }

    @Override
    public void showPriceFromErrorMessage(String message) {
        priceFromEditText.setError(message);
    }

    @Override
    public void showPriceToErrorMessage(String messageEn) {
        priceToEditText.setError(messageEn);
    }

//    @Override
//    public void showTypeErrorMessage(String messageEn) {
//        typeEditText.setError(messageEn);
//    }

    @Override
    public void showColorErrorMessage(String messageEn) {
        colorEditText.setError(messageEn);
    }

    @Override
    public void showPaymentErrorMessage(String messageEn) {
        paymentEditText.setError(messageEn);
    }

    @Override
    public void showCarConditionErrorMessage(String messageEn) {
        conditionEditText.setError(messageEn);
    }

    @Override
    public void showSellerErrorMessage(String messageEn) {
        sellerEditText.setError(messageEn);
    }

    @Override
    public void showImporterErrorMessage(String messageEn) {
        importerEditText.setError(messageEn);
    }

    @Override
    public void showGearErrorMessage(String messageEn) {
        gearEditText.setError(messageEn);
    }

    @Override
    public void showKmToErrorMessage(String messageEn) {
        toKmEditText.setError(messageEn);
    }

    @Override
    public void showkmFromErrorMessage(String messageEn) {
        fromKmEditText.setError(messageEn);
    }

    @Override
    public void showCountryErrorMessage(String messageEn) {
        countryEditText.setError(messageEn);
    }

    @Override
    public void showCityErrorMessage(String messageEn) {
        cityEditText.setError(messageEn);
    }

    @Override
    public void showCategoryErrorMessage(String messageEn) {
        categoryEditText.setError(messageEn);
    }

    @Override
    public void updateUi(CarViewModel searchRequestModel) {
        yearEditText.setText(searchRequestModel.getYearFrom());
        yearToEditText.setText(searchRequestModel.getYearTo());
        priceToEditText.setText(searchRequestModel.getPriceTo());
        switchMvpi.setChecked(searchRequestModel.getMvpi() == 1);
        toKmEditText.setText(searchRequestModel.getKilometerTo());
        priceFromEditText.setText(searchRequestModel.getPriceFrom());
        switchNearby.setChecked(searchRequestModel.getNearby() == 1);
        switchVachel.setChecked(searchRequestModel.getVehical() == 1);
        fromKmEditText.setText(searchRequestModel.getKilometerFrom());
        if (TextUtils.isEmpty(toKmEditText.getText().toString())) {
            toKmEditText.setText("1000000");
        }
        if (TextUtils.isEmpty(fromKmEditText.getText().toString())) {
            fromKmEditText.setText("0");
        }
//        switchSale.setChecked(searchRequestModel.getSaleId() == 1);
        if (searchRequestModel.getCarCondition() != null && searchRequestModel.getCarCondition() != 0) {
            switch (searchRequestModel.getCarCondition()) {
                case CarCondition.NEW:
                    conditionEditText.setText(getString(R.string.newc));
                    carCondition = CarCondition.NEW;
                    break;
                case CarCondition.DAMAGED:
                    conditionEditText.setText(getString(R.string.damaged));
                    carCondition = CarCondition.DAMAGED;
                    break;
                case CarCondition.USED:
                    conditionEditText.setText(getString(R.string.used));
                    carCondition = CarCondition.USED;
                    break;
                case CarCondition.SHOW_ALL:
                    conditionEditText.setText(getString(R.string.show_all));
                    carCondition = CarCondition.SHOW_ALL;
                    break;
            }
        } else {
            conditionEditText.setText(getString(R.string.show_all));
            carCondition = CarCondition.SHOW_ALL;
        }
        if (searchRequestModel.getSellerType() != null && searchRequestModel.getSellerType() != 0) {
            switch (searchRequestModel.getSellerType()) {
                case Gender.FEMALE:
                    sellerEditText.setText(getString(R.string.female));
                    sellerType = Gender.FEMALE;
                    break;
                case Gender.MALE:
                    sellerEditText.setText(getString(R.string.male));
                    sellerType = Gender.MALE;
                    break;
                case Gender.NOT_SPECIFIED:
                    sellerEditText.setText(getString(R.string.not_specified));
                    sellerType = Gender.NOT_SPECIFIED;
                    break;
                case Gender.SHOW_ALL:
                    sellerEditText.setText(getString(R.string.show_all));
                    sellerType = Gender.SHOW_ALL;
                    break;
            }
        } else {
            sellerEditText.setText(getString(R.string.show_all));
            sellerType = Gender.SHOW_ALL;
        }

        if (searchRequestModel.getGearTypeInterest() != null) {
            switch (searchRequestModel.getGearTypeInterest()) {
                case GearType.AUTO:
                    gearEditText.setText(getString(R.string.auto));
                    gearType = GearType.AUTO;
                    break;
                case GearType.NORMAL:
                    gearEditText.setText(getString(R.string.normal));
                    gearType = GearType.NORMAL;
                    break;
                case GearType.SHOW_ALL:
                    gearEditText.setText(getString(R.string.show_all));
                    gearType = GearType.SHOW_ALL;
                    break;
            }
        } else {
            gearEditText.setText(getString(R.string.show_all));
            gearType = GearType.SHOW_ALL;
        }
        if (searchRequestModel.getMethodOfSaleId() != null) {
            switch (searchRequestModel.getMethodOfSaleId()) {
                case EXCHANGE:
                    paymentEditText.setText(getString(R.string.exchange));
                    paymentType = EXCHANGE;
                    break;
                case TRANSFER:
                    paymentEditText.setText(getString(R.string.transfer));
                    paymentType = TRANSFER;
                    break;
                case INSTALLMENT:
                    paymentEditText.setText(getString(R.string.installments));
                    paymentType = INSTALLMENT;
                    break;
                case FULL_PAYMENT:
                    paymentEditText.setText(getString(R.string.full_payment));
                    paymentType = FULL_PAYMENT;
                    break;
                case BIG_SALE:
                    paymentEditText.setText(R.string.big_sale);
                    paymentType = BIG_SALE;
                    break;
                case SHOW_ALL:
                    paymentEditText.setText(getString(R.string.show_all));
                    paymentType = SHOW_ALL;
                    break;
            }
        } else {
            paymentEditText.setText(getString(R.string.show_all));
            paymentType = SHOW_ALL;
        }
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    @OnClick(R.id.et_country)
    public void onCountryClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new CountryAdapter(this, viewModel -> {
                            presenter.setSelectedCountryModel(viewModel);
                            updateCountry(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getCountryList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @Override
    public void updateCountry(CountryViewModel viewModel) {
        countryEditText.setText(viewModel.getName());
        if (viewModel.getCountryViewModels() != null) {
            if (viewModel.getCountryViewModels().size() > 0) {
                updateCity(viewModel.getCountryViewModels().get(0));
                presenter.setSelectedCityModel(viewModel.getCountryViewModels().get(0));
            } else {
                updateCity(CountryViewModel.createDefault());
                presenter.setSelectedCityModel(CountryViewModel.createDefault());
            }
        }
    }

    @OnClick(R.id.et_city)
    void onCityClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        if (presenter.getSelectedCountryModel() != null) {
            List<CountryViewModel> countryModels = new ArrayList<>();
            countryModels.add(CountryViewModel.createDefault());
            countryModels.addAll(presenter.getSelectedCountryModel().getCountryViewModels());
            materialDialog = new MaterialDialog.Builder(this)
                    .adapter(new CityAdapter(this, viewModel -> {
                                presenter.setSelectedCityModel(viewModel);
                                updateCity(viewModel);
                                materialDialog.dismiss();
                            }, countryModels),
                            new LinearLayoutManager(this))
                    .build();
            materialDialog.show();
        } else {
            showWarningMessage(getString(R.string.you_should_select_country));
        }
    }

    @Override
    public void updateCity(CountryViewModel viewModel) {
        cityEditText.setText(viewModel.getName());
    }

    @OnClick(R.id.et_brand)
    public void onBrandClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new BrandAdapter(this, viewModel -> {
                            presenter.setSelectedBrandModel(viewModel);
                            updateBrand(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getBrandsList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @Override
    public void updateBrand(BrandsViewModel viewModel) {
        brandEditText.setText(viewModel.getName());
        if (viewModel.getBrandsModels().size() > 0) {
            updateModel(viewModel.getBrandsModels().get(0));
            presenter.setSelectedModelModel(viewModel.getBrandsModels().get(0));
        } else {
            updateModel(ModelViewModel.createDefault());
            presenter.setSelectedModelModel(ModelViewModel.createDefault());
        }
    }

    @OnClick(R.id.et_model)
    public void onModelClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        if (presenter.getSelectedBrandModel() != null) {
            List<ModelViewModel> modelModelList = new ArrayList<>();
            modelModelList.add(ModelViewModel.createDefault());
            modelModelList.addAll(presenter.getSelectedBrandModel().getBrandsModels());
            materialDialog = new MaterialDialog.Builder(this)
                    .adapter(new ModelAdapter(this, viewModel -> {
                                presenter.setSelectedModelModel(viewModel);
                                updateModel(viewModel);
                                materialDialog.dismiss();
                            }, modelModelList),
                            new LinearLayoutManager(this))
                    .build();
            materialDialog.show();
        } else {
            showWarningMessage(getString(R.string.you_should_select_brand_first));
        }
    }

    @Override
    public void updateModel(ModelViewModel viewModel) {
        modelEditText.setText(viewModel.getName());
        if (viewModel.getCategoryViewModels().size() > 0) {
            updateCategory(viewModel.getCategoryViewModels().get(0));
            presenter.setSelectedCategoryModel(viewModel.getCategoryViewModels().get(0));
        } else {
            updateCategory(CategoryViewModel.createDefault());
            presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
        }
    }

    @OnClick(R.id.et_color)
    public void onColorClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new ColorAdapter(this, viewModel -> {
                            presenter.setSelectedCarColorModel(viewModel);
                            updateColor(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getCarColorList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @Override
    public void updateColor(CarColorViewModel viewModel) {
        colorEditText.setText(viewModel.getName());
    }

//    @OnClick(R.id.et_type)
//    void onTypeClicked(View view) {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.menu_user_type, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(this::onTypeFlowMenuItemClicked);
//        popupMenu.show();
//    }

//    private boolean onTypeFlowMenuItemClicked(MenuItem menuItem) {
//        if (menuItem.getItemId() == R.id.user) {
//            typeEditText.setText(getString(R.string.user));
//            userType = UserType.USER;
//            return true;
//        } else if (menuItem.getItemId() == R.id.show_room) {
//            typeEditText.setText(getString(R.string.show_room));
//            userType = UserType.SHOW_ROOM;
//            return true;
//        }
//        return true;
//    }

    @OnClick(R.id.et_gear)
    void onGearClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_gear, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onGearFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onGearFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.auto) {
            gearEditText.setText(getString(R.string.auto));
            gearType = GearType.AUTO;
            return true;
        } else if (menuItem.getItemId() == R.id.normal) {
            gearEditText.setText(getString(R.string.normal));
            gearType = GearType.NORMAL;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            gearEditText.setText(getString(R.string.show_all));
            gearType = GearType.SHOW_ALL;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_payment)
    void onPaymentClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_payment, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onPaymentFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onPaymentFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.full_payment) {
            paymentEditText.setText(getString(R.string.full_payment));
            paymentType = PaymentType.FULL_PAYMENT;
            return true;
        } else if (menuItem.getItemId() == R.id.installment) {
            paymentEditText.setText(getString(R.string.installments));
            paymentType = PaymentType.INSTALLMENT;
            return true;
        } else if (menuItem.getItemId() == R.id.transfer) {
            paymentEditText.setText(getString(R.string.undetermined));
            paymentType = PaymentType.TRANSFER;
            return true;
        } else if (menuItem.getItemId() == R.id.exchange) {
            paymentEditText.setText(getString(R.string.exchange));
            paymentType = PaymentType.EXCHANGE;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            paymentEditText.setText(getString(R.string.show_all));
            paymentType = PaymentType.SHOW_ALL;
            return true;
        } else if (menuItem.getItemId() == R.id.big_sale) {
            paymentEditText.setText(getString(R.string.big_sale));
            paymentType = PaymentType.BIG_SALE;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_condition)
    void onConditionClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_condition, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onConditionFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onConditionFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_new) {
            conditionEditText.setText(getString(R.string.new_item));
            carCondition = CarCondition.NEW;
            return true;
        } else if (menuItem.getItemId() == R.id.item_used) {
            conditionEditText.setText(getString(R.string.used));
            carCondition = CarCondition.USED;
            return true;
        } else if (menuItem.getItemId() == R.id.item_damaged) {
            conditionEditText.setText(getString(R.string.damaged));
            carCondition = CarCondition.DAMAGED;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            conditionEditText.setText(getString(R.string.show_all));
            carCondition = CarCondition.SHOW_ALL;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_seller)
    void onSellerClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_gender_show_all, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onSellerFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onSellerFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.male) {
            sellerEditText.setText(getString(R.string.male));
            sellerType = Gender.MALE;
            return true;
        } else if (menuItem.getItemId() == R.id.female) {
            sellerEditText.setText(getString(R.string.female));
            sellerType = Gender.FEMALE;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            sellerEditText.setText(getString(R.string.show_al));
            sellerType = Gender.SHOW_ALL;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_importer)
    void onImporterClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new ImporterAdapter(this, viewModel -> {
                            presenter.setSelectedImporterModel(viewModel);
                            updateImporter(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getImporterList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    public void updateImporter(ImporterViewModel viewModel) {
        importerEditText.setText(viewModel.getName());
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.et_year)
    void onYearClicked() {
        MonthPickerDialog.Builder builder =
                new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                        yearEditText.setText(String.valueOf(selectedYear)),
                        limitCalender.get(Calendar.YEAR),
                        limitCalender.get(Calendar.MONTH));
        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1950)
                .setActivatedYear(limitCalender.get(Calendar.YEAR) - 1)
                .setMaxYear(limitCalender.get(Calendar.YEAR) - 1)
                .showYearOnly()
                .setOnYearChangedListener(year -> yearEditText.setText(String.valueOf(year)))
                .build()
                .show();
    }

    @OnClick(R.id.et_year_to)
    void onYearToClicked() {
        MonthPickerDialog.Builder builder =
                new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                        yearToEditText.setText(String.valueOf(selectedYear)),
                        limitCalender.get(Calendar.YEAR) + 1,
                        limitCalender.get(Calendar.MONTH));
        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1950)
                .setActivatedYear(limitCalender.get(Calendar.YEAR) - 1)
                .setMaxYear(limitCalender.get(Calendar.YEAR))
                .showYearOnly()
                .setOnYearChangedListener(year -> yearToEditText.setText(String.valueOf(year)))
                .build()
                .show();
    }

    @OnClick(R.id.et_category)
    void onCategoryClicked() {
        if (materialDialog != null) {
            materialDialog.dismiss();
            materialDialog = null;
        }
        if (presenter.getSelectedModelModel() != null) {
            List<CategoryViewModel> categoryModels = new ArrayList<>();
            categoryModels.add(CategoryViewModel.createDefault());
            categoryModels.addAll(presenter.getSelectedModelModel().getCategoryViewModels());
            materialDialog = new MaterialDialog.Builder(this)
                    .adapter(new CategoryAdapter(this, viewModel -> {
                                presenter.setSelectedCategoryModel(viewModel);
                                updateCategory(viewModel);
                                materialDialog.dismiss();
                            }, categoryModels),
                            new LinearLayoutManager(this))
                    .build();
            materialDialog.show();
        } else {
            showWarningMessage(getString(R.string.you_should_select_model_first));
        }
    }

    @Override
    public void updateCategory(CategoryViewModel viewModel) {
        categoryEditText.setText(viewModel.getName());
    }

    @OnClick(R.id.tv_save)
    public void onViewClicked() {
        presenter.onSaveClicked(gearType, carCondition, sellerType, paymentType,
                priceFromEditText.getText().toString(),
                priceToEditText.getText().toString(),
                fromKmEditText.getText().toString(),
                toKmEditText.getText().toString(),
                yearEditText.getText().toString(),
                yearToEditText.getText().toString(),
                switchMvpi.isChecked() ? 1 : 0,
                switchNearby.isChecked() ? 1 : 0,
//                switchSale.isChecked() ? 1 : 0,
                switchVachel.isChecked() ? 1 : 0);
    }
}