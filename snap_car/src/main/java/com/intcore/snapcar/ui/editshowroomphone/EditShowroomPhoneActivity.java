package com.intcore.snapcar.ui.editshowroomphone;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.editphoneactivition.EditPhoneActivationActivityArgs;
import com.intcore.snapcar.ui.login.CountryAdapter;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.widget.rxedittext.phone.PhoneEditText;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditShowroomPhoneActivity extends BaseActivity implements EditShowRoomPhoneScreen {

    @BindView(R.id.ed_number)
    PhoneEditText mainEditText;
    @BindView(R.id.number_1)
    EditText number1;
    @BindView(R.id.number_2)
    EditText number2;
    @BindView(R.id.number_3)
    EditText number3;
    @BindView(R.id.number_4)
    EditText number4;
    @BindView(R.id.number_5)
    EditText number5;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.number_6)
    EditText number6;
    @BindView(R.id.country_code)
    TextView countryCodeTextView;
    @BindView(R.id.iv_country_code)
    RoundedImageView countryCodeImageView;
    @BindView(R.id.input_layout_name)
    TextInputLayout textInputLayout;
    @Inject
    EditShowRoomPhonePresenter presenter;
    EditText[] editTexts;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    private MaterialDialog materialDialog;
    private String countryCode;
    private String[] showRoomPhones;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        editTexts = new EditText[]{number1, number2, number3, number4, number5, number6};
        setupBackIcon();
        mainEditText.setGravity(Gravity.LEFT);
        number1.setGravity(Gravity.LEFT);
        number2.setGravity(Gravity.LEFT);
        number3.setGravity(Gravity.LEFT);
        number4.setGravity(Gravity.LEFT);
        number5.setGravity(Gravity.LEFT);
        number6.setGravity(Gravity.LEFT);
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_showroom_phone;
    }

    @Override
    public void updateUi(DefaultUserModel currentUser) {
        this.countryCode = String.valueOf(currentUser.getCountryModel().getPhoneCode());
        String p = currentUser.getPhone();
        if (!TextUtils.isEmpty(p)) {
            if (p.contains("-")) {
                mainEditText.setText(p.split("-")[1]);
            } else {
                mainEditText.setText(p);
            }
        }
        showRoomPhones = currentUser.getShowRoomInfoModel().getPhones().replace(" ", "").
                replace("'", "").replace("]", "").
                replace("[", "").split(",");
        EditText[] editText = new EditText[]{number1, number2, number3, number4, number5, number6};
        for (int x1 = 0; x1 < showRoomPhones.length; x1++) {
            String p2 = showRoomPhones[x1].trim();
            if (!TextUtils.isEmpty(p2))
                if (p2.contains("-")) {
                    editText[x1].setText(p2.split("-")[1]);
                } else {
                    editText[x1].setText(p2);
                }
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
    public void onUpdatedSuccessfully(DefaultUserModel defaultUserModel) {
        if (defaultUserModel.getResetPhoneCode().equals("0")) {
            showSuccessMessage(getString(R.string.updated));
            finish();
        } else {
            new EditPhoneActivationActivityArgs(AuthenticationOperationListener.
                    OperationType.ACCOUNT_ACTIVATION, "", countryCode + "-" + mainEditText.getText().toString(), defaultUserModel.getResetPhoneCode(), "updatePhone").launch(this);
        }
    }

    @Override
    public void setupEditText(String phoneRegex) {
        mainEditText.setPhoneValidityListener(presenter::onAfterPhoneChange, phoneRegex);
    }

    @Override
    public void updateCountry(CountryViewModel countryViewModel) {
        this.countryCode = String.valueOf(countryViewModel.getPhoneCode());
        setupEditText(countryViewModel.getPhoneRegex());
        countryCodeTextView.setText(countryCode);
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(countryViewModel.getImage()))
                .centerCrop()
                .into(countryCodeImageView);
    }

    @Override
    public void showPhoneErrorMessage(String messageEn) {
        mainEditText.setError(messageEn);
    }

    @Override
    public void showphoneError(String invalid_phone_number, int i) {
        editTexts[i].setError(invalid_phone_number);
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
    public void onSaveClicked(View v) {
        EditText[] editTexts = new EditText[]{number1, number2, number3, number4, number5, number6};
        ArrayList<String> updatedPhones = new ArrayList<>();
        for (EditText editText : editTexts) {
            if (!TextUtils.isEmpty(editText.getText().toString())) {
                updatedPhones.add("'" + countryCode + "-" + editText.getText().toString() + "'");
            } else {
                updatedPhones.add("'" + "'");
            }
        }
        presenter.saveIsClicked(countryCode + "-" + mainEditText.getText().toString(), updatedPhones);
    }

    @OnClick(R.id.country_code)
    void onCountryCodeClicked() {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new CountryAdapter(this, viewModel -> {
                            updateCountry(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getCountryList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @OnClick({R.id.back_btn})
    public void onBackClicked(View v) {
        finish();
    }

}