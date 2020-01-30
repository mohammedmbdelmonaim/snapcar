package com.intcore.snapcar.ui.feedback;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

import okhttp3.ResponseBody;

public interface FeedbackScreen extends BaseActivityScreen {
    void setAttachment(File s);

    void feedbackSent(ResponseBody responseBody);

    void processLogout();
}
