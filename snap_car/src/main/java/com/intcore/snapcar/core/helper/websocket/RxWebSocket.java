package com.intcore.snapcar.core.helper.websocket;

import com.intcore.snapcar.core.util.Preconditions;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RxWebSocket {

    private final OkHttpClient okHttpClient;
    private final Request request;
    private WebSocket webSocket;

    public RxWebSocket(OkHttpClient okHttpClient, Request request) {
        Preconditions.checkNonNull(okHttpClient, "you must pass a non null okHttpClient object");
        Preconditions.checkNonNull(request, "you must pass a non null request object");
        this.okHttpClient = okHttpClient;
        this.request = request;
    }

    /**
     * Uses {@link Request} to connect  a new web socket.
     */
    public Observable<Event> connect() {
        return Observable.create(emitter -> {
            webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    emitter.onNext(new Event.ConnectedEvent(webSocket, response));
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    emitter.onNext(new Event.MessageEvent(webSocket, text));
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    emitter.onNext(new Event.DisconnectingEvent(webSocket, code, reason));
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    emitter.onNext(new Event.DisconnectedEvent(webSocket, code, reason));
                    RxWebSocket.this.webSocket = null;
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    emitter.onNext(new Event.FailureEvent(t));
                    RxWebSocket.this.webSocket = null;
                }
            });
            emitter.onNext(new Event.ConnectingEvent(webSocket, request, okHttpClient));
        });
    }

    public boolean send(String text) {
        return webSocket != null && webSocket.send(text);
    }

    /**
     * Attempts to initiate a graceful shutdown of this web socket. Any already-enqueued messages will
     * be transmitted before the close message is sent but subsequent calls to {@link #send} will
     * return false and their messages will not be enqueued.
     * <p>
     * <p>This returns true if a graceful shutdown was initiated by this call. It returns false and if
     * a graceful shutdown was already underway or if the web socket is already closed or canceled.
     *
     * @param reason Reason for shutting down or {@code null}.
     */
    public void disconnect(String reason) {
        if (webSocket != null) {
            webSocket.close(1000, reason);
            webSocket = null;
        }
    }

    /**
     * Immediately and violently release resources held by this web socket, discarding any enqueued
     * messages. This does nothing if the web socket has already been closed or canceled.
     */
    public void disconnectImmediately() {
        if (webSocket != null) {
            webSocket.cancel();
            webSocket = null;
        }
    }

    public boolean isConnected() {
        return webSocket != null;
    }

}
