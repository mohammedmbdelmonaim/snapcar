package com.intcore.snapcar.ui.explorer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.UserUpdateLocationRepo;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailMapper;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;
import com.intcore.snapcar.store.model.firebase.FireBaseModel;
import com.intcore.snapcar.store.model.hotzone.HotZone;
import com.intcore.snapcar.store.model.hotzone.HotZoneMapper;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@FragmentScope
class ExplorerPresenter {

    private final FilterDetailMapper filterDetailMapper;
    private final Context context;
    private final DefaultUserMapper defaultUserMapper;
    private final ThreadSchedulers threadSchedulers;
    private final PreferencesUtil preferencesUtil;
    private final CompositeDisposable disposable;
    private final PermissionUtil permissionUtil;
    private final ExplorerScreen explorerScreen;
    private final HotZoneMapper hotZoneMapper;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final CarMapper carMapper;
    private final HomeRepo homeRepo;
    private List<FilterDetailViewModel> trackedCarViewModels;
    private LocationCallback locationCallback;
    private Location lastKnownLocation;
    private UserUpdateLocationRepo updateLocationRepo;
    private ExecutorService executor = Executors.newFixedThreadPool(7);
    private ExecutorService executor2 = Executors.newFixedThreadPool(1);

    @Inject
    ExplorerPresenter(@IOThread ThreadSchedulers threadSchedulers,
                      @ForFragment CompositeDisposable disposable,
                      FilterDetailMapper filterDetailMapper,
                      DefaultUserMapper defaultUserMapper,
                      PreferencesUtil preferencesUtil,
                      PermissionUtil permissionUtil,
                      ExplorerScreen explorerScreen,
                      @ForFragment Context context,
                      HotZoneMapper hotZoneMapper,
                      ResourcesUtil resourcesUtil,
                      UserManager userManager,
                      CarMapper carMapper,
                      HomeRepo homeRepo,
                      UserUpdateLocationRepo updateLocationRepo) {
        this.locationCallback = createLocationCallback();
        this.trackedCarViewModels = new ArrayList<>();
        this.updateLocationRepo = updateLocationRepo;
        this.filterDetailMapper = filterDetailMapper;
        this.defaultUserMapper = defaultUserMapper;
        this.threadSchedulers = threadSchedulers;
        this.preferencesUtil = preferencesUtil;
        this.permissionUtil = permissionUtil;
        this.explorerScreen = explorerScreen;
        this.hotZoneMapper = hotZoneMapper;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.carMapper = carMapper;
        this.homeRepo = homeRepo;
        this.context = context;
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location result = locationResult.getLastLocation();
                if (result != null) {
                    lastKnownLocation = result;
                    fetchData();
                    updateLocations();
                    explorerScreen.updateMap(new LatLng(result.getLatitude(), result.getLongitude()));
                }
            }
        };
    }

    void onViewCreated() {
        this.locationCallback = createLocationCallback();
        explorerScreen.setupMap();
        explorerScreen.clearMap();
        explorerScreen.setupCameraChangeListener();
    }

    void onResume() {
        if (lastKnownLocation != null) {
//            explorerScreen.updateMap(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        }
    }

    private void updateLocations() {
        if (userManager.sessionManager().isSessionActive()) {
            updateLocationRepo.updateUserLocation(userManager.getCurrentUser().getApiToken(),
                    String.valueOf(lastKnownLocation.getLongitude()),
                    String.valueOf(lastKnownLocation.getLatitude()));
        }
    }

    @SuppressLint("CheckResult")
    private void fetchFireBaseData() {
        Handler handler = new Handler(Looper.getMainLooper());
        executor2.submit(new Thread(() -> {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Timber.d("Warning your Thread on UI Thread.....#####");
            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                        FireBaseModel fireBaseModel = snapShot.getValue(FireBaseModel.class);
                        for (FilterDetailViewModel viewModel : trackedCarViewModels) {
                            if (fireBaseModel != null && viewModel != null) {
                                if (fireBaseModel.getCarId() == viewModel.getCarId()) {
                                    if (viewModel.getOldLatLng() != null)
                                        if (!String.valueOf(viewModel.getOldLatLng().longitude).equals(fireBaseModel.getOldLatitude())) {
                                            viewModel.setNewLatLng(new LatLng(Double.valueOf(fireBaseModel.getLatitude()), Double.valueOf(fireBaseModel.getLongitude())));
                                            viewModel.setOldLatLng(new LatLng(Double.valueOf(fireBaseModel.getOldLatitude()), Double.valueOf(fireBaseModel.getOldLongitude())));
                                            handler.post(() -> explorerScreen.setMapIcons(viewModel));
                                        }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Timber.tag("minaFirebase").d(databaseError.getMessage());
                }
            });
        }));
    }

    void fetchData() {
        Timber.d("myLongitude", String.valueOf(lastKnownLocation.getLongitude()));
        Timber.d("myLatitude", String.valueOf(lastKnownLocation.getLatitude()));
        if (lastKnownLocation != null) {
            String apiToken = null;
            if (userManager.sessionManager().isSessionActive()) {
                apiToken = userManager.getCurrentUser().getApiToken();
            }
            disposable.add(homeRepo.filterHome(apiToken,
                    0,
                    0,
                    0,
                    preferencesUtil.getInt("showAll", 1),
                    preferencesUtil.getInt("menCar", 0),
                    preferencesUtil.getInt("womenCar", 0),
                    preferencesUtil.getInt("damagedCar", 0),
                    preferencesUtil.getInt("vipShowRoom", 0),
                    preferencesUtil.getInt("showRoom", 0),
                    preferencesUtil.getInt("vipHotZone", 0),
                    preferencesUtil.getInt("hotZone", 0),
                    lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(filterApiResponse -> {
                        processResult(filterApiResponse);
                        trackedCarViewModels.clear();
                        for (CarApiResponse model : filterApiResponse.getMaleList()) {
                            FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                    .toViewModelFromCar(carMapper.toCarModel(model), FilterType.MALE);
                            trackedCarViewModels.add(filterViewModel1);
                        }
                        for (CarApiResponse model : filterApiResponse.getFemaleList()) {
                            FilterDetailViewModel filterViewModel1 = filterDetailMapper.toViewModelFromCar(carMapper.toCarModel(model), FilterType.FEMALE);
                            trackedCarViewModels.add(filterViewModel1);
                        }
                        for (CarApiResponse model : filterApiResponse.getDamageList()) {
                            FilterDetailViewModel filterViewModel1 = filterDetailMapper.toViewModelFromCar(carMapper.toCarModel(model), FilterType.DAMAGED);
                            trackedCarViewModels.add(filterViewModel1);
                        }
                        fetchFireBaseData();
                    }, this::processError));
        }
    }

    void processResult(FilterApiResponse filterViewModel) {
        explorerScreen.clearMap();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        Timber.d("Warning your Thread on UI Thread.....#####");
                    }
                    for (DefaultUserDataApiResponse.DefaultUserApiResponse model : filterViewModel.getShowRoomList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromShowRoom(defaultUserMapper.toModel(model), FilterType.SHOW_ROOM);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

        });

        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (HotZone model : filterViewModel.getHotZoneFemaleList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromHotZone(hotZoneMapper.toHotZoneModel(model), FilterType.HOT_ZONE_FEMALE);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (HotZone model : filterViewModel.getHotZoneMixList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromHotZone(hotZoneMapper.toHotZoneModel(model), FilterType.HOT_ZONE_MIXED);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (HotZone model : filterViewModel.getHotZoneMaleList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromHotZone(hotZoneMapper.toHotZoneModel(model), FilterType.HOT_ZONE_MALE);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (CarApiResponse model : filterViewModel.getMaleList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper.toViewModelFromCar(carMapper.toCarModel(model), FilterType.MALE);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (CarApiResponse model : filterViewModel.getFemaleList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromCar(carMapper.toCarModel(model), FilterType.FEMALE);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
        executor.submit(new Thread() {
            @Override
            public void run() {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    for (CarApiResponse model : filterViewModel.getDamageList()) {
                        FilterDetailViewModel filterViewModel1 = filterDetailMapper
                                .toViewModelFromCar(carMapper.toCarModel(model), FilterType.DAMAGED);
                        handler.post(() -> explorerScreen.updateClusterItems(filterViewModel1));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
    }


    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            explorerScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                explorerScreen.processLogout();
            }
        } else if (t instanceof IOException) {
            explorerScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            explorerScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    void onStart() {
        startReceivingLocationUpdates();
    }

    void onStop() {
        stopReceivingLocationUpdates();
    }

    private void startReceivingLocationUpdates() {
        if (permissionUtil.hasLocationPermission()) {
            if (permissionUtil.isGPSEnabled()) {
                explorerScreen.startReceivingLocationUpdates(locationCallback);
            } else {
                explorerScreen.showGPSIsRequiredMessage();
            }
        } else {
            explorerScreen.requestLocationPermission();
        }
    }

    private void stopReceivingLocationUpdates() {
        explorerScreen.stopReceivingLocationUpdates(locationCallback);
    }

    void onMyCurrentLocationClick() {
        if (lastKnownLocation != null) {
            explorerScreen.updateMap(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        }
    }

    void getDirection(int hotZoneId, FilterDetailViewModel filterDetailViewModel) {
        if (userManager.sessionManager().isSessionActive()) {
            int type = userManager.getCurrentUser().getType();
            if (type == UserType.USER||type == UserType.SHOW_ROOM) {
                disposable.add(homeRepo.getDirection(userManager.getCurrentUser().getApiToken(), hotZoneId)
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> explorerScreen.showLoadingAnimation())
                        .doFinally(explorerScreen::hideLoadingAnimation)
                        .subscribe(() -> {
                            explorerScreen.hideLoadingAnimation();
                            explorerScreen.shouldNavigateToMap(filterDetailViewModel);
                        }, this::processError));
            } else {
                explorerScreen.shouldNavigateToMap(filterDetailViewModel);
            }
        } else {
            explorerScreen.shouldNavigateToMap(filterDetailViewModel);
        }
    }

    void onFavoriteClicked(String carId, String
            userId, OperationListener<Void> operationListener) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(homeRepo.addItemToFavorite(userManager.getCurrentUser().getApiToken(), carId, userId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(v ->
                            {
                                explorerScreen.showSuccessMessage(v.getMessage());
                                operationListener.onSuccess(null);
                            }
                           , operationListener::onError));
        } else {
            if (resourcesUtil.isEnglish()) {
                explorerScreen.showErrorMessage("You Should be Logged in");
            } else {
                explorerScreen.showErrorMessage("يجب تسجيل الدخول");
            }
        }
    }

    void onDestroy() {
        disposable.clear();
        executor.shutdown();
        executor2.shutdown();
    }

    Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void hotzoneLocationClicked(int id) {

        String apiToken = null;
        if(userManager.sessionManager().isSessionActive())
            apiToken = userManager.getCurrentUser().getApiToken();

        disposable.add(homeRepo.hotzoneLocationClicked(apiToken, id)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(()->Timber.tag("MinaLog").v("HotzoneClicked"), this::processError));
    }
}