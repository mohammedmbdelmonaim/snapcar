package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.PreferencesUtil;

public final class UserSessionManager<T> {

    private static final String KEY_USER = "UserEntityPrefKey";
    private final PreferencesUtil preferencesUtil;
    private final Class<T> tClass;

    public UserSessionManager(Context context, Class<T> tClass) {
        Preconditions.checkNonNull(tClass, "you should specify the exact user class type");
        Preconditions.checkNonNull(context, "you must not pass a non null context");
        this.preferencesUtil = new PreferencesUtil(context);
        this.tClass = tClass;
    }

    public void saveOrUpdateCurrentUser(@NonNull T t) {
        Preconditions.checkNonNull(t, "you should not save a null value");
        Gson gson = new Gson();
        String userJson = gson.toJson(t);
        preferencesUtil.saveOrUpdateString(KEY_USER, userJson);
    }

    public boolean isSessionActive() {
        Gson gson = new Gson();
        String userJson = preferencesUtil.getString(KEY_USER);
        return gson.fromJson(userJson, tClass) != null;
    }

    @Nullable
    public T getCurrentUser() {
        Gson gson = new Gson();
        String userJson = preferencesUtil.getString(KEY_USER);
        return gson.fromJson(userJson, tClass);
    }

    public void logout() {
        preferencesUtil.delete(KEY_USER);
    }
}
