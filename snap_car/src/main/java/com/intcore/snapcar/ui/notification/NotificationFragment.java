package com.intcore.snapcar.ui.notification;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.store.model.notification.NotificationViewModel;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.profile.ProfileFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.widget.LinearLayoutManagerWrapper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@FragmentScope
public class NotificationFragment extends Fragment implements NotificationScreen {

    @Inject
    NotificationRecyclerAdapter adapter;
    @Inject
    NotificationPresenter presenter;
    @Inject
    UiUtil uiUtil;
    @BindView(R.id.rv_notification)
    RecyclerView notificationRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.onViewCreated();
    }

    @Override
    public void onResume() {
        changeStatusBarColor();
        if (getActivity() instanceof HostActivity) {
            ((HostActivity) getActivity()).setHighlightedItem(HomeItem.NOTIFICATION);
        }
        super.onResume();
    }

    private void changeStatusBarColor() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorLayoutBackground));
    }

    @Override
    public void updateUi(List<List<NotificationViewModel>> notificationModels) {
        adapter.updateData(notificationModels);
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    public void showSuccessMessage(String message) {
        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    public void showAnnouncementMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void setupRecyclerView() {
        notificationRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getActivity()));
        notificationRecyclerView.setAdapter(adapter);
    }

    @Override
    public void shouldNavigateToHome() {
        if (getActivity() instanceof HostActivity)
            ((HostActivity) getActivity()).setFragment(new HomeFragment(), HomeFragment.class.getSimpleName());
    }

    @Override
    public void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(presenter::fetchData);
    }

    @Override
    public void showLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void shouldNavigateToProfile() {
        if (getActivity() instanceof HostActivity)
            ((HostActivity) getActivity()).setFragment(new ProfileFragment(), ProfileFragment.class.getSimpleName());
    }
}