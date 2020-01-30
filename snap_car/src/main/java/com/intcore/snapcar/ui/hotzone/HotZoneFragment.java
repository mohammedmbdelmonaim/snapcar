package com.intcore.snapcar.ui.hotzone;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.model.coupon.CouponViewModel;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.UiUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@FragmentScope
public class HotZoneFragment extends Fragment implements HotZoneScreen {

    @Inject
    HotZonePresenter presenter;

    @Inject
    HotZoneAdapter adapter;

    @Inject
    UiUtil uiUtil;
    @BindView(R.id.rv_hot_zone_discount)
    RecyclerView hotZoneDiscountRecyclerView;

    public HotZoneFragment() {
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
        return inflater.inflate(R.layout.fragment_hot_zone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            presenter.onViewCreated(bundle.getParcelableArrayList(CouponViewModel.TAG));
        }
    }

    @Override
    public void updateUi(ArrayList<CouponViewModel> couponViewModels) {
        adapter.updateData(couponViewModels);
    }

    @Override
    public void setupRecyclerView() {
        hotZoneDiscountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hotZoneDiscountRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessToast(message)
                .show();
    }
}