package com.intcore.snapcar.ui.verificationletter;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

import okhttp3.ResponseBody;

public interface VerificationLetterScreen  extends BaseActivityScreen{
    void updateUi(DefaultUserModel defaultUserModel);

    void selectedFile(File file);

    void sendSuccessfully(ResponseBody responseBody);

    void processLogout();
}
