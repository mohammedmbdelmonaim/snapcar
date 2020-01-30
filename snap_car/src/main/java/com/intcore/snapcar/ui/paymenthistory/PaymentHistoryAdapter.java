package com.intcore.snapcar.ui.paymenthistory;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.core.qualifier.ForActivity;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<PaymentHistoryApiResponse> model;
    private Context context;

    PaymentHistoryAdapter(@ForActivity Context context, List<PaymentHistoryApiResponse> model) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.model = model;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_payment_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(model.get(position));
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.tv_car_date)
        TextView dateTextView;
        @BindView(R.id.tv_car_date_end)
        TextView dateEndTextView;
        @BindView(R.id.car_name)
        TextView nameTextView;
        @BindView(R.id.tv_price)
        TextView priceTextView;
        @BindView(R.id.tv_price_type)
        TextView priceTypeTextView;
        @BindView(R.id.image_car)
        ImageView image;
        private char[] c = new char[]{'k', 'm', 'b', 't'};

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
        }

        void bind(PaymentHistoryApiResponse response) {
            dateTextView.setText(response.getPaidAt());
            nameTextView.setText(context.getString(R.string.item_deleted));
            if (response.getStatus().equals("1")) {
                priceTextView.setText(context.getString(R.string.sar) + response.getCommission());
                priceTypeTextView.setText(context.getString(R.string.commission));
                if (response.getCar() != null) {
                    Glide.with(context)
                            .load(ApiUtils.BASE_URL.concat(response.getCar().getImagesApiResponses().get(0).getImage()))
                            .centerCrop()
                            .into(image);
                    if (isEnglishLang()) {
                        nameTextView.setText(response.getCar().getCarNameEn());
                    } else {
                        nameTextView.setText(response.getCar().getCarName());
                    }
                }
            } else if (response.getStatus().equals("2")) {
                priceTextView.setText(context.getString(R.string.sar) + response.getTotalAmount());
                priceTypeTextView.setText(context.getString(R.string.premium_price));
                if (response.getCar() != null) {
                    Glide.with(context)
                            .load(ApiUtils.BASE_URL.concat(response.getCar().getImagesApiResponses().get(0).getImage()))
                            .centerCrop()
                            .into(image);
                    if (isEnglishLang())
                        nameTextView.setText(response.getCar().getCarNameEn());
                    else
                        nameTextView.setText(response.getCar().getCarName());
                }
            } else if (response.getStatus().equals("3")) {
                priceTextView.setText(context.getString(R.string.sar) + response.getTotalAmount());
                priceTypeTextView.setText(context.getString(R.string.featured_ads));
                if (response.getAds() != null) {
                    if (isEnglishLang()) {
                        nameTextView.setText(response.getAds().getNameEn());
                    } else {
                        nameTextView.setText(response.getAds().getNameAr());
                    }
                    dateEndTextView.setVisibility(View.VISIBLE);
                    dateEndTextView.setText(response.getAds().getEndDate());
                    dateTextView.setText(response.getAds().getStartDate());
                    Glide.with(context)
                            .load(ApiUtils.BASE_URL.concat(response.getAds().getImage()))
                            .centerCrop()
                            .into(image);
                }
            }
        }

        boolean isEnglishLang() {
            return Locale.getDefault().getLanguage().equals("en");
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == model.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }
    }
}