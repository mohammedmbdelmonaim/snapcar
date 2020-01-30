package com.intcore.snapcar.ui.phaseoneregisteration;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.ui.activation.ActivationActivityArgs;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.widget.rxedittext.phone.PhoneEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class PhaseOneRegistrationActivity extends BaseActivity implements PhaseOneRegistrationScreen {

    @Inject
    PhaseOneRegistrationPresenter presenter;
    @BindView(R.id.et_phone)
    PhoneEditText phoneEditText;
    @BindView(R.id.btn_proceed)
    TextView proceedButton;
    @BindView(R.id.country_code)
    TextView countryCodeTextView;
    @BindView(R.id.iv_country_code)
    ImageView countryCodeImageView;
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
        setupBackIcon();
        checkZeroInPass();
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
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_phase_one_registeration;
    }

    @Override
    public void setupEditText(String phoneRegex) {
        phoneEditText.setPhoneValidityListener(presenter::onAfterPhoneChange, phoneRegex);
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
    public void onActivationNeeded(String phone, String code) {
        new ActivationActivityArgs(AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION,
                null, phone, code, "")
                .launch(this);
    }

    @Override
    public void showPhoneError(String errorMsg) {
        if (errorMsg != null) {
            phoneEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.md_red_A700), PorterDuff.Mode.SRC_ATOP);
            phoneTextInputLayout.setHintTextAppearance((R.style.style));
            showErrorMessage(getString(R.string.invalid_phone_number));
        } else {
            phoneEditText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorTextPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @OnClick(R.id.btn_proceed)
    void onProceedButtonCLicked() {
        if (countryCode != null) {
            presenter.onProceedClicked(phoneEditText.getText().toString(), countryCode);
        }else
            showErrorMessage(getString(R.string.error_network));
    }

    @Override
    public void updateCountry(CountryViewModel viewModel) {
        this.countryCode = String.valueOf(viewModel.getPhoneCode());
        countryCodeTextView.setText(countryCode);
        setupEditText(viewModel.getPhoneRegex());
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(viewModel.getImage()))
                .centerCrop()
                .into(countryCodeImageView);
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

    @OnClick(R.id.back_btn)
    void onBackClicked() {
        finish();
    }

}