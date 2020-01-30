package com.intcore.snapcar.ui.feedback;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.makeramen.roundedimageview.RoundedImageView;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {


    private final SubjectsAdapter.OnItemClickListener onImporterSelected;
    private final LayoutInflater layoutInflater;
    private List<FeedbackApiResponse.FeedBackData> viewModels;
    private Context context;
    private int selectedId;

    public SubjectsAdapter(@ForActivity Context context, SubjectsAdapter.OnItemClickListener onSelected,
                           List<FeedbackApiResponse.FeedBackData> viewModels, int selectedId) {
        this.layoutInflater = LayoutInflater.from(context);
        this.viewModels = viewModels;
        this.context = context;
        this.onImporterSelected = onSelected;
        this.selectedId = selectedId;
    }

    @Override
    public SubjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubjectsAdapter.ViewHolder(layoutInflater.inflate(R.layout.item_add_car_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(SubjectsAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position), selectedId);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(FeedbackApiResponse.FeedBackData brands);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FeedbackApiResponse.FeedBackData brand;
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
        }

        void bind(FeedbackApiResponse.FeedBackData brand, int selectedId) {
            this.brand = brand;
            if (selectedId != 0 && brand.getId() == selectedId) {
                selectedImage.setVisibility(View.VISIBLE);
            }
            if (isEnglishLang()) {
                titleTextView.setText(brand.getGetNameEn());
            } else {
                titleTextView.setText(brand.getNameAr());
            }
            brandIcon.setVisibility(View.GONE);
            if (getAdapterPosition() == viewModels.size() - 1)
                divider.setVisibility(View.GONE);
        }

        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }
    }
}
