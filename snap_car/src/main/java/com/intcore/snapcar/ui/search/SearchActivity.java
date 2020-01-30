package com.intcore.snapcar.ui.search;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.ui.addinterest.AddInterestActivityArgs;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class SearchActivity extends BaseActivity implements SearchScreen {

    @BindView(R.id.iv_grid)
    ImageView gridImageView;
    @BindView(R.id.iv_list)
    ImageView listImageView;
    @BindView(R.id.rv_cars)
    RecyclerView carsRecyclerView;
    @BindView(R.id.rv_feature)
    RecyclerView featureRecyclerView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    SearchPresenter presenter;
    @Inject
    SearchRecyclerAdapter adapter;
    @Inject
    FeatureRecyclerAdapter featureAdapter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private boolean isSearchViewAsList = true;
//    private RecyclerViewSkeletonScreen skeletonScreen;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        gridImageView.setImageAlpha(100);
        listImageView.setImageAlpha(200);
        setupBackIcon();
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    public void updateUi(List<CarViewModel> models) {
//        carsRecyclerView.postDelayed(() -> skeletonScreen.hide(), 3000);
        adapter.setSearchResultList(models);
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
    public void setupRecyclerView() {
        carsRecyclerView.setLayoutManager(isSearchViewAsList ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));
        adapter.notifyDataSetChanged();
        carsRecyclerView.setAdapter(adapter);
//        skeletonScreen =  Skeleton.bind(carsRecyclerView)
//                .adapter(adapter)
//                .duration(1200)
//                .count(10)
//                .shimmer(false)
//                .load(R.layout.item_skeleton_news)
//                .show();
        adapter.setOnBottomReachedListener(presenter::fetchSearchResult);
    }

    @Override
    public void appendSearchResultToBottom(List<CarViewModel> carModels) {
        adapter.appendSearchResultList(carModels);
    }

    @Override
    public void updateFeatureCars(List<CarViewModel> carViewModels) {
        featureAdapter.setData(carViewModels);
    }

    @Override
    public void setupFeatureRecyclerView() {
        featureRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        featureRecyclerView.setAdapter(featureAdapter);
    }

    @Override
    public void showPopUpInterestMessage(SearchRequestModel searchRequestModel) {
        getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                .text(R.id.tv_message, getString(R.string.find_your_match_car))
                .clickListener(R.id.yes, (dialog, view) -> {
                    new AddInterestActivityArgs(searchRequestModel)
                            .launch(SearchActivity.this);
                    finish();
                }).clickListener(R.id.no, (dialog, view) -> dialog.dismiss())
                .background(R.color.transparent)
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }

    @Override
    public void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.iv_grid)
    void onGridClicked() {
        gridImageView.setImageAlpha(200);
        listImageView.setImageAlpha(100);
        isSearchViewAsList = false;
        adapter.isSearchViewAsList(false);
        setupRecyclerView();
    }

    @OnClick(R.id.iv_list)
    void onListClicked() {
        gridImageView.setImageAlpha(100);
        listImageView.setImageAlpha(200);
        isSearchViewAsList = true;
        adapter.isSearchViewAsList(true);
        setupRecyclerView();
    }

    @OnClick(R.id.tv_sort)
    void onSortClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onPaymentFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onPaymentFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.item_price) {
            presenter.onPriceSortClicked();
        }
       else if (menuItem.getItemId() == R.id.item_price_low) {
            presenter.onPriceSortLowClicked();
        }
        else if (menuItem.getItemId() == R.id.item_newest) {
            presenter.onNewestSortClicked();
        }
        return true;
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

}
