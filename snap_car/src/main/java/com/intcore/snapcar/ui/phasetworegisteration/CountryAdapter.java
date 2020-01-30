package com.intcore.snapcar.ui.phasetworegisteration;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final CountryAdapter.OnItemClickListener onCountrySelected;
    private final LayoutInflater layoutInflater;
    private List<CountryViewModel> countryViewModels;

    public CountryAdapter(@ForActivity Context context, CountryAdapter.OnItemClickListener onCountrySelected, List<CountryViewModel> countryViewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.countryViewModels = countryViewModels;
        this.onCountrySelected = onCountrySelected;
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

        CountryViewModel countryViewModel;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView countryImageView;

        CountryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onCountrySelected.onItemClicked(countryViewModel));
        }

        void bind(CountryViewModel countryViewModel) {
            this.countryViewModel = countryViewModel;
            titleTextView.setText(countryViewModel.getName());
            if (getAdapterPosition() == countryViewModels.size() - 1)
                divider.setVisibility(View.GONE);
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(countryViewModel.getImage()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_doc)
                    .into(countryImageView);
        }
    }
}