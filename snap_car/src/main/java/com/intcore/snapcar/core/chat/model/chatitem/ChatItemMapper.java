package com.intcore.snapcar.core.chat.model.chatitem;

import com.intcore.snapcar.core.chat.ChatSDKManager;
import com.intcore.snapcar.core.chat.model.message.MessageViewModel;
import com.intcore.snapcar.core.chat.model.user.UserModel;

import java.util.ArrayList;
import java.util.List;

import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.CONTACT_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.CONTACT_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.DOCUMENT_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.DOCUMENT_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.IMAGE_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.IMAGE_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.LOCATION_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.LOCATION_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.TEXT_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.TEXT_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.VIDEO_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.VIDEO_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.VOICE_NOTE_RECEIVER;
import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.VOICE_NOTE_SENDER;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.CONTACT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.DOCUMENT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.IMAGE;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.LOCATION;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.TEXT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.VIDEO;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.VOICE_NOTE;

public class ChatItemMapper {

    private final UserModel currentUser;

    public ChatItemMapper() {
        currentUser = ChatSDKManager.getConfig().getCurrentUser();
    }

    public List<ChatItem> toChatItems(List<MessageViewModel> viewModels) {
        List<ChatItem> list = new ArrayList<>();
        for (MessageViewModel viewModel : viewModels) {
            if (viewModel.getUserViewModel() == null || viewModel.getUserViewModel().getId() == currentUser.getId()) {
                list.add(mapToSenderItems(viewModel));
            } else {
                list.add(mapToReceiverItems(viewModel));
            }
        }
        return list;
    }

    private ChatItem mapToSenderItems(MessageViewModel viewModel) {
        switch (viewModel.getMessageType()) {
            case DOCUMENT:
                return new DocumentChatItem(DOCUMENT_SENDER, viewModel);
            case IMAGE:
                return new ImageChatItem(IMAGE_SENDER, viewModel);
            case LOCATION:
                return new LocationChatItem(LOCATION_SENDER, viewModel);
            case TEXT:
                return new TextChatItem(TEXT_SENDER, viewModel);
            case VIDEO:
                return new VideoChatItem(VIDEO_SENDER, viewModel);
            case VOICE_NOTE:
                return new VoiceNoteChatItem(VOICE_NOTE_SENDER, viewModel);
            case CONTACT:
                return new ContactChatItem(CONTACT_SENDER, viewModel);
            default:
                return null;
        }
    }

    private ChatItem mapToReceiverItems(MessageViewModel viewModel) {
        switch (viewModel.getMessageType()) {
            case DOCUMENT:
                return new DocumentChatItem(DOCUMENT_RECEIVER, viewModel);
            case IMAGE:
                return new ImageChatItem(IMAGE_RECEIVER, viewModel);
            case LOCATION:
                return new LocationChatItem(LOCATION_RECEIVER, viewModel);
            case TEXT:
                return new TextChatItem(TEXT_RECEIVER, viewModel);
            case VIDEO:
                return new VideoChatItem(VIDEO_RECEIVER, viewModel);
            case VOICE_NOTE:
                return new VoiceNoteChatItem(VOICE_NOTE_RECEIVER, viewModel);
            case CONTACT:
                return new ContactChatItem(CONTACT_RECEIVER, viewModel);
            default:
                return null;
        }
    }
}
