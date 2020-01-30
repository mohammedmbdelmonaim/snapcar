package com.intcore.snapcar.ui.addcar;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    private final BrandsAdapter.OnItemClickListener onImporterSelected;
    private final LayoutInflater layoutInflater;
    private List<BrandsViewModel> viewModels;
    private Context context;
    private int selectedId;

    public BrandsAdapter(@ForActivity Context context,
                         BrandsAdapter.OnItemClickListener onSelected,
                         List<BrandsViewModel> viewModels,
                         int selectedId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.onImporterSelected = onSelected;
        this.viewModels = viewModels;
        this.selectedId = selectedId;
        this.context = context;
    }

    @Override
    public BrandsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrandsAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(BrandsAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(BrandsViewModel brands);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        BrandsViewModel brand;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.iv_country)
        RoundedImageView brandIcon;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.tv_code)
        View selectedImage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> onImporterSelected.onItemClicked(brand));
        }

        void bind(BrandsViewModel brand) {
            setTopBottomMargins();
            this.brand = brand;
            if (selectedId != 0 && brand.getId() == selectedId) {
                selectedImage.setVisibility(View.VISIBLE);
            } else {
                selectedImage.setVisibility(View.GONE);
            }
            if (!isEnglishLang()) {
                titleTextView.setText(brand.getName());
                titleTextView.setGravity(Gravity.RIGHT);
            } else {
                titleTextView.setText(brand.getName());
                titleTextView.setGravity(Gravity.LEFT);
            }
            Glide.with(context)
                    .load(ApiUtils.BASE_URL.concat(brand.getImage()))
                    .centerCrop()
                    .into(brandIcon);
            if (getAdapterPosition() == viewModels.size() - 1)
                divider.setVisibility(View.GONE);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == viewModels.size() - 1) {
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