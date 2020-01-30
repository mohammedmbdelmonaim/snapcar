package com.intcore.snapcar.ui.favorites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;
import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FavoritesAdapter extends RecyclerView.Adapter {

    private static String[] c = new String[]{" k", " m", " b", " t"};
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<FavoritesApiResponse.Data> favorites = new ArrayList<>();
    private FavoritesPresenter presenter;
    private String lat, lon;
    private FavoritesScreen screen;
    private Geocoder geocoder;
    private List<Address> addresses;
    private boolean isEnglish;

    @Inject
    FavoritesAdapter(@ForFragment Context context, FavoritesPresenter presenter, FavoritesScreen screen) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.presenter = presenter;
        this.screen = screen;
        this.geocoder = new Geocoder(context);
        this.isEnglish = isEnglishLang();
        if (!isEnglishLang()) {
            c = new String[]{" الف", " مليون", " بليون", " t"};
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new FavoritesAdapter.CarViewHolder(layoutInflater.inflate(R.layout.item_my_favorite_car, parent, false));
        } else if (viewType == 2) {
            return new FavoritesAdapter.ShowroomViewHolder(layoutInflater.inflate(R.layout.item_my_favorite_showroom, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FavoritesApiResponse.Data fav = favorites.get(position);
        if (fav.getFavouriteable() != null) {
            if (fav.getType().equals("Car")) {
                ((CarViewHolder) holder).bindView(fav);
            } else if (fav.getType().equals("User")) {
                ((ShowroomViewHolder) holder).bindView(fav);
            }
        }
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (favorites.get(position).getType()) {
            case "Car":
                return 1;
            case "User":
                return 2;
            default:
                return -1;
        }
    }

    public void swapData(List<FavoritesApiResponse.Data> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    class CarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_car)
        ImageView carImageView;
        @BindView(R.id.car_name)
        TextView carNameTextView;
        @BindView(R.id.car_price)
        TextView carPriceTextView;
        @BindView(R.id.car_discount)
        TextView carDiscountTextView;
        @BindView(R.id.car_year)
        TextView carYearTextView;
        @BindView(R.id.car_color)
        TextView carColorTextView;
        @BindView(R.id.car_races)
        TextView carRacesTextView;
        @BindView(R.id.car_gear_type)
        TextView carTransmissionTextView;
        @BindView(R.id.car_location)
        TextView carCityTextView;
        @BindView(R.id.car_importer)
        TextView carImporterTextView;
        @BindView(R.id.examinationImage)
        ImageView examinationImageView;
        @BindView(R.id.fav_ic)
        ImageView favoriteImageView;
        private int carId;

        CarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            favoriteImageView.setOnClickListener(this);
        }

        void bindView(FavoritesApiResponse.Data fav) {
            String carName;
            if (isEnglish)
                carName = fav.getFavouriteable().getCarNameEn();
            else
                carName = fav.getFavouriteable().getCarName();

            int isTracked = fav.getFavouriteable().getIsTracked();
            String carPrice;
            if (fav.getFavouriteable().getPrice() == null) {
                carPrice = "";
            } else {
                carPrice = fav.getFavouriteable().getPrice();
            }

            String carAfter = "";
            if (fav.getFavouriteable().getPriceAfter() == null) {
                carAfter = "";
            } else carAfter = fav.getFavouriteable().getPriceAfter();

            String carInstallTo = "";
            if (fav.getFavouriteable().getInstallmenTo() == null) {
                carInstallTo = "";
            } else carInstallTo = fav.getFavouriteable().getInstallmenTo();

            String carYear = fav.getFavouriteable().getManufacturingYear();
            int carExchange = fav.getFavouriteable().getExchange();
            String carInstallFrom = fav.getFavouriteable().getInstallmentFrom();
            CarResourcesApiResponse.Colors Carcolor = fav.getFavouriteable().getCarCalor();
            String carKmTo = fav.getFavouriteable().getKilometer();
            String importerName = "";
            String carCity = "";
            if (fav.getFavouriteable().getUser().getCity() != null) {
                if (isEnglish) {
                    carCity = fav.getFavouriteable().getUser().getCity().getNameEn();
                } else {
                    carCity = fav.getFavouriteable().getUser().getCity().getNameEn();
                }
            }

            if (fav.getFavouriteable().getCarSpecification() != null) {
                if (isEnglish)
                    importerName = fav.getFavouriteable().getCarSpecification().getNameEn();
                else
                    importerName = fav.getFavouriteable().getCarSpecification().getNameAr();
            }
            int carTransmission = fav.getFavouriteable().getTransmission();
            for (FavoritesApiResponse.Image image : fav.getFavouriteable().getCarImages()) {
                if (image.getIsMain().equals("1")) {
                    Glide.with(context)
                            .load(ApiUtils.BASE_URL.concat(image.getImage()))
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .into(carImageView);
                    break;
                }
            }
            itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ViewCarActivity.class);
                i.putExtra("carId", fav.getFavouriteable().getId());
                context.startActivity(i);
            });
            carNameTextView.setText(carName);

            if (fav.getFavouriteable().getPriceType() == 2) {
                carPriceTextView.setText(context.getString(R.string.undetermined));
            } else if (!carPrice.equals("")) {
                if (carPrice.length() < 4)
                    carPriceTextView.setText(carPrice.concat(" SAR"));
                else
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carPrice), 0).concat(" SAR")));

            }
            if (fav.getFavouriteable().getPriceType() == 3) {
                carPriceTextView.setText(context.getString(R.string.exchange));
                carDiscountTextView.setVisibility(View.INVISIBLE);
            }
            if (fav.getFavouriteable().getPriceType() == 4) {

                if (carAfter.length() < 4) {
                    carPriceTextView.setText(carAfter.concat(" SAR"));
                } else {
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carAfter), 0).concat(" SAR")));
                }

                carDiscountTextView.setVisibility(View.VISIBLE);
                if (fav.getFavouriteable().getPriceBefore().length() < 4) {
                    carDiscountTextView.setText(fav.getFavouriteable().getPriceBefore().concat(" SAR"));
                } else {
                    carDiscountTextView.setText(String.valueOf(coolFormat(Double.parseDouble(fav.getFavouriteable().getPriceBefore()), 0).concat(" SAR")));
                }
                carDiscountTextView.setPaintFlags(carDiscountTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (fav.getFavouriteable().getPriceType() == 5) {
                if (carInstallTo.length() < 4) {
                    carDiscountTextView.setText(String.valueOf(carInstallTo) + " " + context.getString(R.string.month));
                } else {
                    carDiscountTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carInstallTo), 0) + " " + context.getString(R.string.month)));
                }
                if (carInstallFrom.length() < 4) {
                    carPriceTextView.setText(carInstallFrom.concat(" SAR"));
                } else {
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carInstallFrom), 0) + " SAR"));
                }
                carDiscountTextView.setVisibility(View.VISIBLE);
            }

            carYearTextView.setText(carYear);
            if (isEnglish) {
                carColorTextView.setText(Carcolor.getNameEn());
            } else {
                carColorTextView.setText(Carcolor.getNameAr());
            }
            if (carKmTo.length() < 4) {
                carRacesTextView.setText(carKmTo);
            } else {
                carRacesTextView.setText(coolFormat(Double.parseDouble(carKmTo), 0));
            }
            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(fav.getFavouriteable().getLatitude()), Double.parseDouble(fav.getFavouriteable().getLongitude()), 1);
                String country = addresses.get(0).getCountryName();
                carCityTextView.setText(country);
            } catch (Exception e) {
                carCityTextView.setText(carCity);
                Timber.e(e);
            }
            if (!importerName.isEmpty()) {
                carImporterTextView.setText(importerName);
            } else {
                carImporterTextView.setVisibility(View.GONE);
            }
            if (carTransmission == 1) {
                carTransmissionTextView.setText(context.getString(R.string.auto));
            } else if (carTransmission == 2) {
                carTransmissionTextView.setText(context.getString(R.string.normal));
            }

            String examinationImage = "";
            if (fav.getFavouriteable().getExaminationImage() == null) {
                examinationImage = "";
            } else examinationImage = fav.getFavouriteable().getExaminationImage();

            if (examinationImage.equals("")) {
                examinationImageView.setVisibility(View.GONE);
            }
            itemView.setTag(fav.getFavouriteable().getId());
            favoriteImageView.setTag(R.string.car_id, fav.getFavouriteable().getId());
            favoriteImageView.setTag(R.string.tracked_id, isTracked);
            if (!isEnglishLang()) {
                carRacesTextView.setGravity(Gravity.RIGHT);
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.fav_ic) {
                presenter.deleteCarFavorite((int) view.getTag(R.string.car_id));
            }
        }
    }

    class ShowroomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_showroom)
        ImageView showroomImageView;
        @BindView(R.id.image_gender)
        ImageView showroomGenderImageView;
        @BindView(R.id.showroom_gender)
        TextView showroomGenderTextView;
        @BindView(R.id.showroom_name)
        TextView showroomNameTextView;
        @BindView(R.id.showroom_cars)
        TextView showroomCarsTextView;
        @BindView(R.id.showroom_location)
        TextView showroomLocationTextView;
        @BindView(R.id.fav_ic)
        ImageView favoriteImageView;
        @BindView(R.id.vip_ic)
        ImageView isVip;

        ShowroomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            showroomLocationTextView.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);
        }

        void bindView(FavoritesApiResponse.Data fav) {
            String showroomImage = fav.getFavouriteable().getShowroomImage();
            String showroomName = fav.getFavouriteable().getName();
            String showroomCars = fav.getFavouriteable().getShowroomCars();
            int showroomGender = fav.getFavouriteable().getGender();
            if (fav.getFavouriteable().getShowroomInfo() != null) {
                if (fav.getFavouriteable().getShowroomInfo().getLatitude() != null || !fav.getFavouriteable().getShowroomInfo().getLatitude().isEmpty()) {
                     lat = fav.getFavouriteable().getShowroomInfo().getLatitude();
                     lon = fav.getFavouriteable().getShowroomInfo().getLongitude();

                }
            }
            itemView.setOnClickListener(v -> {
                new VisitorProfileActivityArgs(fav.getFavouriteable().getId())
                        .launch(context);
            });
            Glide.with(context)
                    .load(ApiUtils.BASE_URL.concat(showroomImage))
                    .placeholder(R.drawable.image_placeholder)
                    .centerCrop()
                    .into(showroomImageView);
            showroomNameTextView.setText(showroomName);

            if (showroomGender == 1) {
                showroomGenderTextView.setText(R.string.man);
                showroomGenderImageView.setImageResource(R.drawable.man_hotzone);
            } else if (showroomGender == 2) {
                showroomGenderTextView.setText(R.string.woman);
                showroomGenderImageView.setImageResource(R.drawable.lady);
            }

            showroomCarsTextView.setText(showroomCars);
            showroomLocationTextView.setTag(R.string.google_api_key, lat);
            showroomLocationTextView.setTag(R.string.google_crash_reporting_api_key, lon);
            favoriteImageView.setTag(fav.getFavouriteableId());

            if (fav.getFavouriteable().isPremium()) {
                isVip.setVisibility(View.VISIBLE);
            } else {
                isVip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.showroom_location:
                    if (view.getTag(R.string.google_api_key) != null && !((String) view.getTag(R.string.google_api_key)).isEmpty()) {
                        String lat = (String) view.getTag(R.string.google_api_key);
                        String lon = (String) view.getTag(R.string.google_crash_reporting_api_key);
                        String uriBegin = "geo:" + lat + "," + lon;
                        String query = lat + "," + lon;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(mapIntent);
                    } else {
                        screen.showErrorMessage(context.getString(R.string.showroom_location_not_available));
                    }
                    break;
                case R.id.fav_ic:
                    presenter.deleteUserFavorite((int) view.getTag());
            }

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
}
