package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.ChatItemType.CONSULTATION_DETAILS;
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
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({TEXT_SENDER, TEXT_RECEIVER,
        IMAGE_SENDER, IMAGE_RECEIVER,
        VIDEO_SENDER, VIDEO_RECEIVER,
        LOCATION_SENDER, LOCATION_RECEIVER,
        DOCUMENT_SENDER, DOCUMENT_RECEIVER,
        VOICE_NOTE_SENDER, VOICE_NOTE_RECEIVER,
        CONTACT_SENDER, CONTACT_RECEIVER,
        CONSULTATION_DETAILS})
public @interface ChatItemType {
    int TEXT_SENDER = 0;
    int TEXT_RECEIVER = 1;
    int IMAGE_SENDER = 2;
    int IMAGE_RECEIVER = 3;
    int VIDEO_SENDER = 4;
    int VIDEO_RECEIVER = 5;
    int LOCATION_SENDER = 6;
    int LOCATION_RECEIVER = 7;
    int DOCUMENT_SENDER = 8;
    int DOCUMENT_RECEIVER = 9;
    int VOICE_NOTE_SENDER = 10;
    int VOICE_NOTE_RECEIVER = 11;
    int CONSULTATION_DETAILS = 12;
    int CONTACT_SENDER = 13;
    int CONTACT_RECEIVER = 14;
}