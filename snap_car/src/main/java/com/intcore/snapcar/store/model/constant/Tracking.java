package com.intcore.snapcar.store.model.constant;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.Tracking.SHOW_ALL;
import static com.intcore.snapcar.store.model.constant.Tracking.TRACKED;
import static com.intcore.snapcar.store.model.constant.Tracking.UN_TRACKED;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({TRACKED, UN_TRACKED, SHOW_ALL})
public @interface Tracking {
    int SHOW_ALL = 3;
    int UN_TRACKED = 2;
    int TRACKED = 1;
}