package com.intcore.snapcar.core.chat.sdk;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.intcore.snapcar.core.chat.model.message.MessageDao;
import com.intcore.snapcar.core.chat.model.message.MessageEntity;
import com.intcore.snapcar.core.chat.model.user.UserEntity;
import com.intcore.snapcar.core.chat.model.user.UserTypeConverter;
import com.intcore.snapcar.core.chat.ChatDao;
import com.intcore.snapcar.core.chat.ChatEntity;

@Database(entities = {MessageEntity.class, UserEntity.class, ChatEntity.class}, version = 1, exportSchema = false)
@TypeConverters({UserTypeConverter.class})
public abstract class RoomStore extends RoomDatabase {

    public static final String DATABASE_NAME = "app-db";

    abstract MessageDao getMessageDao();

    public abstract ChatDao getChatDao();
}