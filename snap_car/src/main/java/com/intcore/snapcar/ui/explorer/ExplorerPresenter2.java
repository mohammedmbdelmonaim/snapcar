package com.intcore.snapcar.ui.explorer;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.carTrack.TrackModel;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;
import com.intcore.snapcar.store.model.hotzone.HotZone;
import com.intcore.snapcar.util.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@FragmentScope
public class ExplorerPresenter2 {

    private final ExplorerScreen2 screen;
    private final CompositeDisposable disposable;
    private final PreferencesUtil preferencesUtil;
    private final UserManager userManager;
    private final HomeRepo homeRepo;
    private final ResourcesUtil resourcesUtil;

    private ChildEventListener childEventListener;
    private DatabaseReference firebaseReference;

    @Inject
    public ExplorerPresenter2(ExplorerScreen2 screen, @ForFragment CompositeDisposable disposable,
                              PreferencesUtil preferencesUtil,UserManager userManager,
                              HomeRepo homeRepo, ResourcesUtil resourcesUtil){
        this.screen = screen;
        this.disposable = disposable;
        this.preferencesUtil = preferencesUtil;
        this.userManager = userManager;
        this.homeRepo = homeRepo;
        this.resourcesUtil = resourcesUtil;
    }

    void fetchData() {

        Location location = SnapCarApplication.getInstance().locationLiveData.getValue();
        if (location == null){
            location = new Location("gps");
            location.setLatitude(0);
            location.setLongitude(0);
        }

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
                location.getLatitude(),
                location.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filterApiResponse -> {

                    processResult(filterApiResponse);

                    List<CarApiResponse> list = new ArrayList<>();
                    list.addAll(filterApiResponse.getMaleList());
                    list.addAll(filterApiResponse.getFemaleList());
                    list.addAll(filterApiResponse.getDamageList());

                    List<HotZone> hotZoneList = new ArrayList<>();
                    hotZoneList.addAll(filterApiResponse.getHotZoneFemaleList());
                    hotZoneList.addAll(filterApiResponse.getHotZoneMaleList());
                    hotZoneList.addAll(filterApiResponse.getHotZoneMixList());

                    screen.onReceiveCars(list);
                    screen.onReceiveHotZones(hotZoneList);
                    screen.onReceiveShowRoom(filterApiResponse.getShowRoomList());

                }, this::processError));
    }

    void onFavoriteClicked(String carId, String
            userId, OperationListener<Void> operationListener) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(homeRepo.addItemToFavorite(userManager.getCurrentUser().getApiToken(), carId, userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(v ->
                            {
                                // samuel
                                screen.showSuccessMessage(v.getMessage());
                                operationListener.onSuccess(null);
                            }
                            , operationListener::onError));
        } else {
            if (resourcesUtil.isEnglish()) {
                screen.showErrorMessage("You Should be Logged in");
            } else {
                screen.showErrorMessage("يجب تسجيل الدخول");
            }
        }
    }

    public void startCarTracking(){

        if (firebaseReference == null){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }

        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (!dataSnapshot.exists())
                        return;

                    TrackModel trackModel = dataSnapshot.getValue(TrackModel.class);
                    screen.onCarLocationUpdate(trackModel);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (!dataSnapshot.exists())
                        return;

                    TrackModel trackModel = dataSnapshot.getValue(TrackModel.class);
                    screen.onCarLocationUpdate(trackModel);

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }

        firebaseReference.addChildEventListener(childEventListener);
    }

    public void stopCarTracking(){

        if (childEventListener!=null && firebaseReference != null)
            firebaseReference.removeEventListener(childEventListener);
    }

    void getDirection(int hotZoneId, LatLng location) {
        if (userManager.sessionManager().isSessionActive()) {
            int type = userManager.getCurrentUser().getType();
            if (type == UserType.USER||type == UserType.SHOW_ROOM) {
                disposable.add(homeRepo.getDirection(userManager.getCurrentUser().getApiToken(), hotZoneId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                        .doFinally(screen::hideLoadingAnimation)
                        .subscribe(() -> {
                            screen.hideLoadingAnimation();
                            screen.shouldNavigateToMap(location);
                        }, throwable -> {
                            processError(throwable);
                            screen.shouldNavigateToMap(location);
                        }));
            } else {
                screen.shouldNavigateToMap(location);
            }
        } else {
            screen.shouldNavigateToMap(location);
        }
    }

    private void processResult(FilterApiResponse filterApiResponse) {

        for (CarApiResponse model : filterApiResponse.getMaleList())
            model.setCarType(FilterType.MALE);

        for (CarApiResponse model : filterApiResponse.getFemaleList())
            model.setCarType(FilterType.FEMALE);

        for (CarApiResponse model : filterApiResponse.getDamageList())
            model.setCarType(FilterType.DAMAGED);

        for (HotZone model : filterApiResponse.getHotZoneMaleList())
            model.setZoneType(FilterType.HOT_ZONE_MALE);

        for (HotZone model : filterApiResponse.getHotZoneFemaleList())
            model.setZoneType(FilterType.HOT_ZONE_FEMALE);

        for (HotZone model : filterApiResponse.getHotZoneMixList())
            model.setZoneType(FilterType.HOT_ZONE_MIXED);
    }

    void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            }
        } else if (t instanceof IOException) {
            screen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            screen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
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

}
