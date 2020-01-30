package com.intcore.snapcar.ui.privacyactivtiy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class PrivacyActivity extends BaseActivity implements PrivacyScreen {

    @BindView(R.id.tv_about)
    TextView privacyTextView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.action_container)
    LinearLayout actionsContainer;
    @Inject
    PrivacyPresenter presenter;
    @Inject
    UserManager userManager;
    private boolean isEnglish;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        isEnglish = isEnglishLang();
        setupBack();
        if (getIntent() != null) {
            if (getIntent().getStringExtra("fromSetting") != null) {
                ivBack.setVisibility(View.VISIBLE);
                actionsContainer.setVisibility(View.GONE);
            }
        } else {
            ivBack.setVisibility(View.GONE);
            actionsContainer.setVisibility(View.VISIBLE);
        }
    }


    @SuppressLint("ResourceType")
    private void setupBack() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_privacy;
    }

    @Override
    public void policyText(SettingsModel settingsModel) {
        if (isEnglish) {
            privacyTextView.setText(settingsModel.getPrivacy());
        } else {
            privacyTextView.setText(settingsModel.getPrivacyAr());
        }
    }

    @Override
    public void finishActivity(DefaultUserModel defaultUserModel) {
        finish();
    }

    @Override
    public void logoutReady() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    @OnClick(R.id.btn_sign_in)
    void onAgreeClicked() {
        presenter.termsAgreed();
    }

    @OnClick(R.id.tv_cancel)
    void onCancelClicked() {
        presenter.processLogout();
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @OnClick(R.id.iv_back)
    void onBacK() {
        finish();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

//    @Override
//    public void onBackPressed() {
//        userManager.sessionManager().logout();
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//    }
}