package com.intcore.snapcar.ui.editshowroom;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

public interface EditShowRoomScreen extends BaseActivityScreen {
    void showNameErrorMessage(String nameErrorMsg);

    void showEmailErrorMessage(String emailErrorMsg);

    void setNewImagePath(String s);

    void setSelectedImage(File file);

    void updateUi(DefaultUserModel defaultUserModel);

    void setupEditText();

    void onUpdatedSuccessfully(DefaultUserModel defaultUserModel);

    void processLogout();
}
