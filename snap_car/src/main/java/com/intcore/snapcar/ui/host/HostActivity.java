package com.intcore.snapcar.ui.host;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.service.FCMService;
import com.intcore.snapcar.store.UserUpdateLocationRepo;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.firebase.FireBaseModel;
import com.intcore.snapcar.ui.addinterest.AddInterestActivity;
import com.intcore.snapcar.ui.chat.ChatFragment;
import com.intcore.snapcar.ui.favorites.FavoritesFragmnet;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.notification.NotificationFragment;
import com.intcore.snapcar.ui.profile.ProfileFragment;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class HostActivity extends BaseActivity implements HostScreen, UIHostComponentProvider {

    @Inject
    HostPresenter presenter;
    @BindView(R.id.tv_market)
    TextView marketTextView;
    @BindView(R.id.tv_chat)
    TextView chatTextView;
    @BindView(R.id.tv_notification)
    TextView notificationTextView;
    @BindView(R.id.tv_account)
    TextView accountTextView;
    @BindView(R.id.tv_home)
    TextView homeTextView;
    @BindView(R.id.snack_bar_container)
    CoordinatorLayout snackBarContainer;
    @Inject
    JopDispatcher jopDispatcher;
    @Inject
    UserManager userManager;
    @Inject
    UserUpdateLocationRepo updateLocationRepo;
    @Inject
    UserManager sessionManager;
    private boolean canExitImmediately = false;
    private ActivityComponent component;
    private Stack<String> backStack = new Stack<>();

    private FusedLocationProviderClient fusedClient;
    private LocationRequest requestSetting;
    private LocationCallback locationCallback;

    private DatabaseReference firebaseReference;

    private Location oldLocation;
    private long lastServerUpdate = 0;
    private DefaultUserModel currentUser;
    private boolean isActiveSession = false;

    @Override
    protected void onCreateActivityComponents() {
        component = SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this));
        component.inject(this);
        ButterKnife.bind(this);
        setFragment(new HomeFragment(), HomeFragment.class.getSimpleName());
        if (getIntent().getBooleanExtra(FCMService.HOME_TAG, false)) {
            setFragment(new NotificationFragment(), NotificationFragment.class.getSimpleName());
        } else if (getIntent().getBooleanExtra(FCMService.NOTIFICATION_TAG, false)) {
            setFragment(new ChatFragment(), ChatFragment.class.getSimpleName());
        }

        isActiveSession = userManager.sessionManager().isSessionActive();
        currentUser = userManager.getCurrentUser();

        initFusedSetting();
        checkLocationPermission();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_host;
    }

    @Override
    public void onBackPressed() {
        confirmBeforeFinish();
//        if (!backStack.empty() && !backStack.get(backStack.size() - 1).contentEquals(HomeFragment.class.getSimpleName())) {
//            backStack.pop();
//            super.onBackPressed();
//        } else {
//            confirmBeforeFinish();
//        }
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showInterestPopup() {
        getUiUtil().getDialogBuilder(this, R.layout.layout_interest_dialog)
                .text(R.id.tv_message, getString(R.string.do_you_want_add_interest))
                .textGravity(R.id.tv_message, Gravity.CENTER)
                .clickListener(R.id.no, (dialog, view) -> dialog.dismiss())
                .clickListener(R.id.yes, (dialog, view) -> {
                    startActivity(new Intent(this, AddInterestActivity.class));
                    dialog.dismiss();
                })
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

    private void confirmBeforeFinish() {
        if (canExitImmediately) {
            finish();
        } else {
            canExitImmediately = true;
            showAnnouncementMessage(getString(R.string.press_again_to_exit));
        }
    }

    @Override
    public void setFragment(Fragment fragment, String tag) {
        if (!backStack.empty() && backStack.get(backStack.size() - 1).contentEquals(tag)) {
            return;
        }
        backStack.add(tag);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void setHighlightedItem(@HomeItem int item) {
        switch (item) {
            case HomeItem.ACCOUNT:
                notificationTextView.setSelected(false);
                accountTextView.setSelected(true);
                marketTextView.setSelected(false);
                chatTextView.setSelected(false);
                break;
            case HomeItem.CHAT:
                notificationTextView.setSelected(false);
                accountTextView.setSelected(false);
                marketTextView.setSelected(false);
                chatTextView.setSelected(true);
                break;
            case HomeItem.MARKET:
                notificationTextView.setSelected(false);
                accountTextView.setSelected(false);
                marketTextView.setSelected(true);
                chatTextView.setSelected(false);
                break;
            case HomeItem.NOTIFICATION:
                notificationTextView.setSelected(true);
                accountTextView.setSelected(false);
                marketTextView.setSelected(false);
                chatTextView.setSelected(false);
                break;
            case HomeItem.HOME:
                notificationTextView.setSelected(false);
                accountTextView.setSelected(false);
                marketTextView.setSelected(false);
                chatTextView.setSelected(false);
                break;
        }
    }

    @Override
    public void setupTrackedCar() {
        if (presenter.isUserHaveTrackedCar()) {
            FirebaseJobDispatcher dispatcher = jopDispatcher.getInstance();
            Job job = jopDispatcher.createJob(dispatcher);
            dispatcher.mustSchedule(job);
            int carId = userManager.getCurrentUser().getCarModel().getId();
            long userId = userManager.getCurrentUser().getId();
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            final boolean[] isExsit = {false};
            Query query = db.orderByChild("carId").equalTo(carId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                        FireBaseModel fireBaseModel = snapShot.getValue(FireBaseModel.class);
                        if (fireBaseModel.getCarId() == carId) {
                            if (presenter.getLastKnownLocation() != null) {
                                db.child(snapShot.getKey())
                                        .setValue(new FireBaseModel(carId,
                                                (int) userId,
                                                String.valueOf(presenter.getLastKnownLocation().getLongitude()),
                                                String.valueOf(presenter.getLastKnownLocation().getLatitude()),
                                                String.valueOf(presenter.getLastKnownLocation().getLatitude()),
                                                String.valueOf(presenter.getLastKnownLocation().getLongitude())));
                                isExsit[0] = true;
                                break;
                            }
                        } else {
                            isExsit[0] = false;
                        }
                    }
                    if (!isExsit[0]) {
                        String trackingId = db.push().getKey();
                        if (presenter.getLastKnownLocation() != null)
                            db.child(trackingId)
                                    .setValue(new FireBaseModel(carId,
                                            (int) userId,
                                            String.valueOf(presenter.getLastKnownLocation().getLongitude()),
                                            String.valueOf(presenter.getLastKnownLocation().getLatitude()),
                                            String.valueOf(presenter.getLastKnownLocation().getLatitude()),
                                            String.valueOf(presenter.getLastKnownLocation().getLongitude())));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public ActivityComponent getComponent() {
        if (component == null) {
            component = SnapCarApplication.getComponent(this)
                    .plus(new ActivityModule(this));
            component.inject(this);
        }
        return component;
    }

    @OnClick({R.id.tv_market, R.id.tv_chat, R.id.tv_notification, R.id.tv_account, R.id.civ_home})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_market:
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(marketTextView);
                if (userManager.sessionManager().isSessionActive()) {
                    setFragment(new FavoritesFragmnet(), FavoritesFragmnet.class.getSimpleName());
                } else {
                    showLoginFirstPopup();
                }
                break;
            case R.id.tv_chat:
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(chatTextView);
                if (userManager.sessionManager().isSessionActive()) {
                    setFragment(new ChatFragment(), ChatFragment.class.getSimpleName());
                } else {
                    showLoginFirstPopup();
                }
                break;
            case R.id.tv_notification:
                if (userManager.sessionManager().isSessionActive()) {
                    setFragment(new NotificationFragment(), NotificationFragment.class.getSimpleName());
                } else {
                    showLoginFirstPopup();
                }
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(notificationTextView);
                break;
            case R.id.tv_account:
                setFragment(new ProfileFragment(), ProfileFragment.class.getSimpleName());
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(accountTextView);
                break;
            case R.id.civ_home:
                setFragment(new HomeFragment(), HomeFragment.class.getSimpleName());
                YoYo.with(Techniques.BounceIn)
                        .duration(500)
                        .playOn(homeTextView);
                break;
        }
    }

    @Override
    public void showGPSIsRequiredMessage() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.gps_required)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    @SuppressLint("MissingPermission")
    public void startReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(LocationRequest.create(), locationCallback, Looper.myLooper());
    }

    @Override
    public void stopReceivingLocationUpdates(LocationCallback locationCallback) {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onDestroy() {
        FirebaseJobDispatcher dispatcher = jopDispatcher.getInstance();
        dispatcher.cancelAll();
        super.onDestroy();
    }

    private void showLoginFirstPopup() {
        getUiUtil().getBottomSheetBuilder(this, R.layout.layout_login_dialog)
                .clickListener(R.id.tv_login, (dialog, view) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                })
                .clickListener(R.id.tv_cancel, (dialog, view) -> dialog.dismiss())
                .background(R.drawable.inset_bottomsheet_background)
                .transparentBackground(true)
                .cancelable(true)
                .build()
                .show();
    }

    public CoordinatorLayout getSnackBarContainer() {
        return snackBarContainer;
    }

    private void initFusedSetting() {

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        requestSetting = LocationRequest.create();
        requestSetting.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        requestSetting.setInterval(3000);
        requestSetting.setFastestInterval(3000);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location myLocation = locationResult.getLastLocation();
                if (SnapCarApplication.getInstance().locationLiveData.getValue() != null && myLocation.getAccuracy() > 64) //bad accuracy
                    return;

                SnapCarApplication.getInstance().locationLiveData.postValue(myLocation);

                sendLocation(myLocation);
            }
        };
    }

    private void checkLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }

        } else {
            getLocation();
        }
    }

    private void getLocation() {
        fusedClient.requestLocationUpdates(requestSetting, locationCallback, null);
    }

    private void sendLocation(Location location){

        if (!isActiveSession || currentUser == null
                || currentUser.getCarModel() == null)
            return;

        long now = Calendar.getInstance().getTimeInMillis();
        if (now - lastServerUpdate > (2*60*1000) ){
            updateLocationRepo.updateCarLocation(currentUser.getApiToken(),
                    String.valueOf(currentUser.getCarModel().getId()),
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude()));

            lastServerUpdate = now;
        }

        int carId = currentUser.getCarModel().getId();

        if (firebaseReference == null){

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Query query = db.orderByChild("carId").equalTo(carId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            firebaseReference = child.getRef();
                            break;
                        }

                        updateLocationInFirebase(location);
                    }
                    else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            updateLocationInFirebase(location);
        }

    }

    private void updateLocationInFirebase(Location location){

        Log.e("sam" , "location update");
        int userId = (int) currentUser.getId();
        int carId = currentUser.getCarModel().getId();

        if (oldLocation == null)
            oldLocation = location;

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("carId",carId);
        hashMap.put("userId",userId);
        hashMap.put("latitude",String.valueOf(location.getLatitude()));
        hashMap.put("longitude",String.valueOf(location.getLongitude()));
        hashMap.put("oldLatitude",String.valueOf(oldLocation.getLatitude()));
        hashMap.put("oldLongitude",String.valueOf(oldLocation.getLongitude()));
        hashMap.put("os","android");

        firebaseReference.updateChildren(hashMap);

        oldLocation = location;

    }
}