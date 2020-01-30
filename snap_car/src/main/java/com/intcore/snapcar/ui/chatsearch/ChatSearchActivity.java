package com.intcore.snapcar.ui.chatsearch;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatViewModel;
import com.intcore.snapcar.core.widget.LinearLayoutManagerWrapper;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@ActivityScope
public class ChatSearchActivity extends BaseActivity implements ChatSearchScreen {

    @Inject
    ChatSearchPresenter presenter;
    @Inject
    ChatRecyclerAdapter adapter;

    @BindView(R.id.rv_chat)
    RecyclerView chatRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.et_search)
    RxEditText searchEditText;
    @BindView(R.id.tv_no_data)
    TextView noDataTextView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message)
                .show();
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
    protected int getLayout() {
        return R.layout.activity_chat_search;
    }

    @Override
    public void setupEditText() {
        presenter.setAfterEditTextChanged(RxTextView.afterTextChangeEvents(searchEditText));
    }

    @Override
    public void updateUi(List<ChatViewModel> chatModels) {
        noDataTextView.setVisibility(View.GONE);
        adapter.updateData(chatModels);
    }

    @Override
    public void notifyItemRemoved(int position) {
        adapter.notifyRemovedItem(position);
    }

    @Override
    public void onReportSuccessfully() {
        showSuccessMessage(getString(R.string.report_successfully));
    }

    @Override
    public void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(this));
        chatRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showNoData() {
        noDataTextView.setVisibility(View.VISIBLE);
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

}