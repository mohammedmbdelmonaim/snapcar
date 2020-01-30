package com.intcore.snapcar.core.chat.config;

import com.intcore.snapcar.core.chat.model.user.UserModel;
import com.intcore.snapcar.core.chat.sdk.BaseChatScreen;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.util.Preconditions;

import io.reactivex.disposables.CompositeDisposable;

public class ChatConfig {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final UserModel currentUSer;
    private final BaseChatScreen screen;
    private final String socketUrl;
    private final String uploadUrl;
    private final String baseUrl;

    ChatConfig(ThreadSchedulers threadSchedulers, CompositeDisposable disposable, String baseUrl,
               String socketUrl, String uploadUrl, BaseChatScreen screen, UserModel currentUSer) {
        this.threadSchedulers = threadSchedulers;
        this.currentUSer = currentUSer;
        this.disposable = disposable;
        this.uploadUrl = uploadUrl;
        this.socketUrl = socketUrl;
        this.baseUrl = baseUrl;
        this.screen = screen;
    }

    public CompositeDisposable getCompositeDisposable() {
        return disposable;
    }

    public ThreadSchedulers getThreadSchedulers() {
        return threadSchedulers;
    }

    public UserModel getCurrentUser() {
        Preconditions.checkNonNull(currentUSer, "current is not configured");
        return currentUSer;
    }

    public BaseChatScreen getScreen() {
        Preconditions.checkNonNull(screen, "chat screen is not configured");
        return screen;
    }

    public String getUploadUrl() {
        return Preconditions.requireStringNotEmpty(uploadUrl, "uploadUrl is not configured");
    }

    public String getSocketUrl() {
        return Preconditions.requireStringNotEmpty(socketUrl, "socketUrl is not configured");
    }

    public String getBaseUrl() {
        return Preconditions.requireStringNotEmpty(baseUrl, "baseUrl is not configured");
    }
}
