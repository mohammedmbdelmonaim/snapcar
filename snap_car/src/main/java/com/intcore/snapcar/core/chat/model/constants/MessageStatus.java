package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.MessageStatus.FAILURE;
import static com.intcore.snapcar.core.chat.model.constants.MessageStatus.SENDING;
import static com.intcore.snapcar.core.chat.model.constants.MessageStatus.SENT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({FAILURE, SENDING, SENT})
public @interface MessageStatus {
    int FAILURE = 0;
    int SENDING = 1;
    int SENT = 2;
}