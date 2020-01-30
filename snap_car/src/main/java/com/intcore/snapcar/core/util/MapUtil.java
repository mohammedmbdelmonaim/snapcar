package com.intcore.snapcar.core.util;

import android.content.Intent;
import android.net.Uri;

public final class MapUtil {

    private MapUtil() {
    }

    public static Intent getIntent(double latitude, double longitude, String address) {
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + address + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        return new Intent(android.content.Intent.ACTION_VIEW, uri);
    }
}
