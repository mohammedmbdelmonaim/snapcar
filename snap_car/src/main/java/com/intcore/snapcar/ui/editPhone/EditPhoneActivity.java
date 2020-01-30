package com.intcore.snapcar.ui.editPhone;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
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
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.widget.rxedittext.phone.PhoneEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class EditPhoneActivity extends BaseActivity implements EditPhoneScreen {

    @BindView(R.id.ed_number)
    PhoneEditText mainEditText;
    @BindView(R.id.country_code)
    TextView countryCodeTextView;
    @BindView(R.id.iv_country_code)
    RoundedImageView countryCodeImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    EditPhonePresenter presenter;
    private String countryCode;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);

        mainEditText.setGravity(Gravity.LEFT);

    }


    @Override
    protected int getLayout() {
        return R.layout.activity_edit_phone;
    }

    @OnClick({R.id.back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
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
    public void updateUi(DefaultUserModel currentUser) {
        this.countryCode = String.valueOf(currentUser.getCountryModel().getPhoneCode());
        String p = currentUser.getPhone();
        if (p.contains("-")) {
            mainEditText.setText(p.split("-")[1]);
        } else {
            mainEditText.setText(currentUser.getPhone());
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
    public void setupEditText(String phoneRegex) {
        mainEditText.setPhoneValidityListener(presenter::onAfterPhoneChange, phoneRegex);
    }

    @Override
    public void showPhoneError(String messageEn) {
        mainEditText.setError(messageEn);
    }

    @Override
    public void onActivationNeeded(String s, DefaultUserModel code) {
        if (!code.getResetPhoneCode().contentEquals("0")) {
            new EditPhoneActivationActivityArgs(AuthenticationOperationListener.
                    OperationType.ACCOUNT_ACTIVATION, "", countryCode + "-" + mainEditText.getText().toString(), code.getResetPhoneCode(), "updatePhone").launch(this);
        } else {
            showSuccessMessage(getString(R.string.updated_successfully));
            finish();
        }
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
    public void finishScreen() {
        finish();
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

    @OnClick({R.id.save_btn})
    void onSaveClicked(View v) {
        presenter.onProceedClicked(mainEditText.getText().toString(), countryCode);
    }
}
