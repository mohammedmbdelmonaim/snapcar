package com.intcore.snapcar.core.util.authentication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * responsible for providing the the traditional email-and-password registration method capability
 */

public class DefaultRegistrationUtil<T> {

    private static final String HEADER = "application/json";
    private final UserSessionManager<T> userSessionManager;
    private final boolean needsActivation;
    private final JsonObject jsonObject;
    private final Class<T> tClass;
    private final String endPoint;
    private final String baseUrl;

    DefaultRegistrationUtil(String baseUrl, String endPoint, JsonObject jsonObject, Class<T> tClass, UserSessionManager<T> userSessionManager,
                            boolean needsActivation) {
        this.userSessionManager = userSessionManager;
        this.needsActivation = needsActivation;
        this.jsonObject = jsonObject;
        this.endPoint = endPoint;
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     * commits a registration attempt using the credentials provided in the {@link JsonObject}
     * and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param operationListener the operation callback listener
     * @throws NullPointerException if the operation listener is null
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void register(AuthenticationOperationListener<T> operationListener) {
        Log.d("minaDebugRegRequest", baseUrl);
        Log.d("minaDebugRegRequest", endPoint);
        Log.d("minaDebugRegRequest", jsonObject.toString());
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(RegistrationAPIService.class)
                .register(HEADER, baseUrl + endPoint, jsonObject)
                .map(jsonObject -> new Gson().fromJson(jsonObject, tClass))
                .doOnSuccess(t -> {
                    if (!needsActivation) {
                        userSessionManager.saveOrUpdateCurrentUser(t);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(userModel -> operationListener.onSuccess(userModel, AuthenticationOperationListener.OperationType.REGISTRATION),
                        throwable -> throwAppropriateException(operationListener, throwable)));
    }

    /**
     * determine the error type and deliver it through the appropriate {@link AuthenticationOperationListener} callback method
     * (i.e. {@link AuthenticationOperationListener#onHttpError(HttpException)})
     */
    private void throwAppropriateException(AuthenticationOperationListener<T> operationListener, Throwable throwable) {
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            operationListener.onHttpError(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable));
        } else if (throwable instanceof IOException) {
            operationListener.onNetworkError((IOException) throwable);
        } else {
            operationListener.onUnExpectedError(throwable);
        }
    }

    private interface RegistrationAPIService {
        @POST("{fullUrl}")
        Single<JsonObject> register(
                @Header("Accept") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Body JsonObject userModel);
    }
}