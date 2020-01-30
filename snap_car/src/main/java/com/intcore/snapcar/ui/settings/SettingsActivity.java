package com.intcore.snapcar.ui.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.aboutus.AboutUsActivity;
import com.intcore.snapcar.ui.contactus.ContactUsActivity;
import com.intcore.snapcar.ui.privacyactivtiy.PrivacyActivity;
import com.intcore.snapcar.ui.
        splash.SplashActivity;
import com.intcore.snapcar.ui.updatepassword.UpdatePasswordActivity;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

@ActivityScope
public class SettingsActivity extends BaseActivity implements SettingsScreen {

    @BindView(R.id.policy_cell)
    ImageView policyCell;
    @BindView(R.id.password_cell)
    ImageView passwordCell;
    @BindView(R.id.about_cell)
    ImageView aboutCell;
    @BindView(R.id.contact_cell)
    ImageView contactCell;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.switch_lang)
    SwitchCompat switchLanguage;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Inject
    SettingsPresenter presenter;
    @Inject
    UserManager userManager;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        setupBackIcon();
        setupSwitchCompact();
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

    @SuppressLint("ResourceType")
    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        policyCell.setImageDrawable(icons.getDrawable(1));
        aboutCell.setImageDrawable(icons.getDrawable(1));
        passwordCell.setImageDrawable(icons.getDrawable(1));
        contactCell.setImageDrawable(icons.getDrawable(1));
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    private void setupSwitchCompact() {
        if (LocaleUtil.getLanguage(this).contentEquals("en")) {
            switchLanguage.setChecked(true);
        }
        switchLanguage.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                LocaleUtil.setLocale(SettingsActivity.this, "en");
                presenter.changeLanguage("en");
            } else {
                LocaleUtil.setLocale(SettingsActivity.this, "ar");
                presenter.changeLanguage("ar");
            }

            SnapCarApplication.getInstance().notifyLanguageChanged();
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @OnClick(R.id.iv_back)
    public void onBackClicked() {
        finish();
    }

    @OnClick(R.id.password_layout)
    void onChangePasswordClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            startActivity(new Intent(this, UpdatePasswordActivity.class));
        } else {
            showWarningMessage(getString(R.string.please_sign_in));
        }
    }

    @OnClick(R.id.about_us_layout)
    void onAboutUsClicked() {
        startActivity(new Intent(this, AboutUsActivity.class));
    }

    @OnClick(R.id.policy_layout)
    void onPrivacyClicked() {
        Intent intent = new Intent(this, PrivacyActivity.class);
        intent.putExtra("fromSetting", "fromSetting");
        startActivity(intent);
    }

    @OnClick(R.id.contact_layout)
    void onContactUsClicked() {
        startActivity(new Intent(this, ContactUsActivity.class));
    }

    @Override
    public void changeLanguage(ResponseBody responseBody) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}