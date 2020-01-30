package com.intcore.snapcar.core.chat.diffutil;

import androidx.recyclerview.widget.DiffUtil;
import android.util.Pair;

import com.intcore.snapcar.core.chat.model.chatitem.ChatItem;
import com.intcore.snapcar.core.chat.model.chatitem.LocationChatItem;
import com.intcore.snapcar.core.chat.model.chatitem.TextChatItem;
import com.intcore.snapcar.core.chat.model.message.MessageViewModel;

import java.util.Collections;
import java.util.List;

public class ChatDiffCallback extends DiffUtil.Callback {

    private final List<ChatItem> oldItems;
    private final List<ChatItem> newItems;

    public ChatDiffCallback(Pair<List<ChatItem>, List<ChatItem>> oldNewListsPair) {
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
        ChatItem oldItem = oldItems.get(oldItemPosition);
        ChatItem newItem = newItems.get(newItemPosition);
        return oldItem instanceof TextChatItem && newItem instanceof TextChatItem
                || oldItem instanceof LocationChatItem && newItem instanceof LocationChatItem;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatItem oldItem = oldItems.get(oldItemPosition);
        ChatItem newItem = newItems.get(newItemPosition);
        if (oldItem instanceof TextChatItem && newItem instanceof TextChatItem) {
            return areMessagesContentTheSame(((TextChatItem) oldItem).getMessageViewModel(), ((TextChatItem) newItem).getMessageViewModel());
        } else if (oldItem instanceof LocationChatItem && newItem instanceof LocationChatItem) {
            return areMessagesContentTheSame(((LocationChatItem) oldItem).getMessageViewModel(), ((LocationChatItem) newItem).getMessageViewModel());
        }
        return false;
    }

    private boolean areMessagesContentTheSame(MessageViewModel oldItem, MessageViewModel newItem) {
        return oldItem.equals(newItem);
    }

    public List<ChatItem> getNewItems() {
        return Collections.unmodifiableList(newItems);
    }

    public List<ChatItem> getOldItems() {
        return Collections.unmodifiableList(oldItems);
    }
}