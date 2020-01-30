package com.intcore.snapcar.ui.coupon;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.intcore.snapcar.store.model.coupon.CouponViewModel;
import com.intcore.snapcar.store.model.discount.DiscountViewModel;
import com.intcore.snapcar.ui.discounts.DiscountFragment;
import com.intcore.snapcar.ui.hotzone.HotZoneFragment;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

@ActivityScope
class CouponViewPagerAdapter extends FragmentStatePagerAdapter {

    private DiscountViewModel discountViewModel;

    @Inject
    CouponViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    void update(DiscountViewModel discountViewModel) {
        this.discountViewModel = discountViewModel;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle;
        switch (position) {
            case 1:
                DiscountFragment discountFragment = new DiscountFragment();
                if (discountViewModel != null) {
                    bundle = new Bundle();
                    bundle.putParcelableArrayList(CouponViewModel.TAG, discountViewModel.getOtherCouponViewModel());
                    discountFragment.setArguments(bundle);
                }
                return discountFragment;
            case 0:
                HotZoneFragment hotZoneFragment = new HotZoneFragment();
                if (discountViewModel != null) {
                    bundle = new Bundle();
                    bundle.putParcelableArrayList(CouponViewModel.TAG, discountViewModel.getCouponViewModel());
                    hotZoneFragment.setArguments(bundle);
                }
                return hotZoneFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
