package com.intcore.snapcar.ui.home;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@ActivityScope
class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandModelViewHolder> {

    private final BrandAdapter.OnItemClickListener onCountrySelected;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<BrandsViewModel> countryViewModels;

    BrandAdapter(@ForActivity Context context,
                 BrandAdapter.OnItemClickListener onCountrySelected,
                 List<BrandsViewModel> countryViewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.countryViewModels = countryViewModels;
        this.onCountrySelected = onCountrySelected;
        this.context = context;
    }

    @Override
    public BrandModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrandModelViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(BrandModelViewHolder holder, int position) {
        holder.bind(countryViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return countryViewModels.size();
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    public interface OnItemClickListener {
        void onItemClicked(BrandsViewModel viewModel);
    }

    class BrandModelViewHolder extends RecyclerView.ViewHolder {

        BrandsViewModel countryViewModel;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView countryImageView;

        BrandModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onCountrySelected.onItemClicked(countryViewModel));
        }

        void bind(BrandsViewModel countryViewModel) {
            this.countryViewModel = countryViewModel;
            titleTextView.setText(countryViewModel.getName());
            Glide.with(context)
                    .load(ApiUtils.BASE_URL.concat(countryViewModel.getImage()))
                    .centerCrop()
                    .into(countryImageView);
            if (isEnglishLang()) {
                titleTextView.setGravity(Gravity.LEFT);
            } else {
                titleTextView.setGravity(Gravity.RIGHT);
            }
            if (getAdapterPosition() == countryViewModels.size() - 1)
                divider.setVisibility(View.GONE);

            if (!isEnglishLang()) {
                titleTextView.setGravity(Gravity.RIGHT);
            } else {
                titleTextView.setGravity(Gravity.LEFT);
            }
        }
        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }
    }
}