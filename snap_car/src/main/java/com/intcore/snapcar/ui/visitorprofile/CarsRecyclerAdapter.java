package com.intcore.snapcar.ui.visitorprofile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

@ActivityScope
class CarsRecyclerAdapter extends RecyclerView.Adapter<CarsRecyclerAdapter.CarsViewHolder> {

    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<CarViewModel> carViewModels;

    @Inject
    CarsRecyclerAdapter(@ForActivity Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.carViewModels = new ArrayList<>();
        this.context = context;
    }

    private String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration])
                : coolFormat(d, iteration + 1));
    }

    @Override
    public CarsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CarsViewHolder(layoutInflater.inflate(R.layout.item_car, parent, false));
    }

    @Override
    public void onBindViewHolder(CarsViewHolder holder, int position) {
        holder.bind(carViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return carViewModels.size();
    }

    public void updateCars(List<CarViewModel> carViewModels) {
        this.carViewModels.clear();
        this.carViewModels.addAll(carViewModels);
        notifyDataSetChanged();
    }

    class CarsViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.iv_car)
        ImageView carImageView;
        @BindView(R.id.tv_description)
        TextView nameTextView;
        @BindView(R.id.tv_price)
        TextView priceTextView;
        @BindView(R.id.iv_verify)
        ImageView verifyImageView;
        @BindView(R.id.tv_year)
        TextView yearTextView;
        @BindView(R.id.tv_color)
        TextView colorTextView;
        @BindView(R.id.tv_country)
        TextView countryTextView;
        @BindView(R.id.tv_km)
        TextView distanceTextView;
        @BindView(R.id.tv_specification)
        TextView specificationTextView;
        @BindView(R.id.tv_gear)
        TextView gearTextView;
        @BindView(R.id.tv_price_before)
        TextView priceBeforeTextView;
        @BindView(R.id.tv_price_monthly)
        TextView priceMonthlyTextView;
        private CarViewModel carViewModel;

        CarsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ViewCarActivity.class);
                i.putExtra("carId", carViewModel.getId());
                context.startActivity(i);
            });
        }

        void bind(CarViewModel carViewModel) {
            setTopBottomMargins();
            this.carViewModel = carViewModel;
            priceBeforeTextView.setVisibility(View.GONE);
            priceMonthlyTextView.setVisibility(View.GONE);

            if (carViewModel.getPriceType() == 2) {
                priceTextView.setText(R.string.undetermined);
            } else if (carViewModel.getPriceType() == 1) {
                if (carViewModel.getPrice().length() < 4) {
                    priceTextView.setText(carViewModel.getPrice().concat(" ").concat(context.getString(R.string.sar)));
                } else {
                    priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPrice()), 0).concat(" ").concat(context.getString(R.string.sar)));
                }
            } else if (carViewModel.getPriceType() == 3) {
                priceTextView.setText(R.string.exchange);
            } else if (carViewModel.getPriceType() == 4) {
                if (carViewModel.getPriceAfter().length() < 4) {
                    priceTextView.setText(carViewModel.getPriceAfter().concat(" ").concat(context.getString(R.string.sar)));
                } else {
                    priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPriceAfter()), 0).concat(" ").concat(context.getString(R.string.sar)));
                }
                if (carViewModel.getPriceBefore().length() < 4) {
                    priceBeforeTextView.setText(carViewModel.getPriceBefore().concat(" ").concat(context.getString(R.string.sar)));
                } else {
                    priceBeforeTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPriceBefore()), 0).concat(" ").concat(context.getString(R.string.sar)));
                }
                priceBeforeTextView.setVisibility(View.VISIBLE);
                priceBeforeTextView.setPaintFlags(priceBeforeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else if (carViewModel.getPriceType() == 5) {
                if (carViewModel.getInstallmentPriceFrom().length() < 4) {
                    priceTextView.setText(carViewModel.getInstallmentPriceFrom().concat(" ").concat(context.getString(R.string.sar)));
                } else {
                    priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getInstallmentPriceFrom()), 0).concat(" ").concat(context.getString(R.string.sar)));
                }
                if (carViewModel.getInstallmentPriceTo().length() < 4) {
                    priceMonthlyTextView.setText(carViewModel.getInstallmentPriceTo().concat(" ").concat(itemView.getContext().getString(R.string.sar).concat(" ").concat(context.getString(R.string.monthly))));
                } else {
                    priceMonthlyTextView.setText(coolFormat(Double.parseDouble(carViewModel.getInstallmentPriceTo()), 0).concat(" ").concat(itemView.getContext().getString(R.string.sar).concat(" ").concat(context.getString(R.string.monthly))));
                }
                priceMonthlyTextView.setVisibility(View.VISIBLE);
            }

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());
            Double latitude = 0.0, longitude = 0.0;
            if (!TextUtils.isEmpty(carViewModel.getLatitude())) {
                latitude = Double.valueOf(carViewModel.getLatitude());
            }
            if (!TextUtils.isEmpty(carViewModel.getLongitude())) {
                longitude = Double.valueOf(carViewModel.getLongitude());
            }
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    String country = addresses.get(0).getCountryName();
                    countryTextView.setText(country);
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            nameTextView.setText(carViewModel.getCarName());
            if (carViewModel.getKilometer() != null) {
                if (carViewModel.getKilometer().length() < 3) {
                    distanceTextView.setText(carViewModel.getKilometer());
                } else {
                    distanceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getKilometer()), 0));
                }
            }
            yearTextView.setText(carViewModel.getManufacturingYear());
            if (carViewModel.getCarColorViewModel() != null) {
                colorTextView.setText(carViewModel.getCarColorViewModel().getName());
            }
            if (carViewModel.getImporterViewModel() != null) {
                specificationTextView.setText(carViewModel.getImporterViewModel().getName());
            }
            if (carViewModel.getMvpi() == 1) {
                verifyImageView.setVisibility(View.VISIBLE);
            } else {
                verifyImageView.setVisibility(View.GONE);
            }
            if (carViewModel.getTransmission() == GearType.AUTO) {
                gearTextView.setText(context.getString(R.string.auto));
            } else if (carViewModel.getTransmission() == GearType.NORMAL) {
                gearTextView.setText(context.getString(R.string.normal));
            }
            String image = "";
            if (carViewModel.getImageViewModelList().size() > 0) {
                image = carViewModel.getImageViewModelList().get(0).getImage();
            }
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(image))
                    .centerCrop()
                    .placeholder(R.drawable.car_photo)
                    .into(carImageView);
            if (!isEnglishLang()) {
                distanceTextView.setGravity(Gravity.RIGHT);
            }
        }

        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_extra));
                newParams.setMarginStart(context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == carViewModels.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_extra));
                newParams.setMarginStart(context.getResources().getDimensionPixelOffset(R.dimen.margin_micro));
                newParams.setMarginEnd(context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }
    }
}