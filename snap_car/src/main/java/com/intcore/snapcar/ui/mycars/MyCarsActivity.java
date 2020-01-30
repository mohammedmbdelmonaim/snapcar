package com.intcore.snapcar.ui.mycars;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.ui.addcar.AddCarActivity;
import com.intcore.snapcar.ui.confirmcommision.ConfirmCommissionActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

@ActivityScope
public class MyCarsActivity extends BaseActivity implements MyCarsScreen {

    @BindView(R.id.recyclerview_mycars)
    RecyclerView myCarsRecycler;
    @BindView(R.id.recyclerview_sold)
    RecyclerView soldsRecycler;
    @BindView(R.id.tv_sold)
    TextView tvSold;
    @BindView(R.id.tv_my_cars)
    TextView tvMyCars;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    MyCarsAdapter myCarsAdapter;
    @Inject
    SoldAdapter soldCarsAdapter;

    @Inject
    MyCarsPresenter presenter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.no_data_text_view)
    TextView noDataTextView;
    private int carId;
    private MyCarsApiResponse myCarsApiResponse;
    private String deleteReason;
    private RecyclerViewSkeletonScreen skeletonScreen;
    private RecyclerViewSkeletonScreen skeletonScreen2;

    @Override
    protected void onCreateActivityComponents() {
        ActivityComponent component = SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this));
        component.inject(this);

        ButterKnife.bind(this);
        myCarsRecycler.setLayoutManager(new LinearLayoutManager(this));

        soldsRecycler.setLayoutManager(new LinearLayoutManager(this));
        skeletonScreen =  Skeleton.bind(soldsRecycler)
                .adapter(soldCarsAdapter)
                .duration(1000)
                .count(10)
                .shimmer(false)
                .load(R.layout.item_skeleton_news)
                .show();
        skeletonScreen2 =  Skeleton.bind(myCarsRecycler)
                .adapter(myCarsAdapter)
                .duration(1000)
                .count(10)
                .shimmer(false)
                .load(R.layout.item_skeleton_news)
                .show();

        tvMyCars.setSelected(true);
        tvSold.setSelected(false);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activty_my_cars;
    }

    @Override
    public void updateUi(MyCarsApiResponse myCarsApiResponse) {
        soldsRecycler.postDelayed(() -> skeletonScreen.hide(), 3000);
        myCarsRecycler.postDelayed(() -> skeletonScreen2.hide(), 3000);
        this.myCarsApiResponse = myCarsApiResponse;
        myCarsAdapter.swapData(myCarsApiResponse.getFavoritesData());
        soldCarsAdapter.swapData(myCarsApiResponse.getSoldesData());
        if(myCarsApiResponse.getFavoritesData().size()==0){
            noDataTextView.setVisibility(View.VISIBLE);
        }else {
            noDataTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void carRemoved(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.car_was_removed));
        presenter.fetchCarsData();
    }

    @Override
    public void openSwearDialog(SettingsModel settingsApiResponse) {
        getUiUtil().getDialogBuilder(this, R.layout.dialog_sewar)
                .text(R.id.tv_sewar, settingsApiResponse.getSwearText())
                .clickListener(R.id.btn_reject, (dialog, view) -> dialog.dismiss())
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    Intent i = new Intent(this, ConfirmCommissionActivity.class);
                    i.putExtra("commission", settingsApiResponse.getCommissionPercentage());
                    i.putExtra("carId", carId);
                    startActivityForResult(i, 100);
                    dialog.dismiss();
                })
                .background(R.color.colorwhete)
                .build()
                .show();
    }

    @Override
    public void setCarId(int carId) {
        this.carId = carId;
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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

    @SuppressLint("WrongConstant")
    @Override
    public void showConfirmationDialog(int carId, int isTraked) {
        getUiUtil().getDialogBuilder(this, R.layout.layout_delete_car_dialog)
                .editText(R.id.et_review, text -> this.deleteReason = text)
                .clickListener(R.id.tv_submit, (dialog, view) -> {
                    if (deleteReason == null) {
                        showWarningMessage(getString(R.string.please_type_delete_reason));
                        return;
                    }
                    presenter.deleteCar(carId, isTraked, deleteReason);
                    dialog.dismiss();
                })
                .cancelable(true)
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }

    @OnClick({R.id.tv_sold, R.id.tv_my_cars})
    void onTabClicked(View v) {
        noDataTextView.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.tv_sold:
                myCarsRecycler.setVisibility(View.INVISIBLE);
                soldsRecycler.setVisibility(View.VISIBLE);
                tvMyCars.setSelected(false);
                tvSold.setSelected(true);
                if (myCarsApiResponse != null) {
                    if (myCarsApiResponse.getSoldesData().size() == 0) {
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.tv_my_cars:
                myCarsRecycler.setVisibility(View.VISIBLE);
                soldsRecycler.setVisibility(View.INVISIBLE);
                tvMyCars.setSelected(true);
                tvSold.setSelected(false);
                if (myCarsApiResponse != null) {
                    if (myCarsApiResponse.getFavoritesData().size() == 0) {
                        noDataTextView.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    @OnClick(R.id.iv_add_car)
    void onAddCarClicked() {
        startActivity(new Intent(this, AddCarActivity.class));
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }
}