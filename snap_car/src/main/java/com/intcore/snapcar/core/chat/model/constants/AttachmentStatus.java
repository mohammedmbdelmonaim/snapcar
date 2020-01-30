package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.AttachmentStatus.FAILURE;
import static com.intcore.snapcar.core.chat.model.constants.AttachmentStatus.UPLOADED;
import static com.intcore.snapcar.core.chat.model.constants.AttachmentStatus.UPLOADING;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({FAILURE, UPLOADING, UPLOADED})
public @interface AttachmentStatus {
    int FAILURE = 0;
    int UPLOADING = 1;
    int UPLOADED = 2;
}