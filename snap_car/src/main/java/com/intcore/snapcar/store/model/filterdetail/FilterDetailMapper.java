package com.intcore.snapcar.store.model.filterdetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneModel;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.ResourcesUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;

/*
 * This class is responsible for creating different icons based on the object type (Showroom ,Hotzone or individual car)
 */
@ApplicationScope
public class FilterDetailMapper {

    private final ResourcesUtil resourcesUtil;
    private final BitmapFactory.Options options;

    @Inject
    FilterDetailMapper(ResourcesUtil resourcesUtil) {
        this.resourcesUtil = resourcesUtil;
        options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, 100, 100);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
    }

    public FilterDetailViewModel toViewModelFromCar(CarModel model, @FilterType int type) {

        if (model == null) return null;
        Bitmap icon = null;
        View view;
        switch (type) {
            case FilterType.FEMALE:
                if (model.getPremium()) {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_female_feature_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                } else {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_female_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                }
                break;
            case FilterType.MALE:
                if (model.getPremium()) {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_male_feature_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                } else {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_male_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                }
                break;
            case FilterType.DAMAGED:
                if (model.getPremium()) {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_damage_feature_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                } else {
                    view = resourcesUtil.getLayoutInflater().inflate(R.layout.item_damage_map_marker, null);
                    icon = resourcesUtil.loadBitmapFromView(view);
                }
                break;
        }
        BitmapDescriptor bitmapDescriptor;
        if (model.getPremium()) {
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resourcesUtil.resizeMapIcons(icon, 70, 120));
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resourcesUtil.resizeMapIcons(icon, 50, 90));
        }
        icon.recycle();

        LatLng newLatLng = null;
        LatLng oldLatLng = null;
        if (model.getCarTrackingModel() != null) {
            if (!TextUtils.isEmpty(model.getCarTrackingModel().getLatitude())) {
                newLatLng = new LatLng(Double.valueOf(model.getCarTrackingModel().getLatitude()),
                        Double.valueOf(model.getCarTrackingModel().getLongitude()));
            }
            if (!TextUtils.isEmpty(model.getCarTrackingModel().getOldLat())) {
                oldLatLng = new LatLng(Double.valueOf(model.getCarTrackingModel().getOldLat()),
                        Double.valueOf(model.getCarTrackingModel().getOldLong()));
            }
        } else {
            if (!TextUtils.isEmpty(model.getLatitude())) {
                newLatLng = new LatLng(Double.valueOf(model.getLatitude()),
                        Double.valueOf(model.getLongitude()));
            } else {
                newLatLng = new LatLng(0.0, 0.0);
            }
        }
        String image = "";
        if (model.getImageModels().size() > 0) {
            image = model.getImageModels().get(0).getImage();
        }
        return new FilterDetailViewModel(
                model.getUserId(),
                type,
                model.getId(),
                model.getCarName(),
                icon,
                "0",
                newLatLng,
                oldLatLng,
                model.getPremium(),
                false,
                model.getIsFavorite(),
                image,
                false, model.getManufacturingYear(), bitmapDescriptor,false);
    }

    public FilterDetailViewModel toViewModelFromShowRoom(DefaultUserModel model, @FilterType int type) throws IOException {
        if (model == null) return null;
        Bitmap icon;
        if (model.getShowRoomInfoModel().getPremium()) {
            icon = bindShowRoomView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_showroom_feature_map_marker, null), model);
        } else {
            icon = bindShowRoomView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_showroom_map_marker, null), model);
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);
        icon.recycle();
        LatLng newLatLng = null;
        if (model.getShowRoomInfoModel() != null) {
            if (!TextUtils.isEmpty(model.getShowRoomInfoModel().getLatitude())) {
                newLatLng = new LatLng(Double.valueOf(model.getShowRoomInfoModel().getLatitude()),
                        Double.valueOf(model.getShowRoomInfoModel().getLongitude()));
            }
        }
        String carNumbers;
        if (model.getAvailableCars() != null) {
            carNumbers = model.getAvailableCars();
        } else {
            carNumbers = "0";
        }
        return new FilterDetailViewModel(
                (int) model.getId(),
                type,
                0,
                model.getFirstName(),
                icon,
                carNumbers,
                newLatLng,
                null,
                model.getShowRoomInfoModel().getPremium(),
                model.getShowRoomInfoModel().getHotZoneModel() != null,
                model.getFavorite(),
                model.getImageUrl(),
                false, null, bitmapDescriptor, false);
    }

    public FilterDetailViewModel toViewModelFromHotZone(HotZoneModel model, @FilterType int type) throws IOException {
        if (model == null) return null;
        Bitmap icon = null;
        switch (type) {
            case FilterType.HOT_ZONE_FEMALE:
                if (model.isPremium()) {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_female_premium_map_marker, null), model);
                } else {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_female_map_marker, null), model);
                }
                break;
            case FilterType.HOT_ZONE_MALE:
                if (model.isPremium()) {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_male_premium_map_marker, null), model);
                } else {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_male_map_marker, null), model);
                }
                break;
            case FilterType.HOT_ZONE_MIXED:
                if (model.isPremium()) {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_mixed_premium_map_marker, null), model);
                } else {
                    icon = bindHotZoneView(resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_map_mixed_marker, null), model);
                }
                break;
        }
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon);
        icon.recycle();
        LatLng newLatLng = null;
        if (!TextUtils.isEmpty(model.getLatitude())) {
            newLatLng = new LatLng(Double.valueOf(model.getLatitude()),
                    Double.valueOf(model.getLongitude()));
        }
        return new FilterDetailViewModel(
                model.getId(),
                type,
                0,
                model.getName(),
                icon,
                "0",
                newLatLng,
                null,
                model.isPremium(),
                false,
                false,
                model.getImage(),
                model.getProvideDiscount() == 1, null, bitmapDescriptor, model.getShowroom());
    }

    public List<FilterDetailViewModel> toFilterDetailViewModelsFromCars(List<CarModel> models, @FilterType int type) {
        if (models == null) return null;
        List<FilterDetailViewModel> viewModels = new ArrayList<>();
        for (CarModel model : models) {
            viewModels.add(toViewModelFromCar(model, type));
        }
        return viewModels;
    }

    public List<FilterDetailViewModel> toFilterDetailViewModelsFromShowRooms(List<DefaultUserModel> models, @FilterType int type) {
        if (models == null) return null;
        List<FilterDetailViewModel> viewModels = new ArrayList<>();
        for (DefaultUserModel model : models) {
            try {
                viewModels.add(toViewModelFromShowRoom(model, type));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return viewModels;
    }

    public List<FilterDetailViewModel> toFilterDetailViewModelsFromHotZones(List<HotZoneModel> models, @FilterType int type) {
        if (models == null) return null;
        List<FilterDetailViewModel> viewModels = new ArrayList<>();
        for (HotZoneModel model : models) {
            try {
                viewModels.add(toViewModelFromHotZone(model, type));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return viewModels;
    }

    private Bitmap bindHotZoneView(View view, HotZoneModel model) throws IOException {
        CircleImageView userAvatar = view.findViewById(R.id.hot_zone_avatar);
        TextView name = view.findViewById(R.id.tv_name);
        URL url = new URL(ApiUtils.BASE_URL.concat(model.getImage()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(50000);
        Bitmap bmp;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            is = connection.getInputStream();
            bis = new BufferedInputStream(is);
            bmp = BitmapFactory.decodeStream(bis, null, options);
            name.setText(model.getName());
            userAvatar.setImageBitmap(bmp);
        } catch (OutOfMemoryError e) {
            Timber.e(e);
        } finally {
            connection.disconnect();
            if (is != null)
                is.close();
            if (bis != null)
                bis.close();
        }
        return resourcesUtil.loadBitmapFromView(view);
    }

    private Bitmap bindShowRoomView(View view, DefaultUserModel model) throws IOException {
        CircleImageView userAvatar = view.findViewById(R.id.show_room_avatar);
        TextView name = view.findViewById(R.id.tv_name);
        URL url = new URL(ApiUtils.BASE_URL.concat(model.getImageUrl()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(50000);
        Bitmap bmp;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            is = connection.getInputStream();
            bis = new BufferedInputStream(is);
            bmp = BitmapFactory.decodeStream(bis, null, options);
            name.setText(model.getFirstName());
            userAvatar.setImageBitmap(bmp);
        } catch (OutOfMemoryError e) {
            Timber.e(e);
        } finally {
            connection.disconnect();
            if (is != null)
                is.close();
            if (bis != null)
                bis.close();
        }
        return resourcesUtil.loadBitmapFromView(view);
    }
}