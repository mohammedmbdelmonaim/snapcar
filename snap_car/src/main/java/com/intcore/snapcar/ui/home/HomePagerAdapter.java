package com.intcore.snapcar.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.intcore.snapcar.ui.explorer.ExplorerFragment;
import com.intcore.snapcar.ui.explorer.ExplorerFragment2;
import com.intcore.snapcar.ui.nearby.NearByFragment;

import javax.inject.Inject;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fragmentManager;

    private ExplorerFragment2 explorerFragment;

    HomePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;

        explorerFragment = new ExplorerFragment2();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                explorerFragment.setFragmentManager(fragmentManager);
                return explorerFragment;
            case 1:
                NearByFragment nearByFragment = new NearByFragment();
                nearByFragment.setFragmentManager(fragmentManager);
                return nearByFragment;
            default:
                return null;
        }
    }

    public void onDestroy(){
        if (explorerFragment != null)
            explorerFragment.onDestroyView();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
