package com.intcore.snapcar.ui.chatsearch;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.ChatRepo;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatMapper;
import com.intcore.snapcar.core.chat.ChatViewModel;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class ChatSearchPresenter extends BaseActivityPresenter {

    private final BehaviorRelay<List<ChatViewModel>> chatRelay;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ChatSearchScreen chatScreen;
    private final UserManager userManager;
    private final ChatMapper chatMapper;
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;

    @Inject
    ChatSearchPresenter(@IOThread ThreadSchedulers threadSchedulers,
                        @ForActivity CompositeDisposable disposable,
                        ChatSearchScreen chatSearchScreen,
                        UserManager userManager,
                        ChatMapper chatMapper,
                        UserRepo userRepo,
                        ChatRepo chatRepo) {
        super(chatSearchScreen);
        this.chatRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.threadSchedulers = threadSchedulers;
        this.chatScreen = chatSearchScreen;
        this.userManager = userManager;
        this.chatMapper = chatMapper;
        this.disposable = disposable;
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
    }

    @Override
    protected void onCreate() {
        chatScreen.setupEditText();
        chatScreen.setupRecyclerView();
        chatScreen.setupRefreshLayout();
        fetchData();
    }

    void setAfterEditTextChanged(InitialValueObservable<TextViewAfterTextChangeEvent> textViewAfterChange) {
        disposable.add(textViewAfterChange
                .map(TextViewAfterTextChangeEvent::editable)
                .map(CharSequence::toString)
                .map(String::trim)
                .map(this::filterCurrentList)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(eventViewModels -> {
                    chatScreen.updateUi(eventViewModels);
                    if (eventViewModels.size() == 0)
                        chatScreen.showNoData();
                }, this::processError));
    }

    private List<ChatViewModel> filterCurrentList(String word) {
        if (TextUtils.isEmpty(word)) {
            return chatRelay.getValue();
        }
        HashSet<ChatViewModel> hashSet = new HashSet<>();
        for (int i = 0; i < chatRelay.getValue().size(); i++) {
            if (chatRelay.getValue().get(i).getUserViewModel().getFirstName().toLowerCase().contains(word)) {
                hashSet.add(chatRelay.getValue().get(i));
            }
        }
        return new ArrayList<>(hashSet);
    }

    void fetchData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(chatRepo.fetchInboxListSearch(userManager.getCurrentUser().getApiToken())
                    .map(chatMapper::toChatViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(chatViewModels -> {
                        chatScreen.updateUi(chatViewModels);
                        chatRelay.accept(chatViewModels);
                    }, this::processError));
        } else {
            chatScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            chatScreen.showErrorMessage(
                    getHttpErrorMessage(HttpException.wrapJakewhartonException((
                            com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        } else if (t instanceof IOException) {
            chatScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            chatScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return getResourcesUtil().getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return getResourcesUtil().getString(R.string.error_communicating_with_server);
        }
    }

    void onReportClicked(int userId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.reportUser(userManager.getCurrentUser().getApiToken(), userId, "")
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(chatScreen::onReportSuccessfully, this::processError));
        }
    }

    void onBlockClicked(int userId, int position) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.blockUser(userManager.getCurrentUser().getApiToken(), userId, "")
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> chatScreen.showLoadingAnimation())
                    .doFinally(chatScreen::hideLoadingAnimation)
                    .subscribe(() -> {
                        chatScreen.notifyItemRemoved(position);
                        List<ChatViewModel> tempChatViewModels = chatRelay.getValue();
                        for (int i = 0; i < tempChatViewModels.size(); i++) {
                            if (tempChatViewModels.get(i).getUserViewModel().getId() == userId) {
                                tempChatViewModels.remove(i);
                            }
                        }
                        chatRelay.accept(tempChatViewModels);
                    }, this::processError));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}