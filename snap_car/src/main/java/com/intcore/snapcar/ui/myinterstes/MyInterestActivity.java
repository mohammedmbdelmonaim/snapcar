package com.intcore.snapcar.ui.myinterstes;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.ui.addinterest.AddInterestActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
    This class is responsible for Showing my interests added before
*/
@ActivityScope
public class MyInterestActivity extends BaseActivity implements MyInterestScreen {

    @Inject
    MyInterestRecyclerAdapter adapter;
    @Inject
    MyInterestPresenter presenter;
    @BindView(R.id.rv_interest)
    RecyclerView interestRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_add_interest)
    ImageView ivAddInterest;
    @BindView(R.id.toolbar_container)
    RelativeLayout toolbarContainer;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_my_interstes;
    }

    @Override
    public void setupRecyclerView() {
        interestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        interestRecyclerView.setAdapter(adapter);
        adapter.setOnBottomReachedListener(presenter::fetchInterest);
        //ItemTouchHelper is implemented for swipe for delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.END, (viewHolder, direction, position) -> {
            if (viewHolder instanceof MyInterestRecyclerAdapter.MyInterestViewHolder) {
                final CarViewModel deletedItem = adapter.getInterestList().get(viewHolder.getAdapterPosition());
                showConfirmationDialog(deletedItem, viewHolder.getAdapterPosition());
            }
        });
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(interestRecyclerView);
    }


    void showConfirmationDialog(CarViewModel deletedItem, int position) {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                .text(R.id.tv_message, getString(R.string.are_sure_delete_this_interest))
                .clickListener(R.id.no, (dialog, view) -> {
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                })
                .clickListener(R.id.yes, (dialog, view) -> {
                    presenter.removeInterestItem(position, deletedItem.getId());
                    dialog.dismiss();
                })
                .background(R.color.transparent)
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .build()
                .show();
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
    public void setInterestList(List<CarViewModel> carModels) {
        adapter.setInterestList(carModels);
    }

    @Override
    public void appendInterestListToBottom(List<CarViewModel> carModels) {
        adapter.appendInterestList(carModels);
    }

    @Override
    public void removeInterestItem(int interestItemPosition) {
        adapter.removeItemAt(interestItemPosition);
    }

    @Override
    public void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(presenter::refreshData);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.iv_add_interest)
    void onAddInterestClicked() {
        startActivity(new Intent(this, AddInterestActivity.class));
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    @OnClick(R.id.iv_question)
    void onQuestionClicked() {
        showHotZoneInfoPopup();
    }

    private void showHotZoneInfoPopup() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.interest_info))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
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
}