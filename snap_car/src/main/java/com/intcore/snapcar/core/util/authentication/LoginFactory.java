package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.intcore.snapcar.core.util.Preconditions;

/**
 responsible for creating the appropriate login util such as {@link DefaultLoginUtil} {@link SocialLoginUtil}
 */

public class LoginFactory<T> {

    private final UserSessionManager<T> userSessionManager;
    private final String baseUrl;
    private final Class<T> tClass;

    LoginFactory(@NonNull Context context, @NonNull String baseUrl, Class<T> tClass) {
        this.userSessionManager = new UserSessionManager<>(context, tClass);
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     creates default login utility to use the traditional email&password login method

     @param jsonModel the request body in form of a {@link com.google.gson.JsonObject}
     @param endPoint  the login endPoint

     @throws NullPointerException     if the jsonModel was null
     @throws IllegalArgumentException if the endPoint was null or empty
     @apiNote the api uses {@link com.google.gson.JsonObject} not {@link org.json.JSONObject}
     */
    public DefaultLoginUtil<T> createDefaultLoginUtil(@NonNull JsonObject jsonModel, @NonNull String endPoint) {
        Preconditions.checkNonNull(jsonModel, "JsonObject cannot be null");
        String nonEmptyEndPoint = Preconditions.requireStringNotEmpty(endPoint, "end point cannot be empty or null");
        return new DefaultLoginUtil<>(baseUrl, nonEmptyEndPoint, jsonModel, tClass, userSessionManager);
    }

    /**
     creates social login utility to use the different providers login methods suc as Facebook, Google, Twitter, etc...

     @param endPoint the login endPoint

     @throws IllegalArgumentException if the endPoint was null or empty
     */
    public SocialLoginUtil<T> createSocialLoginUtil(@NonNull String endPoint) {
        String notEmptyUrl = Preconditions.requireStringNotEmpty(endPoint, "url cannot be empty or null");
        return new SocialLoginUtil<>(baseUrl, notEmptyUrl, tClass, userSessionManager);
    }
}
