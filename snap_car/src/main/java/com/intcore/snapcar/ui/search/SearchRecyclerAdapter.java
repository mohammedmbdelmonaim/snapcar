package com.intcore.snapcar.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.ui.OnBottomReachedListener;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

@ActivityScope
class SearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final SearchPresenter presenter;
    private final Context context;
    private OnBottomReachedListener onBottomReachedListener;
    private List<CarViewModel> searchResultList;
    private boolean isSearchViewAsList = true;
    private static String[] c = new String[]{" k", " m", " b", " t"};


    @Inject
    SearchRecyclerAdapter(@ForActivity Context context, SearchPresenter presenter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.searchResultList = new ArrayList<>();
        this.presenter = presenter;
        this.context = context;
        if (!LocaleUtil.getLanguage(context).equals("en")) {
            c = new String[]{" الف", " مليون", " بليون", " t"};
        }
    }

    private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration])
                : coolFormat(d, iteration + 1));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isSearchViewAsList) {
            return new ListViewHolder(layoutInflater.inflate(R.layout.item_near_by, parent, false));
        } else {
            return new GridViewHolder(layoutInflater.inflate(R.layout.item_car_search, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == searchResultList.size() - 1 && onBottomReachedListener != null) {
            onBottomReachedListener.loadMore();
        }
        if (isSearchViewAsList) {
            ((ListViewHolder) holder).bind(searchResultList.get(position));
        } else {
            ((GridViewHolder) holder).bind(searchResultList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return searchResultList.size();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        Preconditions.checkNonNull(onBottomReachedListener);
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setSearchResultList(List<CarViewModel> newSearchResult) {
        this.searchResultList.clear();
        this.searchResultList.addAll(newSearchResult);
        notifyDataSetChanged();
    }

    public void appendSearchResultList(List<CarViewModel> newSearchResultList) {
        int i = searchResultList.size();
        searchResultList.addAll(newSearchResultList);
        notifyItemRangeInserted(i, newSearchResultList.size());
    }

    public void isSearchViewAsList(boolean isSearchViewAsList) {
        this.isSearchViewAsList = isSearchViewAsList;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;

        @BindView(R.id.iv_near_by)
        ImageView carImageView;
        @BindView(R.id.tv_description)
        TextView nameTextView;
        @BindView(R.id.tv_price)
        TextView priceTextView;
        @BindView(R.id.tv_year)
        TextView yearTextView;
        @BindView(R.id.tv_color)
        TextView colorTextView;
        @BindView(R.id.tv_km)
        TextView distanceTextView;
        @BindView(R.id.tv_country)
        TextView countryTextView;
        @BindView(R.id.tv_specification)
        TextView specificationTextView;
        @BindView(R.id.tv_gear)
        TextView gearTextView;
        @BindView(R.id.tv_price_before)
        TextView priceBeforeTextView;
        @BindView(R.id.tv_price_monthly)
        TextView priceMonthlyTextView;
        @BindView(R.id.fav_progress_bar)
        ProgressBar favProgressBar;
        @BindView(R.id.iv_fav)
        ImageView favoriteImageView;
        @BindView(R.id.tv_verify)
        TextView verifyTextView;
        private Geocoder geocoder;
        private List<Address> addresses;
        private CarViewModel carViewModel;

        ListViewHolder(View itemView) {
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


            if (carViewModel.getMvpi() == 1) {
                verifyTextView.setVisibility(View.VISIBLE);
            } else {
                verifyTextView.setVisibility(View.GONE);
            }
            Double latitude = 0.0, longitude = 0.0;
            if (!TextUtils.isEmpty(carViewModel.getLatitude())) {
                latitude = Double.valueOf(carViewModel.getLatitude());
            }
            if (!TextUtils.isEmpty(carViewModel.getLongitude())) {
                longitude = Double.valueOf(carViewModel.getLongitude());
            }
            try {
                geocoder = new Geocoder(context);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String country = addresses.get(0).getCountryName();
                countryTextView.setText(country);
            } catch (Exception e) {
                Timber.e(e);
            }
            nameTextView.setText(carViewModel.getCarName());
            distanceTextView.setText(carViewModel.getKilometer());
            yearTextView.setText(carViewModel.getManufacturingYear());
            favoriteImageView.setActivated(carViewModel.isFavorite());
            if (carViewModel.getCarColorViewModel() != null) {
                colorTextView.setText(carViewModel.getCarColorViewModel().getName());
            }
            if (carViewModel.getImporterViewModel() != null) {
                specificationTextView.setText(carViewModel.getImporterViewModel().getName());
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
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @OnClick(R.id.iv_fav)
        void onFavoriteClicked() {
            presenter.onFavoriteClicked(String.valueOf(carViewModel.getId()), null, new OperationListener<Void>() {
                @Override
                public void onPreOperation() {
                    favProgressBar.setVisibility(View.VISIBLE);
                    favoriteImageView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPostOperation() {
                    favProgressBar.setVisibility(View.INVISIBLE);
                    favoriteImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(Void element) {
                    favoriteImageView.setActivated(!favoriteImageView.isActivated());
                }

                @Override
                public void onError(Throwable t) {
                    presenter.processError(t);
                }
            });
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        0);
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == searchResultList.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.item_card)
        ConstraintLayout layout;
        private CarViewModel carViewModel;

        GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();

            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ViewCarActivity.class);
                i.putExtra("carId", carViewModel.getId());
                context.startActivity(i);
            });
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //int width = (displayMetrics.widthPixels/2) - 20;
            //layout.getLayoutParams().width = width;
        }

        public void bind(CarViewModel carViewModel) {
//            setTopBottomMargins();
            this.carViewModel = carViewModel;
//            layout.setMaxWidth((int) getWidth());
            float x = getWidth();
            priceBeforeTextView.setVisibility(View.GONE);
            priceMonthlyTextView.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(carViewModel.getPrice())) {
                if (carViewModel.getPrice().contentEquals("0")) {
                    priceTextView.setText(R.string.undetermined);
                } else {
                    priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPrice()), 0).concat(" ").concat(context.getString(R.string.sar)));
                }
            } else {
                if (!TextUtils.isEmpty(carViewModel.getExchange())) {
                    if (carViewModel.getExchange().contentEquals("1")) {
                        priceTextView.setText(R.string.exchange);
                    } else if (carViewModel.getExchange().contentEquals("0")) {
                        if (!TextUtils.isEmpty(carViewModel.getPriceAfter()) &&
                                !TextUtils.isEmpty(carViewModel.getPriceBefore())) {
                            priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPriceAfter()), 0).concat(" ").concat(context.getString(R.string.sar)));
                            priceBeforeTextView.setText(coolFormat(Double.parseDouble(carViewModel.getPriceBefore()), 0).concat(" ").concat(context.getString(R.string.sar)));
                            priceBeforeTextView.setVisibility(View.VISIBLE);
                            priceBeforeTextView.setPaintFlags(priceBeforeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else if (!TextUtils.isEmpty(carViewModel.getInstallmentPriceFrom().concat(" ").concat(context.getString(R.string.sar)))
                                && !TextUtils.isEmpty(carViewModel.getInstallmentPriceTo())) {
                            priceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getInstallmentPriceFrom()), 0).concat(" ").concat(context.getString(R.string.sar)));
                            priceMonthlyTextView.setText("/ ".concat(carViewModel.getInstallmentPriceTo()).concat(" ").concat(context.getString(R.string.sar)).concat(" ").concat(context.getString(R.string.monthly)));
                            priceMonthlyTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
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
                String country = addresses.get(0).getCountryName();
                countryTextView.setText(country);
            } catch (Exception e) {
                Timber.e(e);
                if (carViewModel.getCountryViewModel() != null)
                    countryTextView.setText(carViewModel.getCountryViewModel().getName());
            }
            nameTextView.setText(carViewModel.getCarName());
            distanceTextView.setText(carViewModel.getKilometer());
            yearTextView.setText(carViewModel.getManufacturingYear());
            if (carViewModel.getCarColorViewModel() != null) {
                colorTextView.setText(carViewModel.getCarColorViewModel().getName());
            }
            if (carViewModel.getImporterViewModel() != null) {
                specificationTextView.setText(carViewModel.getImporterViewModel().getName());
            }
            // sam
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
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == 0) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_extra));
                itemView.setLayoutParams(newParams);
            } else if (getAdapterPosition() == searchResultList.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_micro),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin_extra));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
        }

        private float getWidth() {
            Display display = ((SearchActivity) context).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            float density = context.getResources().getDisplayMetrics().density;
            return ((outMetrics.widthPixels / density)) / 2;
        }
    }
}