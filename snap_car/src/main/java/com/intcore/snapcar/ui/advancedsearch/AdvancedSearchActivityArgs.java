package com.intcore.snapcar.ui.advancedsearch;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;

public class AdvancedSearchActivityArgs implements ActivityArgs {

    private final static String MIN_PRICE_KEY = "minPriceIntent";
    private final static String MAX_PRICE_KEY = "maxPriceIntent";
    private String minPrice;
    private String maxPrice;

    public AdvancedSearchActivityArgs(String minPrice, String maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public static AdvancedSearchActivityArgs deserializeFrom(Intent intent) {
        return new AdvancedSearchActivityArgs(intent.getStringExtra(MIN_PRICE_KEY),
                intent.getStringExtra(MAX_PRICE_KEY));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, AdvancedSearchActivity.class);
        intent.putExtra(MIN_PRICE_KEY, minPrice);
        intent.putExtra(MAX_PRICE_KEY, maxPrice);
        return intent;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }
}
