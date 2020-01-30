package com.intcore.snapcar.core.util;

import android.text.TextUtils;

public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Verifies if the object is not null and returns it or throws a NullPointerException
     * with the given messageEn.
     *
     * @param object the object to verify
     * @throws NullPointerException if object is null
     */
    public static void checkNonNull(Object object) {
        if (object == null) {
            throw new NullPointerException("can not be null");
        }
    }

    /**
     * Verifies if the object is not null and returns it or throws a NullPointerException
     * with the given messageEn.
     *
     * @param object  the object to verify
     * @param message the messageEn to use with the NullPointerException
     * @throws NullPointerException if object is null
     */
    public static void checkNonNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    public static void checkStringNotEmpty(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            throw new NullPointerException("string must be non empty or null");
        }
    }

    public static void checkStringNotEmpty(CharSequence charSequence, String message) {
        if (TextUtils.isEmpty(charSequence)) {
            throw new NullPointerException(message);
        }
    }

    public static void checkMethodAccessValidity(String message, Object... args) {
        if (args == null || args.length == 0) {
            throw new IllegalAccessError(message);
        }
        for (Object arg : args) {
            if (arg == null) {
                throw new IllegalAccessError(message);
            }
        }
    }

    /**
     * Verifies if the object is not null and returns it or throws a NullPointerException
     * with the given messageEn.
     *
     * @param <T>     the value type
     * @param object  the object to verify
     * @param message the messageEn to use with the NullPointerException
     * @return the object itself
     * @throws NullPointerException if object is null
     */
    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    /**
     * Verifies if the object is not null and returns it or throws a NullPointerException
     *
     * @param <T>    the value type
     * @param object the object to verify
     * @return the object itself
     * @throws NullPointerException if object is null
     */
    public static <T> T requireNonNull(T object) {
        if (object == null) {
            throw new NullPointerException("Can not be null");
        }
        return object;
    }

    /**
     * Verifies if the given string is not null or empty and returns it or throws a IllegalArgumentException
     *
     * @param <T>    the value type
     * @param string the string to verify
     * @return the string itself
     * @throws IllegalArgumentException if object is null or empty
     */
    public static <T extends CharSequence> T requireStringNotEmpty(T string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException("can not be empty or null");
        } else {
            return string;
        }
    }

    /**
     * Verifies if the given string is not null or empty and returns it or throws a IllegalArgumentException
     * with the given messageEn.
     *
     * @param <T>    the value type
     * @param string the string to verify
     * @return the string itself
     * @throws IllegalArgumentException if object is null or empty
     */
    public static <T extends CharSequence> T requireStringNotEmpty(T string, String message) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        } else {
            return string;
        }
    }
}
