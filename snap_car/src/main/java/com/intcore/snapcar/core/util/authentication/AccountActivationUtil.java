package com.intcore.snapcar.core.util.authentication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * responsible for activating specific account
 */

public class AccountActivationUtil<T> {

    private static final String HEADER = "application/json";
    private final UserSessionManager<T> userSessionManager;
    private final JsonObject jsonObject;
    private final Class<T> tClass;
    private final String endPoint;
    private final String baseUrl;

    AccountActivationUtil(String baseUrl, String endPoint, JsonObject jsonObject, Class<T> tClass, UserSessionManager<T> userSessionManager) {
        this.userSessionManager = userSessionManager;
        this.jsonObject = jsonObject;
        this.endPoint = endPoint;
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     * commits an account activation attempt using the credentials provided in the {@link JsonObject}
     * and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param operationListener the operation callback listener
     * @throws NullPointerException if the operation listener is null
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void activate(AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(AccountActivationAPIService.class)
                .activate(HEADER, baseUrl + endPoint, jsonObject)
                .map(jsonObject -> new Gson().fromJson(jsonObject, tClass))
                .doOnSuccess(userSessionManager::saveOrUpdateCurrentUser)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(userModel -> operationListener.onSuccess(userModel, AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION),
                        throwable -> throwAppropriateException(operationListener, throwable)));
    }

    /**
     * attempts to resend account activation code and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param operationListener the operation callback listener
     * @throws NullPointerException if the operation listener is null
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void resend(AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(AccountActivationAPIService.class)
                .resend(HEADER, baseUrl + endPoint, jsonObject)
                .map(jsonObject -> new Gson().fromJson(jsonObject, tClass))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(code -> operationListener.onSuccess(code, AuthenticationOperationListener.OperationType.ACCOUNT_ACTIVATION),
                        throwable -> throwAppropriateException(operationListener, throwable)));
    }

    /**
     * determine the error type and deliver it through the appropriate {@link AuthenticationOperationListener} callback method
     * (i.e. {@link AuthenticationOperationListener#onHttpError(com.intcore.snapcar.core.util.authentication.event.HttpException)})
     */
    private void throwAppropriateException(AuthenticationOperationListener<?> operationListener, Throwable throwable) {
        if (throwable instanceof HttpException) {
            operationListener
                    .onHttpError(com.intcore.snapcar.core.util.authentication.event.HttpException.wrapJakewhartonException((HttpException) throwable));
        } else if (throwable instanceof IOException) {
            operationListener.onNetworkError((IOException) throwable);
        } else {
            operationListener.onUnExpectedError(throwable);
        }
    }

    private interface AccountActivationAPIService {
        @PATCH("{fullUrl}")
        Single<JsonObject> activate(
                @Header("Accept") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Body JsonObject userModel);

        @POST("{fullUrl}")
        Single<JsonObject> resend(
                @Header("Accept") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Body JsonObject userModel);
    }
}
