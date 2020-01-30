package com.intcore.snapcar.ui.explorer;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

public class MarkerAnimator {

    private Context context;
    private Map<Integer,MarkerAnimatorItem> map;

    public MarkerAnimator(Context context){
        this.context = context;
    }




    public class MarkerAnimatorItem{
        private int id;
        private Marker marker;
        private LatLng toPosition;
        private boolean rotate = true;

        public MarkerAnimatorItem(int id, Marker marker, LatLng toPosition,boolean rotate) {
            this.id = id;
            this.marker = marker;
            this.toPosition = toPosition;
            this.rotate = rotate;
        }

        public int getId() {
            return id;
        }

        public Marker getMarker() {
            return marker;
        }

        public LatLng getToPosition() {
            return toPosition;
        }
    }

}
