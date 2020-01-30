package com.intcore.snapcar.ui.login;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@ActivityScope
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private final OnItemClickListener onCountrySelected;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<CountryViewModel> countryViewModels;

    public CountryAdapter(@ForActivity Context context, OnItemClickListener onCountrySelected, List<CountryViewModel> countryViewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.countryViewModels = countryViewModels;
        this.onCountrySelected = onCountrySelected;
        this.context = context;
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CountryViewHolder(layoutInflater.inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(CountryViewHolder holder, int position) {
        holder.bind(countryViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return countryViewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(CountryViewModel viewModel);
    }

    class CountryViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        CountryViewModel countryViewModel;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.tv_code)
        TextView codeTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView countryImageView;

        CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> onCountrySelected.onItemClicked(countryViewModel));
        }

        void bind(CountryViewModel countryViewModel) {
            setTopBottomMargins();
            this.countryViewModel = countryViewModel;
            titleTextView.setText(countryViewModel.getName());
            codeTextView.setText(String.valueOf(countryViewModel.getPhoneCode()));
            if (getAdapterPosition() == countryViewModels.size() - 1)
                divider.setVisibility(View.GONE);
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(countryViewModel.getImage()))
                    .centerCrop()
                    .into(countryImageView);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0);
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == countryViewModels.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }
    }
}