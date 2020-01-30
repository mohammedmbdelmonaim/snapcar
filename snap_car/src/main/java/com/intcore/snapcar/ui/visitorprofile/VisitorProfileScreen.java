package com.intcore.snapcar.ui.visitorprofile;

import android.app.Dialog;

import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoModel;
import com.intcore.snapcar.core.base.BaseActivityScreen;
import com.intcore.snapcar.core.chat.ChatModel;

import java.util.List;

public interface VisitorProfileScreen extends BaseActivityScreen {

    void shouldNavigateToChatThread(ChatModel chatModel);

    void updateUi(DefaultUserModel defaultUserModel);

    void onRateSuccessfully(Dialog dialog);

    void setupRecyclerView();

    void showBlockScreen();

    void onBlockSuccess();

    void showUserPhones(String phone, String phones);

    void showLocation(ShowRoomInfoModel showRoomInfoModel);

    void updateCars(List<CarViewModel> carModels);

    void processLogout();

    void setupSkipLogic();
}