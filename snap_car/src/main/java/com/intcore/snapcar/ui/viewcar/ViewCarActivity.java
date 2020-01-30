package com.intcore.snapcar.ui.viewcar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.images.ImagesApiResponse;
import com.intcore.snapcar.ui.chatthread.ChatThreadActivityArgs;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.photos.PhotosActivityArgs;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.intcore.snapcar.util.UserManager;
import com.rd.PageIndicatorView;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatModel;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@ActivityScope
public class ViewCarActivity extends BaseActivity implements ViewCarScreen, OnMapReadyCallback {

    private static String[] c = new String[]{" k", " m", " b", " t"};
    @BindView(R.id.image_pager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;
    @BindView(R.id.tv_engine)
    TextView engineCC;
    @BindView(R.id.tv_warranty)
    TextView warranty;
    @BindView(R.id.tv_gender)
    TextView gender;
    @BindView(R.id.image_gender)
    RelativeLayout imageGender;
    @BindView(R.id.view_ic)
    TextView views;
    @BindView(R.id.tv_model)
    TextView model;
    @BindView(R.id.tv_year)
    TextView year;
    @BindView(R.id.tv_km)
    TextView KM;
    @BindView(R.id.tv_color)
    TextView color;
    @BindView(R.id.tv_condition)
    TextView condition;
    @BindView(R.id.tv_importer)
    TextView importer;
    @BindView(R.id.examination_layout)
    RelativeLayout examinationLayout;
    @BindView(R.id.notes_layout)
    RelativeLayout notesLayout;
    @BindView(R.id.tv_notes)
    TextView notes;
    @BindView(R.id.image_avatar)
    CircleImageView useImage;
    @BindView(R.id.seller_name)
    TextView userName;
    @BindView(R.id.fav_ic)
    ImageView favoritIcon;
    @BindView(R.id.tv_price1)
    TextView price1;
    @BindView(R.id.car_name)
    TextView carName;
    @BindView(R.id.tv_price2)
    TextView price2;
    @BindView(R.id.tv_chat)
    TextView chat;
    @BindView(R.id.tv_call)
    TextView call;
    @BindView(R.id.tv_transmission)
    TextView transmission;
    @BindView(R.id.tv_is_mvpi)
    TextView mvpi;
    @BindView(R.id.tv_post_type)
    TextView postType;
    @BindView(R.id.seller_country)
    TextView sellerCountry;
    @BindView(R.id.tv_category)
    TextView category;
    @BindView(R.id.examination_image)
    ImageView examinationImage;
    @BindView(R.id.brand_image)
    CircleImageView brandImage;
    @BindView(R.id.fav_layout)
    LinearLayout favLayout;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.categ_layout)
    RelativeLayout categLayout;
    @BindView(R.id.engine_layout)
    RelativeLayout engineLayout;
    @BindView(R.id.tv_date)
    TextView dateTextView;
    @Inject
    viewCarPresenter presenter;
    @Inject
    UserManager userManager;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.share_ic)
    TextView shareIc;
    @BindView(R.id.tv_is_registration)
    TextView tvIsRegistration;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.im_examination_label)
    ImageView isExaminationImageView;
    @BindView(R.id.getVehicleRegistrationLayout)
    RelativeLayout vehicleRegistrationLayout;
    private ActivityComponent component;
    private ImagePagerAdapter pagerAdapter;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String lat;
    private String lon;
    private boolean isFav = false;
    private int carId;
    private String[] examinationUrl;
    private String userPhone;
    private long userId;
    private boolean isEnglish = false;
    private CarApiResponse car;

    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration])
                : coolFormat(d, iteration + 1));
    }

    @Override
    protected void onCreateActivityComponents() {
        component = SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this));
        component.inject(this);

        ButterKnife.bind(this);
        this.pagerAdapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_layout);
        presenter.fetchCarData(getIntent().getIntExtra("carId", 0));
        isEnglish = isEnglishLang();
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
        if (!isEnglishLang()) {
            c = new String[]{" الف", " مليون", " بليون", " t"};
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_view_car;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (!lat.isEmpty()) {
            LatLng location = new LatLng(Float.parseFloat(lat), Float.parseFloat(lon));
            mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory
                    .fromBitmap(getResourcesUtil()
                            .loadBitmapFromView(getLayoutInflater().inflate(R.layout.marker, null)))));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        }
    }

    @Override
    public void carDeleted() {
        showErrorMessage(getString(R.string.this_car_has_been_deleted));
        new Handler().postDelayed(() -> finish(), 3000);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void updateUi(CarDTO carApiResponse) {
        if (carApiResponse == null) carDeleted();
        car = carApiResponse.getCar();
        long userId =  -1 ;
        if (userManager != null ){
            if (userManager.getCurrentUser() != null){
                userId = userManager.getCurrentUser().getId() ;
            }
        }

        if ((car.getIsSold() == 2 || car.getIsSold() == 1) && car.getUserId() == userId) {
            chat.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        }
        Timber.tag("CarId").d(String.valueOf(carApiResponse.getCar().getId()));
        if (car != null) {
            List<ImagesApiResponse> imagesApiResponses = car.getImagesApiResponses();
            this.userId = car.getUserId();
            ArrayList<String> images = new ArrayList<>();
            for (ImagesApiResponse response : imagesApiResponses) {
                images.add(response.getImage());
            }
            if (!TextUtils.isEmpty(car.getExaminationImage())) {
                isExaminationImageView.setVisibility(View.VISIBLE);
                examinationUrl = car.getExaminationImage().split(",");
                images.addAll(Arrays.asList(examinationUrl));
            }
            if (isEnglishLang()) {
                warranty.setGravity(Gravity.END);
                dateTextView.setText(car.getCarDateEn());
            } else {
                warranty.setGravity(Gravity.START);
                dateTextView.setText(car.getCarDateAr());
            }
            pagerAdapter.swapData(images);
            pageIndicatorView.setCount(images.size());
            this.lat = car.getLatitude();
            this.lon = car.getLongitude();
            mapFragment.getMapAsync(this);
            if (car.getVehicleRegistration() == 1) {
                tvIsRegistration.setText(getString(R.string.yes));
                vehicleRegistrationLayout.setVisibility(View.VISIBLE);
            } else {
                tvIsRegistration.setText(getString(R.string.no));
            }
            views.setText(String.valueOf(car.getViews()).concat(" ").concat(getString(R.string.views)));
            if (car.getModelApiResponse() == null) {
                model.setText(R.string.unknown);
            } else {
                if (isEnglish) {
                    model.setText(car.getModelApiResponse().getNameEn());
                } else {
                    model.setText(car.getModelApiResponse().getNameAr());
                }
            }
            if (car.getCategoryApiResponse() == null) {
                categLayout.setVisibility(View.GONE);
            } else {
                if (isEnglish) {
                    category.setText(car.getCategoryApiResponse().getNameEn());
                } else {
                    category.setText(car.getCategoryApiResponse().getNameAr());
                }
            }
            year.setText(car.getManufacturingYear());
            if (car.getKilometer() == null) {
                KM.setText(coolFormat(Double.parseDouble("0"), 0));
            } else {
                if (car.getKilometer().length() < 4) {
                    KM.setText(car.getKilometer());
                } else {
                    KM.setText(coolFormat(Double.parseDouble(car.getKilometer()), 0));
                }
            }
            if (car.getCarColorApiResponse() != null) {
                if (isEnglish) {
                    color.setText(car.getCarColorApiResponse().getNameEn());
                } else {
                    color.setText(car.getCarColorApiResponse().getNameAr());
                }
            }
            if (car.getUser() == null) {
                userName.setText(R.string.user_name_view_car);
            } else {
                userName.setText(car.getUser().getName());
            }
            if (car.getUser() != null) {
                if (car.getUser().getCountry() != null && car.getUser().getCity() != null) {
                    if (isEnglish) {
                        sellerCountry.setText(car.getUser().getCity().getNameEn().concat(" - ").concat(car.getUser().getCountry().getNameEn()));
                    } else {
                        sellerCountry.setText(car.getUser().getCity().getNameAr().concat(" - ").concat(car.getUser().getCountry().getNameAr()));
                    }
                }

                switch (car.getUser().getGender()) {
                    case Gender.MALE:
                        gender.setText(R.string.man_car);
                        imageGender.setBackgroundResource(R.drawable.men_label);
                        imageGender.setVisibility(View.VISIBLE);
                        break;
                    case Gender.FEMALE:
                        gender.setText(R.string.woman_car);
                        imageGender.setBackgroundResource(R.drawable.wom_label);
                        imageGender.setVisibility(View.VISIBLE);
                        break;
                }

                this.userPhone = car.getUser().getPhone();
                Glide.with(this)
                        .load(ApiUtils.BASE_URL.concat(car.getUser().getImage()))
                        .placeholder(R.drawable.default_img)
                        .centerCrop()
                        .into(useImage);
            }
            carId = car.getId();
            if (isEnglish) {
                carName.setText(car.getCarNameEn());
            } else {
                carName.setText(car.getCarName());
            }
            if (car.getBrandsApiResponse() != null)
                Glide.with(this)
                        .load(ApiUtils.BASE_URL.concat(car.getBrandsApiResponse().getImage()))
                        .centerCrop()
                        .into(brandImage);
            if (car.getImporterApiResponse() != null) {
                if (isEnglish) {
                    importer.setText(car.getImporterApiResponse().getNameEn());
                } else {
                    importer.setText(car.getImporterApiResponse().getNameAr());
                }
            } else {
                importer.setText(R.string.unknown);
            }
            if (car.getMvpi() == 1) {
                mvpi.setText(getString(R.string.yes));
            } else {
                mvpi.setText(getString(R.string.no));
            }
            if (car.isFvorite()) {
                favoritIcon.setImageResource(R.drawable.favorite_material);
                isFav = true;
            }
            String carPrice;
            if (car.getPrice() == null) {
                carPrice = "";
            } else {
                carPrice = car.getPrice();
            }
            String carAfter;
            if (car.getPriceAfter() == null) {
                carAfter = "";
            } else carAfter = car.getPriceAfter();
            String carInstallTo;
            if (car.getInstallmentPriceTo() == null) {
                carInstallTo = "";
            } else carInstallTo = car.getInstallmentPriceTo();
            if (car.getPriceType() == 2) {
                price1.setText(getString(R.string.undetermined));
                price2.setVisibility(View.GONE);
            } else if (car.getPriceType() == 1) {
                if (carPrice.length() < 4) {
                    price1.setText(carPrice.concat(" ").concat(getString(R.string.sar)));
                } else {
                    price1.setText(String.valueOf(coolFormat(Double.parseDouble(carPrice), 0).concat(" ").concat(getString(R.string.sar))));
                }
                price2.setVisibility(View.GONE);
            }
            if (car.getPriceType() == 3) {
                price1.setText(getString(R.string.exchange));
                price2.setVisibility(View.GONE);
            }
            if (car.getPriceType() == 4) {
                if (carAfter.length() < 4) {
                    price1.setText(carAfter.concat(" ").concat(getString(R.string.sar)));
                } else {
                    price1.setText(String.valueOf(coolFormat(Double.parseDouble(carAfter), 0).concat(" ").concat(getString(R.string.sar))));
                }
                if (car.getPriceBefore().length() < 4) {
                    price2.setText(car.getPriceBefore().concat(" ").concat(getString(R.string.sar)));
                    price2.setPaintFlags(price2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    price2.setText(String.valueOf(coolFormat(Double.parseDouble(car.getPriceBefore()), 0).concat(" ").concat(getString(R.string.sar))));
                    price2.setPaintFlags(price2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            if (car.getPriceType() == 5) {
                if (car.getInstallmentPriceFrom().length() < 4) {
                    price1.setText(car.getInstallmentPriceFrom().concat(" ").concat(getString(R.string.sar)));
                } else {
                    price1.setText(String.valueOf(coolFormat(Double.parseDouble(car.getInstallmentPriceFrom()), 0).concat(" ").concat(getString(R.string.sar))));
                }
                if (carInstallTo.length() < 4) {
                    price2.setText(carInstallTo.concat(" ").concat(getString(R.string.sar)).concat(" ").concat(getString(R.string.month)));
                } else {
                    if (!isEnglish) {
                        price2.setGravity(Gravity.RIGHT);
                    }
                    price2.setText(String.valueOf(coolFormat(Double.parseDouble(carInstallTo), 0).concat(" ").concat(getString(R.string.sar)).concat(" ").concat(getString(R.string.month))));
                }
            }
            if (car.getExaminationImage() != null) {
                if (car.getExaminationImage().isEmpty()) {
                    examinationLayout.setVisibility(View.GONE);
                } else {
                    examinationUrl = car.getExaminationImage().split(",");
                }
            } else {
                examinationLayout.setVisibility(View.GONE);
            }
            if (car.getNotes().isEmpty()) {
                notesLayout.setVisibility(View.GONE);
            } else {
                if (Pattern.compile("^[\\u0621-\\u064A\\u0660-\\u0669 ]+$").matcher(car.getNotes()).find()) {
                    notes.setTextAlignment(View.TEXT_DIRECTION_FIRST_STRONG_RTL);
                }
                notes.setText(car.getNotes());
            }
            switch (car.getTransmission()) {
                case 1:
                    transmission.setText(getString(R.string.auto));
                    break;
                case 2:
                    transmission.setText(getString(R.string.normal));
                    break;
            }
            if (car.getEngineCapacityCc() == null) {
                engineLayout.setVisibility(View.GONE);
            } else {
                if (String.valueOf(car.getEngineCapacityCc()).length() < 4) {
                    engineCC.setText(String.valueOf(car.getEngineCapacityCc()).concat(" CC"));
                } else {
                    engineCC.setText(String.valueOf(coolFormat(Double.parseDouble(String.valueOf(car.getEngineCapacityCc())), 0)).concat(" CC"));
                }
            }
            switch (car.getUnderWarranty()) {
                case 0:
                    warranty.setText(getString((R.string.no)));
                    break;
                case 1:
                    if (car.getWarranty() == null) {
                        warranty.setText(getString(R.string.yes));
                    } else {
                        warranty.setText(getString(R.string.yes).concat(" / ").concat(car.getWarranty()));
                    }
                    break;
            }
            if (car.getCarStatus() == CarCondition.DAMAGED) {
                gender.setText(R.string.damaged);
                imageGender.setBackgroundResource(R.drawable.damg_label);
                imageGender.setVisibility(View.VISIBLE);
            }
            if (car.getUser().getType() == UserType.SHOW_ROOM && car.getCarStatus() != CarCondition.DAMAGED) {
                imageGender.setVisibility(View.GONE);
                gender.setVisibility(View.GONE);
            }
            switch (car.getCarStatus()) {
                case 1:
                    condition.setText(getString(R.string.newc));
                    break;
                case 2:
                    condition.setText(getString(R.string.old));
                    break;
                case 3:
                    condition.setText(getString(R.string.damaged));
                    break;
            }
            switch (car.getContactOption()) {
                case 1:
                    call.setVisibility(View.GONE);
                    break;
                case 2:
                    chat.setVisibility(View.GONE);
                    break;
            }
            if (presenter.currentUserId() != car.getUserId()) {
                favLayout.setVisibility(View.VISIBLE);
            }
            switch (car.getPostType()) {
                case 1:
                    postType.setText(getString(R.string.free));
                    break;
                case 2:
                    postType.setText(getString(R.string.special));
                    break;
            }
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

    @OnClick(R.id.examination_image)
    void onExaminationClicked() {
        new PhotosActivityArgs(new ArrayList<>(Arrays.asList(examinationUrl)), 0).launch(this);
    }

    @OnClick(R.id.fav_layout)
    void onFavClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            if (isFav) {
                presenter.removeFav(carId);
                favoritIcon.setImageResource(R.drawable.favorite_border_material);
                isFav = false;
                showSuccessMessage(getString(R.string.removed_from_fav));
            } else {
                presenter.addFav(carId);
                favoritIcon.setImageResource(R.drawable.favorite_material);
                isFav = true;
                showSuccessMessage(getString(R.string.added_from_fav));
            }
        } else {
            showWarningMessage(getString(R.string.please_sign_in));
        }
    }

    @OnClick(R.id.go_location)
    void onGoLocation() {
        String uriBegin = "geo:" + lat + "," + lon;
        String query = lat + "," + lon;
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(mapIntent);
    }

    @OnClick(R.id.tv_call)
    void onCallClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 200);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + String.valueOf(userPhone)));
                startActivity(intent);
            }
        } else {
            showWarningMessage(getString(R.string.please_sign_in));
        }
    }

    @OnClick(R.id.tv_chat)
    void onChatClicked() {
        if (car.getUser() != null) {
            if (userManager.sessionManager().isSessionActive()) {
                presenter.onChatClicked();
            } else {
                showWarningMessage(getString(R.string.please_sign_in));
            }
        }
    }

    @Override
    public void shouldNavigateToChatThread(ChatModel chatModel) {
        new ChatThreadActivityArgs(chatModel.getId(), 0, chatModel.getUseModel().getFirstName(), null)
                .launch(this);
    }

    @OnClick(R.id.image_avatar)
    public void onAvatarClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            if (car.getUserId() == userManager.getCurrentUser().getId()) {
                return;
            }
        }
        if (car.getUserId() != null) {
            new VisitorProfileActivityArgs(car.getUserId()).launchForResult(this, 100);
        }
    }

    @OnClick(R.id.share_ic)
    void onShareClicked() {
        // concat car data in one string to sharing it

        String sharedText = "";
        if (isEnglish) {
            if (car.getBrandsApiResponse() != null) {
                sharedText = sharedText.concat(getString(R.string.car_brandd)).concat(car.getBrandsApiResponse().getNameEn()).concat("\n")
                        .concat(getString(R.string.car_model)).concat(car.getModelApiResponse().getNameEn()).concat("\n");
            }
            if (car.getCategoryApiResponse() != null) {
                sharedText = sharedText.concat(getString(R.string.car_categoryy)).concat(car.getCategoryApiResponse().getNameEn()).concat("\n");
            }
        } else {
            if (car.getBrandsApiResponse() != null) {
                sharedText = sharedText.concat(getString(R.string.car_brandd)).concat(car.getBrandsApiResponse().getNameAr()).concat("\n")
                        .concat(getString(R.string.car_model)).concat(car.getModelApiResponse().getNameAr()).concat("\n");
            }
            if (car.getCategoryApiResponse() != null) {
                sharedText = sharedText.concat(getString(R.string.car_categoryy)).concat(car.getCategoryApiResponse().getNameAr()).concat("\n");
            }
        }

        sharedText+= getString(R.string.price) + " ";

        switch (car.getPriceType()){
            case 1:
                sharedText+= car.getPrice() + " " + getString(R.string.sar);
                break;

            case 2:
                sharedText+= getString(R.string.undetermined);
                break;

            case 3:
                sharedText+= getString(R.string.exchange);
                break;

            case 4:
                sharedText+= getString(R.string.after) + " " + car.getPriceAfter() + " " +getString(R.string.sar)
                        + " / " + getString(R.string.discount_before) + " " + car.getPriceBefore() + " " +getString(R.string.sar);
                break;

            case 5:
                sharedText+= getString(R.string.installment);
                sharedText+= "\n" + getString(R.string.price) + " "
                + car.getInstallmentPriceFrom() + " " +getString(R.string.sar)
                        + " / " + car.getInstallmentPriceTo() + " " + getString(R.string.monthely);
                break;
        }

//        String carPrice;
//        if (car.getPrice() == null) {
//            carPrice = "";
//        } else {
//            carPrice = car.getPrice();
//        }
//        String carAfter;
//        if (car.getPriceAfter() == null) {
//            carAfter = "";
//        } else carAfter = car.getPriceAfter();
//
//        String carInstallTo;
//        if (car.getInstallmentPriceTo() == null) {
//            carInstallTo = "";
//        } else carInstallTo = car.getInstallmentPriceTo();
//        if (carPrice.equals("0")) {
//            sharedText = sharedText.concat(getString(R.string.price)).concat(getString(R.string.undetermined));
//        } else if (!carPrice.equals("")) {
//            if (carPrice.length() < 4) {
//                sharedText = sharedText.concat(getString(R.string.price)).concat(carPrice.concat(" ").concat(getString(R.string.sar))).concat("\n");
//            } else {
//                sharedText = sharedText.concat(getString(R.string.price)).concat(String.valueOf(coolFormat(Double.parseDouble(carPrice), 0).concat(" ").concat(getString(R.string.sar)))).concat("\n");
//            }
//        }
//        if (car.getExchange().equals("1")) {
//            sharedText = sharedText.concat(getString(R.string.price)).concat(getString(R.string.exchange)).concat("\n");
//        }
//        if (!carAfter.equals("")) {
//            sharedText = sharedText
//                    .concat(getString(R.string.price))
//                    .concat(getString(R.string.after) + carAfter
//                            .concat(getString(R.string.sar)) + " / " + getString(R.string.discount_before) + car.getPriceBefore()
//                            .concat(" ").concat(getString(R.string.sar))).concat("\n");
//        }
//        if (!carInstallTo.equals("")) {
//            sharedText = sharedText
//                    .concat(getString(R.string.price)) + carInstallTo.concat(" ")
//                    .concat(getString(R.string.sar)) + " / " + car.getInstallmentPriceFrom()
//                    .concat(getString(R.string.monthely))
//                    .concat("\n");
//        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharedText);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
            if (resultCode == Activity.RESULT_OK) {
                if (data.getIntExtra("result", 0) == 1) {
                    finish();
                }
            }
    }
}