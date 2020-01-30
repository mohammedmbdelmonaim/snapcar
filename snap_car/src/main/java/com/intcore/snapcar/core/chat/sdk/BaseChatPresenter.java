package com.intcore.snapcar.core.chat.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.DiffUtil;
import android.util.Pair;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.gson.Gson;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.chat.ChatSDKManager;
import com.intcore.snapcar.core.chat.diffutil.ChatDiffCallback;
import com.intcore.snapcar.core.chat.model.ContactDTO;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.chat.model.chatitem.ChatItem;
import com.intcore.snapcar.core.chat.model.chatitem.ChatItemMapper;
import com.intcore.snapcar.core.chat.model.constants.MessageType;
import com.intcore.snapcar.core.chat.model.constants.SocketDTOType;
import com.intcore.snapcar.core.chat.model.message.MessageMapper;
import com.intcore.snapcar.core.chat.model.message.MessageViewModel;
import com.intcore.snapcar.core.chat.model.payload.PayloadDTO;
import com.intcore.snapcar.core.chat.model.socket.SocketDTO;
import com.intcore.snapcar.core.chat.model.user.UserMapper;
import com.intcore.snapcar.core.chat.model.user.UserModel;
import com.intcore.snapcar.core.chat.model.user.UserViewModel;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.util.TextUtil;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class BaseChatPresenter {

    protected final BehaviorRelay<Boolean> consultationActiveRelay;
    protected final BehaviorRelay<SocketDTO> subscribeRoomRelay;
    protected final BehaviorRelay<List<ChatItem>> chatItemRelay;
    protected final BehaviorRelay<String> previewThumbnailRelay;
    protected final BehaviorRelay<Boolean> inputValidityRelay;
    protected final BehaviorRelay<ContactDTO> contactRelay;
    protected final ThreadSchedulers threadSchedulers;
    protected final BehaviorRelay<PlaceDTO> placeRelay;
    protected final CompositeDisposable disposable;
    protected final BehaviorRelay<File> fileRelay;
    protected final ChatItemMapper chatItemMapper;
    protected final MessageMapper messageMapper;
    protected final BaseChatScreen screen;
    protected final UserMapper userMapper;
    protected final UserModel currentUser;
    protected final ChatRepo chatRepo;
    protected final int roomId;
    protected int lastMessageId;

    public BaseChatPresenter(Context context, int roomId) {
        this.chatItemRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.threadSchedulers = ChatSDKManager.getConfig().getThreadSchedulers();
        this.disposable = ChatSDKManager.getConfig().getCompositeDisposable();
        this.consultationActiveRelay = BehaviorRelay.createDefault(true);
        this.currentUser = ChatSDKManager.getConfig().getCurrentUser();
        this.inputValidityRelay = BehaviorRelay.createDefault(false);
        this.screen = ChatSDKManager.getConfig().getScreen();
        this.previewThumbnailRelay = BehaviorRelay.create();
        this.subscribeRoomRelay = BehaviorRelay.create();
        this.chatItemMapper = new ChatItemMapper();
        this.contactRelay = BehaviorRelay.create();
        this.messageMapper = new MessageMapper(context);
        this.placeRelay = BehaviorRelay.create();
        this.fileRelay = BehaviorRelay.create();
        this.chatRepo = new ChatRepo(context);
        this.userMapper = new UserMapper(context);
        this.roomId = roomId;
        this.lastMessageId = 0;
    }

    public void onCreate() {
        screen.setupEditText();
        screen.setupRecyclerView();
        initializePickedPlaceObserver();
    }

    public void onResume() {
        //YOU MUST START SESSION FIRST BEFORE FETCHING HISTORY
        startSession();
        initializeHistoryObserver();
        initializeNetworkObserver();
        chatRepo.fetchUnseenMessages();
    }

    public void onPause() {
        chatRepo.endSession("onPause");
    }

    private void initializePickedPlaceObserver() {
        disposable.add(placeRelay.hide()
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(threadSchedulers.workerThread())
                .subscribeOn(threadSchedulers.mainThread())
                .subscribe(googlePlace -> {
                    String json = new Gson().toJson(googlePlace);
                    chatRepo.sendAttachment(json, MessageType.LOCATION);
                }, Timber::e));
    }

    @SuppressLint("SwitchIntDef")
    private void startSession() {
        if (consultationActiveRelay.getValue()) {
            disposable.add(chatRepo.startSession(roomId, currentUser.getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnNext(event -> {
                        if (event instanceof Event.ConnectingEvent) {
//                            screen.showNetworkConnecting();
                            screen.disableInputs();
                        }
                    })
                    .doOnNext(event -> {
                        if (event instanceof Event.ConnectedEvent) {
//                            screen.showNetworkConnected();
                            screen.enableInputs();
                            chatRepo.subscribeRoom(subscribeRoomRelay.getValue());
                            if (lastMessageId != 0) {
                                chatRepo.subscribeRoom(new SocketDTO(SocketDTOType.SEEN, new PayloadDTO(lastMessageId, roomId)));
                            }
                        }
                    })
                    .doOnNext(event -> {
                        if (event instanceof Event.DisconnectingEvent || event instanceof Event.DisconnectedEvent || event instanceof Event.FailureEvent) {
//                            screen.showNetworkDisconnected();
                            screen.disableInputs();
                        }
                    })
                    .map(Event::toString)
                    .subscribe(Timber::d, Timber::e));
        }
        chatRepo.fetchUnseenMessages();
    }

    private void initializeHistoryObserver() {
        disposable.add(chatRepo.observeChatHistoryChanges()
                .map(messageMapper::toViewModels)
                .map(chatItemMapper::toChatItems)
                .map(newItems -> new Pair<>(chatItemRelay.getValue(), newItems))
                .map(ChatDiffCallback::new)
                .map(chatDiffCallback -> {
                    List<ChatItem> newItems = chatDiffCallback.getNewItems();
                    return new Pair<>(DiffUtil.calculateDiff(chatDiffCallback), newItems);
                })
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(diffResultListPair -> {
                    chatItemRelay.accept(diffResultListPair.second);
                    screen.updateUi(diffResultListPair.first);
                }, Timber::e));
    }

    @SuppressLint("MissingPermission")
    private void initializeNetworkObserver() {
        disposable.add(ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(connected -> {
                    if (connected) {
                        startSession();
                    } else {
                        chatRepo.endSession("no network connection");
                    }
                }, Timber::e));
    }

    public void onAfterTextChange(TextUtil.Result result) {
        inputValidityRelay.accept(result.isValid());
    }

    public void onCloseClicked() {
        chatRepo.endSession("closed clicked");
        screen.closeChatScreen();
    }

    public void onLocationPicked(PlaceDTO place) {
        placeRelay.accept(place);
    }

    public void onSendClicked(String message) {
        if (inputValidityRelay.getValue()) {
            chatRepo.sendTextMessage(message, MessageType.TEXT);
            screen.clearInputs();
            if (chatItemRelay.getValue().size() <= 1) {
                initializeHistoryObserver();
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    public void resendMessage(MessageViewModel vm) {
        if (chatRepo.canSendMessage()) {
            switch (vm.getMessageType()) {
                case MessageType.DOCUMENT:
                case MessageType.IMAGE:
                case MessageType.VIDEO:
                case MessageType.TEXT:
                    chatRepo.resendTextMessage(vm.getText(), vm.getLocalId(), vm.getMessageType());
                    break;
                case MessageType.CONTACT:
                case MessageType.LOCATION:
                    chatRepo.resendAttachment(vm.getLocalId(), vm.getAttachmentUrl(), vm.getMessageType());
                case MessageType.VOICE_NOTE:
                    //TODO resend voice note
                    break;
            }
        }
    }

    public UserViewModel getCurrentUser() {
        return userMapper.toViewModel(currentUser);
    }

    public List<ChatItem> getChatItems() {
        return chatItemRelay.getValue();
    }

    public void onDestroy() {
        chatRepo.endSession("onDestroy");
        disposable.clear();
    }

    public void setSubscribeRoomData(SocketDTO subscribeRoomData) {
        subscribeRoomRelay.accept(subscribeRoomData);
    }

    public void setLastMessageId(int lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}