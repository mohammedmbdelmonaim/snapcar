package com.intcore.snapcar.core.chat.model.user;

import com.intcore.snapcar.core.chat.model.constants.Gender;

public class UserModel {

    private final long id;
    private final String firstName;
    private final String email;
    private final String phone;
    private final String activation;
    private final long type;
    private final String imageUrl;
    private final int gender;
    private final String apiToken;
    private final String createdAt;
    private final String updatedAt;
    private final String tempPhone;
    private final String resetPhoneCode;
    private final String resetPasswordCode;
    private final String androidFirebaseToken;

    public UserModel(long id, String firstName, String email, String phone, String activation, long type,
                     String imageUrl, int gender, String resetPasswordCode, String apiToken,
                     String androidFirebaseToken, String createdAt, String updatedAt,
                     String resetPhoneCode, String tempPhone) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.apiToken = apiToken;
        this.createdAt = createdAt;
        this.firstName = firstName;
        this.updatedAt = updatedAt;
        this.tempPhone = tempPhone;
        this.activation = activation;
        this.resetPhoneCode = resetPhoneCode;
        this.resetPasswordCode = resetPasswordCode;
        this.androidFirebaseToken = androidFirebaseToken;
    }

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

    public String getTempPhone() {
        return tempPhone;
    }

    public String getResetPhoneCode() {
        return resetPhoneCode;
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
}