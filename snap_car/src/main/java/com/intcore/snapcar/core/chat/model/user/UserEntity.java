package com.intcore.snapcar.core.chat.model.user;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatUser")
public class UserEntity {

    @PrimaryKey
    private final long id;
    private final String firstName;
    private final String email;
    private final String phone;
    private final String activation;
    private final long type;
    private final String imageUrl;
    private final int gender;
    private final String resetPasswordCode;
    private final String apiToken;
    private final String androidFirebaseToken;
    private final String createdAt;
    private final String updatedAt;
    private final String resetPhoneCode;
    private final String tempPhone;

    UserEntity(long id, String firstName, String email, String phone,
               String activation, long type, String imageUrl,
               int gender, String resetPasswordCode,
               String apiToken, String androidFirebaseToken,
               String createdAt, String updatedAt,
               String resetPhoneCode, String tempPhone) {
        this.id = id;
        this.type = type;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.apiToken = apiToken;
        this.imageUrl = imageUrl;
        this.firstName = firstName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tempPhone = tempPhone;
        this.activation = activation;
        this.resetPhoneCode = resetPhoneCode;
        this.resetPasswordCode = resetPasswordCode;
        this.androidFirebaseToken = androidFirebaseToken;
    }

    long getId() {
        return id;
    }

    String getFirstName() {
        return firstName;
    }

    String getEmail() {
        return email;
    }

    String getPhone() {
        return phone;
    }

    String getActivation() {
        return activation;
    }

    long getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    int getGender() {
        return gender;
    }

    String getResetPasswordCode() {
        return resetPasswordCode;
    }

    String getApiToken() {
        return apiToken;
    }

    String getAndroidFirebaseToken() {
        return androidFirebaseToken;
    }

    String getCreatedAt() {
        return createdAt;
    }

    String getUpdatedAt() {
        return updatedAt;
    }

    String getResetPhoneCode() {
        return resetPhoneCode;
    }

    String getTempPhone() {
        return tempPhone;
    }
}