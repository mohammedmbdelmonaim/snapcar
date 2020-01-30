package com.intcore.snapcar.ui.notificationsetting;

import com.intcore.snapcar.store.model.NotificationSettingDTO;
import com.intcore.snapcar.core.base.BaseActivityScreen;

public interface NotificationSettingScreen extends BaseActivityScreen {

    void setDefaultData(NotificationSettingDTO notificationSettingDTO);

    void onSavedSuccessfully();

    void processLogout();
}