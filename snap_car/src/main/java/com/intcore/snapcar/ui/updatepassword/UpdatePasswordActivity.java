package com.intcore.snapcar.ui.updatepassword;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.widget.rxedittext.password.PasswordEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class UpdatePasswordActivity extends BaseActivity implements UpdatePasswordScreen {

    @BindView(R.id.et_old_password)
    PasswordEditText oldPasswordEditText;
    @BindView(R.id.et_new_password)
    PasswordEditText newPasswordEditText;
    @BindView(R.id.et_confirm_new_password)
    PasswordEditText confirmNewPasswordEditText;
    @BindView(R.id.input_layout_confirm_new_password)
    TextInputLayout confirmNewInputLayout;
    @BindView(R.id.input_layout_new_password)
    TextInputLayout newTextInputLayout;
    @BindView(R.id.input_layout_old_password)
    TextInputLayout oldTextInputLayout;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    UpdatePasswordPresenter presenter;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backImageView.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_update_password;
    }


    @Override
    public void setupEditText() {
        newPasswordEditText.setValidityListener(presenter::onAfterNewPasswordChange);
        newPasswordEditText.checIfPasswordsMatches(confirmNewPasswordEditText, result -> presenter.observeIfPasswordsMatch(result));
    }

    @Override
    public void showConfirmPasswordErrorMessage(String messageEn) {
        confirmNewInputLayout.setError(messageEn);
    }

    @Override
    public void newPasswordErrorMessage(String messageEn) {
        newTextInputLayout.setError(messageEn);
    }


    @Override
    public void oldPasswordErrorMessage(String messageEn) {
        oldTextInputLayout.setError(messageEn);
    }

    @Override
    public void passwordUpdated(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.password_updated));
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
    void onUpdateClicked() {
        if(oldPasswordEditText.getText().toString().isEmpty()){
            oldPasswordErrorMessage(getString(R.string.old_pasword_hint));
        }else{
        presenter.updateClicked(oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());
    }}

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

    @OnClick(R.id.iv_back)
    public void onBackClicked() {
        finish();
    }
}
