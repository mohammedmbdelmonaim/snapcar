package com.intcore.snapcar.ui.resetpassword;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.AuthEndPoints;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.AuthenticationFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.widget.rxedittext.password.PasswordEditText;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class ResetPasswordActivity extends BaseActivity implements ResetPasswordScreen, AuthenticationOperationListener<DefaultUserDataApiResponse> {

    @Inject
    ResetPasswordPresenter presenter;
    @BindView(R.id.btn_reset)
    TextView resetPasswordButton;
    @BindView(R.id.et_password)
    PasswordEditText passwordEditText;
    @BindView(R.id.et_confirm_password)
    PasswordEditText confirmPasswordEditText;
    @BindView(R.id.input_layout_password)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.input_layout_confirm_password)
    TextInputLayout confirmPasswordTextInputLayout;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;


    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_reset_password;
    }

    @Override
    public void setupEditText() {
        passwordEditText.setValidityListener(presenter::onAfterPasswordChange);
        passwordEditText.checIfPasswordsMatches(confirmPasswordEditText, result -> presenter.observeIfPasswordsMatch(result));
    }

    @Override
    public void showConfirmPasswordErrorMessage(String confirmPasswordErrorMsg) {
        confirmPasswordTextInputLayout.setError(confirmPasswordErrorMsg);
    }

    @Override
    public void showPasswordErrorMessage(String passwordErrorMsg) {
        passwordTextInputLayout.setError(passwordErrorMsg);
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
    public void onReadyToResetPassword(String phone, String code) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", phone);
        jsonObject.addProperty("reset_password_code", code);
        jsonObject.addProperty("password", passwordEditText.getText().toString());
        jsonObject.addProperty("reset_method", "phone");
        jsonObject.addProperty("os", "android");
        jsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
        AuthenticationFactory.getPasswordResetFactory(this, AuthEndPoints.BASE_URL, DefaultUserDataApiResponse.class)
                .createAccountActivationUtil(jsonObject)
                .resetPassword(AuthEndPoints.RESET_PASSWORD_END_POINT, this);
    }

    @OnClick(R.id.btn_reset)
    void onResetPasswordClicked() {
        presenter.onResetPasswordClicked(passwordEditText.getText().toString());
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
        Intent intent = new Intent(this, HostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

}