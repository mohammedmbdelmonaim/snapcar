package com.intcore.snapcar.store.model.explore;

import com.intcore.snapcar.store.model.ads.AdsViewModel;

public class AdsExploreItem extends ExploreItem {

    private final AdsViewModel adsViewModel;

    AdsExploreItem(AdsViewModel adsViewModel) {
        this.adsViewModel = adsViewModel;
    }

    public AdsViewModel getAdsViewModel() {
        return adsViewModel;
    }

    @Override
    public int getType() {
        return ExploreItemType.ADS;
    }
}
