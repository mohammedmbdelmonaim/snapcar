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
 * responsible for account password resetting
 */

public class PasswordResetUtil<T> {

    private static final String HEADER = "application/json";
    private final UserSessionManager<T> userSessionManager;
    private final JsonObject jsonObject;
    private final Class<T> tClass;
    private final String baseUrl;

    PasswordResetUtil(String baseUrl, JsonObject jsonObject, Class<T> tClass, UserSessionManager<T> userSessionManager) {
        this.userSessionManager = userSessionManager;
        this.jsonObject = jsonObject;
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     * commits a send password resetting code attempt using the credentials provided in the {@link JsonObject}
     * and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param endPoint          the reset password endpoint
     * @param operationListener the operation callback listener
     * @throws NullPointerException     if the operation listener is null
     * @throws IllegalArgumentException if the endpoint was null or empty
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void sendCode(String endPoint, AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        String nonEmptyEndPoint = Preconditions.requireStringNotEmpty(endPoint, "end point cannot be empty or null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(ResetPasswordAPIService.class)
                .sendCode(HEADER, baseUrl + nonEmptyEndPoint, jsonObject)
                .map(jsonObject -> new Gson().fromJson(jsonObject, tClass))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(userModel -> operationListener.onSuccess(userModel, AuthenticationOperationListener.OperationType.LOGIN),
                        throwable -> throwAppropriateException(operationListener, throwable)));
    }

    /**
     * commits a password resetting attempt using the credentials provided in the {@link JsonObject}
     * and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param endPoint          the reset password endpoint
     * @param operationListener the operation callback listener
     * @throws NullPointerException     if the operation listener is null
     * @throws IllegalArgumentException if the endpoint was null or empty
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void resetPassword(String endPoint, AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        String nonEmptyEndPoint = Preconditions.requireStringNotEmpty(endPoint, "end point cannot be empty or null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(ResetPasswordAPIService.class)
                .resetPassword(HEADER, baseUrl + nonEmptyEndPoint, jsonObject)
                .map(jsonObject -> new Gson().fromJson(jsonObject, tClass))
                .doOnSuccess(userSessionManager::saveOrUpdateCurrentUser)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(userModel -> operationListener.onSuccess(userModel, AuthenticationOperationListener.OperationType.LOGIN),
                        throwable -> throwAppropriateException(operationListener, throwable)));
    }

    private void throwAppropriateException(AuthenticationOperationListener<T> operationListener, Throwable throwable) {
        if (throwable instanceof HttpException) {
            operationListener
                    .onHttpError(com.intcore.snapcar.core.util.authentication.event.HttpException.wrapJakewhartonException((HttpException) throwable));
        } else if (throwable instanceof IOException) {
            operationListener.onNetworkError((IOException) throwable);
        } else {
            operationListener.onUnExpectedError(throwable);
        }
    }

    private interface ResetPasswordAPIService {
        @POST("{fullUrl}")
        Single<JsonObject> sendCode(
                @Header("Accept") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Body JsonObject userModel);

        @PATCH("{fullUrl}")
        Single<JsonObject> resetPassword(
                @Header("Accept") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Body JsonObject userModel);
    }
}
