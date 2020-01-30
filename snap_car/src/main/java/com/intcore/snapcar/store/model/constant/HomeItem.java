package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.HomeItem.ACCOUNT;
import static com.intcore.snapcar.store.model.constant.HomeItem.CHAT;
import static com.intcore.snapcar.store.model.constant.HomeItem.HOME;
import static com.intcore.snapcar.store.model.constant.HomeItem.MARKET;
import static com.intcore.snapcar.store.model.constant.HomeItem.NOTIFICATION;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({MARKET, CHAT, NOTIFICATION, ACCOUNT, HOME})
public @interface HomeItem {
    int HOME = 4;
    int ACCOUNT = 3;
    int NOTIFICATION = 2;
    int CHAT = 1;
    int MARKET = 0;
}