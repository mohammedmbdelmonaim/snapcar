package com.intcore.snapcar.ui.profile;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;


public interface ProfileScreen extends BaseActivityScreen {

    void updateUi(DefaultUserModel defaultUserModelSingle);

    void showLocation(ShowRoomInfoModel showRoomInfoModel);

    void requestVipSent(ResponseBody responseBody);

    void shouldNavigateToLogin();

    void processLogout();

    void setGuestScreen();

    void setDefaultUi(DefaultUserModel currentUser);
}