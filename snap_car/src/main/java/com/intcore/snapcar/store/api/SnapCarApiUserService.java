package com.intcore.snapcar.store.api;

import com.intcore.snapcar.store.model.country.CountryDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SnapCarApiUserService {

    @POST("valid-phone")
    @FormUrlEncoded
    Single<ResponseBody> validatePhone(
            @Header("Accept") String header,
            @Field("phone") String phone,
            @Field("locale") String lang);

    @GET("country-list")
    Single<CountryDataApiResponse> fetchCountryList();

    @GET("get-profile")
    Single<DefaultUserDataApiResponse> fetchUserProfileData(
            @Header("Accept") String header,
            @Query("api_token") String phone);

    @PATCH("update-profile")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateUser(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Field("name") String name,
            @Field("email") String email,
            @Field("country_id") int country,
            @Field("city_id") int city,
            @Field("area") String area,
            @Field("image") String image);

    @PATCH("update-phone")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateUserPhone(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Field("phone") String phone,
            @Field("temp_phone_code") String code);

    @PATCH("update-profile")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateShowRoom(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("name") String name,
            @Field("email") String email,
            @Field("open_time") String from,
            @Field("close_time") String to,
            @Field("open_time_2") String fromTwo,
            @Field("close_time_2") String toTwo,
            @Field("image") String image,
            @Field("dealing_with") String dealingValue);

    @PATCH("update-location")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateLocation(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("country_id") int country,
            @Field("city_id") int city,
            @Field("area") String area,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude);

    @POST("request-update-phone")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateShowRoomPhones(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("phone") String mainPhone,
            @Field("phones") String phones);

    @POST("request-update-phone")
    @FormUrlEncoded
    Single<DefaultUserDataApiResponse> updateUserPhonee(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("phone") String mainPhone);

    @GET("notifications")
    Single<ResponseBody> fetchMyNotifications(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @PATCH("notifications/{notification_id}")
    Completable updateSeen(
            @Header("Accept") String header,
            @Path("notification_id") String notificationId,
            @Query("api_token") String apiToken);

    @PATCH("update-password")
    @FormUrlEncoded
    Single<ResponseBody> updatePassword(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("old_password") String oldPass,
            @Field("new_password") String newPass);

    @PATCH("language")
    @FormUrlEncoded
    Single<ResponseBody> changeLanguage(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("language") String language);
}