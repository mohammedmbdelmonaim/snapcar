package com.intcore.snapcar.ui.editcar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Looper;
import android.provider.Settings;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.GsonBuilder;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.ui.addcar.BrandsAdapter;
import com.intcore.snapcar.ui.addcar.CategoryAdapter;
import com.intcore.snapcar.ui.addcar.ColorAdapter;
import com.intcore.snapcar.ui.addcar.ModelsAdapter;
import com.intcore.snapcar.ui.addcar.YearsAdapter;
import com.intcore.snapcar.ui.addcarphotes.AddCarPhotosActivity;
import com.intcore.snapcar.ui.editshowroomlocation.EditShowRoomLocationActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivityForAddcar;
import com.intcore.snapcar.util.UserManager;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class EditCarActivity extends BaseActivity implements EditCarScreen {

    private final static int PLACE_PICKER_REQUEST = 999;
    @BindView(R.id.circle_brand_selected)
    CircleImageView brandIsSelected;
    @BindView(R.id.circle_image_selected)
    CircleImageView isImageSelectedCircleImageView;
    @BindView(R.id.sw3)
    SwitchCompat fixedLocationSwitchCompact;
    @BindView(R.id.pickLocationLayout)
    RelativeLayout pickFixedLocationLayout;
    @BindView(R.id.circle_categ_selected)
    CircleImageView categoryIsSelected;
    @BindView(R.id.circle_man_year_selected)
    CircleImageView manufacturingIsSelected;
    @BindView(R.id.circle_transmission_selected)
    CircleImageView transmissionIsSelected;
    @BindView(R.id.circle_importer_selected)
    CircleImageView importerIsSelected;
    @BindView(R.id.circle_examination_selected)
    CircleImageView isExaminationSelectedCircleImageView;
    @BindView(R.id.circle_kilometers_selected)
    CircleImageView kilometersIsSelected;
    @BindView(R.id.circle_engine_selected)
    CircleImageView engineIsSelected;
    @BindView(R.id.priceIsSelected)
    CircleImageView priceIsSelected;
    @BindView(R.id.circle_color_year_selected)
    CircleImageView colorIsSelected;
    @BindView(R.id.circle_condtion_selected)
    CircleImageView conditionIsSelected;
    @BindView(R.id.circle_warranty_selected)
    CircleImageView warrantyIsSelected;
    @BindView(R.id.circle_model_selected)
    CircleImageView modelIsSelected;
    @BindView(R.id.circle_post_selected)
    CircleImageView postIsSelected;
    @BindView(R.id.priceTypingLayout)
    EditText priceTypingLayout;
    @BindView(R.id.sw1)
    SwitchCompat switchCompat1;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.sw6)
    SwitchCompat switchCompat6;
    @BindView(R.id.sw4)
    SwitchCompat switchCompat4;
    @BindView(R.id.sw5)
    SwitchCompat switchCompat5;
    @BindView(R.id.ed_km)
    EditText KmEditText;
    @BindView(R.id.agent_name_editText)
    EditText agentNameEditText;
    @BindView(R.id.notesEditText)
    EditText notesEditText;
    @BindView(R.id.agent_name_layout)
    LinearLayout agentNameLayout;
    @BindView(R.id.installmentLayout)
    ConstraintLayout installmentLayout;
    @BindView(R.id.discountLayout)
    ConstraintLayout discountLayout;
    @BindView(R.id.et_dis_from)
    EditText disc_bef_EditText;
    @BindView(R.id.et_dis_to)
    EditText disc_after_EditText;
    @BindView(R.id.et_install_from)
    EditText install_bef_EditText;
    @BindView(R.id.et_install_to)
    EditText install_to_EditText;
    @BindView(R.id.radio4)
    RadioButton radio4;
    @BindView(R.id.radio5)
    RadioButton radio5;
    @BindView(R.id.radio3)
    RadioButton radio3;
    @BindView(R.id.radio1)
    RadioButton radio1;
    @BindView(R.id.radio2)
    RadioButton radio2;
    @BindView(R.id.imageCancel_one)
    ImageView examinationCancelOneImageView;
    @BindView(R.id.imageCancel_two)
    ImageView examinationCancelTwoImageView;
    @BindView(R.id.imageCancel_three)
    ImageView examinationCancelThreeImageView;
    @BindView(R.id.examinationUploaded_one)
    ImageView examinationOneImageView;
    @BindView(R.id.examinationUploaded_two)
    ImageView examinationTwoImageView;
    @BindView(R.id.examinationUploaded_three)
    ImageView examinationThreeImageView;
    @BindView(R.id.post_layout)
    RelativeLayout postTypeLayout;
    @BindView(R.id.ed_cc)
    EditText enginCCEditText;
    @BindView(R.id.iv_back)
    ImageView ivBack;
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
    @BindView(R.id.sw2)
    SwitchCompat trackedCarSwitchCompact;
    @BindView(R.id.add_examination_image)
    ImageView addExaminationImage;
    @Inject
    JopDispatcher jopDispatcher;
    @Inject
    EditCarPresenter presenter;
    Calendar limitCalender = Calendar.getInstance();
    @Inject
    UserManager userManager;
    @BindView(R.id.add_image_cell)
    ImageView addImageCell;
    @BindView(R.id.location_cell)
    ImageView locationCell;
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
    @BindView(R.id.circle_location_picked)
    CircleImageView isLocationSelectedCircleImageView;
    @BindView(R.id.im_examination_one)
    RelativeLayout imExaminationOne;
    @BindView(R.id.im_examination_two)
    RelativeLayout imExaminationTwo;
    @BindView(R.id.im_examination_three)
    RelativeLayout imExaminationThree;
    private String images = "";
    private int brandId;
    private MaterialDialog materialDialog;
    private String examinationImage = "";
    private int modelId = 0;
    private int isMVPI = 0;
    private int isTracked = 3;
    private int importerId = 0;
    private int mnufacturingYear = 0;
    private int transmissionId = 0;
    private int warrantyId = 0;
    private String price = "";
    private String agentName;
    private int colorId = 0;
    private int posttype = 0;
    private int isSpecialBefore = 0;
    private int categoryId = 0;
    private int conditionId = 0;
    private int radioButtonId = 0;
    private String latTracked = "";
    private String lonTracked = "";
    private String latFixedOnMap = "";
    private String lonFixedOnmap = "";
    private String latFixedLocation = "";
    private String lonFixedLocation = "";
    private String condition = "";
    private String transmission = "";
    private String warranty = "";
    private String categoryy = "";
    private int exchange = 0;
    private int contactChat = 0;
    private int contactCall = 0;
    private int contactOption = 1;
    private int carId;
    private int isTrackedBefore = 0;
    private SettingsModel settingsApiResponse;
    private String instTo;
    private String instFrom;
    private String priceto;
    private String priceFrom;
    private int vehicleRegistration;
    private int currentExaminationPostion;
    private String examinationImageTwo;
    private String examinationImageThree;
    private String examinationImageOne;
    private int addExaminationCount = 0;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        carId = getIntent().getIntExtra("carId", 0);
        presenter.getCarData(carId);
        limitCalender.set(Calendar.YEAR, limitCalender.get(Calendar.YEAR) - 1);
        if (presenter.isShowRoom()) {
            radio3.setVisibility(View.GONE);
            radio4.setVisibility(View.VISIBLE);
        } else if (!presenter.isShowRoom()) {
            radio3.setVisibility(View.VISIBLE);
            radio4.setVisibility(View.GONE);
        }
        initEditTexts();
        setupBack();
        if (userManager.sessionManager().isSessionActive()) {
            if (userManager.getCurrentUser().getType() == 2) {
                mapOptionsDivider.setVisibility(View.GONE);
                mapOptionsLayout.setVisibility(View.GONE);
                isTracked = 0;
                if (userManager.getCurrentUser().getShowRoomInfoModel().getLatitude().isEmpty()) {
//                    showWarningMessage(getString(R.string.you_must_set_location_first));
                } else {
                    latFixedLocation = userManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                    lonFixedLocation = userManager.getCurrentUser().getShowRoomInfoModel().getLongitude();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userManager.sessionManager().isSessionActive()) {
            if (userManager.getCurrentUser().getType() == 2) {
                latFixedLocation = userManager.getCurrentUser().getShowRoomInfoModel().getLatitude();
                lonFixedLocation = userManager.getCurrentUser().getShowRoomInfoModel().getLongitude();
                isTracked = 0;
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_car;
    }

    void initEditTexts() {
        enginCCEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!enginCCEditText.getText().toString().isEmpty()) {
                    engineIsSelected.setImageResource(R.drawable.check_circle);
                } else {
                    engineIsSelected.setImageResource(R.drawable.cancel_material);
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
                    kilometersIsSelected.setImageResource(R.drawable.check_circle);
                } else {
                    kilometersIsSelected.setImageResource(R.drawable.cancel_material);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.add_photes_layout)
    void onAddPhotosClicked() {
        Intent i = new Intent(this, AddCarPhotosActivity.class);
        i.putExtra("json", images);
        startActivityForResult(i, 1);
    }

    @Override
    public void updateUi(CarDTO carDTO) {
        CarApiResponse car = carDTO.getCar();
      Timber.tag("priceType").d(String.valueOf(car.getPriceType()));
        images = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().
                create().toJson(car.getImagesApiResponses());
        brandId = car.getBrandId();
        if (car.getBrandsApiResponse() != null) {
            if (isEnglishLang())
                ivSelectedBrand.setText(car.getBrandsApiResponse().getNameEn());
            else
                ivSelectedBrand.setText(car.getBrandsApiResponse().getNameAr());
        }
        modelId = car.getCarModelId();
        if (car.getModelApiResponse() != null) {
            if (isEnglishLang())
                ivSelectedModel.setText(car.getModelApiResponse().getNameEn());
            else
                ivSelectedModel.setText(car.getModelApiResponse().getNameAr());
        }

        if (car.getImporterApiResponse() != null) {
            importerId = car.getImporterApiResponse().getId();
            if (isEnglishLang())
                ivSelectedImporter.setText(car.getImporterApiResponse().getNameEn());
            else
                ivSelectedImporter.setText(car.getImporterApiResponse().getNameAr());
        }
        colorId = car.getCarColorId();
        if (car.getCarColorApiResponse() != null) {
            if (isEnglishLang())
                ivSelectedColor.setText(car.getCarColorApiResponse().getNameEn());
            else
                ivSelectedColor.setText(car.getCarColorApiResponse().getNameAr());
        }
        if (car.getCategoryId() != null) {
            categoryId = car.getCategoryId();
            categoryIsSelected.setImageResource(R.drawable.check_circle);
            if (isEnglishLang())
                ivSelectedCategory.setText(car.getCategoryApiResponse().getNameEn());
            else
                ivSelectedCategory.setText(car.getCategoryApiResponse().getNameAr());
        }
        mnufacturingYear = Integer.parseInt(car.getManufacturingYear());
        ivSelectedYear.setText(String.valueOf(mnufacturingYear));
        warrantyId = car.getUnderWarranty();
        posttype = car.getPostType();
        examinationImage = car.getExaminationImage();
        KmEditText.setText(car.getKilometer());
        if (car.getVehicleRegistration() == 1) {
            vehicleRegistration = car.getVehicleRegistration();
            switchCompat6.setChecked(true);
        }
        if (car.getEngineCapacityCc() != null) {
            enginCCEditText.setText(String.valueOf(car.getEngineCapacityCc()));
            engineIsSelected.setImageResource(R.drawable.check_circle);
        }
        if (car.getPrice() == null) {
            price = "";
        } else {
            price = car.getPrice();
        }
        if (car.getPriceType() == 3) {
            radioButtonId = 3;
            radio1.setChecked(false);
            radio2.setChecked(false);
            radio3.setChecked(true);
            radio4.setChecked(false);
            radio5.setChecked(false);
            disc_after_EditText.setText("");
            disc_bef_EditText.setText("");
            priceTypingLayout.setText("");
            install_bef_EditText.setText("");
            install_to_EditText.setText("");
            priceTypingLayout.setVisibility(View.INVISIBLE);
            installmentLayout.setVisibility(View.INVISIBLE);
            discountLayout.setVisibility(View.INVISIBLE);
            price = "";
            exchange = 1;
        } else if (car.getPriceType() == 2) {
                radioButtonId = 2;
                radio1.setChecked(false);
                radio2.setChecked(true);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setText("");
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                price = "0";
            } else if (car.getPriceType() == 5) {
                    radioButtonId = 5;
                    radio1.setChecked(false);
                    radio2.setChecked(false);
                    radio3.setChecked(false);
                    radio4.setChecked(false);
                    radio5.setChecked(true);
                    disc_after_EditText.setText("");
                    disc_bef_EditText.setText("");
                    priceTypingLayout.setText("");
                    install_bef_EditText.setText(car.getInstallmentPriceFrom());
                    install_to_EditText.setText(car.getInstallmentPriceTo());
                    priceTypingLayout.setVisibility(View.INVISIBLE);
                    installmentLayout.setVisibility(View.VISIBLE);
                    discountLayout.setVisibility(View.INVISIBLE);
                    price = "";
                } else if (car.getPriceType() == 4) {
                    radioButtonId = 4;
                    radio1.setChecked(false);
                    radio2.setChecked(false);
                    radio3.setChecked(false);
                    radio4.setChecked(true);
                    radio5.setChecked(false);
                    disc_after_EditText.setText(car.getPriceAfter());
                    disc_bef_EditText.setText(car.getPriceBefore());
                    priceTypingLayout.setText("");
                    install_bef_EditText.setText("");
                    install_to_EditText.setText("");
                    priceTypingLayout.setVisibility(View.INVISIBLE);
                    installmentLayout.setVisibility(View.INVISIBLE);
                    discountLayout.setVisibility(View.VISIBLE);
                    price = "";
                } else if (car.getPriceType() == 1) {
                radioButtonId = 1;
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setText(price);
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                priceTypingLayout.setVisibility(View.VISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radio1.setChecked(true);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                price = car.getPrice();
            }
//         filtering image string to urls
        if (examinationImage != null) {
            isExaminationSelectedCircleImageView.setImageResource(R.drawable.check_circle);
            String[] examination = examinationImage.split(",");
            if (examination.length == 3) {
                addExaminationImage.setVisibility(View.GONE);
            } else
                addExaminationImage.setVisibility(View.VISIBLE);
            for (int x = 0; x < examination.length; x++) {
                if (x == 0) {
                    examinationImageOne = examination[0];
                    if (!examinationImageOne.isEmpty()) {
                        imExaminationOne.setVisibility(View.VISIBLE);
                        addExaminationImage.setVisibility(View.GONE);
                    }
                }
                if (x == 1) {
                    examinationImageTwo = examination[1];
                    if (!examinationImageTwo.isEmpty()) {
                        imExaminationTwo.setVisibility(View.VISIBLE);
                        addExaminationImage.setVisibility(View.GONE);
                    }
                }
                if (x == 2) {
                    examinationImageThree = examination[2];
                    if (!examinationImageThree.isEmpty()) {
                        imExaminationThree.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        isMVPI = car.getMvpi();
        switch (isMVPI) {
            case 1:
                switchCompat1.setChecked(true);
                break;
        }
        switch (warrantyId) {
            case 0:
                this.agentNameLayout.setVisibility(View.GONE);
                break;
            case 1:
                this.agentNameLayout.setVisibility(View.VISIBLE);
                this.agentNameLayout.setVisibility(View.VISIBLE);
                agentName = car.getWarranty();
                agentNameEditText.setText(agentName);
                break;
        }
        isTracked = car.getIsTracked();
        isTrackedBefore = car.getIsTracked();
        if (isTracked == 1) {
            trackedCarSwitchCompact.setChecked(true);
            fixedLocationSwitchCompact.setChecked(false);
            picFixedLocationOnMapSwitch.setChecked(false);
            latTracked = car.getLatitude();
            lonTracked = car.getLongitude();
        } else if (isTracked == 0) {
            latFixedLocation = car.getLatitude();
            lonFixedLocation = car.getLongitude();
            trackedCarSwitchCompact.setChecked(false);
            fixedLocationSwitchCompact.setChecked(true);
            picFixedLocationOnMapSwitch.setChecked(false);
            isLocationSelectedCircleImageView.setImageResource(R.drawable.check_circle);
            pickFixedLocationLayout.setVisibility(View.VISIBLE);
        } else if (isTracked == 2) {
            latFixedOnMap = car.getLatitude();
            lonFixedOnmap = car.getLongitude();
            trackedCarSwitchCompact.setChecked(false);
            fixedLocationSwitchCompact.setChecked(false);
            picFixedLocationOnMapSwitch.setChecked(true);
            circleLocationPickedOnMap.setImageResource(R.drawable.check_circle);
            pickLocationOnMapLayout.setVisibility(View.VISIBLE);
        }

        conditionId = car.getCarStatus();
        switch (car.getCarStatus()) {
            case 1:
                condition = getString(R.string.newc);
                ivSelectedCondition.setText(condition);
                break;
            case 2:
                condition = getString(R.string.old);
                ivSelectedCondition.setText(condition);
                break;
            case 3:
                condition = getString(R.string.damaged);
                ivSelectedCondition.setText(condition);
                trackedCarLayout.setVisibility(View.GONE);
                trackedCarSwitchCompact.setChecked(false);
                latTracked = "";
                lonTracked = "";
                break;
        }
        transmissionId = car.getTransmission();
        switch (car.getTransmission()) {
            case 1:
                transmission = getString(R.string.auto);
                ivSelectedTransmission.setText(transmission);
                break;
            case 2:
                transmission = getString(R.string.normal);
                ivSelectedTransmission.setText(transmission);
                break;
        }
        switch (posttype) {
            case 1:
                categoryy = getString(R.string.free);
                ivSelectedPostType.setText(categoryy);
                break;
            case 2:
                categoryy = getString(R.string.special);
                ivSelectedPostType.setText(categoryy);
                isSpecialBefore = 1;
                break;
        }

        contactOption = car.getContactOption();
        switch (contactOption) {
            case 1:
                contactChat = 1;
                switchCompat5.setChecked(true);
                switchCompat4.setChecked(false);
                break;
            case 2:
                switchCompat5.setChecked(false);
                switchCompat4.setChecked(true);
                contactCall = 1;
                break;
            case 3:
                switchCompat5.setChecked(true);
                switchCompat4.setChecked(true);
                contactChat = 1;
                contactCall = 1;
                break;
        }
        notesEditText.setText(car.getNotes());
    }

    @OnClick({R.id.sw1, R.id.sw2, R.id.sw3, R.id.sw4, R.id.sw5, R.id.sw6, R.id.sw7})
    void onSwitchClicked(View v) {
        switch (v.getId()) {
            case R.id.sw1:
                if (isMVPI == 1) {
                    isMVPI = 0;
                } else {
                    isMVPI = 1;
                }
                break;
            case R.id.sw2:
                isTracked = 1;
                presenter.startReceivingLocationUpdates();
                trackedCarSwitchCompact.setChecked(true);
                fixedLocationSwitchCompact.setChecked(false);
                picFixedLocationOnMapSwitch.setChecked(false);
                pickLocationOnMapLayout.setVisibility(View.GONE);
                pickFixedLocationLayout.setVisibility(View.GONE);
                isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                latFixedOnMap = "";
                lonFixedOnmap = "";
                latFixedLocation = "";
                lonFixedLocation = "";
                break;
            case R.id.sw3:
                isTracked = 0;
                trackedCarSwitchCompact.setChecked(false);
                fixedLocationSwitchCompact.setChecked(true);
                picFixedLocationOnMapSwitch.setChecked(false);
                pickLocationOnMapLayout.setVisibility(View.GONE);
                pickFixedLocationLayout.setVisibility(View.VISIBLE);
                isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                latTracked = "";
                lonTracked = "";
                latFixedOnMap = "";
                lonFixedOnmap = "";
                break;
            case R.id.sw7:
                isTracked = 2;
                trackedCarSwitchCompact.setChecked(false);
                fixedLocationSwitchCompact.setChecked(false);
                picFixedLocationOnMapSwitch.setChecked(true);
                pickLocationOnMapLayout.setVisibility(View.VISIBLE);
                pickFixedLocationLayout.setVisibility(View.GONE);
                isLocationSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
                circleLocationPickedOnMap.setImageResource(R.drawable.cancel_material);
                latTracked = "";
                lonTracked = "";
                latFixedLocation = "";
                lonFixedLocation = "";
                break;
            case R.id.sw4:
                if (contactCall == 1) {
                    contactCall = 0;
                } else contactCall = 1;
                break;
            case R.id.sw5:
                if (contactChat == 1) {
                    contactChat = 0;
                } else contactChat = 1;
                break;
            case R.id.sw6:
                vehicleRegistration = (vehicleRegistration == 1 ? 0 : 1);
                break;
        }
    }

    @OnClick({R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5})
    void onRadioClicked(View v) {

        switch (v.getId()) {
            case R.id.radio1:
                radio1.setChecked(true);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio4.setChecked(false);
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setText("");
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                priceTypingLayout.setVisibility(View.VISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 1;
                exchange = 0;
                price = "";
                break;
            case R.id.radio2:
                radio1.setChecked(false);
                radio2.setChecked(true);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio4.setChecked(false);
                priceTypingLayout.setText("");
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 2;
                exchange = 0;
                price = "0";
                break;
            case R.id.radio3:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(true);
                radio4.setChecked(false);
                radio5.setChecked(false);
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 3;
                exchange = 1;
                price = "";
                break;
            case R.id.radio4:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(true);
                radio5.setChecked(false);
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                priceTypingLayout.setText("");
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.INVISIBLE);
                discountLayout.setVisibility(View.VISIBLE);
                radioButtonId = 4;
                exchange = 0;
                price = "";
                break;
            case R.id.radio5:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(true);
                install_bef_EditText.setText("");
                install_to_EditText.setText("");
                priceTypingLayout.setText("");
                disc_after_EditText.setText("");
                disc_bef_EditText.setText("");
                priceTypingLayout.setVisibility(View.INVISIBLE);
                installmentLayout.setVisibility(View.VISIBLE);
                discountLayout.setVisibility(View.INVISIBLE);
                radioButtonId = 5;
                exchange = 0;
                price = "";
                break;
        }
    }

    @OnClick({R.id.post_layout})
    void onPostClicked(View v) {
        if (isSpecialBefore == 1) {
            showWarningMessage(getString(R.string.this_post_is_special));
            return;
        }
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, year -> {
                            updatePostFromDialog(year);
                            materialDialog.dismiss();
                        }, presenter.getPostTypes(), categoryy),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updatePostFromDialog(String category) {
        this.categoryy = category;
        if (category.equals(getString(R.string.free))) {
            this.posttype = 1;
        } else if (category.equals(getString(R.string.special))) {
            this.posttype = 2;
        }
        this.postIsSelected.setImageResource(R.drawable.check_circle);
        ivSelectedPostType.setText(category);
    }

    @OnClick({R.id.warranty_layout})
    void onWarrantySelected(View v) {
        agentNameEditText.setText("");
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new YearsAdapter(this, method -> {
                            updateWarrantyFromDialog(method);
                            materialDialog.dismiss();
                        }, presenter.getWarrantyOptions(), warranty),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateWarrantyFromDialog(String method) {
        this.warranty = method;
        this.warrantyIsSelected.setImageResource(R.drawable.check_circle);
        if (method.equals(getString(R.string.yes))) {
            this.warrantyId = 1;
            this.agentNameLayout.setVisibility(View.VISIBLE);
        } else {
            this.warrantyId = 0;
            this.agentNameLayout.setVisibility(View.GONE);
            this.warrantyIsSelected.setImageResource(R.drawable.check_circle);
        }
        this.warrantyIsSelected.setImageResource(R.drawable.check_circle);

    }

    @OnClick({R.id.color_layout})
    void onColorSelected(View v) {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new ColorAdapter(this, brand -> {
                            updateColorFromDialog(brand);
                            materialDialog.dismiss();
                        }, presenter.getColors(), colorId),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateColorFromDialog(CarColorViewModel brand) {
        this.colorId = brand.getId();
        this.colorIsSelected.setImageResource(R.drawable.check_circle);
        if (isEnglishLang())
            ivSelectedColor.setText(brand.getName());
        else
            ivSelectedColor.setText(brand.getName());
    }

    @OnClick({R.id.transmission_layout})
    void onTransmissionClicked(View v) {
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
        if (type.equals("اوتو") || type.equals("Auto")) {
            this.transmissionId = 1;
        } else if (type.equals("عادي") || type.equals("Manual")) {
            this.transmissionId = 2;
        }
        this.transmissionIsSelected.setImageResource(R.drawable.check_circle);
        ivSelectedTransmission.setText(type);
    }

    @OnClick({R.id.categ_layout})
    void onCategoryClicked(View v) {
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
        this.categoryId = category.getId();
        this.categoryIsSelected.setImageResource(R.drawable.check_circle);
        if (isEnglishLang())
            ivSelectedCategory.setText(category.getName());
        else
            ivSelectedCategory.setText(category.getName());
    }

    @OnClick({R.id.conditions_layout})
    void onConditionClicked(View v) {
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
        this.condition = condition;
        this.conditionIsSelected.setImageResource(R.drawable.check_circle);
        ivSelectedCondition.setText(condition);
        if (condition.equals(getString(R.string.newc))) {
            this.conditionId = 1;
            trackedCarLayout.setVisibility(View.VISIBLE);
        } else if (condition.equals(getString(R.string.old))) {
            this.conditionId = 2;
            trackedCarLayout.setVisibility(View.VISIBLE);
        } else if (condition.equals(getString(R.string.damaged))) {
            this.conditionId = 3;
            trackedCarLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.manu_Layout})
    void onManuFacturingYearClicked(View v) {
        MonthPickerDialog.Builder builder =
                new MonthPickerDialog.Builder(this, (selectedMonth, selectedYear) ->
                        mnufacturingYear = selectedYear,
                        limitCalender.get(Calendar.YEAR),
                        limitCalender.get(Calendar.MONTH));
        builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(1950)
                .setActivatedYear(limitCalender.get(Calendar.YEAR) - 1)
                .setMaxYear(limitCalender.get(Calendar.YEAR) + 1)
                .showYearOnly()
                .setOnYearChangedListener(year -> {
                    mnufacturingYear = year;
                    ivSelectedYear.setText(String.valueOf(year));
                })
                .build()
                .show();
        manufacturingIsSelected.setImageResource(R.drawable.check_circle);
    }

    @OnClick(R.id.importer_layout)
    void onImporterClicked(View v) {
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
        this.importerIsSelected.setImageResource(R.drawable.check_circle);
        this.importerId = id.getId();
        if (isEnglishLang())
            this.ivSelectedImporter.setText(id.getName());
        else
            this.ivSelectedImporter.setText(id.getName());
    }

    @OnClick({R.id.brand_layout})
    void onBrandSelected(View v) {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new BrandsAdapter(this, brand -> {
                            updateBrandFromDialog(brand);
                            materialDialog.dismiss();
                        }, presenter.getBrands(), brandId),
                        new LinearLayoutManager(this))
                .backgroundColor(getResources().getColor(R.color.transparent))
                .build();
        materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
        materialDialog.show();
    }

    private void updateBrandFromDialog(BrandsViewModel brand) {
        this.brandId = brand.getId();
        this.brandIsSelected.setImageResource(R.drawable.check_circle);
        this.modelId = 0;
        this.modelIsSelected.setImageResource(R.color.colorAddCarCircles);
        this.categoryId = 0;
        this.categoryIsSelected.setImageResource(R.color.colorAddCarCircles);
        ivSelectedModel.setText("");
        ivSelectedCategory.setText("");
        if (isEnglishLang())
            ivSelectedBrand.setText(brand.getName());
        else
            ivSelectedBrand.setText(brand.getName());
    }

    @OnClick({R.id.models_layout})
    void onModelsSelected(View v) {
        if (brandId != 0) {
            materialDialog = new MaterialDialog.Builder(this)
                    .adapter(new ModelsAdapter(this, brand -> {
                                updateModelsFromDialog(brand);
                                materialDialog.dismiss();
                            }, presenter.getModels(brandId), modelId),
                            new LinearLayoutManager(this))
                    .backgroundColor(getResources().getColor(R.color.transparent))
                    .build();
            materialDialog.getRecyclerView().setBackground(getResources().getDrawable(R.drawable.shape_rounded_white));
            materialDialog.show();
        } else {
            showWarningMessage(getString(R.string.select_brand_first));
        }
    }

    private void updateModelsFromDialog(ModelViewModel brand) {
        this.modelId = brand.getId();
        this.modelIsSelected.setImageResource(R.drawable.check_circle);
        this.categoryId = 0;
        this.categoryIsSelected.setImageResource(R.color.colorAddCarCircles);
        if (isEnglishLang())
            ivSelectedModel.setText(brand.getName());
        else
            ivSelectedModel.setText(brand.getName());
    }

    @OnClick(R.id.pickLocationLayout)
    void onPicLocationClicked(View v) {
//        showWarningMessage(getString(R.string.recommended));
        startActivityForResult(SnapCarApplication.getPickerIntent(this),PLACE_PICKER_REQUEST);
    }

    // reciving examination position back from presenter
    @Override
    public void setCurrentExaminationPosition(int currentExaminationPosition) {
        this.currentExaminationPostion = currentExaminationPosition;
    }

    @Override
    public void finishActivity() {
        finish();
    }

    // setting Examination images using it's position that it sent to presenter when image is picked

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

    @OnClick(R.id.pickLocationOnMapLayout)
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

    public void updateLatLong(LatLng latLng) {
        this.latTracked = String.valueOf(latLng.latitude);
        this.lonTracked = String.valueOf(latLng.longitude);
    }

    // called when car added successfully and starting service if car changed to tracked and if post type changed
    @Override
    public void carUpdated() {
        FirebaseJobDispatcher dispatcher = jopDispatcher.getInstance();
        Job job = jopDispatcher.createJob(dispatcher);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if (isTracked == 1) {
            dispatcher.schedule(job);
//            final boolean[] isExsit = {false};
//            db.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
//                        FireBaseModel fireBaseModel = snapShot.getValue(FireBaseModel.class);
//                        if (fireBaseModel.getCarId() == carId) {
//                            db.child(snapShot.getKey()).setValue(new FireBaseModel(carId,
//                                    (int) userManager.getCurrentUser().getId(),
//                                    lonTracked,
//                                    latTracked,
//                                    latTracked,
//                                    lonTracked));
//                            isExsit[0] = true;
//                            break;
//                        } else {
//                            isExsit[0] = false;
//                        }
//                    }
//                    if (!isExsit[0]) {
//                        String trackingId = db.push().getKey();
//                        db.child(trackingId).setValue((new FireBaseModel(carId,
//                                (int) userManager.getCurrentUser().getId(),
//                                lonTracked,
//                                latTracked,
//                                latTracked,
//                                lonTracked)));
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        } else {
            dispatcher.cancelAll();
        }
        if (posttype == 2 && isSpecialBefore == 0) {
            Intent i = new Intent(this, PaymentActivityForAddcar.class);
            i.putExtra("commission", settingsApiResponse.getCommissionPercentage());
            i.putExtra("carId", carId);
            startActivityForResult(i, 100);
            finish();
//            getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
//                    .text(R.id.tv_sewar, settingsApiResponse.getSwearText())
//                    .clickListener(R.id.btn_reject, (dialog, view) -> {
//                        dialog.dismiss();
//                    }).clickListener(R.id.btn_accept, (dialog, view) -> {
//
//                dialog.dismiss();
//                finish();
//            }).background(R.color.colorwhete)
//                    .build()
//                    .show();
        } else {
            showSuccessMessage(getString(R.string.car_updated_successfully));
            finish();
        }
    }

    @Override
    public void setSwearData(SettingsModel settingsApiResponse) {
//        premiumPriceTextView.setText(getString(R.string.hint_premium_price).concat(" ").concat(String.valueOf(settingsApiResponse.getPremiumPrice())).concat(" ").concat(
//                getString(R.string.hint_premium_price_two)
//        ));
        this.settingsApiResponse = settingsApiResponse;
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

    // will called when user adding car and his session is deleted from dashboard
    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.btn_publish_car})
    public void onPublishClicked(View v) {
        // validating inputs data
        if (images.isEmpty()) {
            showWarningMessage(getString(R.string.please_add_car_images));
            this.isImageSelectedCircleImageView.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (brandId == 0) {
            showWarningMessage(getString(R.string.please_selected_your_brand));
            this.brandIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (modelId == 0) {
            showWarningMessage(getString(R.string.please_select_car_model));
            this.modelIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (mnufacturingYear == 0) {
            showWarningMessage(getString(R.string.please_select_manufacturing_year));
            this.manufacturingIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (colorId == 0) {
            showWarningMessage(getString(R.string.please_select_car_color));
            this.colorIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (transmissionId == 0) {
            showWarningMessage(getString(R.string.please_select_transmission));
            this.transmissionIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (importerId == 0) {
            showWarningMessage(getString(R.string.please_select_imporeter));
            this.importerIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }
        if (KmEditText.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_select_car_km));
            this.kilometersIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (conditionId == 0) {
            showWarningMessage(getString(R.string.please_select_condition));
            this.conditionIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (warrantyId == 1 && agentNameEditText.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_type_warranty_period));
            this.warrantyIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (posttype == 0) {
            showWarningMessage(getString(R.string.please_select_post_type));
            this.postIsSelected.setImageResource(R.drawable.cancel_material);
            return;
        }

        if (radioButtonId == 1 && priceTypingLayout.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_type_price));
            return;
        }
        if (radioButtonId == 1 && priceTypingLayout.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.please_type_price));
            return;
        }
        if (isTracked == 0 && latFixedLocation.isEmpty()) {
            if (userManager.sessionManager().isSessionActive()) {
                if (userManager.getCurrentUser().getType() == 2)
                    if (userManager.getCurrentUser().getShowRoomInfoModel().getLatitude().isEmpty()) {
                        startActivity(new Intent(this, EditShowRoomLocationActivity.class));
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
                showErrorMessage(getString(R.string.please_type_price));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                price = replaceArabicNumbers(priceTypingLayout.getText().toString());
                priceIsSelected.setImageResource(R.drawable.monetization_on_material);
            }
            price = "";
        }
        if (radioButtonId == 5) {
            if (replaceArabicNumbers(install_bef_EditText.getText().toString()).equals("0") ||
                    replaceArabicNumbers(install_to_EditText.getText().toString()).equals("0")) {
                showErrorMessage(getString(R.string.check_installment_data));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                price = "";
                return;

            }
            if ((replaceArabicNumbers(install_bef_EditText.getText().toString()).isEmpty() ||
                    replaceArabicNumbers(install_to_EditText.getText().toString()).isEmpty())) {
                showErrorMessage(getString(R.string.please_check_discount_data));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else if (Integer.parseInt(replaceArabicNumbers(install_bef_EditText.getText().toString())) <
                    Integer.parseInt(replaceArabicNumbers(install_to_EditText.getText().toString()))) {
                showErrorMessage(getString(R.string.pease_check_instament));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                priceIsSelected.setImageResource(R.drawable.check_circle);
            }
        }
        if (radioButtonId == 4) {
            price = "";
            if (presenter.isShowRoom() &&
                    (replaceArabicNumbers(disc_bef_EditText.getText().toString()).isEmpty() ||
                            replaceArabicNumbers(disc_after_EditText.getText().toString()).isEmpty())) {
                showErrorMessage(getString(R.string.please_check_discount));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else if (replaceArabicNumbers(disc_bef_EditText.getText().toString()).equals("0") ||
                    replaceArabicNumbers(disc_after_EditText.getText().toString()).equals("0")) {
                showErrorMessage(getString(R.string.please_check_discount_data));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else if (presenter.isShowRoom() && (Integer.parseInt(replaceArabicNumbers(disc_bef_EditText.getText().toString()))
                    < Integer.parseInt(replaceArabicNumbers(disc_after_EditText.getText().toString())))) {
                showErrorMessage(getString(R.string.please_check_discount_data));
                priceIsSelected.setImageResource(R.drawable.cancel_material);
                return;
            } else {
                priceIsSelected.setImageResource(R.drawable.check_circle);
            }
        }
        if (install_to_EditText.getText().toString().isEmpty()) {
            instTo = "";
            instFrom = "";
        } else {
            instTo = replaceArabicNumbers(install_to_EditText.getText().toString());
            instFrom = replaceArabicNumbers(install_bef_EditText.getText().toString());
        }
        if (disc_bef_EditText.getText().toString().isEmpty()) {
            priceto = "";
            priceFrom = "";
        } else {
            priceto = replaceArabicNumbers(disc_bef_EditText.getText().toString());
            priceFrom = replaceArabicNumbers(disc_after_EditText.getText().toString());
        }

        if (presenter.isHasTrackedCar() && isTracked == 1 && isTrackedBefore != 1) {
            showErrorMessage(getString(R.string.tracked_more_than_one_car));
            return;
        } else if (isTracked == 1) {
            if (!getPermissionUtil().hasLocationPermission()) {
                showErrorMessage(getString(R.string.please_grand_location_permission));
                trackedCarSwitchCompact.setChecked(false);
                isTracked = 0;
                return;
            }
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
            showErrorMessage(getString(R.string.select_at_least_contact_option));
            return;
        }

        // if all inputs is valid car will be updated depending on the type of tracking
        // lon2 & lat2 --> fixed Location , lon & lat --> tracked car
        if (posttype == 2) {
            getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
                    .text(R.id.tv_sewar, settingsApiResponse != null?settingsApiResponse.getSwearText():" ")
                    .clickListener(R.id.btn_reject, (dialog, view) -> {
                        dialog.dismiss();
                    }).clickListener(R.id.btn_accept, (dialog, view) -> {

                if (isTracked == 0) {
                    presenter.updateCar(
                            String.valueOf(carId),
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
                            priceFrom,
                            priceto,
                            enginCCEditText.getText().toString(),
                            images,
                            instTo,
                            instFrom,
                            exchange,
                            lonFixedLocation,
                            latFixedLocation,
                            getExaminationImages(),
                            categoryId,
                            brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
                } else if (isTracked == 1) {
                    presenter.updateCar(
                            String.valueOf(carId),
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
                            priceFrom,
                            priceto,
                            enginCCEditText.getText().toString(),
                            images,
                            instTo,
                            instFrom,
                            exchange,
                            lonTracked,
                            latTracked,
                            getExaminationImages(),
                            categoryId,
                            brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
                } else if (isTracked == 2) {
                    presenter.updateCar(
                            String.valueOf(carId),
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
                            priceFrom,
                            priceto,
                            enginCCEditText.getText().toString(),
                            images,
                            instTo,
                            instFrom,
                            exchange,
                            lonFixedOnmap,
                            latFixedOnMap,
                            getExaminationImages(),
                            categoryId,
                            brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
                }
            }).build().show();
        } else {
            if (isTracked == 0) {
                presenter.updateCar(
                        String.valueOf(carId),
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
                        priceFrom,
                        priceto,
                        enginCCEditText.getText().toString(),
                        images,
                        instTo,
                        instFrom,
                        exchange,
                        lonFixedLocation,
                        latFixedLocation,
                        getExaminationImages(),
                        categoryId,
                        brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
            } else if (isTracked == 1) {
                presenter.updateCar(
                        String.valueOf(carId),
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
                        priceFrom,
                        priceto,
                        enginCCEditText.getText().toString(),
                        images,
                        instTo,
                        instFrom,
                        exchange,
                        lonTracked,
                        latTracked,
                        getExaminationImages(),
                        categoryId,
                        brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
            } else if (isTracked == 2) {
                presenter.updateCar(
                        String.valueOf(carId),
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
                        priceFrom,
                        priceto,
                        enginCCEditText.getText().toString(),
                        images,
                        instTo,
                        instFrom,
                        exchange,
                        lonFixedOnmap,
                        latFixedOnMap,
                        getExaminationImages(),
                        categoryId,
                        brandId, priceTypingLayout.getText().toString(), vehicleRegistration, radioButtonId);
            }
        }

    }

    // grouping the three examinations images urls in one string
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

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
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

    // replacing arabic digits by english if users typing in arabic.
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

    // inti back drawable to be localized.
    @SuppressLint("ResourceType")
    private void setupBack() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        addImageCell.setImageDrawable(icons.getDrawable(1));
        locationCell.setImageDrawable(icons.getDrawable(1));
        locationOnMapCell.setImageDrawable(icons.getDrawable(1));
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
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
}