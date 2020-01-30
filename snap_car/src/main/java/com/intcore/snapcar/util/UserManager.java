package com.intcore.snapcar.util;

import android.content.Context;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.qualifier.ForApplication;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.authentication.UserSessionManager;

import javax.inject.Inject;

@ApplicationScope
public class UserManager {

    public static final String ACTIVATED = "1";
    public static final String PENDING = "0";
    private final UserSessionManager<DefaultUserDataApiResponse> userUserSessionManager;
    private final DefaultUserMapper userMapper;

    @Inject
    UserManager(@ForApplication Context context, DefaultUserMapper userMapper) {
        this.userUserSessionManager = new UserSessionManager<>(context, DefaultUserDataApiResponse.class);
        this.userMapper = userMapper;
    }

    public static Class<DefaultUserDataApiResponse> getClassOfUserApiResponse() {
        return DefaultUserDataApiResponse.class;
    }

    public UserSessionManager<DefaultUserDataApiResponse> sessionManager() {
        return userUserSessionManager;
    }

    public DefaultUserModel getCurrentUser() {
        try{
            if (userUserSessionManager.getCurrentUser() == null)
                return null;
            return userMapper.toModel(userUserSessionManager.getCurrentUser().getDefaultUserApiResponse());
        }
        catch (Exception e){
            return null;
        }
    }


    public void updateProfile(DefaultUserDataApiResponse userApiResponse) {
        userUserSessionManager.saveOrUpdateCurrentUser(userApiResponse);
    }
}