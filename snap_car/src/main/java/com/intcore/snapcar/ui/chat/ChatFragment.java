package com.intcore.snapcar.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.ui.chatsearch.ChatSearchActivity;
import com.intcore.snapcar.ui.chatthread.ChatThreadActivityArgs;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.widget.LinearLayoutManagerWrapper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * This class is responsible for showing chat history
 */
@FragmentScope
public class  ChatFragment extends Fragment implements ChatScreen {

    @Inject
    ChatPresenter presenter;
    @Inject
    ChatRecyclerAdapter adapter;
    @Inject
    UiUtil uiUtil;

    @BindView(R.id.rv_chat)
    RecyclerView chatRecyclerView;
    @BindView(R.id.snackbar_container)
    CoordinatorLayout snackBarContainer;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBar;

    public ChatFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.onViewCreated();
        Bundle args = getArguments();
        if (args != null) {
            if (args.getParcelable(PlaceDTO.TAG) != null) {
                presenter.setPlace(args.getParcelable(PlaceDTO.TAG));
            }
        }
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
    public void onResume() {
        presenter.onResume();
        changeStatusBarColor();
        if (getActivity() instanceof HostActivity) {
            ((HostActivity) getActivity()).setHighlightedItem(HomeItem.CHAT);
        }
        super.onResume();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorLayoutBackground));
        }
    }

    @Override
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getActivity()));
        chatRecyclerView.setAdapter(adapter);
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
    public void onReportSuccessfully() {
        showSuccessMessage(getString(R.string.report_successfully));
    }

    @Override
    public void onChatDeleted() {
        showSuccessMessage(getString(R.string.chat_deleted_success));
    }

    @Override
    public void showNetworkConnected() {
        uiUtil.getSnackBarBuilder(getActivity(), snackBarContainer)
                .layout(R.layout.layout_network_connected)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
    }

    @Override
    public void showNetworkConnecting() {
        uiUtil.getSnackBarBuilder(getActivity(), snackBarContainer)
                .layout(R.layout.layout_network_connecting)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
    }

    @Override
    public void shouldNavigateToChat(int chatId, int messageId, String firstName, PlaceDTO place) {
        new ChatThreadActivityArgs(chatId, messageId, firstName, place)
                .launch(getActivity());
    }

    @Override
    public void updateUi(DiffUtil.DiffResult diffResult) {
        diffResult.dispatchUpdatesTo(adapter);
    }

    @Override
    public void showNetworkDisconnected() {
        uiUtil.getSnackBarBuilder(getActivity(), snackBarContainer)
                .layout(R.layout.layout_network_disconnected)
                .duration(Snackbar.LENGTH_SHORT)
                .build()
                .show();
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @OnClick(R.id.iv_search)
    void onSearchClicked() {
        startActivity(new Intent(getActivity(), ChatSearchActivity.class));
    }
}