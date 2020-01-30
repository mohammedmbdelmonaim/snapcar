package com.intcore.snapcar.core.chat.sdk;

import androidx.room.Room;
import android.content.Context;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.chat.ChatSDKManager;
import com.intcore.snapcar.core.chat.model.constants.AttachmentStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageStatus;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.message.MessageDao;
import com.intcore.snapcar.core.chat.model.message.MessageEntity;
import com.intcore.snapcar.core.chat.model.message.MessageMapper;
import com.intcore.snapcar.core.chat.model.message.MessageModel;
import com.intcore.snapcar.core.chat.model.payload.PayloadDTO;
import com.intcore.snapcar.core.chat.model.socket.SocketDTO;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.util.Preconditions;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class ChatRepo {

    private static final String ILLEGAL_ACCESS_MSG = "you must start session first";
    private final ThreadSchedulers threadSchedulers;
    private final BehaviorRelay<Integer> roomIdRelay;
    private final BehaviorRelay<String> tokenRelay;
    private final WebServiceStore webServiceStore;
    private final CompositeDisposable disposable;
    private final MessageMapper messageMapper;
    private final MessageDao messageDao;

    public ChatRepo(Context context) {
        this.messageDao = Room.databaseBuilder(context, RoomStore.class, RoomStore.DATABASE_NAME).build().getMessageDao();
        this.threadSchedulers = ChatSDKManager.getConfig().getThreadSchedulers();
        this.disposable = ChatSDKManager.getConfig().getCompositeDisposable();
        this.webServiceStore = new WebServiceStore();
        this.roomIdRelay = BehaviorRelay.create();
        this.tokenRelay = BehaviorRelay.create();
        this.messageMapper = new MessageMapper(context);
    }

    public Observable<Event> startSession(int roomId, String apiToken) {
        roomIdRelay.accept(roomId);
        tokenRelay.accept(apiToken);
        return webServiceStore.startWebSocket()
                .doOnNext(this::insertInDatabaseIfMessage)
                .doOnError(ignored -> webServiceStore.killWebSocket());
    }

    public void fetchUnseenMessages() {
        Preconditions.checkMethodAccessValidity(ILLEGAL_ACCESS_MSG, tokenRelay.getValue(), roomIdRelay.getValue());
        disposable.add(messageDao.getLastMessage(MessageStatus.SENT, roomIdRelay.getValue())
                .doOnComplete(() -> {
                    String apiToken = tokenRelay.getValue();
                    int consultationId = roomIdRelay.getValue();
                    String url = ApisUtil.BASE_URL.concat("api/v1/user/app/chat/")
                            .concat(String.valueOf(consultationId))
                            .concat("/messages/")
                            .concat("0");
                    webServiceStore.fetchUnseenMessages(apiToken, url)
                            .map(messageMapper::toEntities)
                            .subscribe(messageDao::insertMessages, Timber::e);
                })
                .map(MessageEntity::getServerId)
                .flatMapObservable(id -> {
                    String apiToken = tokenRelay.getValue();
                    int consultationId = roomIdRelay.getValue();
                    String url = ApisUtil.BASE_URL.concat("api/v1/user/app/chat/")
                            .concat(String.valueOf(consultationId))
                            .concat("/messages/")
                            .concat(String.valueOf(id));
                    return webServiceStore.fetchUnseenMessages(apiToken, url);
                })
                .map(messageMapper::toEntities)
                .subscribeOn(threadSchedulers.workerThread())
                .subscribe(messageDao::insertMessages, Timber::e));
    }

    void endSession(String reason) {
        webServiceStore.endWebSocket(reason);
    }

    void endSessionImmediately() {
        webServiceStore.killWebSocket();
    }

    void sendTextMessage(String message, @MessageType int type) {
        Preconditions.checkMethodAccessValidity(ILLEGAL_ACCESS_MSG, tokenRelay.getValue(), roomIdRelay.getValue());
        MessageEntity entity = new MessageEntity.Builder(MessageEntity.generateLocalId())
                .consultationId(roomIdRelay.getValue())
                .messageType(type)
                .text(message)
                .build();
        sendMessage(entity);
    }

    void resendTextMessage(String message, String localId, @MessageType int type) {
        Preconditions.checkMethodAccessValidity(ILLEGAL_ACCESS_MSG, tokenRelay.getValue(), roomIdRelay.getValue());
        MessageEntity entity = new MessageEntity.Builder(localId)
                .consultationId(roomIdRelay.getValue())
                .messageType(type)
                .text(message)
                .build();
        sendMessage(entity);
    }

    void sendAttachment(String message, @MessageType int type) {
        Preconditions.checkMethodAccessValidity(ILLEGAL_ACCESS_MSG, tokenRelay.getValue(), roomIdRelay.getValue());
        MessageEntity entity = new MessageEntity.Builder(MessageEntity.generateLocalId())
                .consultationId(roomIdRelay.getValue())
                .messageType(type)
                .attachmentUrl(message)
                .build();
        sendMessage(entity);
    }

    void resendAttachment(String localId, String message, @MessageType int type) {
        MessageEntity entity = new MessageEntity.Builder(localId)
                .consultationId(roomIdRelay.getValue())
                .messageType(type)
                .attachmentUrl(message)
                .build();
        sendMessage(entity);
    }

    boolean canSendMessage() {
        return webServiceStore.isWebSocketConnected();
    }

    Flowable<List<MessageModel>> observeChatHistoryChanges() {
        Preconditions.checkMethodAccessValidity(ILLEGAL_ACCESS_MSG, tokenRelay.getValue(), roomIdRelay.getValue());
        return messageDao.observeChatChanges(roomIdRelay.getValue())
                .map(messageMapper::toModels);
    }

    private void insertInDatabaseIfMessage(Event event) {
        disposable.add(Single.just(event)
                .filter(evnt -> event instanceof Event.MessageEvent)
                .map(evnt -> (Event.MessageEvent) evnt)
                .map(Event.MessageEvent::getBody)
                .map(messageMapper::fromJsonToSocketDTO)
                .filter(response -> response.getType() == SocketDTOType.MESSAGE)
                .doOnSuccess(socketDTO -> {
                    String imageUrl = "";
                    if (messageDao.getLastMessage(roomIdRelay.getValue()) != null) {
                        if (messageDao.getLastMessage(roomIdRelay.getValue()).getUserEntity() != null) {
                            imageUrl = messageDao.getLastMessage(roomIdRelay.getValue()).getUserEntity().getImageUrl();
                        }
                    }
                    if (!socketDTO.getPayload().getMessageDTO().getSender().getImageUrl().contentEquals(imageUrl)
                            && !tokenRelay.getValue().contentEquals(socketDTO.getPayload()
                            .getMessageDTO().getSender().getApiToken())) {
                        refreshChatHistory();
                    }
                })
                .map(SocketDTO::getPayload)
                .map(PayloadDTO::getMessageDTO)
                .map(messageMapper::toEntity)
                .doOnSuccess(messageDao::insertMessage)
                .map(MessageEntity::toString)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(Timber::v, Timber::e));
    }

    private void refreshChatHistory() {
        String apiToken = tokenRelay.getValue();
        int consultationId = roomIdRelay.getValue();
        String url = ApisUtil.BASE_URL.concat("api/v1/user/app/chat/")
                .concat(String.valueOf(consultationId))
                .concat("/messages/")
                .concat("0");
        disposable.add(webServiceStore.fetchUnseenMessages(apiToken, url)
                .map(messageMapper::toEntities)
                .doOnNext(messageEntities -> messageDao.deleteAllChatMessages(roomIdRelay.getValue()))
                .subscribe(messageDao::insertMessages, Timber::e));
    }

    public void subscribeRoom(SocketDTO socketDTO) {
        disposable.add(Single.just(socketDTO)
                .map(messageMapper::toJson)
                .map(webServiceStore::sendOverWebSocket)
                .map(String::valueOf)
                .subscribe(Timber::v, Timber::e));
    }

    private void sendMessage(MessageEntity unPreparedEntity) {
        MessageEntity.Builder readyToSendEntity = unPreparedEntity.builder();
        disposable.add(Single.just(readyToSendEntity)
                .observeOn(threadSchedulers.workerThread())
                .doOnSuccess(messageEntity -> {
                    if (webServiceStore.isWebSocketConnected()) {
                        messageEntity.messageStatus(MessageStatus.SENDING);
                        messageDao.insertMessage(messageEntity.build());
                    } else {
                        messageEntity.messageStatus(MessageStatus.FAILURE);
                        messageDao.insertMessage(messageEntity.build());
                    }
                })
                .map(MessageEntity.Builder::build)
                .map(messageMapper::toSocketDTO)
                .map(messageMapper::toJson)
                .doOnSuccess(s -> Timber.tag("manarDebug").v(s))
                .map(webServiceStore::sendOverWebSocket)
                .doOnSuccess(enqueued -> {
                    if (!enqueued) {
                        updateIfFailure(readyToSendEntity.build());
                    }
                })
                .doOnDispose(() -> updateIfFailure(readyToSendEntity.build()))
                .map(String::valueOf)
                .subscribe(Timber::v, Timber::e));
    }

    private void updateIfFailure(MessageEntity value) {
        disposable.add(Single.just(value)
                .map(MessageEntity::builder)
                .map(builder -> {
                    if (value.getMessageStatus() != MessageStatus.SENT) {
                        builder.messageStatus(MessageStatus.FAILURE);
                    }
                    return builder;
                })
                .map(builder -> {
                    if (value.getAttachmentStatus() != AttachmentStatus.UPLOADED) {
                        builder.attachmentStatus(AttachmentStatus.FAILURE);
                    }
                    return builder;
                })
                .map(MessageEntity.Builder::build)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.workerThread())
                .subscribe(messageDao::insertMessage, Timber::e));
    }
}