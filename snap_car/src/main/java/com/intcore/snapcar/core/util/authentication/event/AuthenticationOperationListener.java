package com.intcore.snapcar.core.util.authentication.event;

import androidx.annotation.IntDef;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION;
import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType.LOGIN;
import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType.PASSWORD_RESET;
import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType.PHONE_VALIDATION;
import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType.REGISTRATION;

public interface AuthenticationOperationListener<T> {

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({LOGIN, REGISTRATION, ACCOUNT_ACTIVATION, PASSWORD_RESET ,PHONE_VALIDATION})
    @interface OperationType {
        int LOGIN = 0;
        int REGISTRATION = 1;
        int ACCOUNT_ACTIVATION = 2;
        int PASSWORD_RESET = 3;
        int PHONE_VALIDATION =4;
    }

    void onPreOperation();

    void onPostOperation();

    void onSuccess(T value, @OperationType int type);

    void onHttpError(HttpException e);

    void onNetworkError(IOException e);

    void onUnExpectedError(Throwable e);
}
