package com.intcore.snapcar.ui.photos;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.intcore.snapcar.R;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class PhotosActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    RtlViewPager viewPager;
    @BindView(R.id.tv_counter)
    TextView tvCounter;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        ButterKnife.bind(this);
        setupViewPager();
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    private void setupViewPager() {
        PhotosActivityArgs photosActivityArgs = PhotosActivityArgs.deserializeFrom(getIntent());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, photosActivityArgs.getPhotosList());
        viewPager.setAdapter(viewPagerAdapter);
        tvCounter.setText(String.valueOf(photosActivityArgs.getPosition())
                .concat(" ")
                .concat(getString(R.string.of))
                .concat(" ")
                .concat(String.valueOf(photosActivityArgs.getPhotosList().size())));
        viewPager.setCurrentItem(photosActivityArgs.getPosition());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvCounter.setText(String.valueOf(position + 1)
                        .concat(" ")
                        .concat(getString(R.string.of))
                        .concat(" ")
                        .concat(String.valueOf(photosActivityArgs.getPhotosList().size())));
            }

            @Override
            public void onPageSelected(int position) {
                tvCounter.setText(String.valueOf(position + 1)
                        .concat(" ")
                        .concat(getString(R.string.of))
                        .concat(" ")
                        .concat(String.valueOf(photosActivityArgs.getPhotosList().size())));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }
}