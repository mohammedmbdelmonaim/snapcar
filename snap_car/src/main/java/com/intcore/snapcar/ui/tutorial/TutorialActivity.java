package com.intcore.snapcar.ui.tutorial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class TutorialActivity extends BaseActivity implements TutorialScreen {

    @Inject
    TutorialPresenter presenter;
    @Inject
    ViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.tv_skip)
    TextView skipTextView;
    @BindView(R.id.iv_next)
    ImageView nextImageView;
    @BindView(R.id.viewPager)
    RtlViewPager viewPager;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView pageIndicatorView;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        nextImageView.setImageDrawable(icons.getDrawable(1));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_tutorial;
    }

    @Override
    public void setupViewPager() {
        pageIndicatorView.setSelection(0);
        pageIndicatorView.setAnimationType(AnimationType.DROP);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 || position == 1 || position == 2) {
                    nextImageView.setVisibility(View.INVISIBLE);
                    skipTextView.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    skipTextView.setVisibility(View.INVISIBLE);
                    nextImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                pageIndicatorView.setSelection(position);
                viewPagerAdapter.animate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.tv_skip)
    void onSkipClicked() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.iv_next)
    void onNextClicked() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}