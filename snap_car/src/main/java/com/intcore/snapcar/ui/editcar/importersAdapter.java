package com.intcore.snapcar.ui.editcar;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class importersAdapter extends RecyclerView.Adapter<importersAdapter.ViewHolder> {


    private final importersAdapter.OnItemClickListener onImporterSelected;
    private final LayoutInflater layoutInflater;
    private List<ImporterViewModel> viewModels;
    private int selectedId;
    private final Context context;

    public importersAdapter(@ForActivity Context context, importersAdapter.OnItemClickListener onSelected,
                            List<ImporterViewModel> viewModels, int selectedId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
        this.selectedId = selectedId;
        this.onImporterSelected = onSelected;
        this.context = context;
    }

    @Override
    public importersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new importersAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(importersAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position), selectedId);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(ImporterViewModel importerId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewGroup.LayoutParams layoutParams;
        ImporterViewModel importers;
        @BindView(R.id.tv_title)
        TextView titleTextView;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.iv_country)
        RoundedImageView brandIcon;
        @BindView(R.id.tv_code)
        View selectedImage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onImporterSelected.onItemClicked(importers));
            layoutParams = itemView.getLayoutParams();
        }

        void bind(ImporterViewModel viewModel, int selectedId) {
            setTopBottomMargins();
            this.importers = viewModel;
            if (selectedId != 0 && viewModel.getId() == selectedId) {

                selectedImage.setVisibility(View.VISIBLE);
            }
            brandIcon.setVisibility(View.GONE);
            brandIcon.setVisibility(View.GONE);
            if (isEnglishLang()) {
                titleTextView.setText(importers.getName());
            } else {
                titleTextView.setText(importers.getName());
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
