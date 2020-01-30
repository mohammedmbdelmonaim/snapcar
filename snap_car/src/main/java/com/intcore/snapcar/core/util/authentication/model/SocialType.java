package com.intcore.snapcar.core.util.authentication.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.intcore.snapcar.core.util.authentication.model.SocialType.FACEBOOK;
import static com.intcore.snapcar.core.util.authentication.model.SocialType.GOOGLE;
import static com.intcore.snapcar.core.util.authentication.model.SocialType.TWITTER;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({FACEBOOK, TWITTER, GOOGLE})
public @interface SocialType {
    int FACEBOOK = 0;
    int TWITTER = 1;
    int GOOGLE = 3;
}
