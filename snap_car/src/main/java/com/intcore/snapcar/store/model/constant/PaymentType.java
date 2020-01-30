package com.intcore.snapcar.store.model.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.store.model.constant.PaymentType.BIG_SALE;
import static com.intcore.snapcar.store.model.constant.PaymentType.EXCHANGE;
import static com.intcore.snapcar.store.model.constant.PaymentType.FULL_PAYMENT;
import static com.intcore.snapcar.store.model.constant.PaymentType.INSTALLMENT;
import static com.intcore.snapcar.store.model.constant.PaymentType.SHOW_ALL;
import static com.intcore.snapcar.store.model.constant.PaymentType.TRANSFER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({FULL_PAYMENT, BIG_SALE, INSTALLMENT, TRANSFER, EXCHANGE, SHOW_ALL})
public @interface PaymentType {
    int SHOW_ALL = 6;
    int EXCHANGE = 3;
    int BIG_SALE = 4;
    int TRANSFER = 2;
    int INSTALLMENT = 5;
    int FULL_PAYMENT = 1;
}