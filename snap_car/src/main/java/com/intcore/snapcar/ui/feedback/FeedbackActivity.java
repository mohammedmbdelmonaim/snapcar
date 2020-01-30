package com.intcore.snapcar.ui.feedback;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

@ActivityScope
public class FeedbackActivity extends BaseActivity implements FeedbackScreen {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_subject)
    RxEditText etSubject;
    @BindView(R.id.et_contact)
    RxEditText contactEditText;
    @BindView(R.id.attachment_layout)
    RelativeLayout attachmentLayout;
    @BindView(R.id.im_attachment)
    ImageView attachmentImage;
    @BindView(R.id.cancel_action)
    ImageView attachmentCancel;
    @BindView(R.id.et_feedback)
    EditText feedBackEditText;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    FeedbackPresenter presenter;
    private MaterialDialog materialDialog;
    private File attachment = null;
    private int subjectId = 0;

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
        return R.layout.activity_feedback;
    }

    @OnClick(R.id.et_contact)
    void onSubjectClicked() {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new SubjectsAdapter(this, id -> {
                            updateSubjectFromDialog(id);
                            materialDialog.dismiss();
                        }, presenter.getSubjects(), subjectId),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @OnClick({R.id.attachment_layout, R.id.im_attachment})
    void onAttachmentClicked() {
        presenter.openGallery(RxPaparazzo.single(this));
    }

    private void updateSubjectFromDialog(FeedbackApiResponse.FeedBackData subj) {
        this.subjectId = subj.getId();
        if (isEnglish()) {
            contactEditText.setText(subj.getGetNameEn());
        } else {
            contactEditText.setText(subj.getNameAr());
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
    public void setAttachment(File s) {
        this.attachment = s;
        attachmentImage.setVisibility(View.GONE);
        attachmentCancel.setVisibility(View.VISIBLE);
        attachmentLayout.setClickable(false);
    }

    @Override
    public void feedbackSent(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.feedback_sent));
        new Handler().postDelayed(this::finish, 1500);
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.cancel_action)
    public void onCancelClicked() {
        if (attachment != null) {
            this.attachment = null;
            attachmentImage.setVisibility(View.VISIBLE);
            attachmentCancel.setVisibility(View.GONE);
            attachmentImage.setImageResource(R.drawable.attachment_material);
            attachmentLayout.setClickable(true);
        }
    }

    @OnClick(R.id.btn_send)
    public void onSendClicked() {
        if (subjectId == 0) {
            showErrorMessage(getString(R.string.plese_select_contact));
            return;
        }
        if (etSubject.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.plese_type_subject));
            return;
        }
        if (feedBackEditText.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.plese_type_feedback));
            return;
        }
        presenter.postFeedback(String.valueOf(subjectId),
                feedBackEditText.getText().toString(),
                attachment,
                etSubject.getText().toString());
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }
}