package com.intcore.snapcar.store.model.constant;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.CarCondition.DAMAGED;
import static com.intcore.snapcar.store.model.constant.CarCondition.NEW;
import static com.intcore.snapcar.store.model.constant.CarCondition.SHOW_ALL;
import static com.intcore.snapcar.store.model.constant.CarCondition.USED;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({NEW, USED, DAMAGED, SHOW_ALL})
public @interface CarCondition {
    int SHOW_ALL = 4;
    int DAMAGED = 3;
    int USED = 2;
    int NEW = 1;
}