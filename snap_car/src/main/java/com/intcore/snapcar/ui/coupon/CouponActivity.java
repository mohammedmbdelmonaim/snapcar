package com.intcore.snapcar.ui.coupon;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.discount.DiscountViewModel;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * This class is responsible for contain two containers one for hotzones and other for coupons
 */
@ActivityScope
public class CouponActivity extends BaseActivity implements CouponScreen, UIHostComponentProvider {

    @Inject
    CouponPresenter presenter;
    @Inject
    CouponViewPagerAdapter adapter;
    @BindView(R.id.view_pager)
    RtlViewPager viewPager;
    @BindView(R.id.tv_hot_zone)
    TextView hotZoneTextView;
    @BindView(R.id.tv_discount)
    TextView discountTextView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private ActivityComponent component;

    @Override
    protected void onCreateActivityComponents() {
        component = SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this));
        component.inject(this);

        ButterKnife.bind(this);
        hotZoneTextView.setSelected(true);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_coupon;
    }

    @Override
    public void updateUi(DiscountViewModel discountViewModel) {
        adapter.update(discountViewModel);
    }

    @Override
    public void setupViewPager() {
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    hotZoneTextView.setSelected(true);
                    discountTextView.setSelected(false);
                } else if (position == 1) {
                    hotZoneTextView.setSelected(false);
                    discountTextView.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    public void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(presenter::fetchData);
    }

    @OnClick(R.id.tv_hot_zone)
    void onHotZoneClicked() {
        discountTextView.setSelected(false);
        hotZoneTextView.setSelected(true);
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.tv_discount)
    void onDiscountClicked() {
        discountTextView.setSelected(true);
        hotZoneTextView.setSelected(false);
        viewPager.setCurrentItem(1);
    }

    @Override
    public void showLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    @Override
    public ActivityComponent getComponent() {
        if (component == null) {
            component = SnapCarApplication.getComponent(this)
                    .plus(new ActivityModule(this));
            component.inject(this);
        }
        return component;
    }

    @OnClick(R.id.iv_question)
    void onQuestionClicked() {
        showHotZoneInfoPopup();
    }

    private void showHotZoneInfoPopup() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.coupon_info))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }
}