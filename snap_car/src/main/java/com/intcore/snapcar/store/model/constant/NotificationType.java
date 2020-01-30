package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.NotificationType.ADMIN_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.CAR_VALIDITY_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.DISABLE_ADS_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.DISCOUNT_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.EDIT_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.HOT_ZONE_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.MATCH_INTEREST_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.NEARBY_INTEREST_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.PAYMENT_REMINDER_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.VERIFICATION_NOTIFICATION;
import static com.intcore.snapcar.store.model.constant.NotificationType.VIP_NOTIFICATION;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({VERIFICATION_NOTIFICATION, HOT_ZONE_NOTIFICATION, VIP_NOTIFICATION, PAYMENT_REMINDER_NOTIFICATION,
        MATCH_INTEREST_NOTIFICATION, NEARBY_INTEREST_NOTIFICATION, ADMIN_NOTIFICATION, DISCOUNT_NOTIFICATION,
        EDIT_NOTIFICATION, DISABLE_ADS_NOTIFICATION, CAR_VALIDITY_NOTIFICATION})
public @interface NotificationType {
    int VERIFICATION_NOTIFICATION = 1;
    int HOT_ZONE_NOTIFICATION = 2;
    int VIP_NOTIFICATION = 3;
    int PAYMENT_REMINDER_NOTIFICATION = 4;
    int MATCH_INTEREST_NOTIFICATION = 5;
    int NEARBY_INTEREST_NOTIFICATION = 6;
    int ADMIN_NOTIFICATION = 7;
    int DISCOUNT_NOTIFICATION = 8;
    int EDIT_NOTIFICATION = 9;
    int DISABLE_ADS_NOTIFICATION = 10;
    int CAR_VALIDITY_NOTIFICATION = 11;
}