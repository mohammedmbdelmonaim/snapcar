package com.intcore.snapcar.store.model.explore;

/**
 * This class represents an abstract form of a timeline item. It can be any thing, title, list, etc...
 */

public abstract class ExploreItem {

    /**
     * @return the type of that item that is represented by {@link ExploreItemType}
     */

    @ExploreItemType
    abstract public int getType();
}
