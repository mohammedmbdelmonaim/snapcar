package com.intcore.snapcar.ui.advancedsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Looper;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SwitchCompat;
import android.text.InputType;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.constant.PostType;
import com.intcore.snapcar.store.model.constant.Tracking;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.ui.addinterest.BrandAdapter;
import com.intcore.snapcar.ui.addinterest.CategoryAdapter;
import com.intcore.snapcar.ui.addinterest.ColorAdapter;
import com.intcore.snapcar.ui.addinterest.ModelAdapter;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.search.SearchActivityArgs;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

/*
 *This class is responsible for allowing user to make advanced search
 */
@ActivityScope
public class AdvancedSearchActivity extends BaseActivity implements AdvancedSearchScreen {

    private static String[] c = new String[]{" K", " m", " b", " t"};
    @BindView(R.id.et_brand)
    RxEditText brandEditText;
    @BindView(R.id.et_model)
    RxEditText modelEditText;
    @BindView(R.id.et_category)
    RxEditText categoryEditText;
    @BindView(R.id.et_color)
    RxEditText colorEditText;
    @BindView(R.id.et_condition)
    RxEditText conditionEditText;
    @BindView(R.id.et_seller)
    RxEditText sellerEditText;
    @BindView(R.id.et_gear)
    RxEditText gearEditText;
    @BindView(R.id.et_warranty)
    RxEditText warrantyEditText;
    @BindView(R.id.et_engine_capacity)
    RxEditText engineCapacityEditText;
    @BindView(R.id.et_from_km)
    RxEditText fromKmEditText;
    @BindView(R.id.et_to_km)
    RxEditText toKmEditText;
    @BindView(R.id.et_post_type)
    RxEditText postTypeEditText;
    @BindView(R.id.et_tracked)
    RxEditText trackedEditText;
    @BindView(R.id.switch_nearby)
    SwitchCompat switchNearby;
    @BindView(R.id.price_slider)
    MultiSlider priceMultiSlider;
    @BindView(R.id.year_slider)
    MultiSlider yearMultiSlider;
    @BindView(R.id.tv_year_min)
    TextView minYearTextView;
    @BindView(R.id.tv_year_max)
    TextView maxYearTextView;
    @BindView(R.id.tv_price_min)
    TextView minPriceTextView;
    @BindView(R.id.tv_price_max)
    TextView maxPriceTextView;
    @BindView(R.id.et_price_from)
    EditText priceFromEditText;
    @BindView(R.id.et_price_to)
    EditText priceToEditText;
    @BindView(R.id.et_year_to)
    EditText yearToEditText;
    @BindView(R.id.et_year_from)
    EditText yearFromEditText;

    @Inject
    AdvancedSearchPresenter presenter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.switch_installment)
    SwitchCompat installmentSwitch;
    @BindView(R.id.switch_big_sale)
    SwitchCompat bigSaleSwitch;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private MaterialDialog materialDialog;
    @Gender
    private int sellerType = Gender.SHOW_ALL;
    @CarCondition
    private int carCondition = CarCondition.SHOW_ALL;
    @Tracking
    private int tracked = Tracking.SHOW_ALL;
    @PostType
    private int postType = PostType.SHOW_ALL;
    @GearType
    private int gearType = GearType.SHOW_ALL;
    private int warranty = 2;
    private int engineCapacity = 7;
    private int isInstallment = 0;
    private int isBigSale = 0;
    Calendar limitCalender = Calendar.getInstance();
    private int fromYear = limitCalender.get(Calendar.YEAR) - 1;
    private int toYear = limitCalender.get(Calendar.YEAR) + 1;

    private static String coolFormat(int n, int iteration) {
        int d = (n / 100) / 10;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration]) : coolFormat(d, iteration + 1));
    }

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    public void initializeInputs() {
        presenter.setSelectedCarColorModel(CarColorViewModel.createDefault());
        updateColor(CarColorViewModel.createDefault());
        presenter.setSelectedBrandModel(BrandsViewModel.createDefault());
        updateBrand(BrandsViewModel.createDefault());
        presenter.setSelectedModelModel(ModelViewModel.createDefault());
        updateModel(ModelViewModel.createDefault());
        presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
        updateCategory(CategoryViewModel.createDefault());
        gearEditText.setText(getString(R.string.show_all));
        gearType = GearType.SHOW_ALL;
        conditionEditText.setText(getString(R.string.show_all));
        carCondition = CarCondition.SHOW_ALL;
        sellerEditText.setText(getString(R.string.show_al));
        sellerType = Gender.SHOW_ALL;
        engineCapacityEditText.setText(getString(R.string.show_all));
        engineCapacity = 7;
        warrantyEditText.setText(getString(R.string.show_all));
        warranty = 2;
        trackedEditText.setText(getString(R.string.show_all));
        postTypeEditText.setText(getString(R.string.show_all));
        fromKmEditText.setText("0");
        toKmEditText.setText("1000000");
    }

    @OnClick({R.id.switch_big_sale, R.id.switch_installment})
    void onPriceSwitchClicked(View v) {
        switch (v.getId()) {
            case R.id.switch_installment:
                isInstallment = (isInstallment == 1 ? 0 : 1);
                isBigSale = 0;
                bigSaleSwitch.setChecked(false);
                break;
            case R.id.switch_big_sale:
                isBigSale = (isBigSale == 1 ? 0 : 1);
                isInstallment = 0;
                installmentSwitch.setChecked(false);
                break;
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_advanced_search;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(this).get())
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper());
    }

    @Override
    public void showGPSIsRequiredMessage() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.gps_required)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
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
    public void stopReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(this).get())
                .removeLocationUpdates(locationCallback);
    }

    @Override
    public void shouldNavigateToSearch(SearchRequestModel searchRequestModel) {
        new SearchActivityArgs(searchRequestModel, false)
                .launch(this);
        //finish();
    }

    @Override
    public void showWarrantyErrorMessage(String messageEn) {
        warrantyEditText.setError(messageEn);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void setupEditText() {
        fromKmEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        toKmEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        engineCapacityEditText.setValidityListener(presenter::onAfterEngineCapacityChange);
        conditionEditText.setValidityListener(presenter::onAfterCarConditionChange);
        categoryEditText.setValidityListener(presenter::onAfterCategoryChange);
        warrantyEditText.setValidityListener(presenter::onAfterWarrantyChange);
        postTypeEditText.setValidityListener(presenter::onAfterPostTypeChange);
        trackedEditText.setValidityListener(presenter::onAfterTrackedChange);
        sellerEditText.setValidityListener(presenter::onAfterSellerChange);
        fromKmEditText.setValidityListener(presenter::onAfterKmFromChange);
        brandEditText.setValidityListener(presenter::onAfterBrandChange);
        colorEditText.setValidityListener(presenter::onAfterColorChange);
        modelEditText.setValidityListener(presenter::onAfterModelChange);
        toKmEditText.setValidityListener(presenter::onAfterKmToChange);
        gearEditText.setValidityListener(presenter::onAfterGearChange);
        disableLongPress(engineCapacityEditText);
        disableLongPress(conditionEditText);
        disableLongPress(categoryEditText);
        disableLongPress(warrantyEditText);
        disableLongPress(postTypeEditText);
        disableLongPress(trackedEditText);
        disableLongPress(sellerEditText);
        disableLongPress(fromKmEditText);
        disableLongPress(brandEditText);
        disableLongPress(colorEditText);
        disableLongPress(modelEditText);
        disableLongPress(toKmEditText);
        disableLongPress(gearEditText);
    }

    @Override
    public void showBrandErrorMessage(String messageEn) {
        brandEditText.setError(messageEn);
    }

    @Override
    public void showModelErrorMessage(String messageEn) {
        modelEditText.setError(messageEn);
    }

    @Override
    public void showEngineCapacityErrorMessage(String messageEn) {
        engineCapacityEditText.setError(messageEn);
    }

    @Override
    public void showSellerTypeErrorMessage(String messageEn) {
        sellerEditText.setError(messageEn);
    }

    @Override
    public void showColorErrorMessage(String messageEn) {
        colorEditText.setError(messageEn);
    }

    @Override
    public void showGearErrorMessage(String messageEn) {
        gearEditText.setError(messageEn);
    }

    @Override
    public void showCarConditionErrorMessage(String messageEn) {
        conditionEditText.setError(messageEn);
    }

    @Override
    public void showCategoryErrorMessage(String messageEn) {
        categoryEditText.setError(messageEn);
    }

    @Override
    public void updatePrice(String minPrice, String maxPrice) {
        if (maxPrice != null && minPrice != null) {
            priceMultiSlider.setMax(Integer.parseInt(maxPrice));
            priceMultiSlider.setMin(Integer.parseInt(minPrice));
            maxPriceTextView.setText(String.valueOf(coolFormat((Integer.parseInt(maxPrice)), 0)));
            minPriceTextView.setText(minPrice);
            priceMultiSlider.getThumb(0).setValue(Integer.parseInt(minPrice));
            priceMultiSlider.getThumb(1).setValue(Integer.parseInt(maxPrice));
        }
        Calendar currentDate = Calendar.getInstance();

        priceToEditText.setText(maxPrice);
        priceFromEditText.setText(minPrice);
        yearToEditText.setText(String.valueOf(currentDate.get(Calendar.YEAR) + 1));
        yearFromEditText.setText("1950");

        if (!isEnglishLang()) {
            minPriceTextView.setGravity(Gravity.RIGHT);
            maxPriceTextView.setGravity(Gravity.LEFT);
        }
        yearMultiSlider.setMax(currentDate.get(Calendar.YEAR) + 1);
        yearMultiSlider.setMin(1950);
        minYearTextView.setText(String.valueOf(yearMultiSlider.getMin()));
        maxYearTextView.setText(String.valueOf(yearMultiSlider.getMax()));
        yearMultiSlider.getThumb(0).setValue(yearMultiSlider.getMin());
        yearMultiSlider.getThumb(1).setValue(yearMultiSlider.getMax());


    }

    private boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }

    @Override
    public void setupSlider() {
        yearMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
            minYearTextView.setText(String.valueOf(multiSlider.getThumb(0).getValue()));
            maxYearTextView.setText(String.valueOf(multiSlider.getThumb(1).getValue()));
        });
        priceMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
            minPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(0).getValue()), 0)));
            maxPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(1).getValue()), 0)));
        });
    }

    @OnClick(R.id.et_brand)
    public void onBrandClicked() {
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

    private void updateBrand(BrandsViewModel viewModel) {
        brandEditText.setText(viewModel.getName());
        if (viewModel.getBrandsModels().size() > 0) {
            updateModel(viewModel.getBrandsModels().get(0));
            presenter.setSelectedModelModel(viewModel.getBrandsModels().get(0));
        }
    }

    @OnClick(R.id.et_model)
    public void onModelClicked() {
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

    private void updateModel(ModelViewModel viewModel) {
        modelEditText.setText(viewModel.getName());
        if (viewModel.getCategoryViewModels().size() > 0) {
            updateCategory(viewModel.getCategoryViewModels().get(0));
            presenter.setSelectedCategoryModel(viewModel.getCategoryViewModels().get(0));
        }
    }

    @OnClick(R.id.et_category)
    void onCategoryClicked() {
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

    private void updateCategory(CategoryViewModel viewModel) {
        categoryEditText.setText(viewModel.getName());
    }

    @OnClick(R.id.et_color)
    public void onColorClicked() {
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

    private void updateColor(CarColorViewModel viewModel) {
        colorEditText.setText(viewModel.getName());
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
            sellerEditText.setText(getString(R.string.show_all));
            sellerType = Gender.SHOW_ALL;
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

    @OnClick(R.id.et_warranty)
    void onWarrantyClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_warranty, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onWarrantyFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onWarrantyFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_yes) {
            warrantyEditText.setText(getString(R.string.yes));
            warranty = 1;
            return true;
        } else if (menuItem.getItemId() == R.id.item_no) {
            warrantyEditText.setText(getString(R.string.no));
            warranty = 0;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            warrantyEditText.setText(getString(R.string.show_all));
            warranty = 2;
            return true;
        }
        return true;
    }

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

    @OnClick(R.id.et_engine_capacity)
    void onEngineCapacityClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_engine_capacity, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onEngineCapacityFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onEngineCapacityFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_1) {
            engineCapacityEditText.setText(getString(R.string._0_800));
            engineCapacity = 1;
            return true;
        } else if (menuItem.getItemId() == R.id.item_2) {
            engineCapacityEditText.setText(getString(R.string._1000_1300));
            engineCapacity = 2;
            return true;
        } else if (menuItem.getItemId() == R.id.item_3) {
            engineCapacityEditText.setText(getString(R.string._1400_1600));
            engineCapacity = 3;
            return true;
        } else if (menuItem.getItemId() == R.id.item_4) {
            engineCapacityEditText.setText(getString(R.string._1800_2000));
            engineCapacity = 4;
            return true;
        } else if (menuItem.getItemId() == R.id.item_5) {
            engineCapacityEditText.setText(getString(R.string._2200_2800));
            engineCapacity = 5;
            return true;
        } else if (menuItem.getItemId() == R.id.item_6) {
            engineCapacityEditText.setText(getString(R.string._3000));
            engineCapacity = 6;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            engineCapacityEditText.setText(getString(R.string.show_all));
            engineCapacity = 7;
            return true;
        } else {
            engineCapacity = 0;
            return true;
        }
    }

    @OnClick(R.id.et_tracked)
    void onTrackedClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_tracked, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onTrackedFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onTrackedFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.tracked) {
            trackedEditText.setText(getString(R.string.tracked_car));
            tracked = Tracking.TRACKED;
            return true;
        } else if (menuItem.getItemId() == R.id.un_tracked) {
            trackedEditText.setText(getString(R.string.un_tracked));
            tracked = Tracking.UN_TRACKED;
            return true;
        } else if (menuItem.getItemId() == R.id.show_all) {
            trackedEditText.setText(getString(R.string.show_all));
            tracked = Tracking.SHOW_ALL;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_post_type)
    void onPostTypeClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_post_type, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onPostTypeFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onPostTypeFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.special) {
            postTypeEditText.setText(getString(R.string.special));
            postType = PostType.SPECIAL;
            return true;
        } else if (menuItem.getItemId() == R.id.free) {
            postTypeEditText.setText(getString(R.string.free));
            postType = PostType.FREE;
            return true;
        } else if (menuItem.getItemId() == R.id.show_all) {
            postTypeEditText.setText(getString(R.string.show_all));
            postType = PostType.SHOW_ALL;
            return true;
        }
        return true;
    }


    @OnClick(R.id.et_year_from)
    void onYearClicked() {
        if (fromYear == limitCalender.get(Calendar.YEAR) - 1) {
            MonthPickerDialog.Builder builder =
                    new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                            yearFromEditText.setText(String.valueOf(selectedYear)),
                            limitCalender.get(Calendar.YEAR),
                            limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(limitCalender.get(Calendar.YEAR) - 1)
                    .setMaxYear(limitCalender.get(Calendar.YEAR) - 1)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        yearFromEditText.setText(String.valueOf(year));
                        fromYear = year;
                    })
                    .build()
                    .show();
        } else {
            MonthPickerDialog.Builder builder =
                    new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                            yearFromEditText.setText(String.valueOf(selectedYear)),
                            limitCalender.get(Calendar.YEAR),
                            limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(fromYear)
                    .setMaxYear(limitCalender.get(Calendar.YEAR) - 1)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        yearFromEditText.setText(String.valueOf(year));
                        fromYear = year;
                    })
                    .build()
                    .show();
        }
    }

    @OnClick(R.id.et_year_to)
    void onYearToClicked() {
        if (toYear == limitCalender.get(Calendar.YEAR) + 2) {
            MonthPickerDialog.Builder builder =
                    new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                            yearToEditText.setText(String.valueOf(selectedYear)),
                            limitCalender.get(Calendar.YEAR) + 2,
                            limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(limitCalender.get(Calendar.YEAR))
                    .setMaxYear(limitCalender.get(Calendar.YEAR) + 1)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        yearToEditText.setText(String.valueOf(year));
                        toYear = year;
                    })
                    .build()
                    .show();
        } else {
            MonthPickerDialog.Builder builder = new
                    MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                    yearToEditText.setText(String.valueOf(selectedYear)),
                    limitCalender.get(Calendar.YEAR) + 2,
                    limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(toYear)
                    .setMaxYear(limitCalender.get(Calendar.YEAR) + 1)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        yearToEditText.setText(String.valueOf(year));
                        toYear = year;
                    })
                    .build()
                    .show();
        }
    }

    @OnClick(R.id.tv_search)
    void onSearchClicked() {
        presenter.onSearchClicked(gearType,
                carCondition,
                sellerType,
                engineCapacity,
                warranty,
                tracked,
                postType,
                yearFromEditText.getText().toString(),
                yearToEditText.getText().toString(),
                priceFromEditText.getText().toString(),
                priceToEditText.getText().toString(),
                fromKmEditText.getText().toString(),
                toKmEditText.getText().toString(),
                switchNearby.isChecked() ? 1 : 0,
                isInstallment,
                isBigSale);
    }

    void disableLongPress(RxEditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

}