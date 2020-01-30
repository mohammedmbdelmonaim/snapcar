package com.intcore.snapcar.ui.paymenthistory;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class PaymentHistoryActivity extends BaseActivity implements PaymentHistoryScreen {

    @BindView(R.id.rv_payment)
    RecyclerView paymentRecycler;
    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Inject
    PaymentHistoryPresenter presenter;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        setupBackIcon();
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backImageView.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_payment_history;
    }

    @Override
    public void updateUi(List<PaymentHistoryApiResponse> paymentHistoryApiResponse) {
        paymentRecycler.setLayoutManager(new LinearLayoutManager(this));
        if (paymentHistoryApiResponse.size() != 0) {
            paymentRecycler.setAdapter(new PaymentHistoryAdapter(this, paymentHistoryApiResponse));
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

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    @OnClick(R.id.iv_question)
    void onQuestionClicked() {
        showHotZoneInfoPopup();
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

    private void showHotZoneInfoPopup() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.payment_history_help))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }
}