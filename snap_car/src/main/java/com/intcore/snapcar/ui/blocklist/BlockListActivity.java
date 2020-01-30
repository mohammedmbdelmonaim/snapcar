package com.intcore.snapcar.ui.blocklist;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * This class is responsible for showing the blocked users and allow to unblock them
 */
@ActivityScope
public class BlockListActivity extends BaseActivity implements BlockListScreen {

    @Inject
    BlockListPresenter presenter;
    @Inject
    BlockListRecyclerAdapter adapter;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_block_list)
    RecyclerView blockListRecyclerView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;


    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        setupBackIcon();
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_block_list;
    }

    @Override
    public void appendBlockListToBottom(List<DefaultUserModel> defaultUserModels) {
        adapter.appendBlockList(defaultUserModels);
    }

    @Override
    public void setBlockList(List<DefaultUserModel> defaultUserModels) {
        adapter.setBlockList(defaultUserModels);
    }

    @Override
    public void removeUnBlockedItem(int blockItemPosition) {
        adapter.removeItemAt(blockItemPosition);
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
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
    public void showLoadingAnimation() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingAnimation() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setupRefreshLayout() {
        refreshLayout.setOnRefreshListener(presenter::refreshComments);
    }

    @Override
    public void setupRecyclerView() {
        blockListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        blockListRecyclerView.setAdapter(adapter);
        adapter.setOnBottomReachedListener(presenter::fetchComments);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

}