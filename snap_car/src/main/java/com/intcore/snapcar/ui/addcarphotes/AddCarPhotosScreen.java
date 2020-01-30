package com.intcore.snapcar.ui.addcarphotes;

import com.intcore.snapcar.core.base.BaseActivityScreen;

import java.io.File;

public interface AddCarPhotosScreen extends BaseActivityScreen {
    void setNewImagePath(String s);

    void setuploadedPosition(int currentImagePos,File f);

    void openCamera();

    void processLogout();

    void showPercentageLoadingAnimation();

    void hidePercentageLoadingAnimation();

    void updatePercentage(int percentage);
}
