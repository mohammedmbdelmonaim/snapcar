package com.intcore.snapcar.ui.visitorprofile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoModel;
import com.intcore.snapcar.ui.addinterest.BrandAdapter;
import com.intcore.snapcar.ui.addinterest.CategoryAdapter;
import com.intcore.snapcar.ui.addinterest.ModelAdapter;
import com.intcore.snapcar.ui.advancedsearch.AdvancedSearchActivityArgs;
import com.intcore.snapcar.ui.chatthread.ChatThreadActivityArgs;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.search.SearchActivityArgs;
import com.jaeger.library.StatusBarUtil;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatModel;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.apptik.widget.MultiSlider;

@ActivityScope
public class VisitorProfileActivity extends BaseActivity implements VisitorProfileScreen {

    @Inject
    VisitorProfilePresenter presenter;
    @Inject
    CarsRecyclerAdapter adapter;

    @BindView(R.id.tv_chat)
    LinearLayout chatTextView;
    @BindView(R.id.tv_block)
    LinearLayout blockTextView;
    @BindView(R.id.tv_phone)
    TextView phoneTextView;
    @BindView(R.id.image_edit)
    ImageView editImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.image_cover)
    ImageView userCover;
    @BindView(R.id.tv_user_car)
    TextView carNumberUserTextView;
    @BindView(R.id.tv_working_hour)
    TextView workingHourTextView;
    @BindView(R.id.iv_edit)
    ImageView editShowRoomImageView;
    @BindView(R.id.user_header)
    ConstraintLayout userHeader;
    @BindView(R.id.tv_user_name)
    TextView usernameTextView;
    @BindView(R.id.image_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.block_container)
    FrameLayout blockContainer;
    @BindView(R.id.show_room_header)
    ConstraintLayout showRoomHeader;
    @BindView(R.id.tv_show_room_name)
    TextView showRoomNameTextView;
    @BindView(R.id.tv_show_room_address)
    TextView showRoomAddressTextView;
    @BindView(R.id.tv_show_room_phone)
    TextView showRoomPhoneTextView;
    @BindView(R.id.show_room_avatar)
    CircleImageView showRoomAvatar;
    @BindView(R.id.tv_show_room_email)
    TextView showRoomEmailTextView;
    @BindView(R.id.mail_container)
    LinearLayout mailContainer;
    @BindView(R.id.tv_name_cars)
    TextView usernameCarsTextView;
    @BindView(R.id.tv_address)
    TextView userAddressTextView;
    @BindView(R.id.tv_sold)
    TextView soldTextView;
    @BindView(R.id.tv_available)
    TextView availableTextView;
    @BindView(R.id.rv_cars)
    RecyclerView carsRecyclerView;
    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.tv_rate)
    TextView rateTextView;
    @BindView(R.id.tv_car_number)
    TextView carNumberTextView;
    @BindView(R.id.tv_verified)
    TextView verifiedTextView;
    @BindView(R.id.tv_user_email)
    TextView visitorEmailTextView;
    @BindView(R.id.et_search)
    RxEditText searchEditText;
    @BindView(R.id.tv_no_data)
    TextView noDataFoundTextView;
    @BindView(R.id.dealsWithTextView)
    TextView dealsWithTextView;
    @BindView(R.id.iv_user_back)
    ImageView ivUserBack;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private MaterialDialog basicSearchDialog;
    private MaterialDialog materialDialog;
    private MaterialDialog showRoomDialog;
    private float rate = 0;
    private String review;
    Calendar limitCalender = Calendar.getInstance();
    private int fromYear = limitCalender.get(Calendar.YEAR) - 1;
    private int toYear = limitCalender.get(Calendar.YEAR) + 1;
    private int minPrice;
    private int maxPrice;
    private static String[] c = new String[]{" k", " m", " b", " t"};
    @Gender
    int gender = Gender.SHOW_ALL;


    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        availableTextView.setSelected(true);
        setupBackIcon();
        if (!isEnglishLang()) {
            c = new String[]{" الف", " مليون", " بليون", " t"};
        }
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
        ivUserBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_visitor_profile2;
    }

    @Override
    public void updateUi(DefaultUserModel defaultUserModel) {
        if (defaultUserModel != null) {
            if (defaultUserModel.getType() == UserType.SHOW_ROOM) {
                showRoomHeader.setVisibility(View.VISIBLE);
                userHeader.setVisibility(View.GONE);
                bindShowRoomData(defaultUserModel);
            } else {
                userHeader.setVisibility(View.VISIBLE);
                showRoomHeader.setVisibility(View.GONE);
                carNumberUserTextView.setVisibility(View.GONE);
                bindUserData(defaultUserModel);
            }
        } else {
            blockContainer.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onRateSuccessfully(Dialog dialog) {
        showSuccessMessage(getString(R.string.you_have_rated_successfully));
        presenter.fetchData();
        dialog.dismiss();
    }

    @Override
    public void setupRecyclerView() {
        carsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carsRecyclerView.setAdapter(adapter);
    }

    private void bindUserData(DefaultUserModel defaultUserModel) {
        phoneTextView.setVisibility(View.GONE);
        searchEditText.setVisibility(View.GONE);
        editImageView.setVisibility(View.GONE);
        usernameTextView.setText(defaultUserModel.getFirstName());
        if (defaultUserModel.getEmail() != null) {
            mailContainer.setVisibility(View.VISIBLE);
            visitorEmailTextView.setText(defaultUserModel.getEmail());
        } else {
            visitorEmailTextView.setVisibility(View.GONE);
        }
        if (defaultUserModel.getCountryModel() != null && defaultUserModel.getCityModel() != null) {
            if (Locale.getDefault().getLanguage().contentEquals("en")) {
                userAddressTextView.setText(defaultUserModel.getCountryModel().getNameEn().concat(" - ").concat(defaultUserModel.getCityModel().getNameEn()));
            } else {
                userAddressTextView.setText(defaultUserModel.getCountryModel().getNameAr().concat(" - ").concat(defaultUserModel.getCityModel().getNameAr()));
            }
        } else {
            userAddressTextView.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                .centerCrop()
                .placeholder(R.drawable.default_img)
                .into(userAvatar);
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                .centerCrop()
                .placeholder(R.drawable.default_img)
                .into(userCover);
        StatusBarUtil.setTransparent(this);
        hideLoadingAnimation();
        titleTextView.setText(R.string.user_profile);

    }

    private void bindShowRoomData(DefaultUserModel defaultUserModel) {


        if (defaultUserModel.getShowRoomInfoModel().getDealingWith() != null) {
            switch (defaultUserModel.getShowRoomInfoModel().getDealingWith()) {
                case "1":
                    dealsWithTextView.setText(Html.fromHtml(getString(R.string.deal_visitor)+" <font color=\"#ce3b3b\">"+getString(R.string.all_carss)+"</font>"));
                    break;
                case "2":
                    dealsWithTextView.setText(Html.fromHtml(getString(R.string.deal_visitor)+"<font color=\"#ce3b3b\">"+getString(R.string.old_carss)+"</font>"));

                    break;
                case "3":
                    dealsWithTextView.setText(Html.fromHtml(getString(R.string.deal_visitor)+" <font color=\"#ce3b3b\">"+getString(R.string.new_carss)+"</font>"));
                    break;
                default:
                    dealsWithTextView.setText(Html.fromHtml(getString(R.string.deal_visitor)+"<font color=\"#ce3b3b\">"+getString(R.string.new_carss)+"</font>"));
                    break;
            }
        }else {
            dealsWithTextView.setText(Html.fromHtml(getString(R.string.deal_visitor)+" <font color=\"#ce3b3b\">"+getString(R.string.all_carss)+"</font>"));
        }
        rating.setRating(defaultUserModel.getTotalRate());
        editShowRoomImageView.setVisibility(View.GONE);
        showRoomNameTextView.setText(defaultUserModel.getFirstName());
        rateTextView.setText(String.valueOf(defaultUserModel.getTotalRate()));
        showRoomPhoneTextView.setText(Html.fromHtml("<u>".concat(defaultUserModel.getPhone()).concat("</u>")), TextView.BufferType.SPANNABLE);
        if (defaultUserModel.getIsVerified() == 1) {
            verifiedTextView.setText(getString(R.string.verified));
            verifiedTextView.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(defaultUserModel.getEmail())) {
            showRoomEmailTextView.setText(defaultUserModel.getEmail());
            showRoomEmailTextView.setVisibility(View.VISIBLE);
        } else {
            showRoomEmailTextView.setVisibility(View.GONE);
        }
        if (defaultUserModel.getAvailableCars() != null) {
            carNumberTextView.setText(getString(R.string.car_available_for_sell).concat(defaultUserModel.getAvailableCars()));
        } else {
            carNumberTextView.setText(getString(R.string.no_aailable_cars));
        }
        if (defaultUserModel.getCountryModel() != null && defaultUserModel.getCityModel() != null) {
            if (Locale.getDefault().getLanguage().contentEquals("en")) {
                showRoomAddressTextView.setText(defaultUserModel.getCountryModel().getNameEn().concat(" - ").concat(defaultUserModel.getCityModel().getNameEn()));
            } else {
                showRoomAddressTextView.setText(defaultUserModel.getCountryModel().getNameAr().concat(" - ").concat(defaultUserModel.getCityModel().getNameAr()));
            }
        } else {
            showRoomAddressTextView.setVisibility(View.GONE);
        }
        if (defaultUserModel.getShowRoomInfoModel() != null) {
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTime()) && !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTime())) {
                if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo()) && !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo())) {
                    String workingTimes;
                    if (isEnglishLang()) {
                        workingTimes = getString(R.string.working_hours).concat("( <font color=\"#ce3b3b\">")
                                .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font>").concat(getString(R.string.to)).concat(" <font color =\"#ce3b3b\"> ")
                                        .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> /  <font color=\"#ce3b3b\">"
                                                .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo())
                                                        .concat("</font>").concat(getString(R.string.to)).concat("<font color=\"#ce3b3b\"> ")
                                                        .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo()))))
                                        .concat(" </font> )"));
                    } else {
                        workingTimes = getString(R.string.working_hours).concat("\n").concat("( <font color=\"#ce3b3b\">")
                                .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font>").concat(" ").concat(getString(R.string.to)).concat(" <font color =\"#ce3b3b\"> ")
                                        .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> /  <font color=\"#ce3b3b\">"
                                                .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo())
                                                        .concat("</font>").concat(" ").concat(getString(R.string.to)).concat("<font color=\"#ce3b3b\"> ")
                                                        .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo()))))
                                        .concat(" </font> )"));
                    }
                    workingHourTextView.setText(Html.fromHtml(workingTimes), TextView.BufferType.SPANNABLE);
                } else {
                    String workingTimes;
                    if (isEnglishLang()) {
                        workingTimes = getString(R.string.working_hours).concat("\n").concat(" ( <font color=\"#ce3b3b\">").concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTime()))
                                .concat("</font> ").concat(" ").concat(getString(R.string.to)).concat("<font color=\"#ce3b3b\"> ").concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                .concat("</font> )");
                    } else {
                        workingTimes = getString(R.string.working_hours).concat(" ( <font color=\"#ce3b3b\">").concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTime()))
                                .concat("</font> ").concat(" ").concat(getString(R.string.to)).concat("<font color=\"#ce3b3b\"> ").concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                .concat("</font> )");
                    }
                    workingHourTextView.setText(Html.fromHtml(workingTimes), TextView.BufferType.SPANNABLE);
                }
            }
//            workingHourTextView.setVisibility(View.GONE);

            if (TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getVerifyLatter())) {
                verifiedTextView.setText(R.string.unverified);
            }
        } else {
            verifiedTextView.setText(R.string.unverified);
//            workingHourTextView.setVisibility(View.GONE);
        }
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                .centerCrop()
                .placeholder(R.drawable.default_img)
                .into(showRoomAvatar);
//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getResources().getColor(R.color.show_room_header_color));
//        }
        hideLoadingAnimation();
        if (!isEnglishLang()) {
            showRoomEmailTextView.setGravity(Gravity.RIGHT);
            showRoomNameTextView.setGravity(Gravity.RIGHT);
        } else {
            showRoomEmailTextView.setGravity(Gravity.LEFT);
            showRoomNameTextView.setGravity(Gravity.LEFT);
        }
        titleTextView.setText(R.string.showrrom_profile);
    }

    @Override
    public void showBlockScreen() {
        blockContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBlockSuccess() {
        showSuccessMessage(getString(R.string.you_should_block));
        new Handler().postDelayed(() -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", 1);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }, 1000);

    }

    @Override
    public void showUserPhones(String phone, String phones) {
        String[] showRoomPhones = {"", "", "", "", "", ""};
        if (!TextUtils.isEmpty(phones)) {
            showRoomPhones = phones.replace(" ", "").
                    replace("'", "").replace("]", "").
                    replace("[", "").split(",");
        }
        String[] finalShowRoomPhones = showRoomPhones;
        showRoomDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.layout_show_room_phone, false)
                .build();
        View inflater = showRoomDialog.getCustomView();
        TextView phoneMain = inflater.findViewById(R.id.phone_main);
        TextView phone1 = inflater.findViewById(R.id.phone_one);
        TextView phone2 = inflater.findViewById(R.id.phone_two);
        TextView phone3 = inflater.findViewById(R.id.phone_three);
        TextView phone4 = inflater.findViewById(R.id.phone_four);
        TextView phone5 = inflater.findViewById(R.id.phone_five);
        TextView phone6 = inflater.findViewById(R.id.phone_six);
        List<TextView> phoneArray = new ArrayList<>();
        phoneArray.add(phone1);
        phoneArray.add(phone2);
        phoneArray.add(phone3);
        phoneArray.add(phone4);
        phoneArray.add(phone5);
        phoneArray.add(phone6);
        phoneMain.setText(phone);
        phoneMain.setVisibility(View.VISIBLE);
        for (int i = 0; i < finalShowRoomPhones.length; i++) {
            if (!TextUtils.isEmpty(finalShowRoomPhones[i])) {
                phoneArray.get(i).setText(finalShowRoomPhones[i]);
                phoneArray.get(i).setVisibility(View.VISIBLE);
                int finalI = i;
                phoneArray.get(i).setOnClickListener(v -> {
                    Uri number = Uri.parse("tel:" + finalShowRoomPhones[finalI]);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                    showRoomDialog.dismiss();
                });
            }
        }
        inflater.findViewById(R.id.tv_cancel).setOnClickListener(v -> showRoomDialog.dismiss());
        showRoomDialog.show();
    }

    @Override
    public void showLocation(ShowRoomInfoModel showRoomInfoModel) {
        double latitude = 0;
        double longitude = 0;
        if (!TextUtils.isEmpty(showRoomInfoModel.getLatitude())) {
            latitude = Double.parseDouble(showRoomInfoModel.getLatitude());
        }
        if (!TextUtils.isEmpty(showRoomInfoModel.getLongitude())) {
            longitude = Double.parseDouble(showRoomInfoModel.getLongitude());
        }
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude;
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(mapIntent);
    }

    @Override
    public void shouldNavigateToChatThread(ChatModel chatModel) {
        new ChatThreadActivityArgs(chatModel.getId(), 0, chatModel.getUseModel().getFirstName(), null)
                .launch(this);
    }

    @Override
    public void updateCars(List<CarViewModel> carModels) {
        if (carModels.size() == 0) {
            noDataFoundTextView.setVisibility(View.VISIBLE);
        } else {
            noDataFoundTextView.setVisibility(View.GONE);
        }
        adapter.updateCars(carModels);
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
    public void setupSkipLogic() {
        showRoomEmailTextView.setVisibility(View.GONE);
        showRoomPhoneTextView.setVisibility(View.GONE);
        visitorEmailTextView.setVisibility(View.GONE);
        phoneTextView.setVisibility(View.GONE);
        mailContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_chat)
    void onChatClicked() {
        presenter.onChatClicked();
    }

    @OnClick(R.id.tv_block)
    void onBlockClicked() {
        getUiUtil().getDialogBuilder(this, R.layout.rate_block_reason)
                .editText(R.id.et_review, text -> this.review = text)
                .clickListener(R.id.tv_submit, (dialog, view) ->
                        presenter.onBlockClicked(review))
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .cancelable(true)
                .build()
                .show();
    }

    @OnClick(R.id.tv_sold)
    void onSoldTextViewClicked() {
        searchEditText.setVisibility(View.GONE);
        availableTextView.setSelected(false);
        updateCars(presenter.getSoldCars());
        soldTextView.setSelected(true);
    }

    @OnClick(R.id.tv_available)
    void onAvailableTextViewClicked() {
        searchEditText.setVisibility(View.VISIBLE);
        updateCars(presenter.getAvailableCars());
        availableTextView.setSelected(true);
        soldTextView.setSelected(false);
    }

    @OnClick({R.id.tv_rate, R.id.rate_layout})
    void onRateClicked() {
        if (presenter.canRateThisUser()) {
            showRatePopup();
        } else {
            getUiUtil().getDialogBuilder(this, R.layout.layout_login_dialog)
                    .text(R.id.tv_warning, getString(R.string.rate_warining))
                    .text(R.id.tv_login, getString(R.string.proceed))
                    .textGravity(R.id.tv_login, Gravity.CENTER)
                    .clickListener(R.id.tv_login, (dialog, view) -> {
                        showRatePopup();
                        dialog.dismiss();
                    })
                    .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                    .background(R.drawable.inset_bottomsheet_background)
                    .gravity(Gravity.CENTER)
                    .cancelable(true)
                    .build()
                    .show();
        }


//        if (presenter.canRateThisUser()) {

//        } else {
//            showWarningMessage(getString(R.string.you_have_rated_person));
//        }
    }

    public void showRatePopup() {
        getUiUtil().getDialogBuilder(this, R.layout.rate_layout)
                .rateChangeListener(R.id.rating_bar, rate -> this.rate = rate)
                .editText(R.id.et_review, text -> this.review = text)
                .clickListener(R.id.tv_submit, (dialog, view) -> presenter.onRateClicked((int) rate, review, dialog))
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .cancelable(true)
                .build()
                .show();
    }

    @OnClick(R.id.tv_show_room_phone)
    void onShowRoomPhoneClicked() {
        presenter.onPhoneClicked();
    }

    @OnClick(R.id.tv_location)
    void onLocationClicked() {
        presenter.onLocationClicked();
    }

    @OnClick(R.id.iv_back)
    void onBackLocation() {
        finish();
    }

    @OnClick(R.id.iv_user_back)
    void onUserBackLocation() {
        finish();
    }

    @OnClick(R.id.et_search)
    void onSearchClicked() {
        basicSearchDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.basic_search_dialog_layout, false)
                .backgroundColor(getResources().getColor(R.color.transparent))
                .cancelable(false)
                .build();
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            basicSearchDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            basicSearchDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        basicSearchDialog.setOnDismissListener(dialog -> {
            presenter.setSelectedModelModel(ModelViewModel.createDefault());
            presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
            presenter.setSelectedBrandModel(BrandsViewModel.createDefault());
        });
        Calendar currentCalender = Calendar.getInstance();
        View inflater = basicSearchDialog.getCustomView();
        MultiSlider yearMultiSlider = inflater.findViewById(R.id.year_slider);
        MultiSlider priceMultiSlider = inflater.findViewById(R.id.price_slider);
        TextView minYearTextView = inflater.findViewById(R.id.tv_year_min);
        TextView maxYearTextView = inflater.findViewById(R.id.tv_year_max);
        TextView minPriceTextView = inflater.findViewById(R.id.tv_price_min);
        TextView maxPriceTextView = inflater.findViewById(R.id.tv_price_max);
        RxEditText categoryEditText = inflater.findViewById(R.id.et_category);
        RxEditText brandEditText = inflater.findViewById(R.id.et_brand);
        RxEditText modelEditText = inflater.findViewById(R.id.et_model);
        EditText priceFromEditText = inflater.findViewById(R.id.et_price_from);
        EditText priceToEditText = inflater.findViewById(R.id.et_price_to);
        EditText yearToEditText = inflater.findViewById(R.id.et_year_to);
        EditText yearFromEditText = inflater.findViewById(R.id.et_year_from);

        disableLongPress(categoryEditText);
        disableLongPress(brandEditText);
        disableLongPress(modelEditText);
        TextView searchTextView = inflater.findViewById(R.id.tv_search);
        TextView cancelTextView = inflater.findViewById(R.id.tv_cancel);
        TextView advancedSearchTextView = inflater.findViewById(R.id.tv_advanced_search);
        advancedSearchTextView.setVisibility(View.GONE);
        RxEditText typeEditText = inflater.findViewById(R.id.et_type);
        SwitchCompat nearBySwitch = inflater.findViewById(R.id.switch_nearby);
        yearMultiSlider.setMax(currentCalender.get(Calendar.YEAR) + 1);
        yearMultiSlider.setMin(1950);
        minYearTextView.setText(String.valueOf(yearMultiSlider.getMin()));
        maxYearTextView.setText(String.valueOf(yearMultiSlider.getMax()));
        yearMultiSlider.getThumb(0).setValue(yearMultiSlider.getMin());
        yearMultiSlider.getThumb(1).setValue(yearMultiSlider.getMax());
        if (presenter.getMaxPrice() != null && presenter.getMinPrice() != null) {

            // init year editText
            yearFromEditText.setText("1950");
            yearToEditText.setText(String.valueOf(toYear));
            yearFromEditText.setOnClickListener(v -> {
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
            });
            yearToEditText.setOnClickListener(v -> {
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
            });

            // init price editText
            priceToEditText.setText(presenter.getMaxPrice() == null ? "1000" : presenter.getMaxPrice());
            priceFromEditText.setText(presenter.getMinPrice());

            priceMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
                minPrice = multiSlider.getThumb(0).getValue();
                maxPrice = multiSlider.getThumb(1).getValue();
                minPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(0).getValue()), 0)));
                maxPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(1).getValue()), 0)));
            });
            priceMultiSlider.setMax(Integer.parseInt(presenter.getMaxPrice() == null ? "1000" : presenter.getMaxPrice()));
            priceMultiSlider.setMin(Integer.parseInt(presenter.getMinPrice()));
            maxPriceTextView.setText(String.valueOf(coolFormat((Integer.parseInt(presenter.getMaxPrice())), 0)));
            minPriceTextView.setText(presenter.getMinPrice());
            priceMultiSlider.getThumb(0).setValue(Integer.parseInt(presenter.getMinPrice()));
            priceMultiSlider.getThumb(1).setValue(Integer.parseInt(presenter.getMaxPrice()));
            if (!isEnglishLang()) {
                minPriceTextView.setGravity(Gravity.RIGHT);
                maxPriceTextView.setGravity(Gravity.LEFT);
            }
        }
        yearMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
            minYearTextView.setText(String.valueOf(multiSlider.getThumb(0).getValue()));
            maxYearTextView.setText(String.valueOf(multiSlider.getThumb(1).getValue()));
        });
        brandEditText.setOnClickListener(v -> {
            materialDialog = new MaterialDialog.Builder(this)
                    .adapter(new BrandAdapter(this, viewModel -> {
                                presenter.setSelectedBrandModel(viewModel);
                                updateBrand(viewModel, brandEditText, modelEditText, categoryEditText);
                                materialDialog.dismiss();
                            }, presenter.getBrandsList()),
                            new LinearLayoutManager(this))
                    .build();
            materialDialog.show();
        });
        modelEditText.setOnClickListener(v -> {
            if (presenter.getSelectedBrandModel() != null) {
                List<ModelViewModel> modelModelList = new ArrayList<>();
                modelModelList.add(ModelViewModel.createDefault());
                modelModelList.addAll(presenter.getSelectedBrandModel().getBrandsModels());
                materialDialog = new MaterialDialog.Builder(this)
                        .adapter(new ModelAdapter(this, viewModel -> {
                                    presenter.setSelectedModelModel(viewModel);
                                    updateModel(viewModel, modelEditText, categoryEditText);
                                    materialDialog.dismiss();
                                }, modelModelList),
                                new LinearLayoutManager(this))
                        .build();
                materialDialog.show();
            } else {
                showWarningMessage(getString(R.string.you_should_select_brand_first));
            }
        });
        categoryEditText.setOnClickListener(v -> {
            if (presenter.getSelectedModelModel() != null) {
                List<CategoryViewModel> categoryModels = new ArrayList<>();
                categoryModels.add(CategoryViewModel.createDefault());
                categoryModels.addAll(presenter.getSelectedModelModel().getCategoryViewModels());
                materialDialog = new MaterialDialog.Builder(this)
                        .adapter(new CategoryAdapter(this, viewModel -> {
                                    presenter.setSelectedCategoryModel(viewModel);
                                    updateCategory(viewModel, categoryEditText);
                                    materialDialog.dismiss();
                                }, categoryModels),
                                new LinearLayoutManager(this))
                        .build();
                materialDialog.show();
            } else {
                showWarningMessage(getString(R.string.you_should_select_model_first));
            }
        });
        typeEditText.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            getMenuInflater().inflate(R.menu.menu_gender_show_all, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> onSellerFlowMenuItemClicked(item, typeEditText));
            popupMenu.show();
        });
        searchTextView.setOnClickListener(v -> {
            int brandId = 0, modelId = 0, categoryId = 0;
            if (presenter.getSelectedBrandModel() != null) {
                brandId = presenter.getSelectedBrandModel().getId();
            }
            if (presenter.getSelectedModelModel() != null) {
                modelId = presenter.getSelectedModelModel().getId();
            }
            if (presenter.getSelectedCategoryModel() != null) {
                categoryId = presenter.getSelectedCategoryModel().getId();
            }

            minPrice = 0;
            maxPrice = Integer.parseInt(presenter.getMaxPrice() == null ? "100000" : presenter.getMaxPrice());
            if (!TextUtils.isEmpty(priceFromEditText.getText().toString())) {
                minPrice = Integer.parseInt(priceFromEditText.getText().toString());
            }
            if (!TextUtils.isEmpty(priceToEditText.getText().toString())) {
                maxPrice = Integer.parseInt(priceToEditText.getText().toString());
            }
            SearchRequestModel.Builder builder = new SearchRequestModel.Builder()
                    .categoryId(categoryId)
                    .brandId(brandId)
                    .modelId(modelId)
                    .priceFrom(String.valueOf(minPrice))
                    .price_to(String.valueOf(maxPrice))
                    .yearFrom(yearFromEditText.getText().toString())
                    .yearTo(yearToEditText.getText().toString())
                    .gender(gender);
//            if (nearBySwitch.isChecked()) {
//                if (homeFragment != null) {
//                    if (homeFragment.getCurrentLocation() != null)
//                        builder.longitude(String.valueOf(homeFragment.getCurrentLocation().getLongitude()))
//                                .latitude(String.valueOf(homeFragment.getCurrentLocation().getLatitude()));
//                }
//            }
            SearchRequestModel searchRequestModel = builder.build();
            basicSearchDialog.dismiss();

            presenter.onSearchClicked(searchRequestModel);
//            new SearchActivityArgs(searchRequestModel, true)
//                    .launch(this);
        });
        advancedSearchTextView.setOnClickListener(v -> {
            if (presenter.getMinPrice() != null) {
                new AdvancedSearchActivityArgs(presenter.getMinPrice(), presenter.getMaxPrice())
                        .launch(this);
            }
            basicSearchDialog.dismiss();
        });
        cancelTextView.setOnClickListener(v -> basicSearchDialog.dismiss());
        basicSearchDialog.show();
    }

    private boolean onSellerFlowMenuItemClicked(MenuItem menuItem, RxEditText typeEditText) {
        if (menuItem.getItemId() == R.id.male) {
            typeEditText.setText(getString(R.string.male));
            gender = Gender.MALE;
            return true;
        } else if (menuItem.getItemId() == R.id.female) {
            typeEditText.setText(getString(R.string.female));
            gender = Gender.FEMALE;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            typeEditText.setText(getString(R.string.show_all));
            gender = Gender.SHOW_ALL;
            return true;
        }
        return true;
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

    private void updateBrand(BrandsViewModel viewModel, RxEditText brandEditText, RxEditText modelEditText, RxEditText categoryEditText) {
        brandEditText.setText(viewModel.getName());
        if (viewModel.getBrandsModels().size() > 0) {
            updateModel(viewModel.getBrandsModels().get(0), modelEditText, categoryEditText);
            presenter.setSelectedModelModel(viewModel.getBrandsModels().get(0));
        }
    }

    private void updateModel(ModelViewModel viewModel, RxEditText modelEditText, RxEditText categoryEditText) {
        modelEditText.setText(viewModel.getName());
        if (viewModel.getCategoryViewModels().size() > 0) {
            updateCategory(viewModel.getCategoryViewModels().get(0), categoryEditText);
            presenter.setSelectedCategoryModel(viewModel.getCategoryViewModels().get(0));
        }
    }

    private void updateCategory(CategoryViewModel viewModel, RxEditText categoryEditText) {
        categoryEditText.setText(viewModel.getName());
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    private String changeTimeEn(String original) {
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
                .replaceAll("٩", "9")
                .replace("م", "PM")
                .replace("ص", "AM")
                .replace("am", "AM")
                .replace("pm", "PM")
                .replace("Am", "AM")
                .replace("Pm", "PM");
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

    private String changeTimeAr(String original) {
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
                .replaceAll("٩", "9")
                .replace("am", "ص")
                .replace("pm", "م")
                .replace("Am", "ص")
                .replace("AM", "ص")
                .replace("Pm", "م")
                .replace("PM", "م");
    }

    private static String coolFormat(int n, int iteration) {
        int d = (n / 100) / 10;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration]) : coolFormat(d, iteration + 1));
    }

}