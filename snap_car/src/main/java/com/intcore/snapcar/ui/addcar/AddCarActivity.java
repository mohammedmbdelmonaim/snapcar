package com.intcore.snapcar.ui.addcar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.ui.addcarphotes.AddCarPhotosActivity;
import com.intcore.snapcar.ui.editshowroomlocation.EditShowRoomLocationActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivityForAddcar;
import com.intcore.snapcar.util.UserManager;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import timber.log.Timber;

@ActivityScope
public class AddCarActivity extends BaseActivity implements AddCarScreen {

    private final static int PLACE_PICKER_REQUEST = 999;
    @BindView(R.id.circle_image_selected)
    CircleImageView isImageSelectedCircleImageView;
    @BindView(R.id.circle_examination_selected)
    CircleImageView isExaminationSelectedCircleImageView;
    @BindView(R.id.circle_brand_selected)
    CircleImageView isBrandSelectedCircleImageView;
    @BindView(R.id.circle_categ_selected)
    CircleImageView isCategorySelectedCircleImageView;
    @BindView(R.id.circle_man_year_selected)
    CircleImageView isYearSelectedCircleImageView;
    @BindView(R.id.circle_transmission_selected)
    CircleImageView isTransmissionSelectedCircleImageView;
    @BindView(R.id.circle_importer_selected)
    CircleImageView isImporterSelectedCircleImageView;
    @BindView(R.id.circle_kilometers_selected)
    CircleImageView isKilometersSelectedCircleImageView;
    @BindView(R.id.circle_engine_selected)
    CircleImageView isEngineSelectedCircleImageView;
    @BindView(R.id.priceIsSelected)
    CircleImageView isPriceSelectedImageView;
    @BindView(R.id.circle_color_year_selected)
    CircleImageView isColorSelectedCircleImageView;
    @BindView(R.id.circle_condtion_selected)
    CircleImageView isConditionSelectedCircleImageView;
    @BindView(R.id.circle_warranty_selected)
    CircleImageView isWarrantySelectedCircleImageView;
    @BindView(R.id.circle_model_selected)
    CircleImageView isModelSelectedCircleImageView;
    @BindView(R.id.circle_post_selected)
    CircleImageView isPostSelectedCircleImageView;
    @BindView(R.id.circle_location_picked)
    CircleImageView isLocationSelectedCircleImageView;
    @BindView(R.id.priceTypingLayout)
    EditText priceTypingLayout;
    @BindView(R.id.pickLocationLayout)
    RelativeLayout pickFixedLocationLayout;
    @BindView(R.id.sw2)
    SwitchCompat trackedCarSwitchCompact;
    @BindView(R.id.sw3)
    SwitchCompat fixedLocationSwitchCompact;
    @BindView(R.id.sw4)
    SwitchCompat phoneCallSwitchCompact;
    @BindView(R.id.sw5)
    SwitchCompat chatSwitchCompact;
    @BindView(R.id.ed_km)
    EditText KmEditText;
    @BindView(R.id.agent_name_editText)
    EditText agentNameEditText;
    @BindView(R.id.tv_premiumPrice)
    TextView premiumPriceTextView;
    @BindView(R.id.notesEditText)
    EditText notesEditText;
    @BindView(R.id.ed_cc)
    EditText engineCcEditText;
    @BindView(R.id.agent_name_layout)
    LinearLayout agentNameLayout;
    @BindView(R.id.installmentLayout)
    ConstraintLayout installmentLayout;
    @BindView(R.id.discountLayout)
    ConstraintLayout discountLayout;
    @BindView(R.id.et_dis_from)
    EditText discountBeforeEditText;
    @BindView(R.id.et_dis_to)
    EditText discountAfterEditText;
    @BindView(R.id.et_install_from)
    EditText installmentFromEditText;
    @BindView(R.id.et_install_to)
    EditText installmentToEditText;
    @BindView(R.id.imageCancel_one)
    ImageView examinationCancelOneImageView;
    @BindView(R.id.imageCancel_two)
    ImageView examinationCancelTwoImageView;
    @BindView(R.id.imageCancel_three)
    ImageView examinationCancelThreeImageView;
    @BindView(R.id.radio4)
    RadioButton discountRadioButton;
    @BindView(R.id.radio5)
    RadioButton installmentRadioButton;
    @BindView(R.id.radio3)
    RadioButton exchangeRadioButton;
    @BindView(R.id.radio1)
    RadioButton fixedPriceRadioButton;
    @BindView(R.id.radio2)
    RadioButton undeterminedRadioButton;
    @BindView(R.id.examinationUploaded_one)
    ImageView examinationOneImageView;
    @BindView(R.id.examinationUploaded_two)
    ImageView examinationTwoImageView;
    @BindView(R.id.examinationUploaded_three)
    ImageView examinationThreeImageView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.add_image_cell)
    ImageView addImageCell;
    @BindView(R.id.location_cell)
    ImageView locationCell;
    @BindView(R.id.add_examination_image)
    ImageView addExaminationImage;
    @Inject
    AddCarPresenter presenter;
    @Inject
    JopDispatcher jopDispatcher;
    @Inject
    UserManager sessionManager;
    @BindView(R.id.iv_selected_brand)
    TextView ivSelectedBrand;
    @BindView(R.id.iv_selected_model)
    TextView ivSelectedModel;
    @BindView(R.id.iv_selected_category)
    TextView ivSelectedCategory;
    @BindView(R.id.iv_selected_year)
    TextView ivSelectedYear;
    @BindView(R.id.iv_selected_color)
    TextView ivSelectedColor;
    @BindView(R.id.iv_selected_transmission)
    TextView ivSelectedTransmission;
    @BindView(R.id.iv_selected_importer)
    TextView ivSelectedImporter;
    @BindView(R.id.iv_selected_condition)
    TextView ivSelectedCondition;
    @BindView(R.id.iv_selected_post_type)
    TextView ivSelectedPostType;
    @BindView(R.id.sw7)
    SwitchCompat picFixedLocationOnMapSwitch;
    @BindView(R.id.circle_location_picked_on_map)
    CircleImageView circleLocationPickedOnMap;
    @BindView(R.id.location_on_map_cell)
    ImageView locationOnMapCell;
    @BindView(R.id.pickLocationOnMapLayout)
    RelativeLayout pickLocationOnMapLayout;
    @BindView(R.id.map_options_layout)
    LinearLayout mapOptionsLayout;
    @BindView(R.id.tracked_car_layout)
    RelativeLayout trackedCarLayout;
    @BindView(R.id.map_options_divider)
    LinearLayout mapOptionsDivider;
    @BindView(R.id.im_examination_one)
    RelativeLayout imExaminationOne;
    @BindView(R.id.im_examination_two)
    RelativeLayout imExaminationTwo;
    @BindView(R.id.im_examination_three)
    RelativeLayout imExaminationThree;
    @BindView(R.id.examination_divider)
    LinearLayout examinationDivider;
    @BindView(R.id.tv_note_counter)
    TextView noteCounterTextView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    private Calendar limitCalender = Calendar.getInstance();
    private MaterialDialog materialDialog;
    private int isMVPI = 0;
    private int isTracked = 3;
    private int importerId = 0;
    private int brandId;
    private int mnufacturingYear = 0;
    private int transmissionId = 0;
    private int modelId;
    private int warrantyId = 0;
    private String price = "";
    private String images = "";
    private int colorId = 0;
    private int posttype = 0;
    private int categoryId = 0;
    private int conditionId = 0;
    private int radioButtonId = 1;
    private String latTracked = "";
    private String lonTracked = "";
    private String latFixedOnMap = "";
    private String lonFixedOnmap = "";
    private String latFixedLocation = "";
    private String lonFixedLocation = "";
    private int contactOption = 1;
    private int exchange = 0;
    private int contactChat = 1;
    private int contactCall = 0;
    private String condition = "";
    private String transmission = "";
    private String warranty = "";
    private String categoryy = "";
    private SettingsModel settingsApiResponse;
    private String instTo;
    private String instFrom;
    private String priceto;
    private String priceFrom;
    private int vehicleRegistration;
    private int currentExaminationPostion;
    private String examinationImageOne;
    private String examinationImageTwo;
    private String examinationImageThree;
    private int addExaminationCount = 0;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        limitCalender.set(Calendar.YEAR, limitCalender.get(Calendar.YEAR) - 1);
        CheckWhetherShowRoom();
        setupBack();
    }

    @Override
    protected void onResume() {
        if (sessionManager.sessionManager().isSessionActive()) {
            if (sessionManager.getCurrentUser().getType() == 2) {
                this.isTracked = 0;
                String latitude = sessionManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                if (TextUtils.isEmpty(latitude)) {
//                    showWarningMessage(getString(R.string.you_must_set_location_first));
                } else {
                    latFixedLocation = sessionManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                    lonFixedLocation = sessionManager.getCurrentUser().getShowRoomInfoModel().getLongitude();
                }
            }
        }
        super.onResume();
    }

    @SuppressLint("ResourceType")
    private void setupBack() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        addImageCell.setImageDrawable(icons.getDrawable(1));
        locationCell.setImageDrawable(icons.getDrawable(1));
        locationOnMapCell.setImageDrawable(icons.getDrawable(1));
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    private void CheckWhetherShowRoom() {
        if (presenter.isShowRoom()) {
            exchangeRadioButton.setVisibility(View.GONE);
            discountRadioButton.setVisibility(View.VISIBLE);
            if (sessionManager.sessionManager().isSessionActive()) {
                mapOptionsDivider.setVisibility(View.GONE);
                mapOptionsLayout.setVisibility(View.GONE);
                this.isTracked = 0;
                String latitude = sessionManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                if (TextUtils.isEmpty(latitude)) {
//                    showWarningMessage(getString(R.string.you_must_set_location_first));
                } else {
                    latFixedLocation = sessionManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                    lonFixedLocation = sessionManager.getCurrentUser().getShowRoomInfoModel().getLongitude();
                }

            }
        } else {
            exchangeRadioButton.setVisibility(View.VISIBLE);
            discountRadioButton.setVisibility(View.GONE);
        }
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
    protected int getLayout() {
        return R.layout.activity_add_car;
    }

    @Override
    public void initEditTexts() {
        engineCcEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!engineCcEditText.getText().toString().isEmpty()) {
                    isEngineSelectedCircleImageView.setImageResource(R.drawable.check_circle);
                } else {
                    isEngineSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        KmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!KmEditText.getText().toString().isEmpty()) {
                    isKilometersSelectedCircleImageView.setImageResource(R.drawable.check_circle);
                } else {
                    isKilometersSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteCounterTextView.setText(String.valueOf(300 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // called when user adding car in skip
    @Override
    public void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void isSuspend(DefaultUserModel defaultUserModel) {
        if (defaultUserModel.getActivation().contentEquals(UserManager.PENDING)) {
            sessionManager.sessionManager().logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void stopReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(this).get())
                .removeLocationUpdates(locationCallback);
    }

    @Override
    public void setCurrentExaminationPosition(int currentExaminationPosition) {
        this.currentExaminationPostion = currentExaminationPosition;
    }

    @OnClick({R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5})
    void onRadioClicked(View v) {
        switch (v.getId()) {
            case R.id.radio1:
                undeterminedRadioButton.setChecked(false);
                installmentRadioButton.setChecked(false);
                fixedPriceRadioButton.setChecked(true);
                exchangeRadioButton.setChecked(false);
                discountRadioButton.setChecked(false);
                installmentFromEditText.setText("");
                discountBeforeEditText.setText("");
                discountAfterEditText.setText("");
                installmentToEditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.VISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 1;
                exchange = 0;
                break;
            case R.id.radio2:
                installmentRadioButton.setChecked(false);
                undeterminedRadioButton.setChecked(true);
                fixedPriceRadioButton.setChecked(false);
                exchangeRadioButton.setChecked(false);
                discountRadioButton.setChecked(false);
                installmentFromEditText.setText("");
                discountBeforeEditText.setText("");
                installmentToEditText.setText("");
                discountAfterEditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 2;
                exchange = 0;
                price = "0";
                break;
            case R.id.radio3:
                undeterminedRadioButton.setChecked(false);
                installmentRadioButton.setChecked(false);
                fixedPriceRadioButton.setChecked(false);
                discountRadioButton.setChecked(false);
                exchangeRadioButton.setChecked(true);
                installmentFromEditText.setText("");
                discountBeforeEditText.setText("");
                installmentToEditText.setText("");
                discountAfterEditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 3;
                exchange = 1;
                break;
            case R.id.radio4:
                undeterminedRadioButton.setChecked(false);
                installmentRadioButton.setChecked(false);
                fixedPriceRadioButton.setChecked(false);
                exchangeRadioButton.setChecked(false);
                discountRadioButton.setChecked(true);
                installmentFromEditText.setText("");
                discountBeforeEditText.setText("");
                installmentToEditText.setText("");
                discountAfterEditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.VISIBLE);
                radioButtonId = 4;
                exchange = 0;
                break;
            case R.id.radio5:
                undeterminedRadioButton.setChecked(false);
                installmentRadioButton.setChecked(true);
                fixedPriceRadioButton.setChecked(false);
                exchangeRadioButton.setChecked(false);
                discountRadioButton.setChecked(false);
                installmentFromEditText.setText("");
                installmentToEditText.setText("");
                priceTypingLayout.setText("");
                discountAfterEditText.setText("");
                discountBeforeEditText.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.VISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 5;
                exchange = 0;
                break;
        }
    }

    @OnCheckedChanged({R.id.sw2,R.id.sw3, R.id.sw7})
    void onSwitchChanged(CompoundButton v, boolean isChecked){
        switch (v.getId()) {
            case R.id.sw2:
                if (isChecked){
                    isTracked = 1;
                    presenter.startReceivingLocationUpdates();
                    pickLocationOnMapLayout.setVisibility(View.GONE);
                    pickFixedLocationLayout.setVisibility(View.GONE);
                    fixedLocationSwitchCompact.setChecked(false);
                    picFixedLocationOnMapSwitch.setChecked(false);
                    isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                    circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                    latFixedOnMap = "";
                    lonFixedOnmap = "";
                    latFixedLocation = "";
                    lonFixedLocation = "";
                }
                break;
            case R.id.sw3:
                if (isChecked){
                    isTracked = 0;
                    pickLocationOnMapLayout.setVisibility(View.GONE);
                    pickFixedLocationLayout.setVisibility(View.VISIBLE);
                    trackedCarSwitchCompact.setChecked(false);
                    picFixedLocationOnMapSwitch.setChecked(false);
                    isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                    circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                    latTracked = "";
                    lonTracked = "";
                    latFixedOnMap = "";
                    lonFixedOnmap = "";
                }
                break;
            case R.id.sw7:
                if (isChecked){
                    isTracked = 2;
                    pickLocationOnMapLayout.setVisibility(View.VISIBLE);
                    pickFixedLocationLayout.setVisibility(View.GONE);
                    trackedCarSwitchCompact.setChecked(false);
                    fixedLocationSwitchCompact.setChecked(false);
                    isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                    circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                    latTracked = "";
                    lonTracked = "";
                    latFixedLocation = "";
                    lonFixedLocation = "";
                }
                break;
        }

        if (!trackedCarSwitchCompact.isChecked() && !fixedLocationSwitchCompact.isChecked() && !picFixedLocationOnMapSwitch.isChecked()){
            isTracked = 3;
            pickLocationOnMapLayout.setVisibility(View.GONE);
            pickFixedLocationLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.sw1, R.id.sw4, R.id.sw5, R.id.sw6})
    void onSwitchClicked(View v) {
        switch (v.getId()) {
            case R.id.sw1:
                isMVPI = (isMVPI == 1 ? 0 : 1);
                break;
            case R.id.sw4:
                contactCall = (contactCall == 1 ? 0 : 1);
                break;
            case R.id.sw5:
                contactChat = (contactChat == 1 ? 0 : 1);
                break;
            case R.id.sw6:
                vehicleRegistration = (vehicleRegistration == 1 ? 0 : 1);
                break;
        }
    }

    @OnClick(R.id.importer_layout)
    void onImporterClicked() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new importersAdapter(this, id -> {
                            updateImporterFromDialog(id);
                            materialDialog.dismiss();
                        }, presenter.getImporter(), importerId),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateImporterFromDialog(ImporterViewModel id) {
        this.isImporterSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.importerId = id.getId();
        if (isEnglishLang()) {
            this.ivSelectedImporter.setText(id.getName());
        } else {
            this.ivSelectedImporter.setText(id.getName());
        }
    }

    @OnClick(R.id.manu_Layout)
    public void onYearClicked() {
        if (mnufacturingYear == 0) {
            MonthPickerDialog.Builder builder =
                    new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                            mnufacturingYear = selectedYear,
                            limitCalender.get(Calendar.YEAR),
                            limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(limitCalender.get(Calendar.YEAR) + 1)
                    .setMaxYear(limitCalender.get(Calendar.YEAR) + 2)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        mnufacturingYear = year;
                        ivSelectedYear.setText(String.valueOf(mnufacturingYear));
                        isYearSelectedCircleImageView.setImageResource(R.drawable.check_circle);
                    })
                    .build()
                    .show();
        } else {
            MonthPickerDialog.Builder builder =
                    new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                            mnufacturingYear = selectedYear,
                            limitCalender.get(Calendar.YEAR),
                            limitCalender.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1950)
                    .setActivatedYear(mnufacturingYear)
                    .setMaxYear(limitCalender.get(Calendar.YEAR) + 2)
                    .showYearOnly()
                    .setOnYearChangedListener(year -> {
                        mnufacturingYear = year;
                        ivSelectedYear.setText(String.valueOf(mnufacturingYear));
                        isYearSelectedCircleImageView.setImageResource(R.drawable.check_circle);
                    })
                    .build()
                    .show();
        }
    }

    @OnClick(R.id.conditions_layout)
    void onConditionClicked() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, year -> {
                            updateConditionFromDialog(year);
                            materialDialog.dismiss();
                        }, presenter.getConditions(), condition),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateConditionFromDialog(String condition) {
        this.isConditionSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        ivSelectedCondition.setText(condition);
        this.condition = condition;
        if (condition.equals(getString(R.string.newc))) {
            this.conditionId = 1;
            trackedCarLayout.setVisibility(View.VISIBLE);
            fixedLocationSwitchCompact.setChecked(false);
            pickFixedLocationLayout.setVisibility(View.GONE);
            isTracked = 3;
            if (sessionManager.sessionManager().isSessionActive()) {
                if (sessionManager.getCurrentUser().getType() == 2) {
                    isTracked = 0;
                }
            }
        } else if (condition.equals(getString(R.string.old))) {
            this.conditionId = 2;
            trackedCarLayout.setVisibility(View.VISIBLE);
            fixedLocationSwitchCompact.setChecked(false);
            pickFixedLocationLayout.setVisibility(View.GONE);
            isTracked = 3;
            if (sessionManager.sessionManager().isSessionActive()) {
                if (sessionManager.getCurrentUser().getType() == 2) {
                    isTracked = 0;
                }
            }
        } else if (condition.equals(getString(R.string.damaged))) {
            this.conditionId = 3;
            trackedCarLayout.setVisibility(View.GONE);
            trackedCarSwitchCompact.setChecked(false);
            fixedLocationSwitchCompact.setChecked(true);
            pickFixedLocationLayout.setVisibility(View.VISIBLE);
            isTracked = 0;
            latTracked = "";
            lonTracked = "";
        }
    }

    @OnClick(R.id.categ_layout)
    void onCategoryClicked() {
        if (modelId != 0) {
            if (presenter.getCategory(brandId, modelId) == null || presenter.getCategory(brandId, modelId).size() == 0) {
                showWarningMessage(getString(R.string.sorry_no_available_data));
            } else {
                if (materialDialog != null)
                    materialDialog.dismiss();
                materialDialog = new MaterialDialog.Builder(this)
                        .adapter(new CategoryAdapter(this, year -> {
                                    updateCategoryFromDialog(year);
                                    materialDialog.dismiss();
                                }, presenter.getCategory(brandId, modelId), categoryId),
                                new LinearLayoutManager(this))
                        .backgroundColor(getResources().getColor(R.color.transparent))
                        .build();
                materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
                materialDialog.show();
            }
        } else {
            showWarningMessage(getString(R.string.please_select_car_model));
        }
    }

    private void updateCategoryFromDialog(CategoryViewModel category) {
        this.isCategorySelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.categoryId = category.getId();
        if (isEnglishLang())
            ivSelectedCategory.setText(category.getName());
        else
            ivSelectedCategory.setText(category.getName());
    }

    @OnClick(R.id.transmission_layout)
    void onTransmissionClicked() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, type -> {
                            updateTransmissionFromDialog(type);
                            materialDialog.dismiss();
                        }, presenter.getTransmissionTypes(), transmission),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateTransmissionFromDialog(String type) {
        this.transmission = type;
        if (type.equals(getString(R.string.auto))) {
            this.transmissionId = 1;
            ivSelectedTransmission.setText(type);
            this.isTransmissionSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        } else if (type.equals(getString(R.string.normal))) {
            this.transmissionId = 2;
            ivSelectedTransmission.setText(type);
            this.isTransmissionSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        }
    }

    @OnClick(R.id.brand_layout)
    void onBrandSelected() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new BrandsAdapter(this, brand -> {
                            updateBrandFromDialog(brand);
                            materialDialog.dismiss();
                        }, presenter.getBrands(), brandId),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        materialDialog.show();
    }

    private void updateBrandFromDialog(BrandsViewModel brand) {
        this.isCategorySelectedCircleImageView.setImageResource(R.color.colorAddCarCircles);
        this.isModelSelectedCircleImageView.setImageResource(R.color.colorAddCarCircles);
        this.isBrandSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        ivSelectedModel.setText("");
        ivSelectedCategory.setText("");
        this.brandId = brand.getId();
        this.categoryId = 0;
        this.modelId = 0;
        if (isEnglishLang())
            ivSelectedBrand.setText(brand.getName());
        else
            ivSelectedBrand.setText(brand.getName());
    }

    @OnClick(R.id.color_layout)
    void onColorSelected() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new ColorAdapter(this, brand -> {
                            updateColorFromDialog(brand);
                            materialDialog.dismiss();
                        }, presenter.getColors(), colorId),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        materialDialog.show();
    }

    private void updateColorFromDialog(CarColorViewModel color) {
        this.isColorSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.colorId = color.getId();
        if (isEnglishLang())
            ivSelectedColor.setText(color.getName());
        else
            ivSelectedColor.setText(color.getName());
    }

    @OnClick(R.id.models_layout)
    void onModelsSelected() {
        if (brandId != 0) {
            if (presenter.getModels(brandId) == null || presenter.getModels(brandId).size() == 0) {
                showWarningMessage(getString(R.string.sorry_no_available_data));
            } else {
                if (materialDialog != null)
                    materialDialog.dismiss();
                materialDialog = new MaterialDialog.Builder(this)
                        .adapter(new ModelsAdapter(this, brand -> {
                                    updateModelsFromDialog(brand);
                                    materialDialog.dismiss();
                                }, presenter.getModels(brandId), modelId),
                                new LinearLayoutManager(this))
                        .backgroundColor(getResources().getColor(R.color.transparent))
                        .build();
                materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
                if ("ar".equals(LocaleUtil.getLanguage(this))) {
                    materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
                materialDialog.show();
            }
        } else {
            showWarningMessage(getString(R.string.select_brand_first));
        }
    }

    private void updateModelsFromDialog(ModelViewModel brand) {
        this.isCategorySelectedCircleImageView.setImageResource(R.color.colorAddCarCircles);
        this.isModelSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.modelId = brand.getId();
        this.categoryId = 0;
        if (isEnglishLang())
            ivSelectedModel.setText(brand.getName());
        else
            ivSelectedModel.setText(brand.getName());
    }

    @OnClick(R.id.warranty_layout)
    void onWarrantySelected() {
        agentNameEditText.setText("");
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, method -> {
                            updateWarrantyFromDialog(method);
                            materialDialog.dismiss();
                        }, presenter.getWarrantyOptions(), warranty),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        materialDialog.show();
    }

    private void updateWarrantyFromDialog(String method) {
        this.isWarrantySelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.warranty = method;
        if (method.equals(getString(R.string.yes))) {
            this.agentNameLayout.setVisibility(View.VISIBLE);
            this.warrantyId = 1;
        } else {
            this.agentNameLayout.setVisibility(View.GONE);
            this.warrantyId = 0;
        }
    }

    @OnClick(R.id.post_layout)
    public void onPostClicked() {
        if (materialDialog != null)
            materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, year -> {
                            updatePostFromDialog(year);
                            materialDialog.dismiss();
                        }, presenter.getPostTypes(), categoryy),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        materialDialog.show();
    }

    private void updatePostFromDialog(String category) {
        this.isPostSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        this.categoryy = category;
        if (categoryy.equals(getString(R.string.free))) {
            posttype = 1;
        } else if (categoryy.equals(getString(R.string.special))) {
            posttype = 2;
        }
        ivSelectedPostType.setText(category);
    }

    @OnClick(R.id.add_photes_layout)
    void onAddPhotosClicked() {
        Intent i = new Intent(this, AddCarPhotosActivity.class);
        i.putExtra("json", images);
        startActivityForResult(i, 1);
    }

    @OnClick({R.id.pickLocationOnMapLayout,R.id.pickLocationLayout})
    void onPicLocationOnMapClicked(View v) {
//        showWarningMessage(getString(R.string.recommended));
        startActivityForResult(SnapCarApplication.getPickerIntent(this),PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                this.images = data.getStringExtra("result");
                this.isImageSelectedCircleImageView.setImageResource(R.drawable.check_circle);
            }
            if (resultCode == Activity.RESULT_CANCELED && images.isEmpty()) {
                this.isImageSelectedCircleImageView.setImageResource(R.drawable.cancel_material);

            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (data != null) {
                VanillaAddress place = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
                if (isTracked == 0) {
                    this.latFixedLocation = String.valueOf(place.getLatitude());
                    this.lonFixedLocation = String.valueOf(place.getLongitude());
                    this.isLocationSelectedCircleImageView.setImageResource(R.drawable.check_circle);
                } else if (isTracked == 2) {
                    this.latFixedOnMap = String.valueOf(place.getLatitude());
                    this.lonFixedOnmap = String.valueOf(place.getLongitude());
                    this.circleLocationPickedOnMap.setImageResource(R.drawable.check_circle);
                }
            }
        }
    }

    @OnClick(R.id.btn_publish_car)
    public void onPublishClicked(View v) {
        if (images.isEmpty()) {
            showWarningMessage(getString(R.string.please_add_car_images));
            this.isImageSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (brandId == 0) {
            showWarningMessage(getString(R.string.please_selected_your_brand));
            this.isBrandSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (modelId == 0) {
            showWarningMessage(getString(R.string.please_select_car_model));
            this.isModelSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (mnufacturingYear == 0) {
            showWarningMessage(getString(R.string.please_select_manufacturing_year));
            this.isYearSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (colorId == 0) {
            showWarningMessage(getString(R.string.please_select_car_color));
            this.isColorSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (transmissionId == 0) {
            showWarningMessage(getString(R.string.please_select_transmission));
            this.isTransmissionSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (importerId == 0) {
            showWarningMessage(getString(R.string.please_select_imporeter));
            this.isImporterSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (KmEditText.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_select_car_km));
            this.isKilometersSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (conditionId == 0) {
            showWarningMessage(getString(R.string.please_select_condition));
            this.isConditionSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (warrantyId == 1 && agentNameEditText.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_type_warranty_period));
            this.isWarrantySelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (radioButtonId == 1 && priceTypingLayout.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_type_price));
            return;
        }
        if (isTracked == 0 && latFixedLocation.isEmpty()) {
            if (sessionManager.sessionManager().isSessionActive()) {
                if (sessionManager.getCurrentUser().getType() == 2)
                    if (sessionManager.getCurrentUser().getShowRoomInfoModel().getLatitude().isEmpty()) {
                        new Handler().postDelayed(() -> startActivity(new Intent(this, EditShowRoomLocationActivity.class)), 700);
                        showWarningMessage(getString(R.string.you_must_set_location_first));
                        return;
                    }
            }
            showWarningMessage(getString(R.string.please_pic_location_));
            this.isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (isTracked == 2 && latFixedOnMap.isEmpty()) {
            showWarningMessage(getString(R.string.please_pic_location_));
            this.circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (radioButtonId == 1) {
            if (priceTypingLayout.getText().toString().isEmpty()) {
                showWarningMessage(getString(R.string.please_type_price_value));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                price = replaceArabicNumbers(priceTypingLayout.getText().toString());
                isPriceSelectedImageView.setImageResource(R.drawable.monetization_on_material);
            }
        }
        if (radioButtonId == 5) {
            if (replaceArabicNumbers(installmentFromEditText.getText().toString()).equals("0") ||
                    replaceArabicNumbers(installmentToEditText.getText().toString()).equals("0")) {
                showWarningMessage(getString(R.string.check_installment_data));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            }
            if ((replaceArabicNumbers(installmentFromEditText.getText().toString()).isEmpty() ||
                    replaceArabicNumbers(installmentToEditText.getText().toString()).isEmpty())) {
                showWarningMessage(getString(R.string.please_type_inst_data));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else if (Integer.parseInt(replaceArabicNumbers(installmentFromEditText.getText().toString())) <
                    Integer.parseInt(replaceArabicNumbers(installmentToEditText.getText().toString()))) {
                showWarningMessage(getString(R.string.pease_check_instament));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                isPriceSelectedImageView.setImageResource(R.drawable.check_circle);
            }
        }
        if (radioButtonId == 4) {
            if (presenter.isShowRoom() &&
                    (replaceArabicNumbers(discountBeforeEditText.getText().toString()).isEmpty() ||
                            replaceArabicNumbers(discountAfterEditText.getText().toString()).isEmpty())) {
                showWarningMessage(getString(R.string.please_check_discount));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else if (replaceArabicNumbers(discountBeforeEditText.getText().toString()).equals("0") ||
                    replaceArabicNumbers(discountAfterEditText.getText().toString()).equals("0")) {
                showWarningMessage(getString(R.string.please_check_discount_data));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else if (presenter.isShowRoom() && (Integer.parseInt(replaceArabicNumbers(discountBeforeEditText.getText().toString()))
                    < Integer.parseInt(replaceArabicNumbers(discountAfterEditText.getText().toString())))) {
                showWarningMessage(getString(R.string.please_check_discount_data));
                isPriceSelectedImageView.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                isPriceSelectedImageView.setImageResource(R.drawable.check_circle);
            }
        }
        if (installmentToEditText.getText().toString().isEmpty()) {
            instFrom = "";
            instTo = "";
        } else {
            instTo = replaceArabicNumbers(installmentToEditText.getText().toString());
            instFrom = replaceArabicNumbers(installmentFromEditText.getText().toString());
        }
        if (discountBeforeEditText.getText().toString().isEmpty()) {
            priceFrom = "";
            priceto = "";
        } else {
            priceto = replaceArabicNumbers(discountBeforeEditText.getText().toString());
            priceFrom = replaceArabicNumbers(discountAfterEditText.getText().toString());
        }

        if (posttype == 0) {
            showWarningMessage(getString(R.string.please_select_post_type));
            this.isPostSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (presenter.isHasTrackedCar() && isTracked == 1) {
            showWarningMessage(getString(R.string.tracked_more_than_one_car));
            return;
        } else if (isTracked == 1) {
            if (!getPermissionUtil().hasLocationPermission()) {
                showWarningMessage(getString(R.string.please_grand_location_permission));
                trackedCarSwitchCompact.setChecked(false);
                isTracked = 0;
                return;
            }
        }
        if (isTracked == 1) {
            if (lonTracked == null) {
                showWarningMessage(getString(R.string.you_must_set_location_first));
                trackedCarSwitchCompact.setChecked(false);
                return;
            }
        }

        if (isTracked == 3) {
            showWarningMessage(getString(R.string.you_must_track_or_fixed));
            return;
        }

        if (contactChat == 1 && contactCall == 1) {
            contactOption = 3;
        } else if (contactChat == 1 && contactCall == 0) {
            contactOption = 1;
        } else if (contactChat == 0 && contactCall == 1) {
            contactOption = 2;
        } else if (contactChat == 0 && contactCall == 0) {
            contactOption = 0;
        }
        if (contactOption == 0) {
            showWarningMessage(getString(R.string.select_at_least_contact_option));
            return;
        }

        if (sessionManager.sessionManager().isSessionActive()) {
            if (!presenter.isHasAvailable()) {
                if (presenter.isShowRoom()) {
                    showRequestVIPDialog();
                    return;
                } else {
                    showWarningMessage(getString(R.string.you_have_consumed_available_cars));
                    return;
                }
            }
        }

        if (posttype == 1) {
            getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
                    .text(R.id.tv_sewar, settingsApiResponse!=null? settingsApiResponse.getSwearText():" ")
                    .clickListener(R.id.btn_reject, (dialog, view) -> dialog.dismiss())
                    .clickListener(R.id.btn_accept, (dialog, view) -> {
                        addCar();
                        dialog.dismiss();
                    })
                    .background(R.color.colorwhete)
                    .build()
                    .show();
        } else {
            getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
                    .text(R.id.tv_sewar, settingsApiResponse!=null? settingsApiResponse.getSwearText():" ")
                    .clickListener(R.id.btn_reject, (dialog, view) -> dialog.dismiss())
                    .clickListener(R.id.btn_accept, (dialog, view) -> {
                        addCar();
                        dialog.dismiss();
                    })
                    .background(R.color.colorwhete)
                    .build()
                    .show();
        }
    }

    private void showRequestVIPDialog() {
        getUiUtil().getDialogBuilder(this, R.layout.dialog_request_vip)
                .clickListener(R.id.btn_reject, (dialog, view) -> {
                    dialog.dismiss();
                })
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    presenter.requestVIP();
                    dialog.dismiss();
                })
                .background(R.color.colorwhete)
                .build()
                .show();
    }

    public void addCar() {
        if (isTracked == 0) {
            presenter.addCar(contactOption,
                    posttype,
                    isTracked,
                    conditionId,
                    mnufacturingYear,
                    importerId,
                    transmissionId,
                    colorId,
                    replaceArabicNumbers(KmEditText.getText().toString()),
                    warrantyId,
                    agentNameEditText.getText().toString(),
                    isMVPI,
                    notesEditText.getText().toString(),
                    modelId,
                    priceto,
                    priceFrom,
                    engineCcEditText.getText().toString(),
                    images,
                    instFrom,
                    instTo,
                    exchange,
                    lonFixedLocation,
                    latFixedLocation,
                    getExaminationImages(),
                    categoryId,
                    brandId, price, vehicleRegistration, radioButtonId);
        } else if (isTracked == 1) {
            presenter.addCar(
                    contactOption,
                    posttype,
                    isTracked,
                    conditionId,
                    mnufacturingYear,
                    importerId,
                    transmissionId,
                    colorId,
                    replaceArabicNumbers(KmEditText.getText().toString()),
                    warrantyId,
                    agentNameEditText.getText().toString(),
                    isMVPI,
                    notesEditText.getText().toString(),
                    modelId,
                    priceto,
                    priceFrom,
                    engineCcEditText.getText().toString(),
                    images,
                    instFrom,
                    instTo,
                    exchange,
                    lonTracked,
                    latTracked,
                    getExaminationImages(),
                    categoryId,
                    brandId, price, vehicleRegistration, radioButtonId);
        } else if (isTracked == 2) {
            presenter.addCar(
                    contactOption,
                    posttype,
                    isTracked,
                    conditionId,
                    mnufacturingYear,
                    importerId,
                    transmissionId,
                    colorId,
                    replaceArabicNumbers(KmEditText.getText().toString()),
                    warrantyId,
                    agentNameEditText.getText().toString(),
                    isMVPI,
                    notesEditText.getText().toString(),
                    modelId,
                    priceto,
                    priceFrom,
                    engineCcEditText.getText().toString(),
                    images,
                    instFrom,
                    instTo,
                    exchange,
                    lonFixedOnmap,
                    latFixedOnMap,
                    getExaminationImages(),
                    categoryId,
                    brandId, price, vehicleRegistration, radioButtonId);
        }
    }

    private String getExaminationImages() {
        String[] s = new String[]{examinationImageOne, examinationImageTwo, examinationImageThree};
        String newString = "";
        for (int x = 0; x < s.length; x++) {
            if (s[x] != null)
                if (x != s.length - 1 && !s[x].isEmpty())
                    newString += s[x] + ",";
                else newString += s[x];
        }
        return newString;
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
    public void updateLatLong(LatLng latLng) {
        this.latTracked = String.valueOf(latLng.latitude);
        this.lonTracked = String.valueOf(latLng.longitude);
    }

    @Override
    public void carAdded(CarDTO carResourcesApiResponse) {
        if (isTracked == 1) {
            presenter.stopService();
            presenter.openService();
//            if (sessionManager.sessionManager().isSessionActive()) {
//                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//                final boolean[] isExsit = {false};
//                db.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
//                            FireBaseModel fireBaseModel = snapShot.getValue(FireBaseModel.class);
//                            if (fireBaseModel.getCarId() == carResourcesApiResponse.getInsertedCar().getId()) {
//                                db.child(snapShot.getKey()).setValue(new FireBaseModel(carResourcesApiResponse.getInsertedCar().getId(),
//                                        (int) sessionManager.getCurrentUser().getId(),
//                                        carResourcesApiResponse.getInsertedCar().getLongitude(),
//                                        carResourcesApiResponse.getInsertedCar().getLatitude(),
//                                        carResourcesApiResponse.getInsertedCar().getLatitude(),
//                                        carResourcesApiResponse.getInsertedCar().getLongitude()));
//                                isExsit[0] = true;
//                                break;
//                            } else {
//                                isExsit[0] = false;
//                            }
//                        }
//                        if (!isExsit[0]) {
//                            String trackingId = db.push().getKey();
//                            db.child(trackingId)
//                                    .setValue(new FireBaseModel(carResourcesApiResponse.getInsertedCar().getId(),
//                                            (int) sessionManager.getCurrentUser().getId(),
//                                            carResourcesApiResponse.getInsertedCar().getLongitude(),
//                                            carResourcesApiResponse.getInsertedCar().getLatitude(),
//                                            carResourcesApiResponse.getInsertedCar().getLatitude(),
//                                            carResourcesApiResponse.getInsertedCar().getLongitude()));
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            }
        }

        if (posttype == 2) {

            Intent i = new Intent(this, PaymentActivityForAddcar.class);
            i.putExtra("commission", settingsApiResponse.getCommissionPercentage());
            i.putExtra("carId", carResourcesApiResponse.getInsertedCar().getId());
            startActivityForResult(i, 100);
            finish();
//            getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
//                    .text(R.id.tv_sewar, settingsApiResponse.getSwearText())
//                    .clickListener(R.id.btn_reject, (dialog, view) -> {
//                        dialog.dismiss();
//                    })
//                    .clickListener(R.id.btn_accept, (dialog, view) -> {
//
//                    }).background(R.color.colorwhete)
//                    .build()
//                    .show();
        } else {
            showSuccessMessage(getString(R.string.car_added_successfully));
            new Handler().postDelayed(this::finish, 1500);
        }
    }

    @Override
    public void setNewImagePath(String s) {
        isExaminationSelectedCircleImageView.setImageResource(R.drawable.check_circle);
        switch (currentExaminationPostion) {
            case 1:
                examinationImageOne = s;
                imExaminationOne.setVisibility(View.VISIBLE);
                break;
            case 2:
                examinationImageTwo = s;
                imExaminationTwo.setVisibility(View.VISIBLE);
                break;
            case 3:
                examinationImageThree = s;
                imExaminationThree.setVisibility(View.VISIBLE);
                addExaminationImage.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void setSwearData(SettingsModel settingsApiResponse) {
        premiumPriceTextView.setText(getString(R.string.hint_premium_price).concat(" ").concat(String.valueOf(settingsApiResponse.getPremiumPrice())).concat(" ").concat(
                getString(R.string.hint_premium_price_two)
        ));
        this.settingsApiResponse = settingsApiResponse;
    }

    @Override
    public void requestVipSent(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.request_vip_sent));
        new Handler().postDelayed(this::finish, 1500);
    }

    @Override
    public void finishActivity() {
        new Handler().postDelayed(this::finish, 1500);
    }

    @Override
    public void updatedUser(DefaultUserModel defaultUserModel) {
        if (isTracked == 1) {
            presenter.openService();
        }
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.examinationUploaded_one, R.id.examinationUploaded_two, R.id.examinationUploaded_three})
    public void onExaminationCanceled(View v) {
        switch (v.getId()) {
            case R.id.examinationUploaded_one:
                imExaminationOne.setVisibility(View.INVISIBLE);
                examinationImageOne = "";
                addExaminationImage.setVisibility(View.VISIBLE);
                break;
            case R.id.examinationUploaded_two:
                imExaminationTwo.setVisibility(View.GONE);
                examinationImageTwo = "";
                addExaminationImage.setVisibility(View.VISIBLE);
                break;
            case R.id.examinationUploaded_three:
                imExaminationThree.setVisibility(View.GONE);
                examinationImageThree = "";
                addExaminationCount = 0;
                addExaminationImage.setVisibility(View.VISIBLE);
                isExaminationSelectedCircleImageView.setImageResource(R.color.colorAddCarCircles);
                break;
        }
    }

    @OnClick({R.id.iv_back})
    public void onBackClicked(View v) {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                .text(R.id.tv_message, getString(R.string.are_sure_want_exit_car))
                .clickListener(R.id.no, (dialog, view) -> {
                    dialog.dismiss();
                })
                .clickListener(R.id.yes, (dialog, view) -> {
                    finish();
                    dialog.dismiss();
                })
                .background(R.color.transparent)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }

    @OnClick(R.id.add_examination_image)
    void onAddExaminationClicked() {
        addExaminationCount++;
        switch (addExaminationCount) {
            case 1:
                presenter.openGallery(RxPaparazzo.single(this), 1);
                break;
            case 2:
                presenter.openGallery(RxPaparazzo.single(this), 2);
                break;
            case 3:
                presenter.openGallery(RxPaparazzo.single(this), 3);
                break;
        }
    }

    public String replaceArabicNumbers(String original) {
        return original.
                replaceAll("٠", "1")
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9");
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    @Override
    public void onBackPressed() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                .text(R.id.tv_message, getString(R.string.are_sure_want_exit_car))
                .clickListener(R.id.no, (dialog, view) -> {
                    dialog.dismiss();
                })
                .clickListener(R.id.yes, (dialog, view) -> {
                    super.onBackPressed();
                    dialog.dismiss();
                })
                .background(R.color.transparent)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }
}