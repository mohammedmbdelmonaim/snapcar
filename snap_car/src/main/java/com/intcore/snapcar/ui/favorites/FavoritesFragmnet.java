package com.intcore.snapcar.ui.favorites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.fragments.BaseFragment;
import okhttp3.ResponseBody;

@FragmentScope
public class FavoritesFragmnet extends BaseFragment implements FavoritesScreen {

    @BindView(R.id.recyclerview_fav)
    RecyclerView favoritesRecycler;
    @BindView(R.id.emptyList)
    TextView isEmpty;
    @Inject
    FavoritesPresenter presenter;
    @Inject
    FavoritesAdapter favoritesAdapter;
    @Inject
    UiUtil uiUtil;
    private RecyclerViewSkeletonScreen skeletonScreen;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIHostComponentProvider) {
            UIHostComponentProvider provider = ((UIHostComponentProvider) context);
            if (provider.getComponent() instanceof ActivityComponent) {
                ((ActivityComponent) provider.getComponent())
                        .plus(new FragmentModule(this))
                        .inject(this);
            } else {
                throw new IllegalStateException("Component must be " + ActivityComponent.class.getName());
            }
        } else {
            throw new AssertionError("host context must implement " + UIHostComponentProvider.class.getName());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        ButterKnife.bind(this, view);
        favoritesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        skeletonScreen =  Skeleton.bind(favoritesRecycler)
                .adapter(favoritesAdapter)
                .duration(1200)
                .count(10)
                .shimmer(false)
                .load(R.layout.item_skeleton_news)
                .show();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_favorites;
    }

    @Override
    public void showDefaultMessage(String message) {

    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showAnnouncementMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void onResume() {
        if (getActivity() instanceof HostActivity) {
            ((HostActivity) getActivity()).setHighlightedItem(HomeItem.MARKET);
        }
        super.onResume();
    }

    @Override
    public void showLoadingAnimation() {
        uiUtil.getProgressDialog(getString(R.string.please_wait))
                .show();
    }

    @Override
    public void hideLoadingAnimation() {
        uiUtil.getProgressDialog().dismiss();
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public ResourcesUtil getResourcesUtil() {
        return null;
    }

    @Override
    public PermissionUtil getPermissionUtil() {
        return null;
    }

    @Override
    public void updateUi(FavoritesApiResponse apiResponse) {
        favoritesRecycler.postDelayed(() -> skeletonScreen.hide(), 3000);
        if (apiResponse.getFavoritesData().size() == 0) {
            isEmpty.setVisibility(View.VISIBLE);
            favoritesRecycler.setVisibility(View.GONE);
        } else {
            favoritesAdapter.swapData(apiResponse.getFavoritesData());
            isEmpty.setVisibility(View.GONE);
            favoritesRecycler.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void deleted(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.removed_from_list));
        presenter.fetchFavData();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}
