package com.intcore.snapcar.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.api.AuthEndPoints;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.ui.activation.ActivationActivityArgs;
import com.intcore.snapcar.ui.forgetpassword.ForgetPasswordActivity;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.phaseoneregisteration.PhaseOneRegistrationActivity;
import com.intcore.snapcar.util.UserManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.AuthenticationFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.widget.rxedittext.password.PasswordEditText;
import com.intcore.snapcar.core.widget.rxedittext.phone.PhoneEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class LoginActivity extends BaseActivity implements LoginScreen, AuthenticationOperationListener<DefaultUserDataApiResponse> {

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.et_phone)
    PhoneEditText phoneEditText;
    @BindView(R.id.btn_sign_in)
    TextView signInButton;
    @BindView(R.id.et_password)
    PasswordEditText passwordEditText;
    @BindView(R.id.country_code)
    TextView countryCodeTextView;
    @BindView(R.id.iv_country_code)
    RoundedImageView countryCodeImageView;
    @BindView(R.id.input_layout_phone)
    TextInputLayout phoneTextInputLayout;
    @BindView(R.id.input_layout_password)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private MaterialDialog materialDialog;
    private String countryCode;
    private String phoneNumber;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
//        requestSmsPermission();
        checkZeroInPass();
        passwordEditText.setGravity(Gravity.LEFT);
        phoneEditText.setGravity(Gravity.LEFT);

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(phoneEditText.getText().length()!=0)
                    if (phoneEditText.getText().toString().charAt(0) == '0') {
                        phoneEditText.setText("");
                    }
            }
        });
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
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!phoneEditText.getText().toString().isEmpty())
                if (phoneEditText.getText().toString().charAt(0) == '0') {
                    phoneEditText.setText(new StringBuilder(phoneEditText.getText().toString()).deleteCharAt(0).toString());
                }
        });
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BROADCAST_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BROADCAST_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.READ_SMS},
                    200);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
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
    public void setupEditText(String phoneRegex) {
        phoneEditText.setPhoneValidityListener(presenter::onAfterPhoneChange, phoneRegex);
        passwordEditText.setValidityListener(presenter::onAfterPasswordChange);
    }

    @Override
    public void showPhoneErrorMsg(String phoneErrorMsg) {
        if (phoneErrorMsg != null) {
            phoneEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.md_red_A700), PorterDuff.Mode.SRC_ATOP);
            phoneTextInputLayout.setHintTextAppearance((R.style.style));
        } else {
            passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void showPasswordErrorMsg(String passwordErrorMsg) {
        if (passwordErrorMsg != null) {
            passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.md_red_A700), PorterDuff.Mode.SRC_ATOP);
            passwordTextInputLayout.setHintTextAppearance((R.style.style));
        } else {
            passwordEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
        }
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
    public void shouldNavigateToHome(String phone, String password) {
        getUiUtil().hideKeyboard(this);
        JsonObject userJsonObject = new JsonObject();
        if (countryCode != null) {
            showLoadingAnimation();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    countryCode + "-".concat(phone).concat("@amotorz.com"), password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    phoneNumber = countryCode + "-" + phoneEditText.getText().toString();
                    userJsonObject.addProperty("name", phoneNumber);
                    userJsonObject.addProperty("password", password);
                    userJsonObject.addProperty("locale", Locale.getDefault().getLanguage());
                    userJsonObject.addProperty("language", Locale.getDefault().getLanguage());
                    userJsonObject.addProperty("os", "android");
                    userJsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
                    AuthenticationFactory.getLoginFactory(this,
                            AuthEndPoints.BASE_URL, UserManager.getClassOfUserApiResponse())
                            .createDefaultLoginUtil(userJsonObject, AuthEndPoints.DEFAULT_LOGIN_END_POINT)
                            .login(this);
                } else {
                    if (task.getException() instanceof com.google.firebase.FirebaseNetworkException) {
                        hideLoadingAnimation();
                        if (getResourcesUtil().isEnglish()) {
                            showErrorMessage("There is No Internet Connection");
                        } else {
                            showErrorMessage("لا يوجد اتصال بالإنترنت");
                        }
                        return;
                    }
                    showErrorMessage(getString(R.string.firebae_message));
                    hideLoadingAnimation();
                }
            });
        }
//            if (checkAccountEmailExistInFirebase(countryCode + "-".concat(phone).concat("@amotorz.com"))) {
//                FirebaseAuth.getInstance().signInWithEmailAndPassword(
//                        countryCode + "-".concat(phone).concat("@amotorz.com"), password).addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        phoneNumber = countryCode + "-" + phoneEditText.getText().toString();
//                        userJsonObject.addProperty("name", phoneNumber);
//                        userJsonObject.addProperty("password", password);
//                        userJsonObject.addProperty("os", "android");
//                        userJsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
//                        AuthenticationFactory.getLoginFactory(this,
//                                AuthEndPoints.BASE_URL, UserManager.getClassOfUserApiResponse())
//                                .createDefaultLoginUtil(userJsonObject, AuthEndPoints.DEFAULT_LOGIN_END_POINT)
//                                .login(this);
//                    } else {
//                        showErrorMessage(getString(R.string.error_communicating_with_server));
//                    }
//                });
//            } else {
//                FirebaseAuth.getInstance().createUserWithEmailAndPassword(countryCode + "-".concat(phone).concat("@amotorz.com")
//                        , password)
//                        .addOnCompleteListener(this, task -> {
//                            if (task.isSuccessful()) {
//                                phoneNumber = countryCode + "-" + phoneEditText.getText().toString();
//                                userJsonObject.addProperty("name", phoneNumber);
//                                userJsonObject.addProperty("password", password);
//                                userJsonObject.addProperty("os", "android");
//                                userJsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
//                                AuthenticationFactory.getLoginFactory(this,
//                                        AuthEndPoints.BASE_URL, UserManager.getClassOfUserApiResponse())
//                                        .createDefaultLoginUtil(userJsonObject, AuthEndPoints.DEFAULT_LOGIN_END_POINT)
//                                        .login(this);
//                            } else {
//                                showErrorMessage(getString(R.string.error_communicating_with_server));
//                            }
//                        });
//            }
    }


    private boolean checkAccountEmailExistInFirebase(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final boolean[] b = new boolean[1];
        //mAuth.fetchProvidersForEmail(email).addOnCompleteListener(task -> b[0] = !task.getResult().getProviders().isEmpty());
        return b[0];
    }

    @OnClick(R.id.btn_sign_in)
    void onSignInClicked() {
        if (countryCode != null) {
            presenter.onSignInClicked(phoneEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }

    @OnClick(R.id.tv_sign_up)
    void onSignUpClicked() {
        startActivity(new Intent(this, PhaseOneRegistrationActivity.class));
    }

    @OnClick({R.id.country_code, R.id.iv_country_code})
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

    @OnClick(R.id.tv_forget_password)
    void onForgetPasswordClicked() {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
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
    public void onSuccess(DefaultUserDataApiResponse value, int type) {
        if (value.getDefaultUserApiResponse().getActivation().contentEquals(UserManager.ACTIVATED)) {
            Intent intent = new Intent(this, HostActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (value.getDefaultUserApiResponse().getActivation().contentEquals(UserManager.PENDING)) {
            showErrorMessage(getString(R.string.this_account_is_suspend));
        } else {
            new ActivationActivityArgs(
                    OperationType.ACCOUNT_ACTIVATION,
                    value.getDefaultUserApiResponse().getApiToken(),
                    value.getDefaultUserApiResponse().getPhone(),
                    value.getDefaultUserApiResponse().getActivation()
                    , "")
                    .launch(this);
        }
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

    @OnClick(R.id.tv_skip)
    void onSkipClicked() {
        Intent intent = new Intent(this, HostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        AuthenticationFactory.tearDown();
        super.onDestroy();
    }
}