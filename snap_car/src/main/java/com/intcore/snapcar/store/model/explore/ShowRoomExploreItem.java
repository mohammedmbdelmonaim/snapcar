package com.intcore.snapcar.store.model.explore;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;

public class ShowRoomExploreItem extends ExploreItem {

    private final DefaultUserModel showRoomModel;

    ShowRoomExploreItem(DefaultUserModel showRoomModel) {
        this.showRoomModel = showRoomModel;
    }


    public DefaultUserModel getShowRoomModel() {
        return showRoomModel;
    }

    @Override
    public int getType() {
        return ExploreItemType.SHOW_ROOM;
    }
}