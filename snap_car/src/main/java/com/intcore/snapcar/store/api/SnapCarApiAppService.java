package com.intcore.snapcar.store.api;

import com.intcore.snapcar.store.model.addinterest.InterestRequiredApiResponse;
import com.intcore.snapcar.store.model.blocklist.BlockDTO;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.car.CarSearchDTO;
import com.intcore.snapcar.store.model.car.CarVisitorDTO;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;
import com.intcore.snapcar.store.model.discount.DiscountApiResponse;
import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.interest.MyInterestDTO;
import com.intcore.snapcar.store.model.messageresponse.MessageApiResponse;
import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsApiResponse;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.store.model.visitor.VisitorApiResponse;
import com.intcore.snapcar.core.chat.ChatApiResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SnapCarApiAppService {

    @GET("user/{user_id}")
    Single<VisitorApiResponse> getVisitorProfile(
            @Header("Accept") String header,
            @Path("user_id") long id,
            @Query("api_token") String apiToken);

    @POST("file/upload")
    @Multipart
    Single<ResponseBody> uploadFile(
            @Header("Accept") String header,
            @Part("api_token") RequestBody apiToken,
            @Part MultipartBody.Part transactionPhoto);

    @POST("block-people")
    @FormUrlEncoded
    Single<ResponseBody> blockUser(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("user_id") long userId,
            @Field("reason") String reason);

    @POST("users/rate/{user_id}")
    @FormUrlEncoded
    Completable rateUser(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Path("user_id") long userId,
            @Field("rate") int rate,
            @Field("note") String note);

    @GET("block-people")
    Single<BlockDTO> fetchBlockList(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("page") int pageNumber);

    @DELETE("block-people/{user_id}")
    Completable removeUserFromBlock(
            @Header("Accept") String header,
            @Path("user_id") long id,
            @Query("api_token") String apiToken);

    @DELETE("interest/{interest_id}")
    Completable removeInterest(
            @Header("Accept") String header,
            @Path("interest_id") int id,
            @Query("api_token") String apiToken);

    @GET("interest/create")
    Single<InterestRequiredApiResponse> getRequiredDataFroInterest(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @POST("interest")
    @FormUrlEncoded
    Completable addInterest(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("gear_type") int gearType,
            @Field("specification_id") int specificationId,
            @Field("method_of_sale_id") int paymentType,
            @Field("car_color_id") int colorId,
            @Field("mvpi") int mvpi,
            @Field("brand_id") int brandId,
            @Field("car_model_id") int modelId,
            @Field("price_from") String priceFrom,
            @Field("price_to") String priceTo,
            @Field("year_from") String year,
            @Field("year_to") String yearTo,
            @Field("car_condition") int carCondition,
            @Field("seller_type") int sellerType,
            @Field("nearby") int nearby,
            @Field("city_id") int cityId,
            @Field("country_id") int countryId,
            @Field("kilometer_from") String kmFrom,
            @Field("kilometer_to") String kmTo,
            @Field("category_id") int categoryId,
            @Field("vehicle_registration") int vehicle);

    @PATCH("interest/{interest_id}")
    Completable updateInterest(
            @Header("Accept") String header,
            @Path("interest_id") int interestId,
            @Query("api_token") String apiToken,
            @Query("gear_type") int gearType,
            @Query("specification_id") int specificationId,
            @Query("method_of_sale_id") int paymentType,
            @Query("car_color_id") int colorId,
            @Query("mvpi") int mvpi,
            @Query("brand_id") int brandId,
            @Query("car_model_id") int modelId,
            @Query("price_from") String priceFrom,
            @Query("price_to") String priceTo,
            @Query("year_from") String year,
            @Query("year_to") String yearTo,
            @Query("car_condition") int carCondition,
            @Query("seller_type") int sellerType,
            @Query("nearby") int nearby,
            @Query("city_id") int cityId,
            @Query("country_id") int countryId,
            @Query("kilometer_from") String kmFrom,
            @Query("kilometer_to") String kmTo,
            @Query("category_id") int categoryId,
            @Query("vehicle_registration") int vehicle);


    @GET("hotzone/{id}/get_directions")
    Completable hotzoneLocationClicked(
            @Header("Accept") String header,
            @Path("id") int id,
            @Query("api_token") String apiToken);

    @GET("home")
    Single<FilterApiResponse> fetchFilterData(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude);

    @GET("car/create")
    Single<CarResourcesApiResponse> getGetCarResources(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @POST("car")
    @FormUrlEncoded
    Single<CarDTO> addCar(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("contact_option") int contactOption,
            @Field("post_type") int postType,
            @Field("is_tracked") int isTracked,
            @Field("car_status") int condition,
            @Field("manufacturing_year") int manufacturingYear,
            @Field("car_specification_id") int importer,
            @Field("transmission") int transmission,
            @Field("car_color_id") int colorId,
            @Field("kilometer") String kmTo,
            @Field("under_warranty") int warranty,
            @Field("warranty") String agentName,
            @Field("mvpi") int mvpi,
            @Field("notes") String notes,
            @Field("car_model_id") int modelId,
            @Field("price_before") String priceBefore,
            @Field("price_after") String priceAfter,
            @Field("engine_capacity_cc") String engineCapacity,
            @Field("images") String image,
            @Field("installment_price_from") String installmentFrom,
            @Field("installment_price_to") String installmentTo,
            @Field("exchange") int exchange,
            @Field("longitude") String longitude,
            @Field("latitude") String latitude,
            @Field("examination_image") String examinationImage,
            @Field("category_id") int categoryId,
            @Field("brand_id") int brandId,
            @Field("price") String price,
            @Field("vehicle_registration") int vehicleRegistration,
            @Field("price_type") int priceType
    );

    @PATCH("car/{id}")
    Single<ResponseBody> updateCar(
            @Header("Accept") String header,
            @Path("id") String carId,
            @Query("api_token") String apiToken,
            @Query("contact_option") int contactOption,
            @Query("post_type") int postType,
            @Query("is_tracked") int isTracked,
            @Query("car_status") int condition,
            @Query("manufacturing_year") int manufacturingYear,
            @Query("car_specification_id") int importer,
            @Query("transmission") int transmission,
            @Query("car_color_id") int colorId,
            @Query("kilometer") String kmTo,
            @Query("under_warranty") int warranty,
            @Query("warranty") String agentName,
            @Query("mvpi") int mvpi,
            @Query("notes") String notes,
            @Query("car_model_id") int modelId,
            @Query("price_before") String priceBefore,
            @Query("price_after") String priceAfter,
            @Query("engine_capacity_cc") String engineCapacity,
            @Query("images") String image,
            @Query("installment_price_from") String installmentFrom,
            @Query("installment_price_to") String installmentTo,
            @Query("exchange") int exchange,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("examination_image") String examinationImage,
            @Query("category_id") int categoryId,
            @Query("brand_id") int brandId,
            @Query("price") String price,
            @Query("vehicle_registration") int vehicleRegistration,
            @Query("price_type") int priceType);

    @POST("favourite")
    @FormUrlEncoded
    Single<MessageApiResponse> addItemToFavorite(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("car_id") String carId,
            @Field("user_id") String userId);

    @POST("filter-map")
    @FormUrlEncoded
    Single<FilterApiResponse> filterHome(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("car_model_id") int carModelId,
            @Field("brand_id") int brandId,
            @Field("category_id") int categoryId,
            @Field("show_all") int showAll,
            @Field("men_car") int menCar,
            @Field("women_car") int womenCar,
            @Field("damaged_car") int damagedCar,
            @Field("vip_showroom") int vipShowRoom,
            @Field("showroom") int showRoom,
            @Field("vip_hotzone") int vipHotZone,
            @Field("hotzone") int hotZone,
            @Field("longitude") double longitude,
            @Field("latitude") double latitude);

    @POST("filter-explore")
    @FormUrlEncoded
    Single<FilterApiResponse> filterExplore(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("car_model_id") int carModelId,
            @Field("brand_id") int brandId,
            @Field("category_id") int categoryId,
            @Field("show_all") int showAll,
            @Field("men_car") int menCar,
            @Field("women_car") int womenCar,
            @Field("damaged_car") int damagedCar,
            @Field("vip_showroom") int vipShowRoom,
            @Field("showroom") int showRoom,
            @Field("vip_hotzone") int vipHotZone,
            @Field("hotzone") int hotZone,
            @Field("longitude") double longitude,
            @Field("latitude") double latitude);

    @GET("car/untracked")
    Single<FilterApiResponse> fetchUnTrackedCar(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @GET("search")
    Single<CarSearchDTO> fetchCarSearch(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("brand_id") int brandId,
            @Query("model_id") int modelId,
            @Query("category_id") int categoryId,
            @Query("gender") int gender,
            @Query("year_form") String yearFrom,
            @Query("year_to") String yearTo,
            @Query("price_from") String priceFrom,
            @Query("price_to") String priceTo,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("page") int pageNumber);

    @GET("search")
    Single<CarSearchDTO> fetchAdvancedCarSearch(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("brand_id") int brandId,
            @Query("model_id") int modelId,
            @Query("category_id") int categoryId,
            @Query("gender") int gender,
            @Query("year_form") String yearFrom,
            @Query("year_to") String yearTo,
            @Query("price_from") String priceFrom,
            @Query("price_to") String priceTo,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("page") int pageNumber,
            @Query("under_warranty") int warranty,
            @Query("engine_capacity_cc") int engineCapacity,
            @Query("color_id") int color_id,
            @Query("kilometer_from") String kilometer_from,
            @Query("kilometer_to") String kilometer_to,
            @Query("transmission") int gear_type,
            @Query("car_status") int car_status,
            @Query("tracked") int tracked,
            @Query("post_type") int postType,
            @Query("is_installment") int installment,
            @Query("is_big_sale") int bigSale);

    @GET("interest")
    Single<MyInterestDTO> fetchMyInterests(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("page") int pageNumber);

    @GET("coupons")
    Single<DiscountApiResponse> fetchCoupons(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @GET("search-user")
    Single<CarVisitorDTO>
    fetchVisitorCars(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("user_id") int userId,
            @Query("brand_id") int brandId,
            @Query("model_id") int modelId,
            @Query("category_id") int categoryId,
            @Query("year_from") String yearFrom,
            @Query("year_to") String yearTo,
            @Query("price_from") String priceFrom,
            @Query("price_to") String priceTo,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude);

    @GET("car-details/{car_id}")
    Single<CarDTO> getCar(
            @Header("Accept") String header,
            @Path("car_id") int id,
            @Query("api_token") String apiToken);

    @GET("car")
    Single<MyCarsApiResponse> fetchMyCars(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("page") int pageNumber);

    @DELETE("car/{id}")
    Single<ResponseBody> deleteCar(
            @Header("Accept") String header,
            @Path("id") int id,
            @Query("delete_reason") String reason,
            @Query("api_token") String apiToken);

    @GET("settings")
    Single<SettingsApiResponse> getSittings(
            @Header("Accept") String header);

    @POST("car/actions/track")
    @FormUrlEncoded
    Completable updateCarLocation(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("car_id") String contactOption,
            @Field("longitude") String longitude,
            @Field("latitude") String latitud);

    @POST("user-move")
    @FormUrlEncoded
    Completable updateUserLocation(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("longitude") String longitude,
            @Field("latitude") String latitud);

    @GET("car/actions/expired-cars")
    Single<CarDTO> getExpiredCars(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @PATCH("car/actions/renew-car/{id}")
    Single<CarApiResponse> renewCar(
            @Header("Accept") String header,
            @Path("id") String carId,
            @Query("api_token") String apiToken);

    @GET("favourite")
    Single<FavoritesApiResponse> fetchFavorites(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Query("page") int pageNumber);

    @POST("favourite")
    @FormUrlEncoded
    Single<ResponseBody> deleteCarFavorite(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Field("car_id") int carId);

    @POST("favourite")
    @FormUrlEncoded
    Single<ResponseBody> deleteShowroomFavorite(
            @Header("Accept") String header,
            @Query("api_token") String apiToken,
            @Field("user_id") int userId);

    @POST("request/vip")
    @FormUrlEncoded
    Single<ResponseBody> requestVIP(
            @Header("Accept") String header,
            @Field("api_token") String apiToken);

    @POST("request/ads")
    @FormUrlEncoded
    Single<ResponseBody> requestAds(
            @Header("Accept") String header,
            @Field("api_token") String apiToken);

    @POST("car/payment-history")
    @FormUrlEncoded
    Single<List<PaymentHistoryApiResponse>> getPaymentHistory(
            @Header("Accept") String header,
            @Field("api_token") String apiToken);

    @GET("feedback/create")
    Single<FeedbackApiResponse> getFeedbackSubjects(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @Multipart
    @POST("feedback")
    Single<ResponseBody> postFeedback(
            @Header("Accept") String header,
            @Part("api_token") RequestBody apiToken,
            @Part("feedback_category_id") RequestBody categoryId,
            @Part("details") RequestBody details,
            @Part MultipartBody.Part attachment,
            @Part("subject") RequestBody subject);

    @GET("survey")
    Single<SurveyApiResponse> fetchSurvies(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @POST("survey")
    @FormUrlEncoded
    Single<ResponseBody> postSurvey(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("servey_id") String surveyId,
            @Field("answer") String answer);

    @POST("survey")
    @FormUrlEncoded
    Single<ResponseBody> postCanceledSurvey(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("servey_id") String surveyId);


    @Multipart
    @POST("send/verification-letter")
    Single<ResponseBody> sendVerfiyLetter(
            @Header("Accept") String header,
            @Part("api_token") RequestBody apiToken,
            @Part MultipartBody.Part attachment);

    @POST("request/hotzone")
    @FormUrlEncoded
    Single<ResponseBody> requestHotZone(
            @Header("Accept") String header,
            @Field("api_token") String apiToken);


    @PATCH("car/actions/skip-payment/{car_id}")
    Single<ResponseBody> skipPayment(
            @Header("Accept") String header,
            @Path("car_id") String carId,
            @Query("api_token") String apiToken,
            @Query("commission") String commition,
            @Query("car_sold_price") String totalPrice);


    @GET("chat/chat")
    Single<List<ChatApiResponse>> fetchInboxList(
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @GET("chat/chat/{user_id}")
    Single<ChatApiResponse> startChat(
            @Header("Accept") String header,
            @Path("user_id") int userId,
            @Query("api_token") String apiToken);

    @DELETE("chat/chat/{chatId}")
    Completable deleteChat(
            @Path("chatId") int chatId,
            @Header("Accept") String header,
            @Query("api_token") String apiToken);

    @POST("report-user")
    @FormUrlEncoded
    Completable reportUser(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("user_id") int userId,
            @Field("reason") String reason);

    @POST("users/setting")
    @FormUrlEncoded
    Completable manageNotification(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("notification_setting") String notificationSetting);

    @POST("contact-us")
    @FormUrlEncoded
    Completable postContactUs(
            @Field("name") String name,
            @Field("email") String email,
            @Field("message") String message);

    @POST("users/terms")
    @FormUrlEncoded
    Completable termsAgreed(
            @Header("Accept") String header,
            @Field("api_token") String apiToken,
            @Field("terms") String terms);

    @POST("logout")
    @FormUrlEncoded
    Completable logout(
            @Header("Accept") String header,
            @Field("api_token") String apiToken);

    @GET("hotzone/{hot_zone}/get_directions")
    Completable getDirection(
            @Header("Accept") String header,
            @Path("hot_zone") int hotZoned,
            @Query("api_token") String apiToken);


}