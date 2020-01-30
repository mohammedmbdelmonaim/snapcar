package com.intcore.snapcar.ui.editshowroom;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.editshowroomlocation.EditShowRoomLocationActivity;
import com.intcore.snapcar.ui.editshowroomphone.EditShowroomPhoneActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.widget.rxedittext.email.EmailEditText;
import com.intcore.snapcar.core.widget.rxedittext.name.NameEditText;

import java.io.File;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
public class EditShowRoomActivity extends BaseActivity implements EditShowRoomScreen {

    @BindView(R.id.ed_name)
    NameEditText nameEditText;
    @BindView(R.id.ed_email)
    EmailEditText emailEditText;
    @BindView(R.id.input_layout_email)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.input_layout_name)
    TextInputLayout nameTextInputLayout;
    @BindView(R.id.image_avatar)
    CircleImageView userImage;
    @BindView(R.id.ed_from)
    TextView fromEditText;
    @BindView(R.id.ed_to)
    TextView toEditText;
    @BindView(R.id.ed_from2)
    TextView fromEditTextTwo;
    @BindView(R.id.ed_to2)
    TextView toEditTextTwo;
    @BindView(R.id.tv_dealing)
    TextView dealingWith;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.secondTimeLayout)
    LinearLayout secondTimeLayout;
    @BindView(R.id.timeTowDivider)
    LinearLayout timeTowDivider;
    @BindView(R.id.addTimeBtn)
    ImageView addNewBtn;
    @Inject
    EditShowRoomPresenter presenter;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.location_cell)
    ImageView locationCell;
    @BindView(R.id.number_cell)
    ImageView numberCell;
    private String imageUrl;
    private String apiToken;
    private String dealingValue;
    private String fromTwo, toTwo;
    private int fromTime = 0, toTime, fromTimeTwo, toTimeTwo;
    private String fromTimee = "";
    private String toTimee = "";
    private String fromTimeTwoo = "";
    private String toTimeTwoo = "";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
        locationCell.setImageDrawable(icons.getDrawable(1));
        numberCell.setImageDrawable(icons.getDrawable(1));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_show_room;
    }

    @Override
    public void showNameErrorMessage(String nameErrorMsg) {
        nameEditText.setError(nameErrorMsg);
    }

    @Override
    public void showEmailErrorMessage(String emailErrorMsg) {
        emailEditText.setError(emailErrorMsg);
    }

    @Override
    public void setNewImagePath(String path) {
        imageUrl = path;
    }

    @Override
    public void setSelectedImage(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        userImage.setImageBitmap(bitmap);
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
    public void updateUi(DefaultUserModel defaultUserModel) {
        nameEditText.setText(defaultUserModel.getFirstName());
        emailEditText.setText(defaultUserModel.getEmail());
        if (defaultUserModel.getShowRoomInfoModel() != null) {
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTime())) {
                fromEditText.setText(defaultUserModel.getShowRoomInfoModel().getOpenTime());
                toEditText.setText(defaultUserModel.getShowRoomInfoModel().getClosedTime());
            }
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo()) || !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo())) {
                fromEditTextTwo.setText(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo());
                toEditTextTwo.setText(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo());
            }
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTime())) {
                fromTimee = defaultUserModel.getShowRoomInfoModel().getOpenTime();
                toTimee = defaultUserModel.getShowRoomInfoModel().getClosedTime();
            }
            if (!TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo()) || !TextUtils.isEmpty(defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo())) {
                fromTimeTwoo = defaultUserModel.getShowRoomInfoModel().getOpenTimeTwo();
                toTimeTwoo = defaultUserModel.getShowRoomInfoModel().getClosedTimeTwo();
            } else {
                addNewBtn.setVisibility(View.VISIBLE);
                secondTimeLayout.setVisibility(View.GONE);
                timeTowDivider.setVisibility(View.GONE);
            }

            if (defaultUserModel.getShowRoomInfoModel().getDealingWith() != null) {
                switch (defaultUserModel.getShowRoomInfoModel().getDealingWith()) {
                    case "1":
                        dealingWith.setText(getString(R.string.all_carss));
                        dealingValue = "1";
                        break;
                    case "2":
                        dealingWith.setText(getString(R.string.old_carss));
                        dealingValue = "2";
                        break;
                    case "3":
                        dealingWith.setText(getString(R.string.new_carss));
                        dealingValue = "3";
                        break;
                    default:
                        dealingWith.setText(getString(R.string.all_carss));
                        dealingValue = "1";
                        break;
                }
            } else {
                dealingWith.setText(getString(R.string.all_carss));
                dealingValue = "1";
            }
        }
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModel.getImageUrl()))
                .centerCrop()
                .into(userImage);
        apiToken = defaultUserModel.getApiToken();
        if (!isEnglishLang()) {
            nameEditText.setGravity(Gravity.RIGHT);
            emailEditText.setGravity(Gravity.RIGHT);
        } else {
            nameEditText.setGravity(Gravity.LEFT);
            emailEditText.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public void setupEditText() {
        nameEditText.setValidityListener(presenter::onAfterNameChange);
//        emailEditText.setValidityListener(presenter::onAfterEmailChange);
    }

    @Override
    public void onUpdatedSuccessfully(DefaultUserModel defaultUserModel) {
        showSuccessMessage(getString(R.string.updated));
        new Handler().postDelayed(this::finish, 1500);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.save_btn)
    void onSaveClicked() {
        if (fromTimee.equals("") || toTimee.equals("")) {
            showErrorMessage(getString(R.string.specify_wrking_hours));
        } else if ((fromTimeTwoo.equals("") && !toTimeTwoo.equals("")) ||
                (!fromTimeTwoo.equals("") && toTimeTwoo.equals(""))) {
            showErrorMessage(getString(R.string.should_specify_second_working));
        } else {
            presenter.saveIsClicked(apiToken,
                    nameEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    fromTimee,
                    toTimee,
                    fromTimeTwoo,
                    toTimeTwoo,
                    imageUrl,
                    dealingValue);
        }
    }

    @OnClick(R.id.image_avatar)
    void onImageClicked(View v) {
        presenter.openGallery(RxPaparazzo.single(this));
    }

    @OnClick({R.id.ed_from, R.id.ed_to, R.id.location_btn, R.id.ed_from2, R.id.ed_to2})
    public void onTimeClicked(View v) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        switch (v.getId()) {
            case R.id.ed_from:
                mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                    fromEditText.setText(getTime(selectedHour, selectedMinute));
                    fromTimee = getTime(selectedHour, selectedMinute);
                }, hour, minute, false);
                mTimePicker.setTitle(getString(R.string.from));
                mTimePicker.show();
                break;
            case R.id.ed_to:
                mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                    toTimee = getTime(selectedHour, selectedMinute); //->
                    toEditText.setText(getTime(selectedHour, selectedMinute));
                }, hour, minute, false);
                mTimePicker.setTitle(getString(R.string.to));
                mTimePicker.show();
                break;
            case R.id.ed_from2:
                mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                    fromTimeTwoo = getTime(selectedHour, selectedMinute); //->
                    fromEditTextTwo.setText(getTime(selectedHour, selectedMinute));
                }, hour, minute, false);
                mTimePicker.setTitle(getString(R.string.from));
                mTimePicker.show();
                break;
            case R.id.ed_to2:
                mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                    toTimeTwoo = getTime(selectedHour, selectedMinute); //->
                    toEditTextTwo.setText(getTime(selectedHour, selectedMinute));
                }, hour, minute, false);
                mTimePicker.setTitle(getString(R.string.to));
                mTimePicker.show();
                break;
            case R.id.location_btn:
                startActivity(new Intent(this, EditShowRoomLocationActivity.class));
                break;
        }
    }

    @OnClick(R.id.dealing_with)
    void onUserTypeClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_showroom_dealing, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onUserTypeFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onUserTypeFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.old_c) {
            dealingWith.setText(getString(R.string.old_carss));
            dealingValue = "2";
            return true;
        } else if (menuItem.getItemId() == R.id.new_c) {
            dealingWith.setText(getString(R.string.new_carss));
            dealingValue = "3";
            return true;
        } else if (menuItem.getItemId() == R.id.all_c) {
            dealingWith.setText(getString(R.string.all_carss));
            dealingValue = "1";
            return true;
        }
        return true;
    }

    @OnClick({R.id.edit_phone_btn})
    public void onPhonesEditClicked(View v) {
        startActivity(new Intent(this, EditShowroomPhoneActivity.class));
    }

    @OnClick({R.id.back_btn})
    public void onBackClicked(View v) {
        finish();
    }

    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    @OnClick(R.id.time_two_remove)
    void onRemoveTimeClicked() {
        toTimeTwoo = "";
        fromTimeTwoo = "";
        timeTowDivider.setVisibility(View.GONE);
        secondTimeLayout.setVisibility(View.GONE);
        addNewBtn.setVisibility(View.VISIBLE);
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    @OnClick(R.id.addTimeBtn)
    void onAddTimeClicked() {
        timeTowDivider.setVisibility(View.VISIBLE);
        secondTimeLayout.setVisibility(View.VISIBLE);
        addNewBtn.setVisibility(View.GONE);
    }
}