package com.intcore.snapcar.store;

import androidx.room.Room;
import android.content.Context;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.chat.sdk.RoomStore;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.chat.ChatDao;
import com.intcore.snapcar.core.chat.ChatMapper;
import com.intcore.snapcar.core.chat.ChatModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

@ActivityScope
public class ChatRepo {

    private final BehaviorRelay<String> tokenRelay;
    private final WebServiceStore webServiceStore;
    private final ChatMapper chatMapper;
    private final ChatDao chatDao;

    @Inject
    ChatRepo(WebServiceStore webServiceStore, ChatMapper chatMapper, @ForActivity Context context) {
        this.tokenRelay = BehaviorRelay.create();
        this.webServiceStore = webServiceStore;
        this.chatMapper = chatMapper;
        this.chatDao = Room.databaseBuilder(context, RoomStore.class, RoomStore.DATABASE_NAME).build().getChatDao();
    }

    public Observable<Event> startSession(String apiToken) {
        tokenRelay.accept(apiToken);
        return webServiceStore.startWebSocket(tokenRelay.getValue())
                .doOnError(ignored -> webServiceStore.killWebSocket());
    }

    public void endSession(String reason) {
        webServiceStore.endWebSocket(reason);
    }

    public void endSessionImmediately() {
        webServiceStore.killWebSocket();
    }

    public Completable fetchInboxList(String apiToken) {
        return webServiceStore.fetchInboxList(apiToken)
                .map(chatMapper::toChatEntities)
                .doOnSuccess(chatEntities -> chatDao.deleteAllChatRooms())
                .doOnSuccess(chatDao::insertMessages)
                .toCompletable();
    }

    public Single<List<ChatModel>> fetchInboxListSearch(String apiToken) {
        return webServiceStore.fetchInboxList(apiToken)
                .map(chatMapper::toChatEntities)
                .map(chatMapper::toChatModels);
    }

    public Completable deleteChat(String apiToken,int chatId){
        return webServiceStore.deleteChat(apiToken,chatId)
                .doOnComplete(() -> chatDao.deleteChat(chatId));
    }

    public Observable<List<ChatModel>> observeChatRooms() {
        return chatDao.observeChatChanges()
                .map(chatMapper::toChatModels)
                .toObservable();
    }

    public void deleteChatRooms() {
        new Thread() {
            @Override
            public void run() {
                try {
                    chatDao.deleteAllChatRooms();
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }.start();
    }
}