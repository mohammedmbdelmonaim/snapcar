package com.intcore.snapcar.store.model.defaultuser;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.intcore.snapcar.core.chat.model.constants.Gender;

@Entity(tableName = "user")
public class DefaultUserEntity {

    @PrimaryKey
    private final long id;
    private final int type;
    @Gender
    private final int gender;
    private final String email;
    private final String phone;
    private final String website;
    private final String address;
    private final int socialType;
    private final String socialId;
    private final String imageUrl;
    private final String coverUrl;
    private final String apiToken;
    private final String firstName;
    private final String createdAt;
    private final String updatedAt;
    private final String birthYear;
    private final String tempPhone;
    private final String activation;
    private final String resetPhoneCode;
    private final String resetPasswordCode;
    private final String androidFirebaseToken;
    private final String cityId;
    private final String countryId;
    private final String area;

    DefaultUserEntity(long id,
                      int type,
                      int gender,
                      String email,
                      String phone,
                      String website,
                      String address,
                      int socialType,
                      String socialId,
                      String imageUrl,
                      String coverUrl,
                      String apiToken,
                      String firstName,
                      String createdAt,
                      String updatedAt,
                      String birthYear,
                      String tempPhone,
                      String activation,
                      String resetPhoneCode,
                      String resetPasswordCode,
                      String androidFirebaseToken,
                      String cityId,
                      String countryId,
                      String area) {
        this.id = id;
        this.type = type;
        this.area = area;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.cityId = cityId;
        this.website = website;
        this.address = address;
        this.socialId = socialId;
        this.imageUrl = imageUrl;
        this.coverUrl = coverUrl;
        this.apiToken = apiToken;
        this.firstName = firstName;
        this.countryId = countryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.birthYear = birthYear;
        this.tempPhone = tempPhone;
        this.socialType = socialType;
        this.activation = activation;
        this.resetPhoneCode = resetPhoneCode;
        this.resetPasswordCode = resetPasswordCode;
        this.androidFirebaseToken = androidFirebaseToken;
    }

    public String getArea() {
        return area;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getTempPhone() {
        return tempPhone;
    }

    public String getActivation() {
        return activation;
    }

    public String getResetPhoneCode() {
        return resetPhoneCode;
    }

    public String getResetPasswordCode() {
        return resetPasswordCode;
    }

    public String getAndroidFirebaseToken() {
        return androidFirebaseToken;
    }

    public String getCityId() {
        return cityId;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public int getSocialType() {
        return socialType;
    }

    public String getSocialId() {
        return socialId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}