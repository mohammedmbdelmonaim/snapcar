package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.intcore.snapcar.core.util.Preconditions;

/**
 responsible for creating the appropriate registration util such as {@link DefaultRegistrationUtil} {@link SocialRegistrationUtil}
 */

public class RegistrationFactory<T> {

    private final UserSessionManager<T> userSessionManager;
    private boolean needsActivation = false;
    private final Class<T> tClass;
    private final String baseUrl;

    RegistrationFactory(@NonNull Context context, @NonNull String baseUrl, Class<T> tClass) {
        this.userSessionManager = new UserSessionManager<>(context, tClass);
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     wither or not the registration process requires account activation step to determine wither to cache user after registration or after activation

     @param needsActivation true if it needs activation, false other wise
     */
    public RegistrationFactory<T> needsAccountActivation(boolean needsActivation) {
        this.needsActivation = needsActivation;
        return this;
    }

    /**
     creates default registration utility to use the traditional email&password registration method

     @param jsonModel the request body in form of a {@link com.google.gson.JsonObject}
     @param endPoint  the login endPoint

     @throws NullPointerException     if the jsonModel was null
     @throws IllegalArgumentException if the endPoint was null or empty
     @apiNote the api uses {@link com.google.gson.JsonObject} not {@link org.json.JSONObject}
     */
    public DefaultRegistrationUtil<T> createDefaultRegistrationUtil(@NonNull JsonObject jsonModel, @NonNull String endPoint) {
        Preconditions.checkNonNull(jsonModel, "JsonObject cannot be null");
        String nonEmptyEndPoint = Preconditions.requireStringNotEmpty(endPoint, "end point cannot be empty or null");
        return new DefaultRegistrationUtil<>(baseUrl, nonEmptyEndPoint, jsonModel, tClass, userSessionManager, needsActivation);
    }

    /**
     creates social login utility to use the different providers login methods suc as Facebook, Google, Twitter, etc...

     @param endPoint the login endPoint

     @throws IllegalArgumentException if the endPoint was null or empty
     */
    public SocialRegistrationUtil<T> createSocialRegistrationUtil(@NonNull String endPoint) {
        String notEmptyUrl = Preconditions.requireStringNotEmpty(endPoint, "url cannot be empty or null");
        return new SocialRegistrationUtil<>(baseUrl, notEmptyUrl, tClass, userSessionManager, needsActivation);
    }
}
