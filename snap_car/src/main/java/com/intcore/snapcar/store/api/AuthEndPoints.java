package com.intcore.snapcar.store.api;

import javax.inject.Inject;

public class AuthEndPoints {

    //public static final String BASE_URL = "http://18.224.193.88/backend/public/api/v1/"; //dev

    public static final String BASE_URL = "https://dashboard.snapcar.sa/backend/public/api/v1/"; //Release

    public static final String DEFAULT_LOGIN_END_POINT = "user/auth/signin";
    public static final String DEFAULT_REGISTRATION_END_POINT = "user/auth/signup";
    public static final String RESET_PASSWORD_END_POINT = "user/auth/password/reset";
    public static final String ACTIVATE_ACCOUNT_END_POINT = "user/auth/active-account";
    public static final String RESEND_ACTIVATION_CODE_END_POINT = "user/auth/valid-phone";
    public static final String SEND_PASSWORD_RESET_CODE_END_POINT = "user/auth/password/email";

    @Inject
    AuthEndPoints() {
    }

}