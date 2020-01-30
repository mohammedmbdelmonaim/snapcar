package com.intcore.snapcar.ui.testmap;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.model.firebase.FireBaseModel;
import com.intcore.snapcar.core.util.ResourcesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapTestActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions marker;
    private ResourcesUtil resourcesUtil;
    @BindView(R.id.ed_user_id)
    EditText userIdEditText;
    @BindView(R.id.ed_car_id)
    EditText carIdEditText;
    int userid =80;
    int carId = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resourcesUtil = new ResourcesUtil(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = new MarkerOptions()
                .position(new LatLng(29.9836211, 31.2307252))
                .icon(BitmapDescriptorFactory
                        .fromBitmap(resourcesUtil
                                .loadBitmapFromView(getLayoutInflater().inflate(R.layout.marker, null))));
        googleMap.addMarker(marker);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        CameraPosition target = CameraPosition.builder()
                .target(new LatLng(29.9836211, 31.2307252))
                .zoom(16)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));

        mMap.setOnMapClickListener(point -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(point));
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Query query = db.orderByChild("carId").equalTo(carId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        db.child(childSnapshot.getKey())
                                .setValue(new FireBaseModel(carId, userid,
                                        String.valueOf(point.longitude),
                                        String.valueOf(point.latitude),
                                        childSnapshot.getValue(FireBaseModel.class).getLatitude(),
                                        childSnapshot.getValue(FireBaseModel.class).getLongitude()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MapTestActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        });
    }

    public void onOkClicked(View view) {
        userid = Integer.parseInt(userIdEditText.getText().toString());
        carId = Integer.parseInt(carIdEditText.getText().toString());
        carIdEditText.setText("");
        userIdEditText.setText("");

    }
}
