package com.intcore.snapcar.store.model.explore;

public class TitleExploreItem extends ExploreItem {

    private final String title;

    TitleExploreItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getType() {
        return ExploreItemType.TITLE;
    }
}