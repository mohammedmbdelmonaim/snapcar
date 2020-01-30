package com.intcore.snapcar.ui.forgetpassword;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.api.AuthEndPoints;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.ui.activation.ActivationActivityArgs;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.AuthenticationFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.util.authentication.model.StringClass;
import com.intcore.snapcar.core.widget.rxedittext.phone.PhoneEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class ForgetPasswordActivity extends BaseActivity implements ForgetPasswordScreen, AuthenticationOperationListener<StringClass> {

    @Inject
    ForgetPasswordPresenter presenter;
    @BindView(R.id.et_phone)
    PhoneEditText phoneEditText;
    @BindView(R.id.btn_proceed)
    TextView proceedButton;
    @BindView(R.id.iv_country_code)
    ImageView countryCodeImageView;
    @BindView(R.id.country_code)
    TextView countryCodeTextView;
    @BindView(R.id.input_layout_phone)
    TextInputLayout phoneTextInputLayout;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private MaterialDialog materialDialog;
    private String countryCode;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
        checkZeroInPass();
        phoneEditText.setGravity(Gravity.LEFT);
    }

    void checkZeroInPass() {
        KeyboardVisibilityEvent.setEventListener(
                this,
                isOpen -> {
                    if (!phoneEditText.getText().toString().isEmpty())
                        if (phoneEditText.getText().toString().charAt(0) == '0') {
                            phoneEditText.setText(new StringBuilder(phoneEditText.getText().toString()).deleteCharAt(0).toString());
                        }
                });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_forget_password;
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
    public void showPhoneError(String phoneErrorMsg) {
        phoneEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.md_red_A700), PorterDuff.Mode.SRC_ATOP);
        phoneTextInputLayout.setHintTextAppearance((R.style.style));
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
    public void onActivationNeeded(String phone) {
        String phoneNumber;
        JsonObject jsonBody = new JsonObject();
        if (countryCode != null) {
            phoneNumber = countryCode + "-" + phoneEditText.getText().toString();
            jsonBody.addProperty("name", phoneNumber);
            jsonBody.addProperty("reset_method", "phone");
            jsonBody.addProperty("locale", Locale.getDefault().getLanguage());
            jsonBody.addProperty("language", Locale.getDefault().getLanguage());
            AuthenticationFactory.getPasswordResetFactory(this, AuthEndPoints.BASE_URL, StringClass.class)
                    .createAccountActivationUtil(jsonBody)
                    .sendCode(AuthEndPoints.SEND_PASSWORD_RESET_CODE_END_POINT, this);
        }
    }

    @Override
    public void setupEditText(String phoneRegex) {
        phoneEditText.setPhoneValidityListener(presenter::onAfterPhoneChange, phoneRegex);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_proceed)
    void onProceedButtonClicked() {
        presenter.onProceedClicked(phoneEditText.getText().toString());
    }

    @Override
    public void onPreOperation() {
        showLoadingAnimation();
    }

    @Override
    public void onPostOperation() {
        hideLoadingAnimation();
    }

    @Override
    public void onSuccess(StringClass value, int type) {
        String phoneNumber;
        phoneNumber = countryCode + "-" + phoneEditText.getText().toString();
        new ActivationActivityArgs(
                OperationType.PASSWORD_RESET,
                null,
                phoneNumber,
                value.getCode()
                , "")
                .launch(this);
    }

    @Override
    public void onHttpError(HttpException e) {
        presenter.processError(e);
    }

    @Override
    public void onNetworkError(IOException e) {
        presenter.processError(e);
    }

    @Override
    public void onUnExpectedError(Throwable e) {
        presenter.processError(e);
    }

    @OnClick({R.id.country_code, R.id.iv_country_code})
    void onCountryCode() {
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
    void onBackClicked(View v) {
        finish();
    }

}