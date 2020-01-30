package com.intcore.snapcar.ui.brushtool;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

public interface BrushToolScreen extends BaseActivityScreen {

    void getSelectedFile(File file);

    void setUploadedUrl(String s);

    void processLogout();

    void updatePercentage(int percentage);

    void showPercentageLoadingAnimation();

    void hidePercentageLoadingAnimation();
}
