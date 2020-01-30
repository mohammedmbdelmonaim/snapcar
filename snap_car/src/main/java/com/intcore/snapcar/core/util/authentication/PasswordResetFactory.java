package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.intcore.snapcar.core.util.Preconditions;


/**
 responsible for resetting account password
 */

public class PasswordResetFactory<T> {

    private final UserSessionManager<T> userSessionManager;
    private final String baseUrl;
    private final Class<T> tClass;

    PasswordResetFactory(@NonNull Context context, @NonNull String baseUrl, Class<T> tClass) {
        this.userSessionManager = new UserSessionManager<>(context, tClass);
        this.baseUrl = baseUrl;
        this.tClass = tClass;
    }

    /**
     creates account activation utility to activate specific account and complete registration process

     @param jsonModel the request body in form of a {@link com.google.gson.JsonObject}

     @throws NullPointerException if the jsonModel was null
     @apiNote the api uses {@link com.google.gson.JsonObject} not {@link org.json.JSONObject}
     */
    public PasswordResetUtil<T> createAccountActivationUtil(@NonNull JsonObject jsonModel) {
        Preconditions.checkNonNull(jsonModel, "JsonObject cannot be null");
        return new PasswordResetUtil<>(baseUrl, jsonModel, tClass, userSessionManager);
    }
}
