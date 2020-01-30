package com.intcore.snapcar.core.chat;

import android.content.Context;

import com.intcore.snapcar.core.chat.model.message.MessageMapper;
import com.intcore.snapcar.core.chat.model.user.UserMapper;
import com.intcore.snapcar.core.qualifier.ForApplication;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class ChatMapper {

    private final UserMapper defaultUserMapper;
    private final MessageMapper messageMapper;

    @Inject
    ChatMapper(@ForApplication Context context) {
        this.defaultUserMapper = new UserMapper(context);
        this.messageMapper = new MessageMapper(context);
    }

    public ChatEntity toChatEntity(ChatApiResponse response) {
        if (response == null) return null;
        return new ChatEntity(response.getId(),
                response.getSeen(),
                response.getDate(),
                response.getUserIdOne(),
                response.getUserIdTwo(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.getLastMessageTime(),
                messageMapper.toEntity(response.getMessageApiResponse()),
                defaultUserMapper.toEntity(response.getUserApiResponse()));
    }

    public List<ChatEntity> toChatEntities(List<ChatApiResponse> responses) {
        if (responses == null) return null;
        List<ChatEntity> entities = new ArrayList<>();
        for (ChatApiResponse response : responses) {
            entities.add(toChatEntity(response));
        }
        return entities;
    }

    public ChatModel toChatModel(ChatEntity response) {
        if (response == null) return null;
        return new ChatModel(response.getId(),
                response.getSeen(),
                response.getDate(),
                response.getUserIdOne(),
                response.getUserIdTwo(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.getLastMessageTime(),
                defaultUserMapper.toModel(response.getUserEntity()),
                messageMapper.toModel(response.getMessageModel()));
    }

    public List<ChatModel> toChatModels(List<ChatEntity> responses) {
        if (responses == null) return null;
        List<ChatModel> models = new ArrayList<>();
        for (ChatEntity response : responses) {
            models.add(toChatModel(response));
        }
        return models;
    }

    public ChatViewModel toChatViewModel(ChatModel model) {
        if (model == null) return null;
        return new ChatViewModel(model.getId(),
                model.getSeen(),
                model.getDate(),
                model.getUserIdOne(),
                model.getUserIdTwo(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getLastMessageTime(),
                defaultUserMapper.toViewModel(model.getUseModel()),
                messageMapper.toViewModel(model.getMessageModel()));
    }

    public List<ChatViewModel> toChatViewModels(List<ChatModel> models) {
        if (models == null) return null;
        List<ChatViewModel> viewModels = new ArrayList<>();
        for (ChatModel model : models) {
            viewModels.add(toChatViewModel(model));
        }
        return viewModels;
    }
}