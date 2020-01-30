package com.intcore.snapcar.ui.viewcar;

import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.core.base.BaseActivityScreen;
import com.intcore.snapcar.core.chat.ChatModel;

public interface ViewCarScreen extends BaseActivityScreen {

    void updateUi(CarDTO carApiResponse);

    void shouldNavigateToChatThread(ChatModel chatModel);

    void processLogout();

    void carDeleted();
}
