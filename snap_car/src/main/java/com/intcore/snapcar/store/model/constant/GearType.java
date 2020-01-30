package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.GearType.AUTO;
import static com.intcore.snapcar.store.model.constant.GearType.NORMAL;
import static com.intcore.snapcar.store.model.constant.GearType.SHOW_ALL;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({AUTO, NORMAL, SHOW_ALL})
public @interface GearType {
    int SHOW_ALL = 3;
    int NORMAL = 2;
    int AUTO = 1;
}