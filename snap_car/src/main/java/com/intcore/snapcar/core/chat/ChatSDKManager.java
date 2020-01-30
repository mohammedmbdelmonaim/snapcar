package com.intcore.snapcar.core.chat;

import com.intcore.snapcar.core.chat.config.ChatConfig;

public final class ChatSDKManager {

    private static ChatConfig config;

    private ChatSDKManager() {
        throw new AssertionError();
    }

    public static void init(ChatConfig config) {
        if (ChatSDKManager.config == null) {
            ChatSDKManager.config = config;
        }
    }

    public static ChatConfig getConfig() {
        if (ChatSDKManager.config == null) {
            throw new IllegalAccessError("chat module must be initialized first");
        }
        return config;
    }

    public static void tearDown() {
        ChatSDKManager.config = null;
    }
}
