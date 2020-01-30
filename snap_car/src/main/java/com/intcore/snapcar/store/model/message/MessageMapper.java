package com.intcore.snapcar.store.model.message;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class MessageMapper {

    private final DefaultUserMapper defaultUserMapper;

    @Inject
    MessageMapper(DefaultUserMapper defaultUserMapper) {
        this.defaultUserMapper = defaultUserMapper;
    }

    public MessageModel toMessageModel(MessageApiResponse response) {
        if (response == null) return null;
        return new MessageModel(response.getType(),
                response.getSeen(),
                response.getRoomId(),
                response.getTime(),
                response.getSenderId(),
                response.getMessageId(),
                response.getMessage(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.getAttachment(),
                response.getLocalIdentifier(),
                defaultUserMapper.toModel(response.getSenderApiResponse()));
    }

    public MessageViewModel toMessageViewModel(MessageModel model) {
        if (model == null) return null;
        return new MessageViewModel(model.getType(),
                model.getSeen(),
                model.getRoomId(),
                model.getTime(),
                model.getSenderId(),
                model.getMessageId(),
                model.getMessage(),
                model.getAttachment(),
                model.getSenderModel());
    }
}