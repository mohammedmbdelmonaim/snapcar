package com.intcore.snapcar.ui.explorer;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterManager;
import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;
import com.intcore.snapcar.ui.chat.ChatFragment;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivityArgs;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.UiUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/*
   This class is for showing tracked cars, showroom and hotzone on Map.
*/
@FragmentScope
public class ExplorerFragment extends Fragment implements ExplorerScreen, OnMapReadyCallback {

    @Inject
    ExplorerPresenter presenter;
    @Inject
    UiUtil uiUtil;
    @Inject
    ResourcesUtil resourcesUtil;
    @Inject
    UserManager userManager;
    ClusterManager<FilterDetailViewModel> mClusterManager;
    private GoogleMap googleMap;
    private SparseArray<Marker> allMarkers = new SparseArray<>();
    private MapMarkerRender renderer;
    private MarkerOptions marker;
    private float v;
    private int emission = 0;
    private ValueAnimator valueAnimator;
    private HomeFragment homeFragment;
//    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public ExplorerFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return inflater.inflate(R.layout.fragment_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter.onViewCreated();
    }

    /*
   This function is used for scale icons on map depending on map zoom.
   */
    @Override
    public void setupCameraChangeListener() {
        if (googleMap != null) {
            googleMap.setOnCameraMoveStartedListener(z -> {
                Log.d("minaZomm", String.valueOf(googleMap.getCameraPosition().zoom));
                float zoomLevel = googleMap.getCameraPosition().zoom;
                int width = (int) zoomLevel;
                int height = (int) zoomLevel;
                for (int i = 0; i < allMarkers.size(); i++) {
                    int key = allMarkers.keyAt(i);
                    Marker x = allMarkers.get(key);
                    FilterDetailViewModel filterDetailViewModel = (FilterDetailViewModel) x.getTag();
                    if (filterDetailViewModel != null)
                        if (filterDetailViewModel.getCarId() != 0) {
//                            x.setIcon(BitmapDescriptorFactory
//                                    .fromBitmap(resizeMapIcons(filterDetailViewModel.getIcon(), width * 3, height * 6)));
                        } else {
//                            x.setIcon(BitmapDescriptorFactory
//                                    .fromBitmap(resizeMapIcons(filterDetailViewModel.getIcon(), width * 8, width * 8)));
                        }
                }
            });
        }
    }

    /*
   This function is called to generate new bitmap with new size.
   */
    private Bitmap resizeMapIcons(Bitmap icon, int width, int height) {
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(getActivity()).get())
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper());
    }

    @Override
    public void stopReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(new WeakReference<Context>(getActivity()).get())
                .removeLocationUpdates(locationCallback);
    }

    @Override
    public void updateClusterItems(FilterDetailViewModel filterDetailViewModel) {
        render(filterDetailViewModel);
    }

    @Override
    public void setupMap() {
        if (getActivity() instanceof HostActivity) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void clearMap() {
        if (googleMap != null) {
            googleMap.clear();
            allMarkers.clear();
            if (marker != null) {
                googleMap.addMarker(marker);
            }
            mClusterManager = new ClusterManager<>(getActivity(), googleMap);
            googleMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterItemClickListener(filterDetailViewModelTag -> {
                if (filterDetailViewModelTag != null) {
                    if (filterDetailViewModelTag.getType() == FilterType.FEMALE ||
                            filterDetailViewModelTag.getType() == FilterType.MALE ||
                            filterDetailViewModelTag.getType() == FilterType.DAMAGED) {
                        showUserPopUp(filterDetailViewModelTag);
                    } else if (filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_FEMALE ||
                            filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_MALE ||
                            filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_MIXED) {
                        showHotZonePopUp(filterDetailViewModelTag);
                    } else if (filterDetailViewModelTag.getType() == FilterType.SHOW_ROOM) {
                        showShowRoomPopUp(filterDetailViewModelTag);
                    }
                }
                return true;
            });
            renderer = new MapMarkerRender(getActivity(), googleMap, mClusterManager);
            mClusterManager.setRenderer(renderer);
            googleMap.setOnCameraIdleListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(cluster -> {
                if (cluster != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                            (float) Math.floor(googleMap.getCameraPosition().zoom + 4)), 300,
                            null);
                }
                return true;
            });
        }
    }

    public void render(FilterDetailViewModel filterDetailViewModel) {
        if (googleMap != null) {
            if (filterDetailViewModel.getNewLatLng() != null) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(filterDetailViewModel.getNewLatLng())
                        .icon(filterDetailViewModel.getBitmapDescriptor());
                Marker marker = googleMap.addMarker(markerOptions);
                marker.setTag(filterDetailViewModel);
                if (filterDetailViewModel.getCarId() != 0) {
                    allMarkers.append(filterDetailViewModel.getCarId(), marker);
//                    marker.setIcon(BitmapDescriptorFactory
//                            .fromBitmap(resizeMapIcons(filterDetailViewModel.getIcon(), width, height)));
                } else {
                    allMarkers.append(filterDetailViewModel.getId(), marker);
//                    marker.setIcon(BitmapDescriptorFactory
//                            .fromBitmap(resizeMapIcons(filterDetailViewModel.getIcon(), width * 2, width * 2)));
                }
            }
            googleMap.setOnMarkerClickListener(marker1 -> {
                FilterDetailViewModel filterDetailViewModelTag = (FilterDetailViewModel) marker1.getTag();
                if (filterDetailViewModelTag != null) {
                    if (filterDetailViewModelTag.getType() == FilterType.FEMALE ||
                            filterDetailViewModelTag.getType() == FilterType.MALE ||
                            filterDetailViewModelTag.getType() == FilterType.DAMAGED) {
                        showUserPopUp(filterDetailViewModelTag);
                    } else if (filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_FEMALE ||
                            filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_MALE ||
                            filterDetailViewModelTag.getType() == FilterType.HOT_ZONE_MIXED) {
                        showHotZonePopUp(filterDetailViewModelTag);
                    } else if (filterDetailViewModelTag.getType() == FilterType.SHOW_ROOM) {
                        showShowRoomPopUp(filterDetailViewModelTag);
                    }
                }
                return true;
            });
        }


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
    public void shouldNavigateToMap(FilterDetailViewModel filterDetailViewModelTag) {
        String uriBegin = "geo:" + filterDetailViewModelTag.getNewLatLng().latitude
                + "," + filterDetailViewModelTag.getNewLatLng().longitude;
        String query = filterDetailViewModelTag.getNewLatLng().latitude + "," + filterDetailViewModelTag.getNewLatLng().longitude;
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(mapIntent);
    }

    @Override
    public void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
    public void onResume() {
        presenter.onResume();
        if (homeFragment != null) {
            homeFragment.setFilterNearByDataReady(presenter::processResult);
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        presenter.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupCameraChangeListener();
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
    }

    /*
     * This method is for updating my location on map on Location Change
     */
    @Override
    public void updateMap(LatLng latLng) {
        if (homeFragment != null) {
            homeFragment.setLocation(presenter.getLastKnownLocation());
        }
        if (googleMap != null) {
            marker = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(resourcesUtil
                                    .loadBitmapFromView(getLayoutInflater().inflate(R.layout.marker, null))));
            googleMap.addMarker(marker);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            CameraPosition target = CameraPosition.builder()
                    .target(latLng)
                    .zoom(17)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        }
    }

    /*
     * This method is for drawing the items on the map when data received from server
     */
    @Override
    public void setMapIcons(FilterDetailViewModel filterDetailViewModel) {
        if (googleMap != null) {
            if (filterDetailViewModel.getOldLatLng() != null && renderer != null) {
                float[] result = new float[4];
                Location.distanceBetween(filterDetailViewModel.getOldLatLng().latitude,
                        filterDetailViewModel.getOldLatLng().longitude,
                        filterDetailViewModel.getNewLatLng().latitude,
                        filterDetailViewModel.getNewLatLng().longitude, result);
                if (result[0] >= 50) {
                    List<LatLng> latLngs = new ArrayList<>();
                    latLngs.add(filterDetailViewModel.getOldLatLng());
                    latLngs.add(filterDetailViewModel.getNewLatLng());
                    if (filterDetailViewModel.getCarId() != 0)
                        animateCarOnMap(allMarkers.get(filterDetailViewModel.getCarId()), latLngs);
                    else
                        animateCarOnMap(allMarkers.get(filterDetailViewModel.getId()), latLngs);
                }
            }
        }
    }

    private float getBearing(LatLng begin, LatLng end) {
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

    private void animateCarOnMap(final Marker marker, final List<LatLng> latLngs) {
        if (latLngs.size() == 2) {
            if (latLngs.get(0) != null || latLngs.get(1) != null) {
                if (valueAnimator != null) {
                    if (valueAnimator.isRunning()) {
                        valueAnimator.cancel();
                    }
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : latLngs) {
                    builder.include(latLng);
                }
                if (emission == 1) {
                }
                marker.setPosition(latLngs.get(0));
                valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(10000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(valueAnimator1 -> {
                    v = valueAnimator1.getAnimatedFraction();
                    double lng = v * latLngs.get(1).longitude + (1 - v)
                            * latLngs.get(0).longitude;
                    double lat = v * latLngs.get(1).latitude + (1 - v)
                            * latLngs.get(0).latitude;
                    LatLng newPos = new LatLng(lat, lng);
                    marker.setPosition(newPos);
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(getBearing(latLngs.get(0), newPos));
                });
                valueAnimator.start();
            }
        }
    }


    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    /*
     *This method is for sharing the hotzone location on the map in internal chat
     */
    private void processShareClicked(FilterDetailViewModel filterDetailViewModelTag) {
        PlaceDTO placeDTO = new PlaceDTO("", filterDetailViewModelTag.getNewLatLng().latitude,
                filterDetailViewModelTag.getNewLatLng().longitude);
        Bundle args = new Bundle();
        args.putParcelable(PlaceDTO.TAG, placeDTO);
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);
        if (getActivity() instanceof HostActivity)
            ((HostActivity) getActivity()).setFragment(chatFragment, ChatFragment.class.getSimpleName());
    }

    /*
     *This method is for showing car details popup when click on car on map
     */
    @SuppressLint("ResourceType")
    private void showUserPopUp(FilterDetailViewModel filterDetailViewModelTag) {
        @FilterType int type = FilterType.MALE;
        if (filterDetailViewModelTag.getType() == FilterType.MALE) {
            type = FilterType.MALE;
        } else if (filterDetailViewModelTag.getType() == FilterType.FEMALE) {
            type = FilterType.FEMALE;
        } else if (filterDetailViewModelTag.getType() == FilterType.DAMAGED) {
            type = FilterType.DAMAGED;
        }
        //                .visibility(R.id.tv_damaged, 0, type == FilterType.DAMAGED)
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_car_map_detail)
                .text(R.id.tv_hot_zone_name, filterDetailViewModelTag.getName())
                .drawableEnd(R.id.tv_view_more, icons.getDrawable(1))
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                .setActivated(R.id.tv_type, type == FilterType.FEMALE)
                .text(R.id.tv_type, filterDetailViewModelTag.getType() == FilterType.FEMALE ? getString(R.string.femalee) : getString(R.string.malee))
                .text(R.id.tv_model, filterDetailViewModelTag.getYear() != null ? getString(R.string.yearr).concat(" ").concat(filterDetailViewModelTag.getYear()) : "")
                .textColor(R.id.tv_type, filterDetailViewModelTag.getType() == FilterType.FEMALE ? R.color.colorAccent : R.color.colorPrimary)
                .setActivated(R.id.iv_fav, filterDetailViewModelTag.isFavorite())
                .visibility(R.id.tv_damaged, R.id.tv_type, type == FilterType.DAMAGED)

                .clickListener(R.id.car_container, (dialog, view) -> {
                    Intent i = new Intent(getContext(), ViewCarActivity.class);
                    i.putExtra("carId", filterDetailViewModelTag.getCarId());
                    startActivity(i);
                    dialog.dismiss();
                })
                .clickListener(R.id.iv_fav, (dialog, view) -> {
                    if (userManager.sessionManager().isSessionActive()) {
                        if (filterDetailViewModelTag.getId() != userManager.getCurrentUser().getId()) {
                            presenter.onFavoriteClicked(String.valueOf(filterDetailViewModelTag.getCarId()),
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

    /*
     *This method is for showing hotzone details popup when click on hotzone on map
     */
    @SuppressLint("ResourceType")
    private void showHotZonePopUp(FilterDetailViewModel filterDetailViewModelTag) {

        uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_hot_zone_map_detail)
                .setVisbility(R.id.moreTextView,!filterDetailViewModelTag.getShowroom())
                .text(R.id.tv_hot_zone_name, filterDetailViewModelTag.getName())
                .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                .clickListener(R.id.tv_location, (dialog, view) -> {
                    presenter.getDirection(filterDetailViewModelTag.getId(), filterDetailViewModelTag);
                })
                .clickListener(R.id.moreTextView, (dialog, view) -> {
                    new VisitorProfileActivityArgs(filterDetailViewModelTag.getId())
                            .launch(getActivity());
                })
                .clickListener(R.id.iv_share, ((dialog, view) -> {
                    processShareClicked(filterDetailViewModelTag);
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
     *This method is for showing showroom details popup when click on showroom on map
     */
    @SuppressLint("ResourceType")
    private void showShowRoomPopUp(FilterDetailViewModel filterDetailViewModelTag) {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        String numberOfCars;
        if (filterDetailViewModelTag.getCarNumbers().contentEquals("0")) {
            numberOfCars = getString(R.string.no_aailable_cars);
        } else {
            numberOfCars = getString(R.string.car_available_for_sell).concat(filterDetailViewModelTag.getCarNumbers());
        }
        if (filterDetailViewModelTag.isHotZone()) {
            uiUtil.getBottomSheetBuilder(getActivity(), R.layout.item_show_room_hot_zone_map_details)
                    .text(R.id.tv_hot_zone_name, filterDetailViewModelTag.getName())
                    .drawableEnd(R.id.tv_view_more, icons.getDrawable(1))
                    .text(R.id.tv_available_car, numberOfCars)
                    .setVisbility(R.id.im_vip, !filterDetailViewModelTag.isPremium())
                    .image(R.id.hot_zone_avatar, ApiUtils.BASE_URL.concat(filterDetailViewModelTag.getImageUrl()))
                    .setActivated(R.id.iv_fav, filterDetailViewModelTag.isFavorite())
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
                        String uriBegin = "geo:" + filterDetailViewModelTag.getNewLatLng().latitude
                                + "," + filterDetailViewModelTag.getNewLatLng().longitude;
                        String query = filterDetailViewModelTag.getNewLatLng().latitude + "," + filterDetailViewModelTag.getNewLatLng().longitude;
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                        startActivity(mapIntent);
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
                    .setActivated(R.id.iv_fav, filterDetailViewModelTag.isFavorite())
                    .setVisbility(R.id.im_vip, !filterDetailViewModelTag.isPremium())
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
                        String uriBegin = "geo:" + filterDetailViewModelTag.getNewLatLng().latitude
                                + "," + filterDetailViewModelTag.getNewLatLng().longitude;
                        String query = filterDetailViewModelTag.getNewLatLng().latitude + "," + filterDetailViewModelTag.getNewLatLng().longitude;
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

    public void setFragmentManager(FragmentManager fragmentManager) {
        homeFragment = ((HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName()));
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessToast(message).show();
//        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showGPSIsRequiredMessage() {
        if (getActivity() instanceof HostActivity)
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.gps_required)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED)
                    presenter.onStart();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}