package com.intcore.snapcar.core.chat.model.message;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.intcore.snapcar.core.chat.model.constants.MessageStatus;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessageEntity messageEntity);

    @Query("SELECT * FROM CHATMESSAGE WHERE consultationId =:consultationId")
    Flowable<List<MessageEntity>> observeChatChanges(int consultationId);

    @Query("SELECT * FROM CHATMESSAGE WHERE messageStatus=:status and consultationId =:roomId ORDER BY serverId DESC LIMIT 1")
    Maybe<MessageEntity> getLastMessage(@MessageStatus int status, int roomId);

    @Query("SELECT * FROM CHATMESSAGE WHERE consultationId =:roomId ORDER BY serverId DESC LIMIT 1")
    MessageEntity getLastMessage(int roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<MessageEntity> messageEntities);

    @Query("DELETE FROM CHATMESSAGE WHERE consultationId =:roomId ")
    void deleteAllChatMessages(int roomId);

}
