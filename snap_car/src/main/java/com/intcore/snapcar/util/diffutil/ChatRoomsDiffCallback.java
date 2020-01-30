package com.intcore.snapcar.util.diffutil;

import androidx.recyclerview.widget.DiffUtil;
import android.util.Pair;

import com.intcore.snapcar.core.chat.ChatViewModel;

import java.util.Collections;
import java.util.List;

public class ChatRoomsDiffCallback extends DiffUtil.Callback {

    private final List<ChatViewModel> oldItems;
    private final List<ChatViewModel> newItems;

    public ChatRoomsDiffCallback(Pair<List<ChatViewModel>, List<ChatViewModel>> oldNewListsPair) {
        this.oldItems = oldNewListsPair.first;
        this.newItems = oldNewListsPair.second;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ChatViewModel oldItem = oldItems.get(oldItemPosition);
        ChatViewModel newItem = newItems.get(newItemPosition);
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatViewModel oldItem = oldItems.get(oldItemPosition);
        ChatViewModel newItem = newItems.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    public List<ChatViewModel> getNewItems() {
        return Collections.unmodifiableList(newItems);
    }

    public List<ChatViewModel> getOldItems() {
        return Collections.unmodifiableList(oldItems);
    }
}