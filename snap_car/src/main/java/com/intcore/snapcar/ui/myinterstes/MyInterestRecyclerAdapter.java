package com.intcore.snapcar.ui.myinterstes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.ui.OnBottomReachedListener;
import com.intcore.snapcar.ui.editinterest.EditInterestActivityArgs;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
class MyInterestRecyclerAdapter extends RecyclerView.Adapter<MyInterestRecyclerAdapter.MyInterestViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<CarViewModel> interestList;
    private OnBottomReachedListener onBottomReachedListener;

    @Inject
    MyInterestRecyclerAdapter(@ForActivity Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.interestList = new ArrayList<>();
        this.context = context;
    }

    @Override
    public MyInterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyInterestViewHolder(layoutInflater.inflate(R.layout.item_interest, parent, false));
    }

    @Override
    public void onBindViewHolder(MyInterestViewHolder holder, int position) {
        if (position == interestList.size() - 1 && onBottomReachedListener != null) {
            onBottomReachedListener.loadMore();
        }
        holder.bind(interestList.get(position));
    }

    @Override
    public int getItemCount() {
        return interestList.size();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        Preconditions.checkNonNull(onBottomReachedListener);
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void appendInterestList(List<CarViewModel> newInterestList) {
        int i = interestList.size();
        interestList.addAll(newInterestList);
        notifyItemRangeInserted(i, newInterestList.size());
    }

    public void removeItemAt(int interestItemPosition) {
        if (interestItemPosition > -1 && interestItemPosition < interestList.size()) {
            interestList.remove(interestItemPosition);
            notifyItemRemoved(interestItemPosition);
            notifyDataSetChanged();
        }
    }

    public List<CarViewModel> getInterestList() {
        return interestList;
    }

    public void setInterestList(List<CarViewModel> newInterestList) {
        this.interestList.clear();
        this.interestList.addAll(newInterestList);
        notifyDataSetChanged();
    }

    class MyInterestViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.tv_name)
        TextView nameTextView;
        @BindView(R.id.civ_interest)
        CircleImageView interestCircleImageView;
        @BindView(R.id.view_forground)
        ConstraintLayout viewForeground;
        private CarViewModel interestViewModel;

        @SuppressLint("ResourceType")
        MyInterestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> new EditInterestActivityArgs(interestViewModel)
                    .launch(context));
            layoutParams = itemView.getLayoutParams();
            Resources res = context.getResources();
            TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
            nameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icons.getDrawable(1), null);
        }

        public void bind(CarViewModel interestViewModel) {
            setTopBottomMargins();
            this.interestViewModel = interestViewModel;

            if (interestViewModel.getBrandsViewModel() != null) {
                nameTextView.setText(interestViewModel.getBrandsViewModel().getName().concat(" ").concat(context.getString(R.string.interest)));
                if (interestViewModel.getBrandId() != 0) {
                    Glide.with(itemView)
                            .load(ApiUtils.BASE_URL.concat(interestViewModel.getBrandsViewModel().getImage()))
                            .centerCrop()
                            .into(interestCircleImageView);
                }
            } else {
                nameTextView.setText(R.string.all);
//                interestCircleImageView.setImageResource(R.drawable.car_photo);
            }
            if (interestViewModel.getBrandsViewModel() == null) {
                interestCircleImageView.setImageResource(R.drawable.border_gradient);
            }
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == interestList.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }
    }
}