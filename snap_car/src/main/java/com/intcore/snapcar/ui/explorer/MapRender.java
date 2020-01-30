package com.intcore.snapcar.ui.explorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intcore.snapcar.R;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.hotzone.HotZone;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;

public class MapRender {

    private Context context;
    private ResourcesUtil resourcesUtil;

    public MapRender(Context context, ResourcesUtil resourcesUtil) {
        this.context = context;
        this.resourcesUtil = resourcesUtil;
    }

    public Observable<RenderItem> getCarRenderObserver(List<CarApiResponse> list){

        int width = 75;
        int height = 130;

        int widthVIP = 100;
        int heightVIP = 145;

        Bitmap maleCar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.men),width,height,false);
        Bitmap maleCarVIP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.men_fet),widthVIP,heightVIP,false);

        Bitmap femaleCar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.women),width,height,false);
        Bitmap femaleCarVIP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.wome_fet),widthVIP,heightVIP,false);

        Bitmap damageCar = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.damged),width,height,false);
        Bitmap damageCarVIP = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.damged_fet),widthVIP,heightVIP,false);

        Observable<RenderItem> observable = Observable.fromIterable(list)
                .filter(item -> item.getLocation() != null)
                .map(car -> {

                    BitmapDescriptor bitmapDescriptor = null;

                    switch (car.getCarType()) {
                        case FilterType.FEMALE:
                            if (car.getPremium())
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(femaleCarVIP);
                            else
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(femaleCar);
                            break;
                        case FilterType.MALE:
                            if (car.getPremium())
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(maleCarVIP);
                            else
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(maleCar);
                            break;
                        case FilterType.DAMAGED:
                            if (car.getPremium())
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(damageCarVIP);
                            else
                                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(damageCar);
                            break;
                    }

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(car.getLocation())
                            .icon(bitmapDescriptor);

                    return new RenderItem(markerOptions,car.getId());
                });

        return observable;
    }

    public Observable<RenderItem> getHotZoneRenderObserver(List<HotZone> list){

        Observable<HotZoneRenderItem> imageObservable = Observable.fromIterable(list).map(hotZone -> {

            Drawable drawable = Glide.with(context)
                    .load(ApiUtils.BASE_URL.concat(hotZone.getImage()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit(128, 128).get();

            return new HotZoneRenderItem(drawable,hotZone);
        });

        View hotZoneMale = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_male_map_marker, null);
        View hotZoneMaleVIP = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_male_premium_map_marker, null);

        View hotZoneFemale = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_female_map_marker, null);
        View hotZoneFemaleVIP = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_female_premium_map_marker, null);

        View hotZoneMix = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_map_mixed_marker, null);
        View hotZoneMixVIP = resourcesUtil.getLayoutInflater().inflate(R.layout.item_hotzone_mixed_premium_map_marker, null);


        Observable<RenderItem> observable = imageObservable
                .filter(item -> item.getHotZone().getLocation() != null)
                .map(item -> {
                    View view = null;

                    switch (item.getHotZone().getZoneType()){

                        case FilterType.HOT_ZONE_MALE:
                            if (item.getHotZone().isPremium())
                                view = hotZoneMaleVIP;
                            else
                                view = hotZoneMale;
                            break;

                        case FilterType.HOT_ZONE_FEMALE:
                            if (item.getHotZone().isPremium())
                                view = hotZoneFemaleVIP;
                            else
                                view = hotZoneFemale;
                            break;

                        case FilterType.HOT_ZONE_MIXED:
                            if (item.getHotZone().isPremium())
                                view = hotZoneMixVIP;
                            else
                                view = hotZoneMix;
                            break;
                    }

                    CircleImageView userAvatar = view.findViewById(R.id.hot_zone_avatar);
                    TextView name = view.findViewById(R.id.tv_name);


                    if (item.drawable != null)
                        userAvatar.setImageDrawable(item.drawable);

                    name.setText(item.hotZone.getName());

                    Bitmap bitmap =  resourcesUtil.loadBitmapFromView(view);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(item.hotZone.getLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                    return new RenderItem(markerOptions,item.hotZone.getId());

                });


        return observable;
    }

    public Observable<RenderItem> getShowRoomRenderObserver(List<DefaultUserDataApiResponse.DefaultUserApiResponse> list){

        Observable<ShowRoomRenderItem> imageObservable = Observable.fromIterable(list).map(showRoom -> {

            Drawable drawable = Glide.with(context)
                    .load(ApiUtils.BASE_URL.concat(showRoom.getImageUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .submit(128, 128).get();

            return new ShowRoomRenderItem(drawable,showRoom);
        });

        View showRoom = resourcesUtil.getLayoutInflater().inflate(R.layout.item_showroom_map_marker, null);
        View showRoomVIP = resourcesUtil.getLayoutInflater().inflate(R.layout.item_showroom_feature_map_marker, null);

        Observable<RenderItem> observable = imageObservable
                .filter(item -> item.getShowRoom().getShowRoomLocation() != null)
                .map(item -> {
                    View view = null;

                    if (item.getShowRoom().getShowroomInfo().getPremium())
                        view = showRoomVIP;
                    else
                        view = showRoom;

                    CircleImageView userAvatar = view.findViewById(R.id.show_room_avatar);
                    TextView name = view.findViewById(R.id.tv_name);

                    if (item.drawable != null)
                        userAvatar.setImageDrawable(item.drawable);

                    name.setText(item.getShowRoom().getName());

                    Bitmap bitmap =  resourcesUtil.loadBitmapFromView(view);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(item.getShowRoom().getShowRoomLocation())
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                    return new RenderItem(markerOptions, (int) item.showRoom.getId());

                });


        return observable;
    }

    public class HotZoneRenderItem {
        private Drawable drawable;
        private HotZone hotZone;

        public HotZoneRenderItem(Drawable drawable, HotZone hotZone) {
            this.drawable = drawable;
            this.hotZone = hotZone;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public HotZone getHotZone() {
            return hotZone;
        }
    }

    public class ShowRoomRenderItem {
        private Drawable drawable;
        private DefaultUserDataApiResponse.DefaultUserApiResponse showRoom;

        public ShowRoomRenderItem(Drawable drawable, DefaultUserDataApiResponse.DefaultUserApiResponse showRoom) {
            this.drawable = drawable;
            this.showRoom = showRoom;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public DefaultUserDataApiResponse.DefaultUserApiResponse getShowRoom() {
            return showRoom;
        }
    }

    public class RenderItem {
        private MarkerOptions markerOptions;
        private int id;

        public RenderItem(MarkerOptions markerOptions, int id) {
            this.markerOptions = markerOptions;
            this.id = id;
        }

        public MarkerOptions getMarkerOptions() {
            return markerOptions;
        }

        public int getId() {
            return id;
        }
    }
}
