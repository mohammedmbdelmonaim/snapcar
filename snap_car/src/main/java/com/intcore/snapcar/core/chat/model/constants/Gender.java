package com.intcore.snapcar.core.chat.model.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.intcore.snapcar.core.chat.model.constants.Gender.FEMALE;
import static com.intcore.snapcar.core.chat.model.constants.Gender.MALE;
import static com.intcore.snapcar.core.chat.model.constants.Gender.NOT_SPECIFIED;
import static com.intcore.snapcar.core.chat.model.constants.Gender.SHOW_ALL;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({FEMALE, MALE, NOT_SPECIFIED, SHOW_ALL})
public @interface Gender {
    int SHOW_ALL = 3;
    int FEMALE = 2;
    int MALE = 1;
    int NOT_SPECIFIED = 4;
}