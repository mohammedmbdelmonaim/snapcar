package com.intcore.snapcar.ui.explorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.CallSuper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.intcore.snapcar.store.model.filterdetail.FilterDetailViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapMarkerRender extends DefaultClusterRenderer<FilterDetailViewModel> {

    private Map<FilterDetailViewModel, Marker> clusterMarkerMap = new HashMap<>();
    private ClusterManager<FilterDetailViewModel> clusterManager;

    MapMarkerRender(Context context, GoogleMap map, ClusterManager<FilterDetailViewModel> clusterManager) {
        super(context, map, clusterManager);
        this.clusterManager = clusterManager;
    }

    @Override
    protected int getColor(int clusterSize) {
        return Color.parseColor("#FF0C4D6E");
    }

    @Override
    protected void onBeforeClusterItemRendered(FilterDetailViewModel item, MarkerOptions markerOptions) {
        Bitmap venueCircle = item.getIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(venueCircle));
    }

    @Override
    @CallSuper
    protected void onClusterRendered(Cluster<FilterDetailViewModel> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
        cleanCache();
        for (FilterDetailViewModel clusterItem : cluster.getItems()) {
            clusterMarkerMap.put(clusterItem, marker);
        }
    }

    private void cleanCache() {
        ArrayList<FilterDetailViewModel> deleteQueue = new ArrayList<>();
        Collection<Marker> clusterMarkers = clusterManager.getClusterMarkerCollection().getMarkers();
        for (FilterDetailViewModel clusterItem : clusterMarkerMap.keySet()) {
            if (!clusterMarkers.contains(clusterMarkerMap.get(clusterItem))) {
                deleteQueue.add(clusterItem);
            }
        }
        for (FilterDetailViewModel clusterItem : deleteQueue) {
            clusterMarkerMap.remove(clusterItem);
        }
        deleteQueue.clear();
    }

    @Override
    public Marker getMarker(FilterDetailViewModel clusterItem) {
        super.getMarker(clusterItem);
        if (clusterMarkerMap.containsKey(clusterItem))
            return clusterMarkerMap.get(clusterItem);
        else return null;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<FilterDetailViewModel> cluster) {
        return cluster.getSize() > 1;
    }

}