package com.intcore.snapcar.core.chat.model.user;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.core.chat.model.constants.Gender;

public class UserDataApiResponse {

    @SerializedName("user")
    private UserApiResponse userApiResponse;

    public UserApiResponse getUserApiResponse() {
        return userApiResponse;
    }

    public static class UserApiResponse {

        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String firstName;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("activation")
        private String activation;
        @SerializedName("type")
        private long type;
        @SerializedName("image")
        private String imageUrl;
        @SerializedName("gender")
        private int gender;
        @SerializedName("reset_password_code")
        private String resetPasswordCode;
        @SerializedName("api_token")
        private String apiToken;
        @SerializedName("android_token")
        private String androidFirebaseToken;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        @SerializedName("reset_phone_code")
        private String resetPhoneCode;
        @SerializedName("temp_phone")
        private String tempPhone;

        public long getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getActivation() {
            return activation;
        }

        public long getType() {
            return type;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getResetPhoneCode() {
            return resetPhoneCode;
        }

        public String getTempPhone() {
            return tempPhone;
        }

        @Gender
        public int getGender() {
            return gender;
        }

        public String getResetPasswordCode() {
            return resetPasswordCode;
        }

        public String getApiToken() {
            return apiToken;
        }

        public String getAndroidFirebaseToken() {
            return androidFirebaseToken;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public static class ErrorResponse {

            @SerializedName("name")
            private String name;

            @SerializedName("email")
            private String email;

            @SerializedName("phone")
            private String phone;

            @SerializedName("password")
            private String password;

            @SerializedName("error")
            private String error;

            public String getName() {
                return name;
            }

            public String getEmail() {
                return email;
            }

            public String getPhone() {
                return phone;
            }

            public String getPassword() {
                return password;
            }

            public String getError() {
                return error;
            }
        }
    }
}
