package com.intcore.snapcar.core.util.authentication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener.OperationType;

/**
 * responsible for providing the the traditional email-and-password log in method capability
 */

public class DefaultLoginUtil<T> {

    private static final String HEADER = "application/json";
    private final UserSessionManager<T> userSessionManager;
    private final JsonObject jsonModel;
    private final Class<T> tClass;
    private final String endPoint;
    private final String baseUrl;

    DefaultLoginUtil(String baseUrl, String endPoint, JsonObject jsonModel, Class<T> tClass, UserSessionManager<T> userSessionManager) {
        this.userSessionManager = userSessionManager;
        this.jsonModel = jsonModel;
        this.endPoint = endPoint;
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     * commits a login attempt using the credentials provided in the {@link JsonObject}
     * and deliver the operation callback result through the passed {@link AuthenticationOperationListener}
     *
     * @param operationListener the operation callback listener
     * @throws NullPointerException if the operation listener is null
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    public void login(AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operationListener cannot be null");
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(APIService.class)
                .execute(HEADER, baseUrl + endPoint, jsonModel)
                .map(jsonObject -> {
                    JsonObject user = jsonObject.get("user").getAsJsonObject();
                    String activation = user.get("activation").getAsString();
                    T t = new Gson().fromJson(jsonObject, tClass);
                    if (activation.contentEquals("1")) {
                        userSessionManager.saveOrUpdateCurrentUser(t);
                    }
                    return t;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(ignored -> operationListener.onPreOperation())
                .doFinally(operationListener::onPostOperation)
                .subscribe(model -> operationListener.onSuccess(model, OperationType.LOGIN), e -> throwAppropriateException(operationListener, e)));
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
}
