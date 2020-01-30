package com.intcore.snapcar.ui.editcar;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {


    private final ColorAdapter.OnItemClickListener onImporterSelected;
    private final LayoutInflater layoutInflater;
    private List<CarResourcesApiResponse.Colors> viewModels;
    private int selectedId;
    private final Context context;

    public ColorAdapter(@ForActivity Context context, ColorAdapter.OnItemClickListener onSelected,
                        List<CarResourcesApiResponse.Colors> viewModels, int selectedId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
        this.selectedId = selectedId;
        this.onImporterSelected = onSelected;
        this.context = context;
    }

    @Override
    public ColorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(ColorAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position), selectedId);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(CarResourcesApiResponse.Colors brands);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewGroup.LayoutParams layoutParams;
        CarResourcesApiResponse.Colors brand;
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
            itemView.setOnClickListener(v -> onImporterSelected.onItemClicked(brand));
            layoutParams = itemView.getLayoutParams();
        }

        void bind(CarResourcesApiResponse.Colors brand, int selectedId) {
            setTopBottomMargins();
            this.brand = brand;
            Log.d("minaaaa", String.valueOf(selectedId));
            if (selectedId != 0 && brand.getId() == selectedId) {

                selectedImage.setVisibility(View.VISIBLE);
            }
            brandIcon.setVisibility(View.GONE);
            if (isEnglishLang()) {
                titleTextView.setText(brand.getNameEn());
            } else {
                titleTextView.setText(brand.getNameAr());
            }

            if (getAdapterPosition() == viewModels.size() - 1)
                divider.setVisibility(View.GONE);

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
