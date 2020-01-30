package com.intcore.snapcar.ui.aboutus;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class AboutUsActivity extends BaseActivity implements AboutUsScreen {

    @BindView(R.id.tv_about)
    TextView aboutUsTextView;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    AboutUsPresenter presenter;
    private SettingsModel settingModel;

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

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_about_us;
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

    @OnClick(R.id.fb_ic)
    void onFBClicked() {
        startActivity(getOpenFacebookIntent());
    }

    @OnClick(R.id.snap_ic)
    void onSnapClicked() {
        startActivity(getOpenSnapIntent());
    }

    @OnClick(R.id.tw_ic)
    void onTwitterClicked() {
        startActivity(getOpenTwitterIntent());
    }

    @OnClick(R.id.youtube_ic)
    void onYoutubeClicked() {
        startActivity(getOpenYoutubeIntent());
    }

    @OnClick(R.id.instgram_ic)
    void onInstgramClicked() {
        startActivity(getOpenInstgramIntent());
    }

    @OnClick({R.id.iv_back})
    public void onBackClicked(View v) {
        finish();
    }

    @Override
    public void aboutUsText(SettingsModel settingsModel) {
        this.settingModel = settingsModel;
        if (isEnglishLang()) {
            aboutUsTextView.setText(settingsModel.getSocialMedia());
        } else {
            aboutUsTextView.setText(settingsModel.getSocialMediaAr());
        }
    }

    public Intent getOpenSnapIntent() {

        if (settingModel.getSnapChat() != null)
            return new Intent(Intent.ACTION_VIEW, Uri.parse(settingModel.getSnapChat()));
        else
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
    }

    public Intent getOpenTwitterIntent() {

        if (settingModel.getTwitter() != null)
            return new Intent(Intent.ACTION_VIEW, Uri.parse(settingModel.getTwitter()));
        else
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/USERID_OR_PROFILENAME"));

    }

    public Intent getOpenFacebookIntent() {

        if (settingModel.getFacebook() != null)
            return new Intent(Intent.ACTION_VIEW, Uri.parse(settingModel.getFacebook()));
        else return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));

    }

    public Intent getOpenYoutubeIntent() {

        if (settingModel.getYoutube() != null)
            return new Intent(Intent.ACTION_VIEW, Uri.parse(settingModel.getYoutube()));
        else return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"));

    }

    public Intent getOpenInstgramIntent() {

        if (settingModel.getIntagram() != null)
            return new Intent(Intent.ACTION_VIEW, Uri.parse(settingModel.getIntagram()));
        else return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));

    }
}
