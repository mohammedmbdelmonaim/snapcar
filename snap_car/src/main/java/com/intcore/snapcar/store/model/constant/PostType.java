package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.PostType.FREE;
import static com.intcore.snapcar.store.model.constant.PostType.SHOW_ALL;
import static com.intcore.snapcar.store.model.constant.PostType.SPECIAL;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({SPECIAL, FREE, SHOW_ALL})
public @interface PostType {
    int SHOW_ALL = 3;
    int FREE = 1;
    int SPECIAL = 2;
}