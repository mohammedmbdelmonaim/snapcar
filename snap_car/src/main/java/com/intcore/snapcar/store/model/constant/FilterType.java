package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.FilterType.DAMAGED;
import static com.intcore.snapcar.store.model.constant.FilterType.FEMALE;
import static com.intcore.snapcar.store.model.constant.FilterType.HOT_ZONE_FEMALE;
import static com.intcore.snapcar.store.model.constant.FilterType.HOT_ZONE_MALE;
import static com.intcore.snapcar.store.model.constant.FilterType.HOT_ZONE_MIXED;
import static com.intcore.snapcar.store.model.constant.FilterType.MALE;
import static com.intcore.snapcar.store.model.constant.FilterType.SHOW_ROOM;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({FEMALE, MALE, SHOW_ROOM, HOT_ZONE_FEMALE, HOT_ZONE_MALE, HOT_ZONE_MIXED, DAMAGED})
public @interface FilterType {
    int DAMAGED = 6;
    int HOT_ZONE_MIXED = 5;
    int HOT_ZONE_MALE = 4;
    int HOT_ZONE_FEMALE = 3;
    int FEMALE = 2;
    int MALE = 1;
    int SHOW_ROOM = 0;
}