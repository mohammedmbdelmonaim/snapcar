package com.intcore.snapcar.core.util.authentication.event;

import retrofit2.Response;

/** Exception for an unexpected, non-2xx HTTP response. */
public final class HttpException extends Exception {

    private final int code;
    private final String message;
    private final transient Response<?> response;

    private static String getMessage(Response<?> response) {
        if (response == null) {
            throw new NullPointerException("response == null");
        }
        return "HTTP " + response.code() + " " + response.message();
    }

    private HttpException(Response<?> response) {
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    public static HttpException wrapJakewhartonException(com.jakewharton.retrofit2.adapter.rxjava2.HttpException e) {
        return new HttpException(e.response());
    }

    /** HTTP status code. */
    public int code() {
        return code;
    }

    /** HTTP status message. */
    public String message() {
        return message;
    }

    /**
     The full HTTP response. This may be null if the exception was serialized.
     */
    public Response<?> response() {
        return response;
    }
}
