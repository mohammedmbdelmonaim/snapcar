package com.intcore.snapcar.di.fragment;

import com.intcore.snapcar.ui.chat.ChatFragment;
import com.intcore.snapcar.ui.discounts.DiscountFragment;
import com.intcore.snapcar.ui.explorer.ExplorerFragment;
import com.intcore.snapcar.ui.explorer.ExplorerFragment2;
import com.intcore.snapcar.ui.favorites.FavoritesFragmnet;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.hotzone.HotZoneFragment;
import com.intcore.snapcar.ui.nearby.NearByFragment;
import com.intcore.snapcar.ui.notification.NotificationFragment;
import com.intcore.snapcar.ui.profile.ProfileFragment;
import com.intcore.snapcar.core.scope.FragmentScope;

import dagger.Subcomponent;

/**
 * This interface is used by dagger to generate the code that defines the connection between the provider of objects
 * (i.e. {@link FragmentModule}), and the object which expresses a dependency.
 */

@FragmentScope
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {

    void inject(ExplorerFragment explorerFragment);

    void inject(ExplorerFragment2 explorerFragment2);

    void inject(NearByFragment nearByFragment);

    void inject(HomeFragment homeFragment);

    void inject(ProfileFragment profileFragment);

    void inject(HotZoneFragment hotZoneFragment);

    void inject(DiscountFragment discountFragment);

    void inject(FavoritesFragmnet favoritesFragmnet);

    void inject(ChatFragment chatFragment);

    void inject(NotificationFragment notificationFragment);
}