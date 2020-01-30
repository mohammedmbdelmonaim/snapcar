package com.intcore.snapcar.ui.addinterest;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@ActivityScope
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final ColorAdapter.OnItemClickListener onCountrySelected;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<CarColorViewModel> countryViewModels;

    public ColorAdapter(@ForActivity Context context, ColorAdapter.OnItemClickListener onCountrySelected, List<CarColorViewModel> countryViewModels) {
        this.layoutInflater = LayoutInflater.from(context);
        this.countryViewModels = countryViewModels;
        this.onCountrySelected = onCountrySelected;
        this.context = context;
    }

    @Override
    public ColorAdapter.ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorAdapter.ColorViewHolder(layoutInflater.inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(ColorAdapter.ColorViewHolder holder, int position) {
        holder.bind(countryViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return countryViewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(CarColorViewModel viewModel);
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        CarColorViewModel countryViewModel;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView countryImageView;

        ColorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> onCountrySelected.onItemClicked(countryViewModel));
        }

        void bind(CarColorViewModel countryViewModel) {
            setTopBottomMargins();
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
        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }
    }
}