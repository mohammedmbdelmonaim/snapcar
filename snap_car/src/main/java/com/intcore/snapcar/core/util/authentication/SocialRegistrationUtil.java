package com.intcore.snapcar.core.util.authentication;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.model.SocialType;
import com.intcore.snapcar.core.socialloginhelper.SocialLoginListener;
import com.intcore.snapcar.core.socialloginhelper.facebook.FacebookLoginUtil;
import com.intcore.snapcar.core.socialloginhelper.google.GoogleLoginUtil;
import com.intcore.snapcar.core.socialloginhelper.twitter.TwitterLoginUtil;
import com.intcore.snapcar.core.util.Preconditions;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 responsible for providing the the different providers registration methods capability (i.e. register by Facebook)
 */

public class SocialRegistrationUtil<T> {

    private static final String HEADER = "application/json";
    private final UserSessionManager<T> userSessionManager;
    private final Class<T> tClass;
    private final String endPoint;
    private final String baseUrl;
    private final boolean needsActivation;
    private TwitterLoginUtil twitterLoginUtil;
    private GoogleLoginUtil googleLoginUtil;
    private FacebookLoginUtil facebookLoginUtil;

    SocialRegistrationUtil(String baseUrl, String endPoint, Class<T> tClass, UserSessionManager<T> userSessionManager, boolean needsActivation) {
        this.userSessionManager = userSessionManager;
        this.needsActivation = needsActivation;
        this.endPoint = endPoint;
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    private interface RegistrationAPIService {
        @POST("{fullUrl}")
        @FormUrlEncoded
        Single<JsonObject> register(
                @Header("content-type") String header,
                @Path(value = "fullUrl", encoded = true) String fullUrl,
                @Field("social_id") String socialId,
                @Field("social_type") @SocialType int socialType);
    }

    /**
     commits a registration attempt using Twitter login api and deliver the operation callback result through the passed {@link AuthenticationOperationListener}

     @param activity          the activity responsible for holding the login operation
     @param consumerKey       the consumer key provided by twitter on the developers dashboard
     @param secretKey         the secret key provided by twitter on the developers dashboard
     @param operationListener the operation callback listener

     @throws IllegalArgumentException if the consumer key or the secret key was null or empty
     @throws NullPointerException     if the passed operation listener or they activity context was null
     @apiNote you MUST register your app first at https://apps.twitter.com/
     */
    public void registerByTwitter(Activity activity, String consumerKey, String secretKey, AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        Preconditions.checkNonNull(activity, "you must pass a non null activity context");
        String nonEmptyConsumerKey = Preconditions.requireStringNotEmpty(consumerKey, "consumer key must be non empty or null string");
        String nonEmptySecretKey = Preconditions.requireStringNotEmpty(secretKey, "consumer key must be non empty or null string");
        twitterLoginUtil = new TwitterLoginUtil.Builder(activity, nonEmptyConsumerKey, nonEmptySecretKey)
                .registerCallback(new SocialLoginListener() {
                    @Override
                    public void onLoggedIn(String id, String firstName, String lastName, @Nullable String email) {
                        commitRegistration(id, SocialType.TWITTER, operationListener);
                    }

                    @Override
                    public void onLoggedOut() {
                        //No extra logic needed
                    }

                    @Override
                    public void onError(Throwable t) {
                        operationListener.onUnExpectedError(t);
                    }
                })
                .build();
        twitterLoginUtil.login(activity);
    }

    /**
     commits a registration attempt using Google sign-in api and deliver the operation callback result through the passed {@link AuthenticationOperationListener}

     @param activity          the activity responsible for holding the login operation
     @param operationListener the operation callback listener

     @throws NullPointerException if the passed operation listener or they activity context was null
     @apiNote you MUST register your app first at https://console.developers.google.com/apis/credentials
     */
    public void registerByGoogle(Activity activity, AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        googleLoginUtil = new GoogleLoginUtil.Builder()
                .requestPublicProfile()
                .registerCallback(new SocialLoginListener() {
                    @Override
                    public void onLoggedIn(String id, String firstName, String lastName, @Nullable String email) {
                        //No extra logic needed
                    }

                    @Override
                    public void onLoggedOut() {
                        //No extra logic needed
                    }

                    @Override
                    public void onError(Throwable t) {
                        //No extra logic needed
                    }
                })
                .build();
        googleLoginUtil.login(activity);
    }

    /**
     commits a registration attempt using Facebook login api and deliver the operation callback result through the passed {@link AuthenticationOperationListener}

     @param activity          the activity responsible for holding the login operation
     @param operationListener the operation callback listener

     @throws NullPointerException if the passed operation listener or they activity context was null
     @apiNote you MUST register your app first at https://developers.facebook.com/apps/
     */
    public void registerByFacebook(Activity activity, AuthenticationOperationListener<T> operationListener) {
        Preconditions.checkNonNull(operationListener, "operation cannot be null");
        facebookLoginUtil = new FacebookLoginUtil.Builder(activity)
                .requestPublicProfile()
                .registerCallback(new SocialLoginListener() {
                    @Override
                    public void onLoggedIn(String id, String firstName, String lastName, @Nullable String email) {
                        commitRegistration(id, SocialType.FACEBOOK, operationListener);
                    }

                    @Override
                    public void onLoggedOut() {
                        //No extra logic needed
                    }

                    @Override
                    public void onError(Throwable t) {
                        operationListener.onUnExpectedError(t);
                    }
                })
                .build();
        facebookLoginUtil.login(activity);
    }

    /**
     Must be called to complete the registration attempt
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (googleLoginUtil != null) {
            googleLoginUtil.onActivityResult(requestCode, resultCode, data);
        }
        if (facebookLoginUtil != null) {
            facebookLoginUtil.onActivityResult(requestCode, resultCode, data);
        }
        if (twitterLoginUtil != null) {
            twitterLoginUtil.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     commits a login attempt using the credentials provided by the social login provider
     and deliver the operation callback result through the passed {@link AuthenticationOperationListener}

     @param operationListener the operation callback listener
     */
    @SchedulerSupport(SchedulerSupport.NEW_THREAD)
    void commitRegistration(String socialId, @SocialType int socialType, AuthenticationOperationListener<T> operationListener) {
        operationListener.onPreOperation();
        AuthenticationFactory.addToDisposables(new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build()
                .create(RegistrationAPIService.class)
                .register(HEADER, baseUrl + endPoint, socialId, socialType)
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
     determine the error type and deliver it through the appropriate {@link AuthenticationOperationListener} callback method
     (i.e. {@link AuthenticationOperationListener#onHttpError(com.intcore.snapcar.core.util.authentication.event.HttpException)})
     */
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
}
