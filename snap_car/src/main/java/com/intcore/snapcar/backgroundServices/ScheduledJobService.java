package com.intcore.snapcar.backgroundServices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.intcore.snapcar.backgroundServices.di.JopDispatcherModule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.store.UserUpdateLocationRepo;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.HashMap;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScheduledJobService extends JobService implements LocationListener {

    @Inject
    UserUpdateLocationRepo updateLocationRepo;
    @Inject
    UserManager sessionManager;

    private FusedLocationProviderClient fusedClient;
    private LocationRequest requestSetting;
    private LocationCallback locationCallback;

    private DatabaseReference firebaseReference;

    private Location oldLocation;

    private static final String TAG = ScheduledJobService.class.getSimpleName();

    @Override
    public void onCreate() {
        SnapCarApplication.getComponent(this)
                .plus(new JopDispatcherModule(this))
                .inject(this);
    }

    @Override
    public boolean onStartJob(JobParameters job) {

        initFusedSetting();
        checkPermission();

        return true;
    }

    private void initFusedSetting() {

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        requestSetting = LocationRequest.create();
        requestSetting.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        requestSetting.setInterval(5000);
        requestSetting.setFastestInterval(5000);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                Location myLocation = locationResult.getLastLocation();
                if (myLocation.getAccuracy() > 70) //bad accuracy
                    return;

                //sendLocation(myLocation);
            }
        };
    }

    private void checkPermission() {

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


    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d("manarDebug", "stopped");
        fusedClient.removeLocationUpdates(locationCallback);
        return true;
    }

    private void sendLocation(Location location){

        if (!sessionManager.sessionManager().isSessionActive()
            || sessionManager.getCurrentUser().getCarModel() == null)
            return;

        updateLocationRepo.updateCarLocation(sessionManager.getCurrentUser().getApiToken(),
                String.valueOf(sessionManager.getCurrentUser().getCarModel().getId()),
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()));

        int carId = sessionManager.getCurrentUser().getCarModel().getId();

        if (firebaseReference == null){

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Query query = db.orderByChild("carId").equalTo(carId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        firebaseReference = dataSnapshot.getRef();
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

        int userId = (int) sessionManager.getCurrentUser().getId();
        int carId = sessionManager.getCurrentUser().getCarModel().getId();

        if (oldLocation == null)
            oldLocation = location;

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("carId",carId);
        hashMap.put("userId",userId);
        hashMap.put("latitude",String.valueOf(location.getLatitude()));
        hashMap.put("longitude",String.valueOf(location.getLongitude()));
        hashMap.put("oldLatitude",String.valueOf(oldLocation.getLatitude()));
        hashMap.put("oldLongitude",String.valueOf(oldLocation.getLongitude()));

        firebaseReference.updateChildren(hashMap);

        oldLocation = location;

        SnapCarApplication.getInstance().locationLiveData.postValue(location);
    }


    @Override
    public void onLocationChanged(Location location) {
        if (sessionManager.sessionManager().isSessionActive()) {
            if (sessionManager.getCurrentUser().getCarModel() != null) {

                updateLocationRepo.updateCarLocation(sessionManager.getCurrentUser().getApiToken(),
                        String.valueOf(sessionManager.getCurrentUser().getCarModel().getId()),
                        String.valueOf(location.getLongitude()),
                        String.valueOf(location.getLatitude()));

                if (sessionManager.getCurrentUser().getCarModel() != null
                    && sessionManager.getCurrentUser().getCarModel().getId() != null) {
                    int userId = (int) sessionManager.getCurrentUser().getId();
                    int carId = sessionManager.getCurrentUser().getCarModel().getId();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();


                    Query query = db.orderByChild("carId").equalTo(carId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                }
                            }
                            else {

                            }

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

//                                        HashMap<String,Object> hashMap = new HashMap<>();
//                                        hashMap.put("carId",carId);
//                                        hashMap.put("userId",userId);
//                                        hashMap.put("latitude",location.getLatitude());
//                                        hashMap.put("longitude",location.getLongitude());
//                                        hashMap.put("oldLatitude",childSnapshot.getValue(FireBaseModel.class).getLatitude());
//                                        hashMap.put("oldLongitude",childSnapshot.getValue(FireBaseModel.class).getLongitude());
//                                        db.updateChildren(hashMap);
//                                         db.child(childSnapshot.getKey())
//                                                .setValue(new FireBaseModel(carId,
//                                                        userId,
//                                                        String.valueOf(location.getLongitude()),
//                                                        String.valueOf(location.getLatitude()),
//                                                        childSnapshot.getValue(FireBaseModel.class).getLatitude(),
//                                                        childSnapshot.getValue(FireBaseModel.class).getLongitude()));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Timber.tag("fireBaseError").d(databaseError.getMessage());
                        }
                    });


                    if (db != null) {
                        if (sessionManager.getCurrentUser().getCarModel().getId() != null) {

                        }
                    }
                }
            }
        }
    }
}
