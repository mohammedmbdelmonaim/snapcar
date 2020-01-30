package com.intcore.snapcar.core.util.authentication;

import android.content.Context;

import androidx.annotation.NonNull;

import com.intcore.snapcar.core.util.Preconditions;

import java.lang.reflect.Modifier;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 Acts as a gate for all the authentication factories such as {@link LoginFactory} {@link RegistrationFactory} {@link AccountActivationFactory}
 and is responsible for releasing all the resources held by them by calling {@link AuthenticationFactory#tearDown()}
 */
public final class AuthenticationFactory {

    private static final CompositeDisposable DISPOSABLE = new CompositeDisposable();

    private AuthenticationFactory() {

    }

    /**
     creates and returns {@link LoginFactory} which is responsible for creating {@link DefaultLoginUtil} and {@link SocialLoginUtil} to commit a login attempt

     @param context  needed for creating {@link UserSessionManager} instance
     @param baseUrl  the api base url
     @param classOfT the class of user model which is later used for parsing the api response

     @throws AssertionError           if the model class is abstract
     @throws IllegalArgumentException if the url was not a valid http url
     */
    public static <T> LoginFactory<T> getLoginFactory(@NonNull Context context, @NonNull String baseUrl, @NonNull Class<T> classOfT) {
        Preconditions.checkNonNull(context, "Context can not be a null reference");
        Preconditions.checkNonNull(classOfT, "You must specify the response model class");
        if (Modifier.isAbstract(classOfT.getModifiers())) {
            throw new AssertionError(classOfT.getSimpleName() + "shouldn't be abstract");
        }
        String nonEmptyUrl = Preconditions.requireStringNotEmpty(baseUrl, "the base url can not be empty or null");
//        if (!URLUtil.isHttpsUrl(nonEmptyUrl)) {
//            throw new IllegalArgumentException(nonEmptyUrl + " is not a valid http url");
//        }
        return new LoginFactory<>(context, nonEmptyUrl, classOfT);
    }

    /**
     creates and returns {@link RegistrationFactory} which is responsible for creating {@link DefaultRegistrationUtil} and {@link SocialRegistrationUtil}
     to commit a registration attempt

     @param context  needed for creating {@link UserSessionManager} instance
     @param baseUrl  the api base url
     @param classOfT the class of user model which is later used for parsing the api response

     @throws AssertionError           if the model class is abstract
     @throws IllegalArgumentException if the url was not a valid http url
     */
    public static <T> RegistrationFactory<T> getRegistrationFactory(@NonNull Context context, @NonNull String baseUrl, @NonNull Class<T> classOfT) {
        Preconditions.checkNonNull(context, "Context can not be a null reference");
        Preconditions.checkNonNull(classOfT, "You must specify the response model class");
        if (Modifier.isAbstract(classOfT.getModifiers())) {
            throw new AssertionError(classOfT.getSimpleName() + "shouldn't be abstract");
        }
        String nonEmptyUrl = Preconditions.requireStringNotEmpty(baseUrl, "the base url can not be empty or null");
//        if (!URLUtil.isHttpsUrl(nonEmptyUrl)) {
//            throw new IllegalArgumentException(nonEmptyUrl + " is not a valid http url");
//        }
        return new RegistrationFactory<>(context, nonEmptyUrl, classOfT);
    }

    /**
     creates and returns {@link AccountActivationFactory} which is responsible for creating {@link AccountActivationUtil} to commit account activation attempt

     @param context  needed for creating {@link UserSessionManager} instance
     @param baseUrl  the api base url
     @param classOfT the class of user model which is later used for parsing the api response

     @throws AssertionError           if the model class is abstract
     @throws IllegalArgumentException if the url was not a valid http url
     */
    public static <T> AccountActivationFactory<T> getAccountActivationFactory(@NonNull Context context, @NonNull String baseUrl, @NonNull Class<T> classOfT) {
        Preconditions.checkNonNull(context, "Context can not be a null reference");
        Preconditions.checkNonNull(classOfT, "You must specify the response model class");
        if (Modifier.isAbstract(classOfT.getModifiers())) {
            throw new AssertionError(classOfT.getSimpleName() + "shouldn't be abstract");
        }
        String nonEmptyUrl = Preconditions.requireStringNotEmpty(baseUrl, "the base url can not be empty or null");
//        if (!URLUtil.isHttpsUrl(nonEmptyUrl)) {
//            throw new IllegalArgumentException(nonEmptyUrl + " is not a valid http url");
//        }
        return new AccountActivationFactory<>(context, nonEmptyUrl, classOfT);
    }

    /**
     creates and returns {@link PasswordResetFactory} which is responsible for creating {@link PasswordResetUtil} to commit password reset attempt

     @param context  needed for creating {@link UserSessionManager} instance
     @param baseUrl  the api base url
     @param classOfT the class of user model which is later used for parsing the api response

     @throws AssertionError           if the model class is abstract
     @throws IllegalArgumentException if the url was not a valid http url
     */
    public static <T> PasswordResetFactory<T> getPasswordResetFactory(@NonNull Context context, @NonNull String baseUrl, @NonNull Class<T> classOfT) {
        Preconditions.checkNonNull(context, "Context can not be a null reference");
        Preconditions.checkNonNull(classOfT, "You must specify the response model class");
        if (Modifier.isAbstract(classOfT.getModifiers())) {
            throw new AssertionError(classOfT.getSimpleName() + "shouldn't be abstract");
        }
        String nonEmptyUrl = Preconditions.requireStringNotEmpty(baseUrl, "the base url can not be empty or null");
//        if (!URLUtil.isHttpsUrl(nonEmptyUrl)) {
//            throw new IllegalArgumentException(nonEmptyUrl + " is not a valid http url");
//        }
        return new PasswordResetFactory<>(context, nonEmptyUrl, classOfT);
    }

    /**
     MUST be explicitly called to release all the memory resources held by the factories to avoid memory leaks
     */
    public static void tearDown() {
        DISPOSABLE.clear();
    }

    static void addToDisposables(Disposable disposable) {
        DISPOSABLE.add(disposable);
    }
}
