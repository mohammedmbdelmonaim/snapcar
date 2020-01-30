package com.intcore.snapcar.store.model.defaultuser;


import com.intcore.snapcar.store.model.NotificationSettingDTO;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.country.CountryModel;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoModel;

import java.util.List;

public class DefaultUserModel {

    private final long id;
    @UserType
    private final int type;
    private final int gender;
    private final String area;
    private final String email;
    private final String phone;
    private final String website;
    private final String address;
    private final int socialType;
    private final String socialId;
    private final String apiToken;
    private final String imageUrl;
    private final String coverUrl;
    private final String firstName;
    private final String createdAt;
    private final String updatedAt;
    private final String tempPhone;
    private final String birthYear;
    private final String activation;
    private  float userRate;
    private final float totalRate;
    private final String carLimitNumber;
    private final String availableCars;
    private final String resetPhoneCode;
    private final String resetPasswordCode;
    private final String androidFirebaseToken;
    private final String cityId;
    private final String countryId;
    private final Boolean isFavorite;
    private final CountryModel countryModel;
    private final CountryModel cityModel;
    private final ShowRoomInfoModel showRoomInfoModel;
    private final Boolean requestVip;
    private final Boolean requestHotzone;
    private final int isVerified;
    private final Boolean hasHotZone;
    private final Boolean hasRequestAds;
    private final String termsAgreed;
    private final NotificationSettingDTO notificationSettingDTO;
    private final List<DefaultUserDataApiResponse.PhonesPositionsApiResponse> phonesPositionsApiResponse;
    private CarModel carModel;

    public boolean isComingFromHotZone = false ;
    DefaultUserModel(long id,
                     int type,
                     int gender,
                     String email,
                     String phone,
                     String website,
                     String address,
                     int socialType,
                     String socialId,
                     String apiToken,
                     String imageUrl,
                     String coverUrl,
                     String firstName,
                     String createdAt,
                     String updatedAt,
                     String tempPhone,
                     String birthYear,
                     String activation,
                     String resetPhoneCode,
                     String resetPasswordCode,
                     String androidFirebaseToken,
                     String cityId,
                     String countryId,
                     String area,
                     CountryModel countryModel,
                     CountryModel cityModel,
                     float userRate,
                     float totalRate,
                     String availableCars,
                     String carLimitNumber,
                     ShowRoomInfoModel showRoomInfoModel,
                     Boolean isFavorite,
                     CarModel carModel,
                     boolean requestVip,
                     boolean requestHotzone,
                     int isVerified,
                     boolean hasHotZone,
                     NotificationSettingDTO notificationSettingDTO,
                     Boolean hasRequestAds,
                     List<DefaultUserDataApiResponse.PhonesPositionsApiResponse> phonesPositionsApiResponse, String termsAgreed) {
        this.id = id;
        this.type = type;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.address = address;
        this.socialType = socialType;
        this.socialId = socialId;
        this.carLimitNumber = carLimitNumber;
        this.apiToken = apiToken;
        this.imageUrl = imageUrl;
        this.coverUrl = coverUrl;
        this.firstName = firstName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tempPhone = tempPhone;
        this.birthYear = birthYear;
        this.activation = activation;
        this.userRate = userRate;
        this.totalRate = totalRate;
        this.availableCars = availableCars;
        this.resetPhoneCode = resetPhoneCode;
        this.resetPasswordCode = resetPasswordCode;
        this.androidFirebaseToken = androidFirebaseToken;
        this.cityId = cityId;
        this.countryId = countryId;
        this.countryModel = countryModel;
        this.cityModel = cityModel;
        this.showRoomInfoModel = showRoomInfoModel;
        this.area = area;
        this.notificationSettingDTO = notificationSettingDTO;
        this.isFavorite = isFavorite;
        this.carModel = carModel;
        this.requestVip = requestVip;
        this.requestHotzone = requestHotzone;
        this.isVerified = isVerified;
        this.hasHotZone = hasHotZone;
        this.hasRequestAds = hasRequestAds;
        this.phonesPositionsApiResponse = phonesPositionsApiResponse;
        this.termsAgreed = termsAgreed;
    }



    public String getTermsAgreed() {
        return termsAgreed;
    }

    public List<DefaultUserDataApiResponse.PhonesPositionsApiResponse> getPhonesPositionsApiResponse() {
        return phonesPositionsApiResponse;
    }

    public Boolean hasRequestAds() {
        return hasRequestAds;
    }

    public Boolean getHasHotZone() {
        return hasHotZone;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public Boolean getRequestVip() {
        return requestVip;
    }

    public Boolean getRequestHotzone() {
        return requestHotzone;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public Boolean getFavorite() {
        return isFavorite;
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

    public float getUserRate() {
        return userRate;
    }

    public void setUserRate(float userRate) {
        this.userRate = userRate;
    }

    public float getTotalRate() {
        return totalRate;
    }

    public String getAvailableCars() {
        return availableCars;
    }

    public ShowRoomInfoModel getShowRoomInfoModel() {
        return showRoomInfoModel;
    }

    public int getGender() {
        return gender;
    }

    public String getCountryId() {
        return countryId;
    }

    public String getEmail() {
        return email;
    }

    public CountryModel getCityModel() {
        return cityModel;
    }

    public CountryModel getCountryModel() {
        return countryModel;
    }

    public String getPhone() {
        return phone;
    }

    public String getCarLimitNumber() {
        return carLimitNumber;
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

    public String getArea() {
        return area;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
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

    public String getTempPhone() {
        return tempPhone;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getActivation() {
        return activation;
    }

    public NotificationSettingDTO getNotificationSettingDTO() {
        return notificationSettingDTO;
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
}