package com.intcore.snapcar.di.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.intcore.snapcar.store.model.defaultuser.DefaultUserEntity;

@Database(entities = {DefaultUserEntity.class}, version = 1)
public abstract class RoomStore extends RoomDatabase {

    public static final String DATABASE_NAME = "aman-db";
}