package com.intcore.snapcar.ui.discounts;

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
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.UiUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * This class responsible for showing other disocunts came from dashboard
 */
@FragmentScope
public class DiscountFragment extends Fragment implements DiscountScreen {

    @Inject
    DiscountPresenter presenter;
    @Inject
    DiscountAdapter adapter;
    @Inject
    UiUtil uiUtil;
    @BindView(R.id.rv_other_discount)
    RecyclerView otherDiscountRecyclerView;

    public DiscountFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_discount, container, false);
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
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessToast(message)
                .show();
    }

    @Override
    public void setupRecyclerView() {
        otherDiscountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        otherDiscountRecyclerView.setAdapter(adapter);
    }

}