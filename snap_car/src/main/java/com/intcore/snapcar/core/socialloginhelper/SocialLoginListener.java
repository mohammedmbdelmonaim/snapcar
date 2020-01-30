package com.intcore.snapcar.core.socialloginhelper;

import androidx.annotation.Nullable;

public interface SocialLoginListener {

    void onLoggedIn(String id, String firstName, String lastName, @Nullable String email);

    void onLoggedOut();

    void onError(Throwable t);
}
