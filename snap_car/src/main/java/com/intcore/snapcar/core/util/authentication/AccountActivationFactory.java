package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.intcore.snapcar.core.util.Preconditions;

/**
 responsible for activating newly created accounts, only gets called if {@link RegistrationFactory#needsAccountActivation(boolean)} was sat to true
 */

public class AccountActivationFactory<T> {

    private final UserSessionManager<T> userSessionManager;
    private final String baseUrl;
    private final Class<T> tClass;

    AccountActivationFactory(@NonNull Context context, @NonNull String baseUrl, Class<T> tClass) {
        this.userSessionManager = new UserSessionManager<>(context, tClass);
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     creates account activation utility to activate specific account and complete registration process

     @param jsonModel the request body in form of a {@link com.google.gson.JsonObject}
     @param endPoint  the login endPoint

     @throws NullPointerException     if the jsonModel was null
     @throws IllegalArgumentException if the endPoint was null or empty
     @apiNote the api uses {@link com.google.gson.JsonObject} not {@link org.json.JSONObject}
     */
    public AccountActivationUtil<T> createAccountActivationUtil(@NonNull JsonObject jsonModel, @NonNull String endPoint) {
        Preconditions.checkNonNull(jsonModel, "JsonObject cannot be null");
        String nonEmptyEndPoint = Preconditions.requireStringNotEmpty(endPoint, "end point cannot be empty or null");
        return new AccountActivationUtil<>(baseUrl, nonEmptyEndPoint, jsonModel, tClass, userSessionManager);
    }
}
