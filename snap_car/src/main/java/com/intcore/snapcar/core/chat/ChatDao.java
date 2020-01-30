package com.intcore.snapcar.core.chat;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM CHATTABLE ORDER BY updatedAt DESC ")
    Flowable<List<ChatEntity>> observeChatChanges();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<ChatEntity> messageEntities);

    @Query("DELETE FROM CHATTABLE")
    void deleteAllChatRooms();

    @Query("DELETE FROM CHATTABLE WHERE id=:chatId")
    void deleteChat(int chatId);

}