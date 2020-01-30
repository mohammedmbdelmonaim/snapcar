package com.intcore.snapcar.store.model.defaultuser;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.NotificationSettingDTO;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.country.CountryDataApiResponse;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoApiResponse;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.util.authentication.model.SocialType;

import java.util.List;

public class DefaultUserDataApiResponse {

    @SerializedName("user")
    private DefaultUserApiResponse defaultUserApiResponse;

    public DefaultUserApiResponse getDefaultUserApiResponse() {
        return defaultUserApiResponse;
    }

    public static class DefaultUserApiResponse {
        @SerializedName("id")
        private long id;
        @SerializedName("name")
        private String Name;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("activation")
        private String activation;
        @UserType
        @SerializedName("type")
        private int type;
        @SerializedName("image")
        private String imageUrl;
        @SerializedName("cover")
        private String coverUrl;
        @SerializedName("website")
        private String website;
        @SerializedName("address")
        private String address;
        @SerializedName("gender")
        private int gender;
        @SerializedName("notification_setting_object")
        private NotificationSettingDTO notificationSettingDTO;
        @SerializedName("social_id")
        private String socialId;
        @SerializedName("social_type")
        private int socialType;
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
        @SerializedName("birth_year")
        private String birthYear;
        @SerializedName("reset_phone_code")
        private String resetPhoneCode;
        @SerializedName("user_rate")
        private float userRate;
        @SerializedName("total_rate")
        private float totalRate;
        @SerializedName("temp_phone")
        private String tempPhone;
        @SerializedName("area")
        private String area;
        @SerializedName("city_id")
        private String cityId;
        @SerializedName("country_id")
        private String countryId;
        @SerializedName("available_car")
        private String availableCar;
        @SerializedName("car_limt")
        private String limitCarNumber;
        @SerializedName("is_fav")
        private Boolean isFav;
        @SerializedName("showroom_info")
        private ShowRoomInfoApiResponse showroomInfo;
        @SerializedName("country")
        private CountryDataApiResponse.CountryApiResponse countryApiResponse;
        @SerializedName("city")
        private CountryDataApiResponse.CountryApiResponse cityApiResponse;
        @SerializedName("tracked_car")
        private CarApiResponse trackedCar;
        @SerializedName("request_vip")
        private boolean requestVip;
        @SerializedName("request_hotzone")
        private boolean requestHotzone;
        @SerializedName("is_verified")
        private int isVerified;
        @SerializedName("has_hotzone")
        private boolean hasHotZone;
        @SerializedName("request_ads")
        private boolean hasRequestAd;
        @SerializedName("terms")
        private String termsAgreed;
        @SerializedName("position")
        private List<PhonesPositionsApiResponse> phonesPositionsApiResponses;


        public List<PhonesPositionsApiResponse> getPhonesPositionsApiResponses() {
            return phonesPositionsApiResponses;
        }

        public String isTermsAgreed() {
            return termsAgreed;
        }

        public boolean isHasRequestAd() {
            return hasRequestAd;
        }

        public boolean isHasHotZone() {
            return hasHotZone;
        }

        public int getIsVerified() {
            return isVerified;
        }

        public boolean isRequestVip() {
            return requestVip;
        }

        public String getLimitCarNumber() {
            return limitCarNumber;
        }

        public boolean isRequestHotzone() {
            return requestHotzone;
        }

        public CarApiResponse getTrackedCar() {
            return trackedCar;
        }

        public String getName() {
            return Name;
        }

        public String getArea() {
            return area;
        }

        public String getCityId() {
            return cityId;
        }

        public String getCountryId() {
            return countryId;
        }

        public CountryDataApiResponse.CountryApiResponse getCityApiResponse() {
            return cityApiResponse;
        }

        public CountryDataApiResponse.CountryApiResponse getCountryApiResponse() {
            return countryApiResponse;
        }

        public NotificationSettingDTO getNotificationSettingDTO() {
            return notificationSettingDTO;
        }

        public String getAvailableCar() {
            return availableCar;
        }

        public ShowRoomInfoApiResponse getShowroomInfo() {
            return showroomInfo;
        }

        public LatLng getShowRoomLocation(){
            if (showroomInfo == null)
                return null;

            return showroomInfo.getLocation();
        }

        public long getId() {
            return id;
        }

        public float getTotalRate() {
            return totalRate;
        }

        public float getUserRate() {
            return userRate;
        }

        public String getFirstName() {
            return Name;
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

        @UserType
        public int getType() {
            return type;
        }

        public Boolean getFav() {
            return isFav;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public String getWebsite() {
            return website;
        }

        public String getAddress() {
            return address;
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

        public String getSocialId() {
            return socialId;
        }

        @SocialType
        public int getSocialType() {
            return socialType;
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

        public String getBirthYear() {
            return birthYear;
        }

    }

    public class PhonesPositionsApiResponse {
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }
    }
}
