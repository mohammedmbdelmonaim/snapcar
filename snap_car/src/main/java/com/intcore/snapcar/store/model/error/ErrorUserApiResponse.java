package com.intcore.snapcar.store.model.error;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorUserApiResponse {

    @SerializedName("errors")
    private List<ErrorResponse> errorResponseList;

    public List<ErrorResponse> getErrorResponseList() {
        return errorResponseList;
    }

    public static class ErrorResponse {

        @SerializedName("name")
        private String name;
        @SerializedName("message")
        private String message;
        @SerializedName("code")
        private int code;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getName() {
            return name;
        }
    }
}