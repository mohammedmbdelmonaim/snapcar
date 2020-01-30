package com.intcore.snapcar.ui.notificationsetting;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.NotificationSettingDTO;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class NotificationSettingActivity extends BaseActivity implements NotificationSettingScreen {

    @Inject
    NotificationSettingPresenter presenter;

    @BindView(R.id.switch_match_car)
    SwitchCompat matchCarSwitch;
    @BindView(R.id.switch_offer)
    SwitchCompat offerSwitch;
    @BindView(R.id.switch_chat)
    SwitchCompat chatSwitch;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_notification_setting;
    }

    @OnClick(R.id.tv_save)
    void onSaveClicked() {
        presenter.onSaveClicked(matchCarSwitch.isChecked(),
                chatSwitch.isChecked(),
                offerSwitch.isChecked());
    }

    @Override
    public void setDefaultData(NotificationSettingDTO notificationSettingDTO) {
        if (notificationSettingDTO != null) {
            matchCarSwitch.setChecked(notificationSettingDTO.isMatchCarNotify());
            offerSwitch.setChecked(notificationSettingDTO.isOfferNotify());
            chatSwitch.setChecked(notificationSettingDTO.isChatNotify());
        }
    }

    @Override
    public void onSavedSuccessfully() {
        showSuccessMessage(getString(R.string.updated_successfully));
        finish();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, HostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }
}