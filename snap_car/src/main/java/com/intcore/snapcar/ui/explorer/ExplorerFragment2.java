package com.intcore.snapcar.ui.explorer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.carTrack.TrackModel;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailMapper;
import com.intcore.snapcar.store.model.hotzone.HotZone;
import com.intcore.snapcar.ui.chat.ChatFragment;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.intcore.snapcar.util.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.view.FrameMetrics.ANIMATION_DURATION;

@FragmentScope
public class ExplorerFragment2 extends Fragment implements ExplorerScreen2, OnMapReadyCallback {

    @Inject
    ExplorerPresenter2 presenter;
    @Inject
    UiUtil uiUtil;
    @Inject
    ResourcesUtil resourcesUtil;
    @Inject
    UserManager userManager;
    @Inject
    FilterDetailMapper filterDetailMapper;

    private Map<Integer,Marker> markerMap = new HashMap<>();
    private Map<Integer,Object> mapItems = new HashMap<>();


    private GoogleMap googleMap;
    private HomeFragment homeFragment;
    private View view;
    private Marker myMarker;
    private MapRender mapRender;
    private int myCarId = -1;
    private CompositeDisposable disposable = new CompositeDisposable();


    private Handler moveHandler = new Handler();
    private Handler rotateHandler= new Handler();

    public ExplorerFragment2() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIHostComponentProvider) {
            UIHostComponentProvider provider = ((UIHostComponentProvider) context);
            if (provider.getComponent() instanceof ActivityComponent) {
                ((ActivityComponent) provider.getComponent())
                        .plus(new FragmentModule(this))
                        .inject(this);
            } else {
                throw new IllegalStateException("Component must be " + ActivityComponent.class.getName());
            }
        } else {
            throw new AssertionError("host context must implement " + UIHostComponentProvider.class.getName());
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
//        homeFragment = ((HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName()));
//        if (homeFragment != null)
//            homeFragment.setFilterNearByDataReady(this::processResult);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_explorer, container, false);

        ButterKnife.bind(this, view);
        setLanguage();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (userManager.getCurrentUser() != null && userManager.getCurrentUser().getCarModel() !=null)
            myCarId = userManager.getCurrentUser().getCarModel().getId();

        mapRender = new MapRender(getContext(),resourcesUtil);

        homeFragment = ((HomeFragment) getParentFragment());
        if (homeFragment != null)
            homeFragment.setFilterNearByDataReady(this::processResult);

        setupMap();

        SnapCarApplication.getInstance().locationLiveData.observe(this,this::setMyLocation);
    }

    @Override
    public void onDestroyView() {
        try {
            if (presenter != null)
                presenter.stopCarTracking();

            disposable.dispose();
            mapItems.clear();
            markerMap.clear();
            moveHandler.removeCallbacksAndMessages(null);
            rotateHandler.removeCallbacksAndMessages(null);
        }
        catch (Exception e){

        }
        super.onDestroyView();
    }

    private void setLanguage(){
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

    public void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);
    }

    private void setMyLocation(Location location){
        if (location == null || googleMap == null)
            return;

        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());

        if (myMarker == null){

            presenter.fetchData(); //first time

            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .anchor(0.5f,0.5f)
                    .icon(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getActivity().getDrawable(R.drawable.mylocation_icon))));

            myMarker = googleMap.addMarker(options);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,17f));
        }
        else{
            animateMyMarker(position);
        }

        if (homeFragment != null) {
            homeFragment.setLocation(location);
        }
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        int width = 200;
        int height = 200;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    void processResult(FilterApiResponse filterViewModel) {
        presenter.stopCarTracking();
        moveHandler.removeCallbacksAndMessages(null);
        rotateHandler.removeCallbacksAndMessages(null);
        googleMap.clear();
        markerMap.clear();
        mapItems.clear();

        myCarId = -1;
        myMarker = null;
        setMyLocation(SnapCarApplication.getInstance().locationLiveData.getValue());
        presenter.fetchData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        googleMap.setOnMarkerClickListener(marker -> {

            if (marker.getTag() == null)
                return false;

            int id = (int) marker.getTag();

            if (!mapItems.containsKey(id))
                return false;

            Object object = mapItems.get(id);

            if (object instanceof HotZone){
                HotZone item = (HotZone) object;
                // samuel
                if (item.isShowroom){
                    showShowRoomPopUp(item.user);
                }
                else {
                    showHotZonePopUp(item);
                }
            }
            else if (object instanceof CarApiResponse){
                CarApiResponse item = (CarApiResponse) object;
                showUserPopUp(item);
            }
            else if (object instanceof DefaultUserDataApiResponse.DefaultUserApiResponse){
                DefaultUserDataApiResponse.DefaultUserApiResponse item = (DefaultUserDataApiResponse.DefaultUserApiResponse) object;
                showShowRoomPopUp(item);
            }

            return false;
        });

        setMyLocation(SnapCarApplication.getInstance().locationLiveData.getValue());
    }

    @Override
    public void showLoadingAnimation() {
        uiUtil.getProgressDialog(getString(R.string.please_wait_dotted))
                .show();
    }

    @Override
    public void hideLoadingAnimation() {
        uiUtil.getProgressDialog()
                .hide();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessToast(message).show();
//        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showErrorMessage(String msg) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), msg).show();
    }

    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void shouldNavigateToMap(LatLng location) {
        if (location == null)
            return;

        String uriBegin = "geo:" + location.latitude
                + "," + location.longitude;
        String query = location.latitude + "," + location.longitude;
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(mapIntent);
    }

    @Override
    public void onReceiveHotZones(List<HotZone> list) {
        // sam
        if (list == null)
            return;

        for (HotZone hotZone : list)
            mapItems.put(hotZone.getId(),hotZone);

        disposable.add(mapRender.getHotZoneRenderObserver(list)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> Timber.d(String.valueOf(System.currentTimeMillis())))
                .doOnComplete(() -> Timber.d(String.valueOf(System.currentTimeMillis())))
                .subscribe(this::addMarker,throwable -> {
                    Timber.e(throwable);
                    showErrorMessage(throwable.getMessage());
                }));

    }

    @Override
    public void onReceiveCars(List<CarApiResponse> list) {
        if (list == null)
            return;

        for (CarApiResponse item : list)
            mapItems.put(item.getId(),item);

        disposable.add(mapRender.getCarRenderObserver(list)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> Timber.d(String.valueOf(System.currentTimeMillis())))
                .doOnComplete(() ->{
                    Timber.d(String.valueOf(System.currentTimeMillis()));
                    presenter.startCarTracking();
                })
                .subscribe(this::addMarker,throwable -> {
                    Timber.e(throwable);
                    showErrorMessage(throwable.getMessage());
                }));
    }

    @Override
    public void onReceiveShowRoom(List<DefaultUserDataApiResponse.DefaultUserApiResponse> list) {
        if (list == null)
            return;

        for (DefaultUserDataApiResponse.DefaultUserApiResponse item : list)
            mapItems.put((int) item.getId(),item);

        disposable.add(mapRender.getShowRoomRenderObserver(list)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> Timber.d(String.valueOf(System.currentTimeMillis())))
                .doOnComplete(() -> Timber.d(String.valueOf(System.currentTimeMillis())))
                .subscribe(this::addMarker,throwable -> {
                    Timber.e(throwable);
                    showErrorMessage(throwable.getMessage());
                }));
    }

    private void addMarker(MapRender.RenderItem item){
        if (markerMap.containsKey(item.getId()))
            return;

        Marker marker = googleMap.addMarker(item.getMarkerOptions());
        marker.setAnchor(0.5f, 0.5f);
        marker.setZIndex(1);
        marker.setTag(item.getId());
        markerMap.put(item.getId(),marker);
    }

    @Override
    public void onCarLocationUpdate(TrackModel trackModel) {
        if (trackModel == null)
            return;

        if (!markerMap.containsKey(trackModel.getCarId())
                || trackModel.getCarId() == myCarId
                || !(mapItems.get(trackModel.getCarId()) instanceof CarApiResponse)) //navigate myCar with local GPS
            return;

        Marker marker = markerMap.get(trackModel.getCarId());
        animateMarker(marker,trackModel.getLocation());
    }

    public void animateMarker(final Marker marker, final LatLng toPosition) {
        animateMarker(marker,toPosition,true);
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        rotateHandler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    rotateHandler.postDelayed(this, 16);
                } else {

                }
            }
        });

    }

    public void animateMyMarker(final LatLng toPosition) {
        if (myMarker == null || toPosition == null)
            return;


        final Marker myCar = markerMap.get(myCarId);

        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = myMarker.getPosition();
        final long duration = 500;

        if (myCar!= null && canRotate(myCar.getPosition(),toPosition))
            rotateMarker(myCar,getBearing(myCar.getPosition(),toPosition));

        final Interpolator interpolator = new LinearInterpolator();
        moveHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;


                myMarker.setPosition(new LatLng(lat, lng));

                if (myCar != null)
                    myCar.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    moveHandler.postDelayed(this, 16);
                }
            }
        });
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,boolean rotate) {
        if (marker == null || toPosition == null)
            return;


//        final LatLng startPosition = marker.getPosition();
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
//        final float durationInMs = 3000;
//        final long rotateDuration = 1000;
//
//        final float startRotation = marker.getRotation();
//        final float toRotation = getBearing(marker.getPosition(),toPosition);
//
//
//
//        handler.post(new Runnable() {
//            long elapsed;
//            float t;
//            float v;
//
//            @Override
//            public void run() {
//                // Calculate progress using interpolator
//                elapsed = SystemClock.uptimeMillis() - start;
//                t = elapsed / durationInMs;
//                v = interpolator.getInterpolation(t);
//
//                LatLng currentPosition = new LatLng(
//                        startPosition.latitude*(1-t)+toPosition.latitude*t,
//                        startPosition.longitude*(1-t)+toPosition.longitude*t);
//
//                marker.setPosition(currentPosition);
//
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / rotateDuration);
//                float rot = t * toRotation + (1 - t) * startRotation;
//
//                if (canRotate(startPosition,toPosition))
//                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
//
//                // Repeat till progress is complete.
//                if (t < 1) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                }
//            }
//
//        });


        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final long duration = 500;

        if (rotate && canRotate(marker.getPosition(),toPosition))
            rotateMarker(marker,getBearing(marker.getPosition(),toPosition));

        final Interpolator interpolator = new LinearInterpolator();
        moveHandler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    moveHandler.postDelayed(this, 16);
                } else {
                    marker.setVisible(true);
                }
            }
        });
    }

    private synchronized Projection getMapProjection(){
        return googleMap.getProjection();
    }

    private static boolean canRotate(LatLng old, LatLng newLocation){
        if (old == null || newLocation == null)
            return false;

        Location location1 = new Location("gps");
        location1.setLatitude(old.latitude);
        location1.setLongitude(old.longitude);

        Location location2 = new Location("gps");
        location2.setLatitude(newLocation.latitude);
        location2.setLongitude(newLocation.longitude);

        return location1.distanceTo(location2) > 15;
    }

    private static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }


    private void showUserPopUp(CarApiResponse carItem) {
        @FilterType int type = FilterType.MALE;
        if (carItem.getCarType() == FilterType.MALE) {
            type = FilterType.MALE;
        } else if (carItem.getCarType() == FilterType.FEMALE) {
            type = FilterType.FEMALE;
        } else if (carItem.getCarType() == FilterType.DAMAGED) {
            type = FilterType.DAMAGED;
        }
        //                .visibility(R.id.tv_damaged, 0, type == FilterType.DAMAGED)
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_car_map_detail)
                .text(R.id.tv_hot_zone_name, carItem.getCarName())
                .drawableEnd(R.id.tv_view_more, icons.getDrawable(1))
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(carItem.getUser().getImage()))
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(carItem.getUser().getImage()))
                .setActivated(R.id.tv_type, type == FilterType.FEMALE)
                .text(R.id.tv_type, carItem.getCarType() == FilterType.FEMALE ? getString(R.string.femalee) : getString(R.string.malee))
                .text(R.id.tv_model, carItem.getManufacturingYear() != null ? getString(R.string.yearr).concat(" ").concat(carItem.getManufacturingYear()) : "")
                .textColor(R.id.tv_type, carItem.getCarType() == FilterType.FEMALE ? R.color.colorAccent : R.color.colorPrimary)
                .setActivated(R.id.iv_fav, carItem.getFavorite())
                .visibility(R.id.tv_damaged, R.id.tv_type, type == FilterType.DAMAGED)

                .clickListener(R.id.car_container, (dialog, view) -> {
                    Intent i = new Intent(getContext(), ViewCarActivity.class);
                    i.putExtra("carId", carItem.getId());
                    startActivity(i);
                    dialog.dismiss();
                })
                .clickListener(R.id.iv_fav, (dialog, view) -> {
                    if (userManager.sessionManager().isSessionActive()) {
                        if (carItem.getId() != userManager.getCurrentUser().getId()) {
                            presenter.onFavoriteClicked(String.valueOf(carItem.getId()),
                                    null,
                                    new OperationListener<Void>() {
                                        @Override
                                        public void onPreOperation() {
                                            showLoadingAnimation();
                                        }

                                        @Override
                                        public void onPostOperation() {
                                            hideLoadingAnimation();
                                        }

                                        @Override
                                        public void onSuccess(Void element) {
                                            view.setActivated(!view.isActivated());
                                            presenter.fetchData();
                                        }

                                        @Override
                                        public void onError(Throwable t) {
                                            presenter.processError(t);
                                        }
                                    });
                        } else {
                            showWarningMessage(getString(R.string.you_are_not_allowed_favorite_your_item));
                        }
                    } else {
                        showLoginFirstPopup();
                    }
                })
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }

    private void showHotZonePopUp(HotZone hotZone) {

        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_hot_zone_map_detail)
                .setVisbility(R.id.moreTextView,!hotZone.getShowroom())
                .text(R.id.tv_hot_zone_name, hotZone.getName())
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(hotZone.getImage()))
                .clickListener(R.id.tv_location, (dialog, view) -> {
                    presenter.getDirection(hotZone.getId(), hotZone.getLocation());
                })
                .clickListener(R.id.moreTextView, (dialog, view) -> {
                    new VisitorProfileActivityArgs(hotZone.getId())
                            .launch(getActivity());
                })
                .clickListener(R.id.iv_share, ((dialog, view) -> {
                    processShareClicked(hotZone.getLocation());
                    dialog.dismiss();
                }))
                .clickListener(R.id.iv_question, (dialog, view) -> {
                    showHotZoneInfoPopup();
                    dialog.dismiss();
                })
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }

    /*
     *This method is for showing definition of hotzone when click on question Mark
     */
    private void showHotZoneInfoPopup() {
        uiUtil.getDialogBuilder(getActivity(), R.layout.layout_hotzone_info_dialog)
                .text(R.id.tv_message, getString(R.string.hotzone_info))
                .clickListener(R.id.yes, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .cancelable(true)
                .build()
                .show();
    }

    private void showShowRoomPopUp(DefaultUserDataApiResponse.DefaultUserApiResponse filterDetailViewModelTag) {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        String numberOfCars;
        if (filterDetailViewModelTag.getAvailableCar().contentEquals("0")) {
            numberOfCars = getString(R.string.no_aailable_cars);
        } else {
            numberOfCars = getString(R.string.car_available_for_sell).concat(filterDetailViewModelTag.getAvailableCar());
        }
        if (filterDetailViewModelTag.isHasHotZone()) {
            uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_show_room_hot_zone_map_details)
                    .text(R.id.tv_hot_zone_name, filterDetailViewModelTag.getName())
                    .drawableEnd(R.id.tv_view_more, icons.getDrawable(1))
                    .text(R.id.tv_available_car, numberOfCars)
                    .setVisbility(R.id.im_vip, !filterDetailViewModelTag.getShowroomInfo().getPremium())
                    .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                    .setActivated(R.id.iv_fav, filterDetailViewModelTag.getFav())
                    .clickListener(R.id.car_container, (dialog, view) -> {
                        if (userManager.sessionManager().isSessionActive()) {
                            if (userManager.getCurrentUser().getId() != filterDetailViewModelTag.getId()) {
                                new VisitorProfileActivityArgs(filterDetailViewModelTag.getId())
                                        .launch(getActivity());
                            } else {
                                showWarningMessage(getString(R.string.visite_your_profile));
                            }
                        } else {
                            new VisitorProfileActivityArgs(filterDetailViewModelTag.getId())
                                    .launch(getActivity());
                        }
                        dialog.dismiss();
                    })
                    .clickListener(R.id.iv_fav, (dialog, view) -> {
                        if (userManager.sessionManager().isSessionActive()) {
                            if (filterDetailViewModelTag.getId() != userManager.getCurrentUser().getId()) {
                                presenter.onFavoriteClicked(null,
                                        String.valueOf(filterDetailViewModelTag.getId()),
                                        new OperationListener<Void>() {
                                            @Override
                                            public void onPreOperation() {
                                                showLoadingAnimation();
                                            }

                                            @Override
                                            public void onPostOperation() {
                                                hideLoadingAnimation();
                                            }

                                            @Override
                                            public void onSuccess(Void element) {
                                                view.setActivated(!view.isActivated());
                                                presenter.fetchData();
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                presenter.processError(t);
                                            }
                                        });
                            }
                        } else {
                            showLoginFirstPopup();
                        }
                    })
                    .clickListener(R.id.tv_location, (dialog, view) -> {
                        presenter.getDirection(filterDetailViewModelTag.getShowroomInfo().getHotZoneId(), filterDetailViewModelTag.getShowRoomLocation());
                    })
                    .background(R.drawable.inset_bottomsheet_background)
                    .cancelable(true)
                    .build()
                    .show();

        } else {
            uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_show_room_map_detail)
                    .text(R.id.tv_hot_zone_name, filterDetailViewModelTag.getName())
                    .drawableEnd(R.id.tv_view_more, icons.getDrawable(1))
                    .text(R.id.tv_available_car, numberOfCars)
                    .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                    .setActivated(R.id.iv_fav, filterDetailViewModelTag.getFav())
                    .setVisbility(R.id.im_vip, !filterDetailViewModelTag.getShowroomInfo().getPremium())
                    .clickListener(R.id.car_container, (dialog, view) -> {
                        if (userManager.sessionManager().isSessionActive()) {
                            if (userManager.getCurrentUser().getId() != filterDetailViewModelTag.getId()) {
                                new VisitorProfileActivityArgs(filterDetailViewModelTag.getId())
                                        .launch(getActivity());
                            } else {
                                showWarningMessage(getString(R.string.visite_your_profile));
                            }
                        } else {
                            new VisitorProfileActivityArgs(filterDetailViewModelTag.getId())
                                    .launch(getActivity());
                        }
                        dialog.dismiss();
                    })
                    .clickListener(R.id.iv_fav, (dialog, view) -> {
                        if (userManager.sessionManager().isSessionActive()) {
                            if (filterDetailViewModelTag.getId() != userManager.getCurrentUser().getId()) {
                                presenter.onFavoriteClicked(null,
                                        String.valueOf(filterDetailViewModelTag.getId()),
                                        new OperationListener<Void>() {
                                            @Override
                                            public void onPreOperation() {
                                                showLoadingAnimation();
                                            }

                                            @Override
                                            public void onPostOperation() {
                                                hideLoadingAnimation();
                                            }

                                            @Override
                                            public void onSuccess(Void element) {
                                                view.setActivated(!view.isActivated());
                                                presenter.fetchData();
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                presenter.processError(t);
                                            }
                                        });
                            } else {
                                showWarningMessage(getString(R.string.you_are_not_allowed_favorite_your_item));
                            }
                        } else {
                            showLoginFirstPopup();
                        }
                    })
                    .clickListener(R.id.tv_location, (dialog, view) -> {
                        String uriBegin = "geo:" + filterDetailViewModelTag.getShowRoomLocation().latitude
                                + "," + filterDetailViewModelTag.getShowRoomLocation().longitude;
                        String query = filterDetailViewModelTag.getShowRoomLocation().latitude + "," + filterDetailViewModelTag.getShowRoomLocation().longitude;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
                    })
                    .background(R.drawable.inset_bottomsheet_background)
                    .cancelable(true)
                    .build()
                    .show();
        }
    }

    private void showLoginFirstPopup() {
        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.layout_login_dialog)
                .clickListener(R.id.tv_login, (dialog, view) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    dialog.dismiss();
                })
                .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .transparentBackground(true)
                .cancelable(true)
                .build()
                .show();
    }

    /*
     *This method is for sharing the hotzone location on the map in internal chat
     */
    private void processShareClicked(LatLng location) {
        PlaceDTO placeDTO = new PlaceDTO("", location.latitude, location.longitude);
        Bundle args = new Bundle();
        args.putParcelable(PlaceDTO.TAG, placeDTO);
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);
        if (getActivity() instanceof HostActivity)
            ((HostActivity) getActivity()).setFragment(chatFragment, ChatFragment.class.getSimpleName());
    }

}
