package com.intcore.snapcar.ui.home;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@ActivityScope
class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final CategoryAdapter.OnItemClickListener onCountrySelected;
    private final LayoutInflater layoutInflater;
    private List<CategoryViewModel> countryViewModels;
    private final Context context;

    public CategoryAdapter(@ForActivity Context context, CategoryAdapter.OnItemClickListener onCountrySelected, List<CategoryViewModel> countryViewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.countryViewModels = countryViewModels;
        this.onCountrySelected = onCountrySelected;
        this.context = context;
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryAdapter.CategoryViewHolder(layoutInflater.inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, int position) {
        holder.bind(countryViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return countryViewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(CategoryViewModel viewModel);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        CategoryViewModel countryViewModel;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView countryImageView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onCountrySelected.onItemClicked(countryViewModel));
        }

        void bind(CategoryViewModel countryViewModel) {
            this.countryViewModel = countryViewModel;
            titleTextView.setText(countryViewModel.getName());
            if (getAdapterPosition() == countryViewModels.size() - 1)
                divider.setVisibility(View.GONE);
            countryImageView.setVisibility(View.GONE);
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