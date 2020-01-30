package com.intcore.snapcar.ui.verificationletter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.util.ImageFileFilter;
import com.intcore.snapcar.util.VerifiedLetterFileFilter;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class VerificationLetterActivity extends BaseActivity implements VerificationLetterScreen {

    @BindView(R.id.im_add_letter)
    ImageView verivicationImage;
    @BindView(R.id.done_btn)
    AppCompatButton appCompatButton;
    @BindView(R.id.cancel_btn)
    ImageView cancelBtn;
    @BindView(R.id.tv_preview_image)
    TextView prview;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    VerificationLetterPresenter presenter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private String verifyLatter;
    private int isVerified = 0;
    private File file;



    @SuppressLint("ResourceType")
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
        return R.layout.activity_verivication_letter;
    }

    @OnClick(R.id.iv_back)
    public void onBackClicked() {
        finish();
    }

    @Override
    public void updateUi(DefaultUserModel defaultUserModel) {
        if (defaultUserModel.getShowRoomInfoModel() != null) {
            if (defaultUserModel.getShowRoomInfoModel().getVerifyLatter() != null) {
                verifyLatter = defaultUserModel.getShowRoomInfoModel().getVerifyLatter();
            }
            isVerified = defaultUserModel.getIsVerified();
            if (!verifyLatter.equals("")) {
                if (!new ImageFileFilter(verifyLatter).accept(verifyLatter)) {
                    verivicationImage.setImageResource(R.drawable.pdf);
                    verivicationImage.setClickable(false);
                } else {
                    verivicationImage.setImageResource(R.drawable.photo);
                    verivicationImage.setClickable(false);
                }
                appCompatButton.setText(R.string.finish);
                prview.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void selectedFile(File file) {
        if (!new VerifiedLetterFileFilter().accept(file)) {
            showWarningMessage(getString(R.string.not_supported_file));
            return;
        }
        if (!new ImageFileFilter(file).accept(file)) {
            verivicationImage.setImageResource(R.drawable.pdf);
            verivicationImage.setClickable(false);
            cancelBtn.setVisibility(View.VISIBLE);
            this.file = file;
        } else {
            verivicationImage.setImageResource(R.drawable.photo);
            verivicationImage.setClickable(false);
            cancelBtn.setVisibility(View.VISIBLE);
            this.file = file;
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

    @OnClick(R.id.cancel_btn)
    void onCancelClicked() {
        this.file = null;
        verivicationImage.setImageResource(R.drawable.add_photo);
        verivicationImage.setClickable(true);
        cancelBtn.setVisibility(View.GONE);
    }

    @Override
    public void sendSuccessfully(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.request_vip_sent));
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

    @OnClick(R.id.im_add_letter)
    void onAddLetterClicked() {
        presenter.openGallery(RxPaparazzo.single(this));
    }

    @OnClick(R.id.done_btn)
    void onSendClicked() {
        if (!verifyLatter.equals("")) {
            finish();
            return;
        }
        if (isVerified == 1) {
            showSuccessMessage(getString(R.string.you_are_verified));
        } else {
            if (file != null) {
                presenter.sendVerifyLetter(file);
                file = null;
            } else {
                showWarningMessage(getString(R.string.please_select_image));
            }
        }
    }

    @OnClick(R.id.tv_preview_image)
    void onPreviewClicked() {
        if (new ImageFileFilter(verifyLatter).accept(verifyLatter)) {
            new ImagePreviewDialog(this, verifyLatter).show();
        } else {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.parse(ApiUtils.BASE_URL.concat(verifyLatter)), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                Timber.e(ignored);
            }
        }
    }

    @OnClick(R.id.iv_question)
    void onQuestionClicked() {
        showHotZoneInfoPopup();
    }

    private void showHotZoneInfoPopup() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.verification_info))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }
}