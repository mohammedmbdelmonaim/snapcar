package com.intcore.snapcar.ui.editcar;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YearsAdapter extends RecyclerView.Adapter<YearsAdapter.ViewHolder> {


    private final YearsAdapter.OnItemClickListener onImporterSelected;
    private final LayoutInflater layoutInflater;
    private List<String> viewModels;
    private String selected;
    private final Context context;

    public YearsAdapter(@ForActivity Context context, YearsAdapter.OnItemClickListener onSelected,
                        List<String> viewModels, String selected) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
        this.onImporterSelected = onSelected;
        this.selected = selected;
        this.context = context;
    }

    @Override
    public YearsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YearsAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(YearsAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position), selected);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(String year);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewGroup.LayoutParams layoutParams;
        String year;
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
            itemView.setOnClickListener(v -> onImporterSelected.onItemClicked(year));
            layoutParams = itemView.getLayoutParams();
        }

        void bind(String viewModel, String selected) {
            setTopBottomMargins();
            this.year = viewModel;
            if (!selected.isEmpty() && selected.equals(year)) {
                selectedImage.setVisibility(View.VISIBLE);
            }
            brandIcon.setVisibility(View.GONE);
            titleTextView.setText(year);
            brandIcon.setVisibility(View.GONE);
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
            if (Locale.getDefault().getLanguage().equals("en")) {
                return true;
            } else return false;
        }
    }
}
