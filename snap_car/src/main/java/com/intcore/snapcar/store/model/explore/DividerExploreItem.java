package com.intcore.snapcar.store.model.explore;

import static com.intcore.snapcar.store.model.explore.ExploreItemType.DIVIDER;

public class DividerExploreItem extends ExploreItem {

    private final String divider;

    public DividerExploreItem(String divider) {
        this.divider = divider;
    }

    public String getDivider() {
        return divider;
    }

    @Override
    public int getType() {
        return DIVIDER;
    }
}