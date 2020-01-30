package com.intcore.snapcar.di.fragment;

import android.app.Activity;
import androidx.lifecycle.Lifecycle;
import android.content.Context;
import androidx.fragment.app.Fragment;

import com.intcore.snapcar.ui.chat.ChatScreen;
import com.intcore.snapcar.ui.discounts.DiscountScreen;
import com.intcore.snapcar.ui.explorer.ExplorerScreen;
import com.intcore.snapcar.ui.explorer.ExplorerScreen2;
import com.intcore.snapcar.ui.favorites.FavoritesScreen;
import com.intcore.snapcar.ui.home.HomeScreen;
import com.intcore.snapcar.ui.hotzone.HotZoneScreen;
import com.intcore.snapcar.ui.nearby.NearByScreen;
import com.intcore.snapcar.ui.notification.NotificationScreen;
import com.intcore.snapcar.ui.profile.ProfileScreen;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * This class is responsible for providing the requested objects to {@link FragmentScope} annotated classes
 */

@Module
public class FragmentModule {

    private final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @FragmentScope
    @Provides
    Fragment provideFragment() {
        return fragment;
    }

    @FragmentScope
    @ForFragment
    @Provides
    Context provideFragmentContext() {
        return fragment.getContext();
    }

    @FragmentScope
    @ForFragment
    @Provides
    Activity provideActivity() {
        return fragment.getActivity();
    }

    @FragmentScope
    @ForFragment
    @Provides
    Lifecycle provideFragmentLifeCycle() {
        return fragment.getLifecycle();
    }

    @FragmentScope
    @ForFragment
    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @FragmentScope
    @Provides
    HomeScreen provideHomeScreen() {
        return (HomeScreen) fragment;
    }

    @FragmentScope
    @Provides
    ExplorerScreen provideExplorerScreen() {
        return (ExplorerScreen) fragment;
    }

    @FragmentScope
    @Provides
    ExplorerScreen2 provideExplorerScreen2() {
        return (ExplorerScreen2) fragment;
    }

    @FragmentScope
    @Provides
    NearByScreen provideNearByScreen() {
        return (NearByScreen) fragment;
    }

    @FragmentScope
    @Provides
    ProfileScreen providesProfileScreen() {
        return (ProfileScreen) fragment;
    }

    @FragmentScope
    @Provides
    FavoritesScreen providesFavoritesScreen() {
        return (FavoritesScreen) fragment;
    }


    @FragmentScope
    @Provides
    HotZoneScreen providesHotZoneScreen() {
        return (HotZoneScreen) fragment;
    }

    @FragmentScope
    @Provides
    DiscountScreen providesDiscountScreen() {
        return (DiscountScreen) fragment;
    }

    @FragmentScope
    @Provides
    ChatScreen providesChatScreen() {
        return (ChatScreen) fragment;
    }

    @FragmentScope
    @Provides
    NotificationScreen providesNotificationScreen() {
        return (NotificationScreen) fragment;
    }
}