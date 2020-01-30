package com.intcore.snapcar.core.chat.config;

import androidx.annotation.NonNull;

import com.intcore.snapcar.core.chat.model.user.UserModel;
import com.intcore.snapcar.core.chat.sdk.BaseChatScreen;
import com.intcore.snapcar.core.schedulers.IOThreadSchedulers;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;

import io.reactivex.disposables.CompositeDisposable;

public class ChatConfigBuilder {

    private ThreadSchedulers threadSchedulers = new IOThreadSchedulers();
    private CompositeDisposable disposable = new CompositeDisposable();
    private UserModel currentUSer;
    private BaseChatScreen screen;
    private String uploadUrl;
    private String socketUrl;
    private String baseUrl;

    public ChatConfigBuilder threadSchedulers(@NonNull ThreadSchedulers threadSchedulers) {
        this.threadSchedulers = threadSchedulers;
        return this;
    }

    public ChatConfigBuilder compositeDisposable(@NonNull CompositeDisposable disposable) {
        this.disposable = disposable;
        return this;
    }

    public ChatConfigBuilder currentUSer(@NonNull UserModel currentUSer) {
        this.currentUSer = currentUSer;
        return this;
    }

    public ChatConfigBuilder socketUrl(@NonNull String socketUrl) {
        this.socketUrl = socketUrl;
        return this;
    }

    public ChatConfigBuilder uploadUrl(@NonNull String uploadUrl) {
        this.uploadUrl = uploadUrl;
        return this;
    }

    public ChatConfigBuilder screen(@NonNull BaseChatScreen screen) {
        this.screen = screen;
        return this;
    }

    public ChatConfigBuilder baseUrl(@NonNull String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ChatConfig build() {
        return new ChatConfig(threadSchedulers, disposable, baseUrl, socketUrl, uploadUrl, screen, currentUSer);
    }
}
