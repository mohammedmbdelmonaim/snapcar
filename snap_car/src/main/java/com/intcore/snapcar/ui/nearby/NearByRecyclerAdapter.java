package com.intcore.snapcar.ui.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.intcore.snapcar.store.model.ads.AdsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.explore.AdsExploreItem;
import com.intcore.snapcar.store.model.explore.CarExploreItem;
import com.intcore.snapcar.store.model.explore.DividerExploreItem;
import com.intcore.snapcar.store.model.explore.ExploreItem;
import com.intcore.snapcar.store.model.explore.ExploreItemType;
import com.intcore.snapcar.store.model.explore.FeaturedExploreItem;
import com.intcore.snapcar.store.model.explore.HotZoneExploreItem;
import com.intcore.snapcar.store.model.explore.ShowRoomExploreItem;
import com.intcore.snapcar.store.model.explore.TitleExploreItem;
import com.intcore.snapcar.store.model.hotzone.HotZone;
import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;
import com.intcore.snapcar.ui.OnBottomReachedListener;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.helper.SwitchCase;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

@FragmentScope
class NearByRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String[] c = new String[]{" k", " m", " b", " t"};
    private final LayoutInflater layoutInflater;
    private final NearByPresenter presenter;
    private final UserManager userManager;
    private final Context context;
    private List<ExploreItem> exploreItems;
    private OnBottomReachedListener onBottomReachedListener;

    @Inject
    NearByRecyclerAdapter(NearByPresenter presenter,
                          @ForFragment Context context,
                          UserManager userManager) {
        this.layoutInflater = LayoutInflater.from(context);
        this.userManager = userManager;
        this.presenter = presenter;
        this.context = context;
        this.exploreItems = new ArrayList<>();
        if (!isEnglishLang()) {
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

    void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        Preconditions.checkNonNull(onBottomReachedListener);
        this.onBottomReachedListener = onBottomReachedListener;
    }

    private boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ExploreItemType.ADS:
                return new AdsViewHolder(layoutInflater.inflate(R.layout.item_ads, parent, false));
            case ExploreItemType.CAR:
                return new CarViewHolder(layoutInflater.inflate(R.layout.item_near_by, parent, false));
            case ExploreItemType.DIVIDER:
                return new DividerViewHolder(layoutInflater.inflate(R.layout.item_divider, parent, false));
            case ExploreItemType.FEATURE_LIST:
                return new FeatureViewHolder(layoutInflater.inflate(R.layout.item_feature, parent, false));
            case ExploreItemType.HOT_ZONE:
                return new HotZoneViewHolder(layoutInflater.inflate(R.layout.item_hotzone, parent, false));
            case ExploreItemType.SHOW_ROOM:
                return new ShowRoomViewHolder(layoutInflater.inflate(R.layout.item_show_room, parent, false));
            case ExploreItemType.TITLE:
                return new TitleViewHolder(layoutInflater.inflate(R.layout.item_title, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (position == presenter.getPaginationList().size() - 1 && onBottomReachedListener != null) {
            onBottomReachedListener.loadMore();
        }

        new SwitchCase(viewHolder)
                .whenInstanceOf(AdsViewHolder.class, holder -> ((AdsViewHolder) holder).bind(((AdsExploreItem) exploreItems.get(position)).getAdsViewModel()))
                .whenInstanceOf(TitleViewHolder.class, holder -> ((TitleViewHolder) holder).bind(((TitleExploreItem) exploreItems.get(position)).getTitle()))
                .whenInstanceOf(FeatureViewHolder.class, holder -> ((FeatureViewHolder) holder).bind(((FeaturedExploreItem) exploreItems.get(position)).getFeatureList()))
                .whenInstanceOf(CarViewHolder.class, holder -> ((CarViewHolder) holder).bind(((CarExploreItem) exploreItems.get(position)).getCarItem()))
                .whenInstanceOf(HotZoneViewHolder.class, holder -> ((HotZoneViewHolder) holder).bind(((HotZoneExploreItem) exploreItems.get(position)).getHotZoneViewModel()))
                .whenInstanceOf(ShowRoomViewHolder.class, holder -> ((ShowRoomViewHolder) holder).bind(((ShowRoomExploreItem) exploreItems.get(position)).getShowRoomModel()))
                .whenInstanceOf(DividerViewHolder.class, holder -> ((DividerViewHolder) holder).bind(((DividerExploreItem) exploreItems.get(position)).getDivider()));



    }

    @Override
    public int getItemViewType(int position) {
        return exploreItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return exploreItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).setRecycleChildrenOnDetach(true);
        }
    }

    public void setdata(List<ExploreItem> data) {
        this.exploreItems.clear();
        this.exploreItems.addAll(data);
        notifyDataSetChanged();
    }

    public void appendData(List<ExploreItem> newexploreItems) {
        int i = exploreItems.size();
        exploreItems.addAll(newexploreItems);
        notifyItemRangeInserted(i, newexploreItems.size());
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_title)
        TextView titleTextView;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String title) {
            titleTextView.setText(title);
        }


    }

    class FeatureViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_feature)
        RecyclerView featureRecyclerView;

        FeatureViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(List<CarViewModel> carViewModels) {
            presenter.setFeatureList(carViewModels);
            featureRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            FeatureRecyclerAdapter adapter = new FeatureRecyclerAdapter(context, presenter);
            featureRecyclerView.setAdapter(adapter);
            if (carViewModels.size() > 2) {
                featureRecyclerView.smoothScrollToPosition(2);
            }
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder {

        DividerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String divider) {
            Timber.tag("manarDebug").v("divider");
        }

    }

    class ShowRoomViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;
        @BindView(R.id.iv_near_by)
        ImageView hotZoneImageView;
        @BindView(R.id.tv_hot_zone_name)
        TextView hotZoneNameTextView;
        @BindView(R.id.tv_cars_number)
        TextView carNumberTextView;
        @BindView(R.id.iv_vip)
        ImageView vipImageView;
        @BindView(R.id.iv_fav)
        ImageView favoriteImageView;
        @BindView(R.id.fav_progress_bar)
        ProgressBar favProgressBar;
        private DefaultUserModel showRoomModel;

        ShowRoomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
            itemView.setOnClickListener(v -> new VisitorProfileActivityArgs(showRoomModel.getId())
                    .launch(context));
        }

        void bind(DefaultUserModel showRoomModel) {
            setTopBottomMargins();
            this.showRoomModel = showRoomModel;
            hotZoneNameTextView.setText(showRoomModel.getFirstName());
            favoriteImageView.setActivated(showRoomModel.getFavorite());
            if (showRoomModel.getShowRoomInfoModel() != null) {
                if (showRoomModel.getShowRoomInfoModel().getPremium()) {
                    vipImageView.setVisibility(View.VISIBLE);
                } else {
                    vipImageView.setVisibility(View.INVISIBLE);
                }
                if (showRoomModel.getShowRoomInfoModel().getPremium()) {
                    vipImageView.setVisibility(View.VISIBLE);
                } else {
                    vipImageView.setVisibility(View.GONE);
                }
            }
            if (showRoomModel.getAvailableCars() != null) {
                carNumberTextView.setText(showRoomModel.getAvailableCars());
            } else {
                carNumberTextView.setText("0");
            }
            if (!isEnglishLang()) {
                hotZoneNameTextView.setGravity(Gravity.RIGHT);
            } else {
                hotZoneNameTextView.setGravity(Gravity.LEFT);
            }
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(showRoomModel.getImageUrl()))
                    .centerCrop()
                    .into(hotZoneImageView);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == exploreItems.size() - 1) {
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

        @OnClick(R.id.tv_location)
        public void onViewClicked() {
            if (showRoomModel.getShowRoomInfoModel() != null) {
                double latitude = 0;
                double longitude = 0;
                if (!TextUtils.isEmpty(showRoomModel.getShowRoomInfoModel().getLatitude())) {
                    latitude = Double.parseDouble(showRoomModel.getShowRoomInfoModel().getLatitude());
                }
                if (!TextUtils.isEmpty(showRoomModel.getShowRoomInfoModel().getLongitude())) {
                    longitude = Double.parseDouble(showRoomModel.getShowRoomInfoModel().getLongitude());
                }
                if (latitude == 0) {

                    presenter.locationNotAvailable();
                    return;
                }
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude;
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                context.startActivity(mapIntent);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @OnClick(R.id.iv_fav)
        void onFavoriteClicked() {
            presenter.onFavoriteClicked(null, String.valueOf(showRoomModel.getId()), new OperationListener<Void>() {
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
                    // samuel
//                    favoriteImageView.setActivated(!favoriteImageView.isActivated());
                    presenter.fetchData();

                }

                @Override
                public void onError(Throwable t) {
                    presenter.processError(t);
                }
            });
        }

    }

    class HotZoneViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;

        @BindView(R.id.cart_parent)
        ConstraintLayout cart;


        @BindView(R.id.iv_near_by)
        ImageView hotZoneImageView;
        @BindView(R.id.tv_hot_zone_name)
        TextView hotZoneNameTextView;
        @BindView(R.id.tv_type)
        TextView hotZoneTypeTextView;
        @BindView(R.id.iv_vip)
        ImageView vipImageView;


        @BindView(R.id.iv_fav)
        ImageView favoriteImageView;
        @BindView(R.id.tv_type_hot_zone)
        TextView tvTypeHotZone;
        @BindView(R.id.tv_cars_number)
        TextView tvCarsNumber;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.fav_progress_bar)
        ProgressBar favProgressBar;
        @BindView(R.id.tv_isShowroom)
        TextView tv_isShowroom;

        private HotZoneViewModel hotZoneViewModel;

        HotZoneViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();

        }

        //SAMUEL
        void bind(HotZoneViewModel hotZoneViewModel) {
            setTopBottomMargins();
            this.hotZoneViewModel = hotZoneViewModel;
             if (hotZoneViewModel.isShowroom()){
                 tv_isShowroom.setVisibility(View.VISIBLE);
                 tvLocation.setBackgroundResource(R.drawable.shape_blue_rectangle_background);
                 favoriteImageView.setVisibility(View.VISIBLE);
                 tvTypeHotZone.setVisibility(View.VISIBLE);
                 tvCarsNumber.setVisibility(View.VISIBLE);
                 tvCarsNumber.setText(hotZoneViewModel.getShowRoomModel().getAvailableCars());
                 favoriteImageView.setActivated(hotZoneViewModel.getShowRoomModel().getFavorite());
                 cart.setOnClickListener(v -> new VisitorProfileActivityArgs(hotZoneViewModel.getShowRoomModel().getId())
                         .launch(context));

             }
             else {
                 tv_isShowroom.setVisibility(View.GONE);
                 tvLocation.setBackgroundResource(R.drawable.shape_rectangle_orange_background);
                 favoriteImageView.setVisibility(View.GONE);
                 tvTypeHotZone.setVisibility(View.GONE);
                 tvCarsNumber.setVisibility(View.GONE);
             }
            hotZoneNameTextView.setText(hotZoneViewModel.getName());
            if (hotZoneViewModel.getPremium()) {
                vipImageView.setVisibility(View.VISIBLE);
            } else {
                vipImageView.setVisibility(View.GONE);
            }
            if (hotZoneViewModel.getGender() == Gender.FEMALE) {
                hotZoneTypeTextView.setText(context.getString(R.string.female));
                hotZoneTypeTextView.setActivated(true);
            } else if (hotZoneViewModel.getGender() == Gender.MALE) {
                hotZoneTypeTextView.setText(context.getString(R.string.male));
                hotZoneTypeTextView.setActivated(false);
            } else {
                hotZoneTypeTextView.setText(context.getString(R.string.mixed));
            }
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(hotZoneViewModel.getImage()))
                    .centerCrop()
                    .into(hotZoneImageView);
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        @OnClick(R.id.iv_fav)
        void onFavoriteClicked() {
            presenter.onFavoriteClicked(null, String.valueOf(hotZoneViewModel.getShowRoomModel().getId()), new OperationListener<Void>() {
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
                    // samuel
//                    favoriteImageView.setActivated(!favoriteImageView.isActivated());
                    presenter.fetchData();

                }

                @Override
                public void onError(Throwable t) {
                    presenter.processError(t);
                }
            });
        }

        @OnClick(R.id.tv_location)
        public void onViewClicked() {
            double latitude = 0;
            double longitude = 0;
            if (!TextUtils.isEmpty(hotZoneViewModel.getLatitude())) {
                latitude = Double.parseDouble(hotZoneViewModel.getLatitude());
            }
            if (!TextUtils.isEmpty(hotZoneViewModel.getLongitude())) {
                longitude = Double.parseDouble(hotZoneViewModel.getLongitude());
            }
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude;
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            context.startActivity(mapIntent);
        }




        private void setTopBottomMargins() {
            if (getAdapterPosition() == exploreItems.size() - 1) {
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

    class AdsViewHolder extends RecyclerView.ViewHolder {

        private final ViewGroup.LayoutParams layoutParams;

        @BindView(R.id.iv_ads)
        ImageView adsImageView;
        private AdsViewModel adsViewModel;

        AdsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutParams = itemView.getLayoutParams();
        }

        void bind(AdsViewModel adsViewModel) {
            this.adsViewModel = adsViewModel;
            setTopBottomMargins();
            Glide.with(itemView)
                    .load(ApiUtils.BASE_URL.concat(adsViewModel.getImage()))
                    .placeholder(R.color.light_transparent)
                    .centerCrop()
                    .into(adsImageView);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == exploreItems.size() - 1) {
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

        @OnClick(R.id.iv_ads)
        void onAdsClicked() {
            if (adsViewModel.getUrl() != null) {
                if (adsViewModel.getUrl().contains("http")) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(adsViewModel.getUrl())));
                }
            }
        }
    }

    class CarViewHolder extends RecyclerView.ViewHolder {

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

        CarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            geocoder = new Geocoder(context, Locale.getDefault());
            layoutParams = itemView.getLayoutParams();
            if (!isEnglishLang()) {
                c = new String[]{" الف", " مليون", " بليون", " t"};
            }
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
            if (userManager.sessionManager().isSessionActive()) {
                if (userManager.getCurrentUser().getId() == carViewModel.getUserId()) {
                    favoriteImageView.setVisibility(View.GONE);
                }
            }
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

            if (!TextUtils.isEmpty(carViewModel.getExaminationImage())) {
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
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() >= 1) {
                    String country = null;
                    if (addresses.get(0).getLocality() != null)
                        country = addresses.get(0).getLocality();
                    else if (addresses.get(0).getAdminArea() != null)
                        country = addresses.get(0).getAdminArea();
                    else country = addresses.get(0).getCountryName();
                    countryTextView.setText(country);
                }
            } catch (IOException e) {
                Timber.e(e);
            }
            nameTextView.setText(carViewModel.getCarName());
            if (carViewModel.getKilometer() != null) {
                if (carViewModel.getKilometer().length() < 4) {
                    distanceTextView.setText(carViewModel.getKilometer());
                } else {
                    distanceTextView.setText(coolFormat(Double.parseDouble(carViewModel.getKilometer()), 0));
                }
            }
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
                    .placeholder(R.color.light_transparent)
                    .into(carImageView);
        }

        private void setTopBottomMargins() {
            if (getAdapterPosition() == exploreItems.size() - 1) {
                FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
                newParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin),
                        context.getResources().getDimensionPixelOffset(R.dimen.margin));
                itemView.setLayoutParams(newParams);
            } else {
                itemView.setLayoutParams(layoutParams);
            }
            if (!isEnglishLang()) {
                distanceTextView.setGravity(Gravity.RIGHT);
            }
        }

        boolean isEnglishLang() {
            return LocaleUtil.getLanguage(context).equals("en");
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
                    // samuel
//                    favoriteImageView.setActivated(!favoriteImageView.isActivated());
                    presenter.fetchData();
                }

                @Override
                public void onError(Throwable t) {
                    presenter.processError(t);
                }
            });
        }
    }
}