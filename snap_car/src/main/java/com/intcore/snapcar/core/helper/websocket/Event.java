package com.intcore.snapcar.core.helper.websocket;

import androidx.annotation.NonNull;

import com.intcore.snapcar.core.util.Preconditions;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

public abstract class Event {

    public static class ConnectingEvent extends Event {

        private final OkHttpClient okHttpClient;
        private final WebSocket webSocket;
        private final Request request;

        ConnectingEvent(WebSocket webSocket, Request request, OkHttpClient okHttpClient) {
            Preconditions.checkNonNull(okHttpClient);
            Preconditions.checkNonNull(webSocket);
            Preconditions.checkNonNull(request);
            this.okHttpClient = okHttpClient;
            this.webSocket = webSocket;
            this.request = request;
        }

        public WebSocket getWebSocket() {
            return webSocket;
        }

        public OkHttpClient getOkHttpClient() {
            return okHttpClient;
        }

        public Request getRequest() {
            return request;
        }

        @Override
        public String toString() {
            return "Connecting to: " + request.url().toString();
        }
    }

    public static class ConnectedEvent extends Event {

        private final WebSocket webSocket;
        private final Response response;

        ConnectedEvent(WebSocket webSocket, Response response) {
            Preconditions.checkNonNull(webSocket);
            Preconditions.checkNonNull(response);
            this.webSocket = webSocket;
            this.response = response;
        }


        public WebSocket getWebSocket() {
            return webSocket;
        }

        public Response getResponse() {
            return response;
        }


        @Override
        public String toString() {
            return "Connected";
        }
    }

    public static class MessageEvent extends Event {

        private final WebSocket webSocket;
        private final String body;

        MessageEvent(WebSocket webSocket, String body) {
            Preconditions.checkNonNull(webSocket);
            Preconditions.checkNonNull(body);
            this.webSocket = webSocket;
            this.body = body;
        }


        public WebSocket getWebSocket() {
            return webSocket;
        }

        public String getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "New message: "
                    + "\n"
                    + body;
        }
    }

    public static class DisconnectingEvent extends Event {

        private final WebSocket webSocket;
        private final String reason;
        private final int code;

        DisconnectingEvent(WebSocket webSocket, int code, String reason) {
            this.code = code;
            Preconditions.checkNonNull(webSocket);
            Preconditions.checkNonNull(reason);
            this.webSocket = webSocket;
            this.reason = reason;
        }


        public WebSocket getWebSocket() {
            return webSocket;
        }

        public String getReason() {
            return reason;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "Disconnecting: " + reason;
        }
    }

    public static class DisconnectedEvent extends Event {

        private final WebSocket webSocket;
        private final String reason;
        private final int code;

        DisconnectedEvent(WebSocket webSocket, int code, String reason) {
            this.code = code;
            Preconditions.checkNonNull(webSocket);
            Preconditions.checkNonNull(reason);
            this.webSocket = webSocket;
            this.reason = reason;
        }


        public WebSocket getWebSocket() {
            return webSocket;
        }

        public String getReason() {
            return reason;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "Disconnected: " + reason;
        }
    }

    public static class FailureEvent extends Event {

        private final Throwable throwable;

        public FailureEvent(@NonNull Throwable throwable) {
            Preconditions.checkNonNull(throwable);
            this.throwable = throwable;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public String toString() {
            return "Failure: "
                    + "\n"
                    + throwable.toString();
        }
    }
}
