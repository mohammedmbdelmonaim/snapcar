package com.intcore.snapcar.ui.chat;

import androidx.recyclerview.widget.DiffUtil;
import android.util.Pair;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.ChatRepo;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.util.diffutil.ChatRoomsDiffCallback;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.chat.model.PlaceDTO;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.chat.ChatMapper;
import com.intcore.snapcar.core.chat.ChatViewModel;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.functions.Functions;
import timber.log.Timber;

@FragmentScope
class ChatPresenter {

    private final BehaviorRelay<List<ChatViewModel>> chatListRelay;
    private final BehaviorRelay<Boolean> consultationActiveRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final ChatMapper chatMapper;
    private final ChatScreen chatScreen;
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private PlaceDTO placeRelay;

    @Inject
    ChatPresenter(@IOThread ThreadSchedulers threadSchedulers,
                  @ForActivity CompositeDisposable disposable,
                  ResourcesUtil resourcesUtil,
                  UserManager userManager,
                  ChatMapper chatMapper,
                  ChatRepo chatRepo,
                  ChatScreen chatScreen,
                  UserRepo userRepo) {
        this.chatListRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.consultationActiveRelay = BehaviorRelay.createDefault(true);
        this.threadSchedulers = threadSchedulers;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.chatMapper = chatMapper;
        this.disposable = disposable;
        this.chatScreen = chatScreen;
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
    }

    public void onViewCreated() {
        chatScreen.setupRecyclerView();
        chatScreen.setupRefreshLayout();
        initializeChatRoomsObservable();
    }

    private void initializeChatRoomsObservable() {
        disposable.add(chatRepo.observeChatRooms()
                .map(chatMapper::toChatViewModels)
                .map(newItems -> new Pair<>(chatListRelay.getValue(), newItems))
                .map(ChatRoomsDiffCallback::new)
                .map(chatDiffCallback -> {
                    List<ChatViewModel> newItems = chatDiffCallback.getNewItems();
                    return new Pair<>(DiffUtil.calculateDiff(chatDiffCallback), newItems);
                })
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                .doFinally(chatScreen::hideLoadingAnimation)
                .subscribe(diffResultListPair -> {
//
//                    for(int x =0 ;x< diffResultListPair.second.size();x++){
//                        if(diffResultListPair.second.get(x).getMessageViewModel()==null)
//                            diffResultListPair.second.remove(chatViewModel);
//                    }

                    chatListRelay.accept(diffResultListPair.second);
                    chatScreen.updateUi(diffResultListPair.first);
                }, this::processError));
    }

    void onResume() {
        startSession();
        fetchData();
    }

    void fetchData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(chatRepo.fetchInboxList(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(Functions.EMPTY_ACTION, this::processError));
        } else {
            chatRepo.deleteChatRooms();
            chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            chatScreen.showErrorMessage(
                    getHttpErrorMessage(HttpException.wrapJakewhartonException((
                            com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        } else if (t instanceof IOException) {
            chatScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            chatScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    public void onPause() {
        chatRepo.endSessionImmediately();
    }

    private void startSession() {
        if (userManager.sessionManager().isSessionActive()) {
            if (consultationActiveRelay.getValue()) {
                disposable.add(chatRepo.startSession(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnNext(event -> {
                            if (event instanceof Event.ConnectingEvent) {
//                                chatScreen.showNetworkConnecting();
                            }
                        })
                        .doOnNext(event -> {
                            if (event instanceof Event.ConnectedEvent) {
//                                chatScreen.showNetworkConnected();
                            }
                        })
                        .doOnNext(event -> {
                            if (event instanceof Event.DisconnectingEvent || event instanceof Event.DisconnectedEvent || event instanceof Event.FailureEvent) {
//                                chatScreen.showNetworkDisconnected();
                            }
                        })
                        .doOnNext(event -> {
                            if (event instanceof Event.MessageEvent) {
                                // update data when new message came
                                fetchData();
                            }
                        })
                        .map(Event::toString)
                        .subscribe(Timber::d, Timber::e));
            }
        } else {
            chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    List<ChatViewModel> getChatList() {
        return chatListRelay.getValue();
    }

    void onReportClicked(int userId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.reportUser(userManager.getCurrentUser().getApiToken(), userId, "")
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(chatScreen::onReportSuccessfully, this::processError));
        } else {
            chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    void deleteChat(int chatId){
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(
                    chatRepo.deleteChat(userManager.getCurrentUser().getApiToken(),chatId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(chatScreen::onChatDeleted, this::processError)
            );
        } else {
            chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    void onBlockClicked(int userId, String reason) {
        if (reason == null) {
            chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_type_block_reason));
            return;
        }
            if (userManager.sessionManager().isSessionActive()) {
                disposable.add(userRepo.blockUser(userManager.getCurrentUser().getApiToken(), userId, reason)
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                        .doFinally(chatScreen::hideLoadingAnimation)
                        .subscribe(this::fetchData, this::processError));
            } else {
                chatScreen.showWarningMessage(resourcesUtil.getString(R.string.please_sign_in));
            }
    }

    public void onChatClicked(int chatId, int messageId, String firstName) {
        chatScreen.shouldNavigateToChat(chatId, messageId, firstName, placeRelay);
        placeRelay = null;
    }

    void setPlace(PlaceDTO place) {
        placeRelay = place;
    }

    void onDestroy() {
        disposable.clear();
    }
}