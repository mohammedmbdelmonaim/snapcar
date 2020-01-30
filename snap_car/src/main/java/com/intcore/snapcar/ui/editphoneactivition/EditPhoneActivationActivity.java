package com.intcore.snapcar.ui.editphoneactivition;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.AuthEndPoints;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.ui.activation.PinEntryEditText;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.resetpassword.PasswordResetActivityArgs;
import com.intcore.snapcar.ui.resetpassword.ResetPasswordActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.AuthenticationFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.util.authentication.model.StringClass;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class EditPhoneActivationActivity extends BaseActivity implements EditPhoneActivationScreen,
        AuthenticationOperationListener<DefaultUserDataApiResponse> {

    @Inject
    EditPhoneActivationPresenter presenter;

    @BindView(R.id.tv_didnt_receive_code)
    TextView resendCodeMessage;
    @BindView(R.id.btn_activate)
    TextView doneButton;
    @BindView(R.id.tv_resend)
    TextView resendTextView;
    @BindView(R.id.pin_entry)
    PinEntryEditText pinEntryEditText;
    @BindView(R.id.pb_resend)
    ProgressBar resendProgressBar;
    @BindView(R.id.tv_phone)
    TextView phoneTextView;
    @BindView(R.id.invalid_msg)
    TextView InvalidText;
    @BindView(R.id.back_btn)
    ImageView backBtn;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_activation;
    }

    @Override
    public void setupEditText() {
        pinEntryEditText.setOnPinEnteredListener(code -> presenter.onAfterTextChange(code));
    }

    @Override
    public void shouldResetPassword() {
        startActivity(new Intent(this, ResetPasswordActivity.class));
        finish();
    }

    @Override
    public void shouldNavigateToPhaseTwo() {
//        getUiUtil().getDialogBuilder(this, R.layout.layout_activation_dialog)
//                .clickListener(R.id.tv_complete, (dialog, view) -> {
//                    new PhaseTwoRegistrationActivityArgs(presenter.getPhone())
//                            .launch(this);
//                    dialog.dismiss();
//                })
//                .background(R.drawable.inset_bottomsheet_background)
//                .gravity(Gravity.CENTER)
//                .cancelable(false)
//                .build()
//                .show();
    }

    @Override
    public void setActivationError(Boolean valid) {
        pinEntryEditText.setError(!valid);
        if (!valid) {
            pinEntryEditText.postDelayed(() -> pinEntryEditText.setText(null), 1000);
        }
    }

    @Override
    public void updatePhoneField(String phoneNumber) {
        phoneTextView.setText(phoneNumber);
    }

    @Override
    public void setDoneButtonEnabled(Boolean enabled) {
        doneButton.setEnabled(enabled);
        pinEntryEditText.setEnabled(false);
        if (enabled) {
            getUiUtil().hideKeyboard(this);
            InvalidText.setVisibility(View.GONE);
        } else {
            pinEntryEditText.setEnabled(true);
            getUiUtil().showKeyboard(pinEntryEditText);
            InvalidText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setRetryButtonEnabled(boolean enable) {
        resendTextView.setEnabled(enable);
    }

    @Override
    public void setCounterVisibility(boolean shouldShow) {
        if (shouldShow) {
            resendCodeMessage.setVisibility(View.VISIBLE);
            resendTextView.setVisibility(View.VISIBLE);
        } else {
            resendCodeMessage.setVisibility(View.GONE);
            resendTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResendCodeRequested(Integer operationType, String key) {
        if (operationType == OperationType.ACCOUNT_ACTIVATION) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("phone", key);
            jsonObject.addProperty("reset_method", "phone");
            AuthenticationFactory.getAccountActivationFactory(this, AuthEndPoints.BASE_URL, StringClass.class)
                    .createAccountActivationUtil(jsonObject, AuthEndPoints.RESEND_ACTIVATION_CODE_END_POINT)
                    .resend(new AuthenticationOperationListener<StringClass>() {
                        @Override
                        public void onPreOperation() {
                            resendTextView.setVisibility(View.INVISIBLE);
                            resendProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onPostOperation() {
                            resendTextView.setVisibility(View.VISIBLE);
                            resendProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(StringClass stringClass, int type) {
                            presenter.onCodeResent(stringClass.getCode());
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
                    });
        } else if (operationType == OperationType.PASSWORD_RESET) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", key);
            jsonObject.addProperty("reset_method", "phone");
            AuthenticationFactory.getAccountActivationFactory(this, AuthEndPoints.BASE_URL, StringClass.class)
                    .createAccountActivationUtil(jsonObject, AuthEndPoints.SEND_PASSWORD_RESET_CODE_END_POINT)
                    .resend(new AuthenticationOperationListener<StringClass>() {
                        @Override
                        public void onPreOperation() {
                            resendTextView.setVisibility(View.INVISIBLE);
                            resendProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onPostOperation() {
                            resendTextView.setVisibility(View.VISIBLE);
                            resendProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(StringClass value, int type) {
                            presenter.onCodeResent(value.getCode());
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
                    });
        }
    }

    @Override
    public void onActivationReady(int operationType, String key, String code) {

        if (operationType == OperationType.ACCOUNT_ACTIVATION) {
            shouldNavigateToPhaseTwo();
        } else if (operationType == OperationType.PASSWORD_RESET) {
            new PasswordResetActivityArgs(key, code)
                    .launch(this);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void onUpdatedSuccessfully() {
        showSuccessMessage(getResourcesUtil().getString(R.string.number_activated));
        finish();
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
    public void updateRetryCounter(String counter) {
        resendTextView.setText(counter);
    }

    @Override
    public void setPinEntryEnabled(boolean enabled) {
        resendTextView.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        AuthenticationFactory.tearDown();
        super.onDestroy();
    }

    @OnClick(R.id.btn_activate)
    void onActivateClicked() {
        presenter.onActivateClicked();
    }

    @OnClick(R.id.tv_resend)
    void onResendClicked() {
        presenter.onResendClicked();
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

    @OnClick({R.id.back_btn})
    void onBackClicked(View v) {
        finish();
    }

}