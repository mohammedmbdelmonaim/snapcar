package com.intcore.snapcar.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoModel;
import com.intcore.snapcar.ui.blocklist.BlockListActivity;
import com.intcore.snapcar.ui.coupon.CouponActivity;
import com.intcore.snapcar.ui.editUser.EditUserActivity;
import com.intcore.snapcar.ui.editshowroom.EditShowRoomActivity;
import com.intcore.snapcar.ui.feedback.FeedbackActivity;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.mycars.MyCarsActivity;
import com.intcore.snapcar.ui.myinterstes.MyInterestActivity;
import com.intcore.snapcar.ui.notificationsetting.NotificationSettingActivity;
import com.intcore.snapcar.ui.paymenthistory.PaymentHistoryActivity;
import com.intcore.snapcar.ui.settings.SettingsActivity;
import com.intcore.snapcar.ui.verificationletter.VerificationLetterActivity;
import com.intcore.snapcar.util.UserManager;
import com.jaeger.library.StatusBarUtil;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import timber.log.Timber;

import static com.intcore.snapcar.store.api.ApiUtils.BASE_URL;

@FragmentScope
public class ProfileFragment extends Fragment implements ProfileScreen {

    public DefaultUserModel defaultUserModelSingle;
    @Inject
    ProfilePresenter presenter;
    @Inject
    UiUtil uiUtil;
    @BindView(R.id.tv_user_name)
    TextView name;
    @BindView(R.id.image_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.image_cover)
    ImageView userCover;
    @BindView(R.id.tv_user_email)
    TextView email;
    @BindView(R.id.tv_phone)
    TextView phone;
    @BindView(R.id.tv_address)
    TextView userAddressTextView;
    @BindView(R.id.user_header)
    ConstraintLayout userHeaderContainer;
    @BindView(R.id.show_room_header)
    ConstraintLayout showRoomHeaderContainer;
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
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.tv_rate)
    TextView rateTextView;
    @BindView(R.id.tv_working_hour)
    TextView workingHourTextView;
    @BindView(R.id.tv_verified)
    TextView verifiedTextView;
    @BindView(R.id.tv_car_number)
    TextView carNumberTextView;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_requuest_vip)
    TextView requestVIPTextView;
    @BindView(R.id.tv_my_cars)
    TextView myCarsTextView;
    @BindView(R.id.tv_my_interest)
    TextView myInterestTextView;
    @BindView(R.id.tv_blocked_members)
    TextView blockedMemberTextView;
    @BindView(R.id.tv_payment_history)
    TextView paymentHistoryTextView;
    @BindView(R.id.tv_discounts)
    TextView discountTextView;
    @BindView(R.id.tv_deal_with)
    TextView dealWithTextView;
    @BindView(R.id.tv_share_app)
    TextView shareAppTextView;
    @BindView(R.id.tv_feedback)
    TextView feedbackTextView;
    @BindView(R.id.tv_notification_setting)
    TextView notificationSettingTextView;
    @BindView(R.id.tv_setting)
    TextView settingTextView;
    @BindView(R.id.favorite_divider)
    View favoriteDivider;
    @BindView(R.id.car_divider)
    View carDivider;
    @BindView(R.id.interest_divider)
    View interestDivider;
    @BindView(R.id.block_divider)
    View blockDivider;
    @BindView(R.id.payment_divider)
    View paymentDivider;
    @BindView(R.id.discount_divider)
    View discountDivider;
    @BindView(R.id.deadl_divider)
    View deadlDivider;
    @BindView(R.id.share_divider)
    View shareDivider;
    @BindView(R.id.feedback_divider)
    View feedbackDivider;
    @BindView(R.id.notification_setting_divider)
    View notificationSettingDivider;
    @BindView(R.id.setting_divider)
    View settingDivider;
    @BindView(R.id.tv_deal_with_type)
    TextView dealingWith;
    @BindView(R.id.tv_join_hotzone)
    TextView joinHotZoneTextView;
    @BindView(R.id.hotzone_divider)
    View hotzoneDivider;
    @BindView(R.id.tv_verified_letter)
    TextView verifiedLetterTextView;
    @BindView(R.id.tv_logout)
    TextView logoutBtn;
    @BindView(R.id.verified_letter_divider)
    View verifiedLetterDivider;
    @BindView(R.id.deal_with_container)
    RelativeLayout dealWithContainer;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.iv_user_back)
    ImageView userBackImageView;
    @BindView(R.id.im_request_vip)
    ImageView requestVipImage;
    @BindView(R.id.request_vip_layout)
    LinearLayout requestVipLayout;
    @BindView(R.id.im_hotzone_ic)
    ImageView hotzoneIcon;
    @BindView(R.id.im_latter_ic)
    ImageView letterIcon;
    @BindView(R.id.request_hotzone_layout)
    LinearLayout requestHotzoneLayout;
    @BindView(R.id.request_letter_layout)
    LinearLayout requestLetterLayout;
    @BindView(R.id.user_option_layout)
    LinearLayout userOptionLayout;
    @BindView(R.id.tv_user_car)
    TextView carNumberUserTextView;
    @BindView(R.id.image_edit)
    ImageView editImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    UserManager userManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIHostComponentProvider) {
            UIHostComponentProvider provider = ((UIHostComponentProvider) context);
            if (provider.getComponent() instanceof ActivityComponent) {
                ((ActivityComponent) provider.getComponent())
                        .plus(new FragmentModule(this))
                        .inject(this);
            } else {
                throw new IllegalStateException("Component must be " + ActivityComponent.class.getName());
            }
        } else {
            throw new AssertionError("host context must implement " + UIHostComponentProvider.class.getName());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        presenter.onResume();
        if (getActivity() instanceof HostActivity) {
            ((HostActivity) getActivity()).setHighlightedItem(HomeItem.ACCOUNT);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return rootView;
    }

    @Override
    public void updateUi(DefaultUserModel defaultUserModel) {
        if (defaultUserModel.getActivation().contentEquals(UserManager.PENDING)) {
            userManager.sessionManager().logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
        this.defaultUserModelSingle = defaultUserModel;
        userBackImageView.setVisibility(View.GONE);
        backImageView.setVisibility(View.GONE);
        userOptionLayout.setVisibility(View.VISIBLE);
        if (defaultUserModel.getType() == UserType.USER) {
            bindUserData(defaultUserModel);
        } else {
            bindShowRoomData(defaultUserModel);
        }
//        if (defaultUserModel.getType() == UserType.USER)
//            phone.setVisibility(View.GONE);
    }

    private void bindShowRoomData(DefaultUserModel defaultUserModel) {
        showRoomHeaderContainer.setVisibility(View.VISIBLE);
        verifiedLetterTextView.setVisibility(View.VISIBLE);
        verifiedLetterDivider.setVisibility(View.VISIBLE);
        blockedMemberTextView.setVisibility(View.VISIBLE);
        joinHotZoneTextView.setVisibility(View.VISIBLE);
        requestLetterLayout.setVisibility(View.VISIBLE);
        requestHotzoneLayout.setVisibility(View.VISIBLE);
        requestVipLayout.setVisibility(View.VISIBLE);
        blockDivider.setVisibility(View.VISIBLE);
        dealingWith.setVisibility(View.VISIBLE);
        carDivider.setVisibility(View.GONE);
        myCarsTextView.setVisibility(View.GONE);
        deadlDivider.setVisibility(View.VISIBLE);
        dealWithTextView.setVisibility(View.VISIBLE);
        joinHotZoneTextView.setVisibility(View.VISIBLE);
        dealWithTextView.setVisibility(View.VISIBLE);
        hotzoneDivider.setVisibility(View.VISIBLE);
        deadlDivider.setVisibility(View.VISIBLE);
        rating.setRating(defaultUserModel.getTotalRate());
        if (defaultUserModel.getEmail() != null) {
            showRoomEmailTextView.setText(defaultUserModel.getEmail());
            showRoomEmailTextView.setVisibility(View.VISIBLE);
        } else {
            showRoomEmailTextView.setVisibility(View.GONE);
        }
        showRoomNameTextView.setText(defaultUserModel.getFirstName());
        rateTextView.setText(String.valueOf(defaultUserModel.getTotalRate()));
        if (defaultUserModel.getIsVerified() == 1) {
            verifiedTextView.setText(getString(R.string.verified));
            letterIcon.setImageResource(R.drawable.receipt_red);
            verifiedTextView.setVisibility(View.VISIBLE);
        }
        if (defaultUserModel.getAvailableCars() != null) {
            carNumberTextView.setText(getString(R.string.car_available_for_sell).concat(defaultUserModel.getAvailableCars())
                    .concat(" / ").concat(defaultUserModel.getCarLimitNumber() == null ? String.valueOf("0") : defaultUserModel.getCarLimitNumber()));
        } else {
            carNumberTextView.setText(getString(R.string.car_available_for_sell).concat("0").concat(" / ")
                    .concat(defaultUserModel.getCarLimitNumber()));
        }
        if (defaultUserModel.getHasHotZone()) {
            hotzoneIcon.setImageResource(R.drawable.grade_red);
        }
        showRoomPhoneTextView.setText(Html.fromHtml("<u>".concat(defaultUserModel.getPhone()).concat("</u>")), TextView.BufferType.SPANNABLE);
        if (defaultUserModel.getShowRoomInfoModel() != null) {
            if (defaultUserModel.getShowRoomInfoModel().getPremium()) {
                requestVipImage.setImageResource(R.drawable.vip_icon);
            } else {
                requestVipImage.setImageResource(R.drawable.star_ic);
            }
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTime()) && !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTime())) {
                if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo()) && !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo())) {
                    String workingTimes;
                    if (isEnglishLang()) {
                        workingTimes = getString(R.string.working_hours).concat(" <br/> ( <font color=\"#ce3b3b\">"
                                .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font> ").concat(getString(R.string.to)).concat(" <font color=\"#ce3b3b\"> ")
                                        .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> ) / ( <font color=\"#ce3b3b\">"
                                                .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo())
                                                        .concat("</font>").concat(getString(R.string.to)).concat(" <font color=\"#ce3b3b\"> ")
                                                        .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo()))))
                                        .concat(" </font> )")));
                    } else {
                        workingTimes = getString(R.string.working_hours).concat(" <br/> ( <font color=\"#ce3b3b\">"
                                .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font> ").concat(getString(R.string.to)).concat(" <font color =\"#ce3b3b\"> ")
                                        .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> ) / ( <font color=\"#ce3b3b\">"
                                                .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo())
                                                        .concat("</font> ").concat(getString(R.string.to)).concat(" <font color=\"#ce3b3b\"> ")
                                                        .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo()))))
                                        .concat(" </font> )")));
                    }
                    workingHourTextView.setText(Html.fromHtml(workingTimes), TextView.BufferType.SPANNABLE);
                } else {
                    String workingTimes;
                    if (isEnglishLang()) {
                        workingTimes = getString(R.string.working_hours).concat(" <br/> " + "( <font color=\"#ce3b3b\">"
                                .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font> ").concat(getString(R.string.to))
                                        .concat("<font color=\"#ce3b3b\"> ")
                                        .concat(changeTimeEn(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> )")));
                    } else {
                        workingTimes = getString(R.string.working_hours).concat(" <br/> " + "( <font color=\"#ce3b3b\">"
                                .concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getOpenTime())
                                        .concat("</font> ").concat(getString(R.string.to)).concat("  <font color=\"#ce3b3b\"> ").concat(changeTimeAr(defaultUserModel.getShowRoomInfoModel().getClosedTime()))
                                        .concat("</font> )")));
                    }
                    workingHourTextView.setText(Html.fromHtml(workingTimes), TextView.BufferType.SPANNABLE);
                }
            } else {
                workingHourTextView.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getVerifyLatter())) {
                verifiedTextView.setText(R.string.unverified);
            }
            if (defaultUserModel.getShowRoomInfoModel().getDealingWith() != null) {
                switch (defaultUserModel.getShowRoomInfoModel().getDealingWith()) {
                    case "1":
                        dealingWith.setText(getString(R.string.all_carss));
                        break;
                    case "2":
                        dealingWith.setText(getString(R.string.old_carss));
                        break;
                    case "3":
                        dealingWith.setText(getString(R.string.new_carss));
                        break;
                    default:
                        dealingWith.setText(getString(R.string.new_carss));
                        break;
                }
            } else {
                dealingWith.setText(getString(R.string.all_carss));
            }
        } else {
            verifiedTextView.setText(R.string.unverified);
            workingHourTextView.setVisibility(View.GONE);
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
        showRoomAvatar.setImageResource(R.drawable.default_img);
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                .placeholder(R.drawable.default_img)
                .centerCrop()
                .into(showRoomAvatar);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.show_room_header_color));
        if (!isEnglishLang()) {
            showRoomEmailTextView.setGravity(Gravity.RIGHT);
            showRoomNameTextView.setGravity(Gravity.RIGHT);
        } else {
            showRoomEmailTextView.setGravity(Gravity.LEFT);
            showRoomNameTextView.setGravity(Gravity.LEFT);
        }
    }

    private void bindUserData(DefaultUserModel defaultUserModel) {
        userHeaderContainer.setVisibility(View.VISIBLE);
        mailContainer.setVisibility(View.VISIBLE);
        if (defaultUserModel.getEmail() != null) {
            email.setText(defaultUserModel.getEmail());
        } else {
            email.setVisibility(View.GONE);
        }
        name.setText(defaultUserModel.getFirstName());
        phone.setText(defaultUserModel.getPhone());

        myCarsTextView.setVisibility(View.VISIBLE);
        dealingWith.setVisibility(View.GONE);
        deadlDivider.setVisibility(View.GONE);
        hotzoneDivider.setVisibility(View.GONE);
        favoriteDivider.setVisibility(View.GONE);
        dealWithTextView.setVisibility(View.GONE);
        requestVipLayout.setVisibility(View.GONE);
        dealWithContainer.setVisibility(View.GONE);
        requestLetterLayout.setVisibility(View.GONE);
        joinHotZoneTextView.setVisibility(View.GONE);
        requestHotzoneLayout.setVisibility(View.GONE);
        verifiedLetterDivider.setVisibility(View.GONE);
        verifiedLetterTextView.setVisibility(View.GONE);
        if (defaultUserModel.getCountryModel() != null && defaultUserModel.getCityModel() != null) {
            if (Locale.getDefault().getLanguage().contentEquals("en")) {
                userAddressTextView.setText(defaultUserModel.getCountryModel().getNameEn()
                        .concat(" - ")
                        .concat(defaultUserModel.getCityModel().getNameEn()));
            } else {
                userAddressTextView.setText(defaultUserModel.getCountryModel().getNameAr()
                        .concat(" - ")
                        .concat(defaultUserModel.getCityModel().getNameAr()));
            }
        } else {
            userAddressTextView.setVisibility(View.GONE);
        }
        if (defaultUserModel.getAvailableCars() != null && defaultUserModel.getCarLimitNumber() != null) {
            carNumberUserTextView.setText(getString(R.string.car_available_for_sell)
                    .concat(defaultUserModel.getAvailableCars())
                    .concat(" / ")
                    .concat(defaultUserModel.getCarLimitNumber()));
        } else if (defaultUserModel.getAvailableCars() != null) {
            carNumberUserTextView.setText(getString(R.string.car_available_for_sell)
                    .concat(defaultUserModel.getAvailableCars()));
        } else {
            carNumberUserTextView.setVisibility(View.GONE);
        }
        try {
            userAvatar.setImageResource(R.drawable.default_img);
            userCover.setImageResource(R.drawable.default_img);
            Glide.with(this)
                    .load(BASE_URL.concat(defaultUserModel.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.default_img)
                    .into(userAvatar);

            Glide.with(this)
                    .load(BASE_URL.concat(defaultUserModel.getImageUrl()))
                    .centerCrop()
                    .placeholder(R.drawable.default_img)
                    .into(userCover);
        } catch (Exception e) {
            Timber.e(e);
        }
        StatusBarUtil.setTransparent(getActivity());
    }

    @Override
    public void showLocation(ShowRoomInfoModel showRoomInfoModel) {
        double latitude;
        double longitude = 0;
        if (!TextUtils.isEmpty(showRoomInfoModel.getLatitude())) {
            latitude = Double.parseDouble(showRoomInfoModel.getLatitude());
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
        } else {
            showWarningMessage(getString(R.string.you_must_set_location_first));
            startActivity(new Intent(getActivity(), EditShowRoomActivity.class));
        }
    }

    @Override
    public void requestVipSent(ResponseBody responseBody) {

        showSuccessMessage(getString(R.string.request_vip_sent));

    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void setGuestScreen() {
        userHeaderContainer.setVisibility(View.VISIBLE);
        mailContainer.setVisibility(View.VISIBLE);
        name.setText(getString(R.string.user_name_view_car));
        editImageView.setVisibility(View.GONE);
        verifiedLetterTextView.setVisibility(View.GONE);
        carNumberUserTextView.setVisibility(View.GONE);
        verifiedLetterDivider.setVisibility(View.GONE);
        joinHotZoneTextView.setVisibility(View.GONE);
        dealWithContainer.setVisibility(View.GONE);
        dealWithTextView.setVisibility(View.GONE);
        hotzoneDivider.setVisibility(View.GONE);
        deadlDivider.setVisibility(View.GONE);
        dealingWith.setVisibility(View.GONE);
        requestVipLayout.setVisibility(View.GONE);
        favoriteDivider.setVisibility(View.GONE);
        requestLetterLayout.setVisibility(View.GONE);
        requestHotzoneLayout.setVisibility(View.GONE);
        logoutBtn.setText(getString(R.string.log_in));
    }

    @Override
    public void setDefaultUi(DefaultUserModel currentUser) {
        userAvatar.setImageResource(R.drawable.default_img);
        userCover.setImageResource(R.drawable.default_img);
        showRoomAvatar.setImageResource(R.drawable.default_img);
        switch (currentUser.getType()) {
            case UserType.SHOW_ROOM:
                showRoomHeaderContainer.setVisibility(View.VISIBLE);
                bindShowRoomData(currentUser);
                break;
            case UserType.USER:
                bindUserData(currentUser);
                break;
        }
    }

    @Override
    public void showDefaultMessage(String message) {

    }

    @Override
    public void showAnnouncementMessage(String message) {

    }

    @Override
    public void showLoadingAnimation() {
        if (isVisible())
            uiUtil.getProgressDialog(getString(R.string.please_wait))
                    .show();
    }

    @Override
    public void hideLoadingAnimation() {
        if (isVisible())
            uiUtil.getProgressDialog()
                    .hide();
    }

    @OnClick(R.id.iv_question)
    void onQuestionClicked() {
        uiUtil.getDialogBuilder(getActivity(), R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.jion_hot_zone_info))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public ResourcesUtil getResourcesUtil() {
        return null;
    }

    @Override
    public PermissionUtil getPermissionUtil() {
        return null;
    }

    @OnClick({R.id.image_edit})
    public void onEditClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getContext(), EditUserActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity()))
                .getSnackBarContainer(), message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity()))
                .getSnackBarContainer(), message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity()))
                .getSnackBarContainer(), message).show();
    }

    @OnClick(R.id.tv_logout)
    void onLogoutClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            uiUtil.getDialogBuilder(getActivity(), R.layout.layout_login_dialog)
                    .text(R.id.tv_warning, getString(R.string.sure_want_logout))
                    .text(R.id.tv_login, getString(R.string.okk))
                    .textGravity(R.id.tv_login, Gravity.CENTER)
                    .clickListener(R.id.tv_login, (dialog, view) -> {
                        presenter.onLogoutClicked();
                        dialog.dismiss();
                    })
                    .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                    .background(R.drawable.inset_bottomsheet_background)
                    .gravity(Gravity.CENTER)
                    .cancelable(true)
                    .build()
                    .show();
        } else {
            presenter.onLogoutClicked();
        }
    }

    @OnClick(R.id.tv_location)
    void onViewClicked() {
    }

    @OnClick(R.id.iv_edit)
    void onEditShwRoomClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getContext(), EditShowRoomActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_location)
    void onLocationClicked() {
        presenter.onLocationClicked();
    }

    @OnClick(R.id.tv_blocked_members)
    void onBlockedMembersClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), BlockListActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_my_interest)
    void onMyInterestsClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), MyInterestActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_discounts)
    void onDiscountClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), CouponActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_my_cars)
    void onMyCarsClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getContext(), MyCarsActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_car_number)
    void onAvailableCarsClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getContext(), MyCarsActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.request_vip_layout)
    void onRequestVipClicked() {
        if (userManager.getCurrentUser().getShowRoomInfoModel().getPremium()) {
            showWarningMessage(getString(R.string.you_are_vip_showroom));
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_request_vip_2)
                    .text(R.id.tv_sewar, getString(R.string.request_vip_dialog_msgg))
                    .clickListener(R.id.btn_reject, (dialog, view) -> {
                        dialog.dismiss();
                    })
                    .clickListener(R.id.btn_accept, (dialog, view) -> {
                        presenter.requestVip();
                        dialog.dismiss();
                    })
                    .background(R.drawable.inset_bottomsheet_background)
                    .gravity(Gravity.CENTER)
                    .build()
                    .show();
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_ads)
    void onRequestAdClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_request_vip_2)
                    .text(R.id.tv_sewar, getString(R.string.request_vip_dialog_msg))
                    .textGravity(R.id.tv_sewar, Gravity.CENTER)
                    .clickListener(R.id.btn_reject, (dialog, view) -> {
                        dialog.dismiss();
                    })
                    .clickListener(R.id.btn_accept, (dialog, view) -> {
                        presenter.requestAd();
                        dialog.dismiss();
                    })
                    .background(R.drawable.inset_bottomsheet_background)
                    .gravity(Gravity.CENTER)
                    .build()
                    .show();
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_payment_history)
    void onPaymentHistoryClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), PaymentHistoryActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.tv_share_app)
    public void shareApp(){
        String msg = "Sell your car or find the car you want through SnapCars Application!" +
                "\n\nAndroid: https://play.google.com/store/apps/details?id=com.intcore.snapcar"
                +"\n\niOS: https://apps.apple.com/us/app/snapcars-سناب-كارز/id1434355539?ls=1";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(intent);
    }

    @OnClick({R.id.tv_feedback})
    void onFeedbackClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), FeedbackActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.request_letter_layout)
    void onVerifiedLetterClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), VerificationLetterActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @OnClick(R.id.request_hotzone_layout)
    void onJoinHotZoneClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            presenter.requestHotZone();
        } else {
            showLoginFirstPopup();
        }
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @OnClick(R.id.tv_setting)
    void onSettingsClicked() {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
    }

    @OnClick(R.id.tv_notification_setting)
    void onNotificationSettingClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(getActivity(), NotificationSettingActivity.class));
        } else {
            showLoginFirstPopup();
        }
    }

    @Override
    public void shouldNavigateToLogin() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void showLoginFirstPopup() {
        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.layout_login_dialog)
                .clickListener(R.id.tv_login, (dialog, view) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    dialog.dismiss();
                })
                .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .transparentBackground(true)
                .cancelable(true)
                .build()
                .show();
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(getActivity()).equals("en");
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
}