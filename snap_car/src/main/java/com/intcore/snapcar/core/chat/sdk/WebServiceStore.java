package com.intcore.snapcar.core.chat.sdk;

import com.intcore.snapcar.core.chat.ChatSDKManager;
import com.intcore.snapcar.core.chat.model.message.MessageDTO;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.helper.websocket.RxWebSocket;
import com.intcore.snapcar.core.util.Preconditions;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import timber.log.Timber;

class WebServiceStore {

    private static final String HEADER = "application/json";
    private final ApisUtil apisUtil;
    private RxWebSocket rxWebSocket;

    WebServiceStore() {
        this.apisUtil = new ApisUtil();
    }

    Observable<Event> startWebSocket() {
        if (isWebSocketConnected()) {
            rxWebSocket.disconnectImmediately();
        }
        rxWebSocket = new RxWebSocket(apisUtil.createOkHttpClient(), apisUtil.createWebSocketRequest());
        return rxWebSocket.connect();
    }

    void endWebSocket(String reason) {
        if (rxWebSocket.isConnected()) {
            rxWebSocket.disconnect(reason);
        }
    }

    void killWebSocket() {
        if (rxWebSocket.isConnected()) {
            rxWebSocket.disconnectImmediately();
        }
    }

    boolean sendOverWebSocket(String body) {
        Timber.tag("manarDebug").v(body);
        return rxWebSocket.send(body);
    }

    boolean isWebSocketConnected() {
        return rxWebSocket != null && rxWebSocket.isConnected();
    }

    Single<ResponseBody> uploadImage(String apiToken, File file) {
        Preconditions.checkNonNull(file, "can not upload null file");
        RequestBody apiTokenBody = RequestBody.create(MediaType.parse("text/plain"), apiToken);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        return apisUtil.getApiService()
                .upload(HEADER, ChatSDKManager.getConfig().getUploadUrl(), apiTokenBody, fileBody);
    }

    Observable<List<MessageDTO>> fetchUnseenMessages(String apiToken, String url) {
        Timber.tag("manarDebug").v(apiToken + " " + url);
        return apisUtil.getApiService()
                .fetchUnseenMessages(HEADER, url, apiToken);
    }
}