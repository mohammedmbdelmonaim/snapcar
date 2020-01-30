package com.intcore.snapcar.core.base;

import androidx.lifecycle.Lifecycle;
import android.content.Intent;

import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

public interface BaseActivityScreen {

    void showDefaultMessage(String message);

    void showSuccessMessage(String message);

    void showAnnouncementMessage(String message);

    void showWarningMessage(String message);

    void showErrorMessage(String message);

    void showLoadingAnimation();

    void hideLoadingAnimation();

    Intent getIntent();

    Lifecycle getLifecycle();

    ResourcesUtil getResourcesUtil();

    PermissionUtil getPermissionUtil();
}
