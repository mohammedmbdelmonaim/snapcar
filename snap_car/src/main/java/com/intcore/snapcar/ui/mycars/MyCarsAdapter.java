package com.intcore.snapcar.ui.mycars;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;
import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.ui.editcar.EditCarActivity;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MyCarsAdapter extends RecyclerView.Adapter {

    private static String[] c = new String[]{" k", " m", " b", " t"};
    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<MyCarsApiResponse.Favouriteable> myCars = new ArrayList<>();
    private MyCarsPresenter presenter;
    private List<Address> addresses;
    private MyCarsScreen screen;
    private Geocoder geocoder;
    private boolean isEnglish;

    @Inject
    MyCarsAdapter(@ForActivity Context context, MyCarsPresenter presenter, MyCarsScreen screen) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.presenter = presenter;
        this.screen = screen;
        geocoder = new Geocoder(context);
        isEnglish = isEnglishLang();
        if (!isEnglishLang()) {
            c = new String[]{" الف ", " مليون ", " بليون ", " t "};
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
        return new CarViewHolder(layoutInflater.inflate(R.layout.item_my_cars, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CarViewHolder) holder).bindView(myCars.get(position));
    }

    @Override
    public int getItemCount() {
        return myCars.size();
    }

    public void swapData(List<MyCarsApiResponse.Favouriteable> myCars) {
        this.myCars = myCars;
        notifyDataSetChanged();
    }

    private boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    class CarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_car)
        ImageView carImage;
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
        MyCarsActivity activity;
        private int carId;
        private int isTracked;

        CarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);
            this.activity = (MyCarsActivity) context;
        }

        private void bindView(MyCarsApiResponse.Favouriteable car) {
            if (!isEnglishLang()) {
                carRacesTextView.setGravity(Gravity.RIGHT);
            }
            carDiscountTextView.setPaintFlags(0);
            String carName;
            if (isEnglish) {
                carName = car.getCarNameEn();
            } else {
                carName = car.getCarName();
            }
            int isTracked = car.getIsTracked();
            String carPrice;
            if (car.getPrice() == null) {
                carPrice = "";
            } else {
                carPrice = car.getPrice();
            }

            String carAfter;
            if (car.getPriceAfter() == null) {
                carAfter = "";
            } else {
                carAfter = car.getPriceAfter();
            }
            String carInstallTo = "";
            if (car.getInstallmenTo() == null) {
                carInstallTo = "";
            } else {
                carInstallTo = car.getInstallmenTo();
            }
            String carYear = car.getManufacturingYear();
            int carExchange = car.getExchange();
            String carInstallFrom = car.getInstallmentFrom();
            CarResourcesApiResponse.Colors carColor = null;
            if (car.getCarCalor() != null) {
                carColor = car.getCarCalor();
            }
            String carKmTo = car.getKilometer();
            String importerName = "";
            String carCity = "";
            if (car.getUser() != null) {
                if (car.getUser().getCity() != null) {
                    if (isEnglish) {
                        carCity = car.getUser().getCity().getNameEn();
                    } else {
                        carCity = car.getUser().getCity().getNameAr();
                    }
                }
            }
            if (car.getCarSpecification() != null) {
                if (isEnglish) {
                    importerName = car.getCarSpecification().getNameEn();
                } else {
                    importerName = car.getCarSpecification().getNameAr();
                }
            }
            int carTransmission = car.getTransmission();
            for (MyCarsApiResponse.Image image : car.getCarImages()) {
                if (image.getIsMain().equals("1")) {
                    Glide.with(context)
                            .load(ApiUtils.BASE_URL.concat(image.getImage()))
                            .centerCrop()
                            .placeholder(R.drawable.image_placeholder)
                            .into(carImage);
                    break;
                }
            }
            carNameTextView.setText(carName);
            if (car.getPriceType() == 2) {
                carPriceTextView.setText(context.getString(R.string.undetermined));
                carDiscountTextView.setVisibility(View.INVISIBLE);
            } else if (car.getPriceType() == 1) {
                if (carPrice.length() < 4) {
                    carPriceTextView.setText(carPrice.concat(context.getString(R.string.sar)));
                } else {
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carPrice), 0).concat(context.getString(R.string.sar))));
                }
            }
            if (car.getPriceType() == 3) {
                carPriceTextView.setText(context.getString(R.string.exchange));
                carDiscountTextView.setVisibility(View.INVISIBLE);
            }
            if (car.getPriceType() == 4) {
                if (carAfter.length() < 4) {
                    carPriceTextView.setText(carAfter.concat(context.getString(R.string.sar)));
                } else {
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carAfter), 0).concat(context.getString(R.string.sar))));
                }
                carDiscountTextView.setVisibility(View.VISIBLE);
                if (car.getPriceBefore().length() < 4) {
                    carDiscountTextView.setText(car.getPriceBefore().concat(context.getString(R.string.sar)));
                } else {
                    carDiscountTextView.setText(String.valueOf(coolFormat(Double.parseDouble(car.getPriceBefore()), 0).concat(context.getString(R.string.sar))));
                }
                carDiscountTextView.setPaintFlags(carDiscountTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (car.getPriceType() == 5) {
                if (carInstallTo.length() < 4) {
                    carDiscountTextView.setText(String.valueOf(carInstallTo).concat(" ").concat(context.getString(R.string.month)));
                } else {
                    carDiscountTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carInstallTo), 0) + " " + context.getString(R.string.month)));
                }
                if (carInstallFrom.length() < 4) {
                    carPriceTextView.setText(carInstallFrom.concat(context.getString(R.string.sar)));
                } else {
                    carPriceTextView.setText(String.valueOf(coolFormat(Double.parseDouble(carInstallFrom), 0) + context.getString(R.string.sar)));
                }
                carDiscountTextView.setVisibility(View.VISIBLE);
            }
            carYearTextView.setText(carYear);
            if (car.getCarCalor() != null) {
                if (isEnglish) {
                    carColorTextView.setText(carColor.getNameEn());
                } else {
                    carColorTextView.setText(carColor.getNameAr());
                }
            }
            if (carKmTo.length() < 4) {
                carRacesTextView.setText(carKmTo);
            } else {
                carRacesTextView.setText(coolFormat(Double.parseDouble(carKmTo), 0));
            }
            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(car.getLatitude()), Double.parseDouble(car.getLongitude()), 1);
                if (addresses.size() > 0) {
                    String country = addresses.get(0).getCountryName();
                    carCityTextView.setText(country);
                }
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
            if (car.getExaminationImage() == null) {
                examinationImage = "";
            } else {
                examinationImage = car.getExaminationImage();
            }
            if (examinationImage.equals("")) {
                examinationImageView.setVisibility(View.GONE);
            }
            itemView.setTag(car.getId());
            favoriteImageView.setTag(R.string.car_id, car.getId());
            favoriteImageView.setTag(R.string.tracked_id, isTracked);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.fav_ic) {
                this.carId = (int) view.getTag(R.string.car_id);
                this.isTracked = (int) view.getTag(R.string.tracked_id);
                PopupMenu popupMenu = new PopupMenu(context, view);
                activity.getMenuInflater().inflate(R.menu.menu_my_cars, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this::onFlowMenuItemClicked);
                popupMenu.show();
            } else {
                Intent i = new Intent(context, ViewCarActivity.class);
                i.putExtra("carId", (int) view.getTag());
                context.startActivity(i);
            }
        }

        private boolean onFlowMenuItemClicked(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete_car) {
                screen.showConfirmationDialog(carId, isTracked);
                return true;
            } else if (menuItem.getItemId() == R.id.edit_car) {
                Intent i = new Intent(context, EditCarActivity.class);
                i.putExtra("carId", carId);
                context.startActivity(i);
                return true;
            } else if (menuItem.getItemId() == R.id.make_as_sold) {
                presenter.fetchSwearData(carId);
                return true;
            }
            return true;
        }
    }
}