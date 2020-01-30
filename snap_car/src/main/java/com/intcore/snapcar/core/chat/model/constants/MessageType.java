package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.MessageType.CONTACT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.DOCUMENT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.IMAGE;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.LOCATION;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.TERMINATION;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.TEXT;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.UNKNOWN;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.VIDEO;
import static com.intcore.snapcar.core.chat.model.constants.MessageType.VOICE_NOTE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({UNKNOWN, TEXT, IMAGE, VIDEO, LOCATION, DOCUMENT, VOICE_NOTE, CONTACT, TERMINATION})
public @interface MessageType {
    int UNKNOWN = -1;
    int TEXT = 0;
    int IMAGE = 1;
    int VIDEO = 2;
    int LOCATION = 3;
    int DOCUMENT = 4;
    int VOICE_NOTE = 5;
    int CONTACT = 7;
    int TERMINATION = 6;
}