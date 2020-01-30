package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.SocketDTOType.COUNTER;
import static com.intcore.snapcar.core.chat.model.constants.SocketDTOType.ERROR;
import static com.intcore.snapcar.core.chat.model.constants.SocketDTOType.MESSAGE;
import static com.intcore.snapcar.core.chat.model.constants.SocketDTOType.SEEN;
import static com.intcore.snapcar.core.chat.model.constants.SocketDTOType.SUBSCRIBE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({ERROR, MESSAGE, COUNTER, SUBSCRIBE, SEEN})
public @interface SocketDTOType {
    int ERROR = 0;
    int MESSAGE = 1;
    int COUNTER = 2;
    int SUBSCRIBE = 3;
    int SEEN = 4;
}