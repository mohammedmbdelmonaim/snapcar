package com.intcore.snapcar.core.chat.model.message;

import android.content.Context;

import com.google.gson.Gson;
import com.intcore.snapcar.core.chat.model.constants.AttachmentStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.payload.PayloadDTO;
import com.intcore.snapcar.core.chat.model.socket.SocketDTO;
import com.intcore.snapcar.core.chat.model.socket.SocketModel;
import com.intcore.snapcar.core.chat.model.user.UserMapper;
import com.intcore.snapcar.core.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class MessageMapper {

    private final UserMapper userMapper;
    private final TimeUtil timeUtil;

    public MessageMapper(Context context) {
        this.timeUtil = new TimeUtil(context);
        this.userMapper = new UserMapper(context);
    }

    public SocketDTO fromJsonToSocketDTO(String rawResponse) {
        Gson gson = new Gson();
        return gson.fromJson(rawResponse, SocketDTO.class);
    }

    public SocketDTO toSocketDTO(MessageEntity entity) {
        if (entity == null) return null;
        MessageDTO messageDTO = new MessageDTO(entity.getConsultationId(),
                entity.getText(),
                entity.getAttachmentUrl(),
                entity.getMessageType(),
                entity.getLocalId(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getServerId(),
                entity.getUnixTime(),
                entity.getThumbnailUrl(),
                null);
        PayloadDTO payloadDTO = new PayloadDTO(messageDTO);
        return new SocketDTO(SocketDTOType.MESSAGE, payloadDTO);
    }

    public MessageDTO toDataTransferObject(MessageEntity entity) {
        if (entity == null) return null;
        return new MessageDTO(entity.getConsultationId(),
                entity.getText(),
                entity.getAttachmentUrl(),
                entity.getMessageType(),
                entity.getLocalId(),
                entity.getUpdatedAt(),
                entity.getCreatedAt(),
                entity.getServerId(),
                entity.getUnixTime(),
                entity.getThumbnailUrl(),
                null);
    }

    public String toJson(MessageDTO messageDTO) {
        Gson gson = new Gson();
        return gson.toJson(messageDTO);
    }

    public SocketDTO toDataTransferObject(SocketModel model) {
        if (model == null) return null;
        return new SocketDTO(model.getType(),
                new PayloadDTO(0, model.getPayloadModel().getRomId()));
    }

    public String toJson(SocketDTO socketDTO) {
        Gson gson = new Gson();
        return gson.toJson(socketDTO);
    }

    public MessageEntity toEntity(MessageDTO messageDTO) {
        if (messageDTO == null) return null;
        return new MessageEntity(AttachmentStatus.UPLOADED,
                messageDTO.getConsultationId(),
                messageDTO.getText(),
                messageDTO.getAttachmentUrl(),
                messageDTO.getMessageType(),
                messageDTO.getLocalId(),
                MessageStatus.SENT,
                messageDTO.getUpdatedAt(),
                messageDTO.getCreatedAt(),
                messageDTO.getServerId(),
                messageDTO.getUnixTime(),
                messageDTO.getThumbnail(),
                userMapper.toEntity(messageDTO.getSender()));
    }

    public List<MessageEntity> toEntities(List<MessageDTO> messageDTOList) {
        if (messageDTOList == null) return null;
        List<MessageEntity> entities = new ArrayList<>();
        for (MessageDTO response : messageDTOList) {
            entities.add(toEntity(response));
        }
        return entities;
    }

    public MessageModel toModel(MessageEntity messageEntity) {
        if (messageEntity == null) return null;
        String attachmentUrl = messageEntity.getAttachmentUrl();
        return new MessageModel(messageEntity.getMessageStatus(),
                messageEntity.getAttachmentStatus(),
                messageEntity.getConsultationId(),
                messageEntity.getText(),
                attachmentUrl,
                messageEntity.getMessageType(),
                messageEntity.getLocalId(),
                messageEntity.getUpdatedAt(),
                messageEntity.getCreatedAt(),
                messageEntity.getServerId(),
                messageEntity.getUnixTime(),
                messageEntity.getThumbnailUrl(),
                userMapper.toModel(messageEntity.getUserEntity()));
    }

    public List<MessageModel> toModels(List<MessageEntity> messageEntities) {
        if (messageEntities == null) return null;
        List<MessageModel> models = new ArrayList<>();
        for (MessageEntity entity : messageEntities) {
            models.add(toModel(entity));
        }
        return models;
    }

    public MessageViewModel toViewModel(MessageModel messageModel) {
        if (messageModel == null) return null;
        return new MessageViewModel(messageModel.getMessageStatus(),
                messageModel.getAttachmentStatus(),
                messageModel.getConsultationId(),
                messageModel.getText(),
                messageModel.getAttachmentUrl(),
                messageModel.getMessageType(),
                messageModel.getLocalId(),
                messageModel.getUpdatedAt(),
                messageModel.getCreatedAt(),
                messageModel.getServerId(),
                timeUtil.formatTime(messageModel.getUnixTime()),
                messageModel.getThumbnail(),
                userMapper.toViewModel(messageModel.getUserModel()));
    }

    public List<MessageViewModel> toViewModels(List<MessageModel> messageModels) {
        if (messageModels == null) return null;
        List<MessageViewModel> viewModels = new ArrayList<>();
        for (MessageModel model : messageModels) {
            viewModels.add(toViewModel(model));
        }
        return viewModels;
    }
}