package com.intcore.snapcar.core.chat.model.user;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intcore.snapcar.core.chat.model.message.MessageEntity;

import java.lang.reflect.Type;

public class UserTypeConverter {

    @TypeConverter
    public static UserEntity StringToUserEntity(String data) {
        if (data == null) {
            return null;
        }
        Type listType = new TypeToken<UserEntity>() {
        }.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String userEntityToString(UserEntity defaultUserEntity) {
        return new Gson().toJson(defaultUserEntity);
    }

    @TypeConverter
    public static MessageEntity StringToMessageEntity(String data) {
        if (data == null) {
            return null;
        }
        Type listType = new TypeToken<MessageEntity>() {
        }.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String messageEntityToString(MessageEntity defaultUserEntity) {
        return new Gson().toJson(defaultUserEntity);
    }
}