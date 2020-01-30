package com.intcore.snapcar.store.model.explore;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.intcore.snapcar.store.model.explore.ExploreItemType.ADS;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.CAR;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.DIVIDER;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.FEATURE_LIST;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.HOT_ZONE;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.SHOW_ROOM;
import static com.intcore.snapcar.store.model.explore.ExploreItemType.TITLE;

/**
 * This annotation defines a set of explore items to make sure there is no room for mistakes
 */

@Retention(RetentionPolicy.RUNTIME)
@IntDef({TITLE, FEATURE_LIST, DIVIDER, CAR, SHOW_ROOM, HOT_ZONE, ADS})
public @interface ExploreItemType {
    int TITLE = 0;
    int FEATURE_LIST = 1;
    int DIVIDER = 2;
    int CAR = 3;
    int SHOW_ROOM = 4;
    int HOT_ZONE = 5;
    int ADS = 6;
}