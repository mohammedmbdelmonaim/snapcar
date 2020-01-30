package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.UserType.SHOW_ROOM;
import static com.intcore.snapcar.store.model.constant.UserType.USER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({USER, SHOW_ROOM})
public @interface UserType {
    int SHOW_ROOM = 2;
    int USER = 1;
}