package com.intcore.snapcar.store;

import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredApiResponse;
import com.intcore.snapcar.store.model.blocklist.BlockDTO;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.car.CarSearchDTO;
import com.intcore.snapcar.store.model.car.CarVisitorDTO;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;
import com.intcore.snapcar.store.model.country.CountryDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.discount.DiscountApiResponse;
import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.messageresponse.MessageApiResponse;
import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsApiResponse;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.store.model.visitor.VisitorApiResponse;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.helper.websocket.Event;
import com.intcore.snapcar.core.helper.websocket.RxWebSocket;
import com.intcore.snapcar.core.chat.ChatApiResponse;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import kotlin.Triple;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@ApplicationScope
public
class WebServiceStore {

    private static final String HEADER = "application/json";
    private final ApiUtils apiUtils;
    private RxWebSocket rxWebSocket;

    @Inject
    WebServiceStore(ApiUtils apiUtils) {
        this.apiUtils = apiUtils;
    }

    private boolean isWebSocketConnected() {
        return rxWebSocket != null && rxWebSocket.isConnected();
    }

    Observable<Event> startWebSocket(String apiToken) {
        if (isWebSocketConnected()) {
            rxWebSocket.disconnectImmediately();
        }
        rxWebSocket = new RxWebSocket(apiUtils.createOkHttpClient(), apiUtils.createWebSocketRequest(apiToken));
        return rxWebSocket.connect();
    }

    void endWebSocket(String reason) {
        if (rxWebSocket.isConnected()) {
            rxWebSocket.disconnect(reason);
        }
    }

    void killWebSocket() {
        if (rxWebSocket.isConnected()) {
            rxWebSocket.disconnectImmediately();
        }
    }

    Single<ResponseBody> validatePhone(String phone,String lang) {
        return apiUtils.getSnapCarApiUserService()
                .validatePhone(HEADER, phone, lang);
    }

    Single<DefaultUserDataApiResponse> updateLocation(String apiToken, int country, int city, String area, String lon, String lat) {
        return apiUtils.getSnapCarApiUserService()
                .updateLocation(HEADER, apiToken, country, city, area, lon, lat);
    }

    Single<FilterApiResponse> fetchFilterData(String apiToken, String latitude, String longitude) {
        return apiUtils.getSnapCarApiAppService()
                .fetchFilterData(HEADER, apiToken, latitude, longitude);
    }

    Single<CountryDataApiResponse> fetchCountryList() {
        return apiUtils.getSnapCarApiUserService()
                .fetchCountryList();
    }

    Single<DefaultUserDataApiResponse> fetchUserProfile(String ApiToken) {
        return apiUtils.getSnapCarApiUserService()
                .fetchUserProfileData(HEADER, ApiToken);
    }

    Single<VisitorApiResponse> fetchVisitorProfile(String ApiToken, long userId) {
        return apiUtils.getSnapCarApiAppService()
                .getVisitorProfile(HEADER, userId, ApiToken);
    }

    Single<CarDTO> getCar(int carId, String ApiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getCar(HEADER, carId, ApiToken);
    }

    Single<ResponseBody> uploadFile(String apiToken, File image, RequestBodyProgress.UploadCallbacks uploadCallbacks) {
        MultipartBody.Part imageBody = null;
        if (image != null) {
            RequestBodyProgress reqFile = new RequestBodyProgress(image, uploadCallbacks);
            imageBody = MultipartBody.Part.createFormData("file", image.getName(), reqFile);
        }

        RequestBody apiTokenBody = null;
        if (apiToken != null)
            RequestBody.create(MediaType.parse("text/plain"), apiToken);

        return apiUtils
                .getSnapCarApiAppService()
                .uploadFile(WebServiceStore.HEADER,
                        apiTokenBody,
                        imageBody);
    }

    Single<ResponseBody> blockUser(String ApiToken, long userId, String reason) {
        return apiUtils.getSnapCarApiAppService()
                .blockUser(HEADER, ApiToken, userId, reason);
    }

    Single<DefaultUserDataApiResponse> updateUser(String apiToken, String name, String email, int country, int city, String area, String image) {
        return apiUtils.getSnapCarApiUserService().
                updateUser(HEADER, apiToken, name, email, country, city, area, image);
    }

    Single<DefaultUserDataApiResponse> updateUserPhone(String apiToken, String phone, String code) {
        return apiUtils.getSnapCarApiUserService().
                updateUserPhone(HEADER, apiToken, phone, code);
    }

    Single<DefaultUserDataApiResponse> updateShowRoom(String apiToken, String name, String email, String from, String to, String fromTwo, String toTwo, String image, String dealingValue) {
        return apiUtils.getSnapCarApiUserService()
                .updateShowRoom(HEADER, apiToken, name, email, from, to, fromTwo, toTwo, image, dealingValue);
    }

    Completable rateUser(String apiToken, long userId, int rate, String note) {
        return apiUtils.getSnapCarApiAppService()
                .rateUser(HEADER, apiToken, userId, rate, note);
    }

    Single<DefaultUserDataApiResponse> updateShowRoomPhones(String apiToken, String mainPhone, String phones) {
        return apiUtils.getSnapCarApiUserService()
                .updateShowRoomPhones(HEADER, apiToken, mainPhone, phones);
    }

    Single<DefaultUserDataApiResponse> updatUserPhonee(String apiToken, String mainPhone) {
        return apiUtils.getSnapCarApiUserService()
                .updateUserPhonee(HEADER, apiToken, mainPhone);
    }

    Single<Triple<Integer, Boolean, List<BlockDTO.BlockListData>>>
    fetchBlockList(String apiToken, int pageNumber) {
        return apiUtils
                .getSnapCarApiAppService()
                .fetchBlockList(HEADER, apiToken, pageNumber)
                .map(blockDTO -> {
                    int currentPage = blockDTO.getCurrentPage();
                    boolean canLoadMore = blockDTO.getCurrentPage() < blockDTO.getLastPage();
                    return new Triple<>(currentPage, canLoadMore, blockDTO.getDataList());
                });
    }

    Completable removeUserFromBlock(String apiToken, long userId) {
        return apiUtils.getSnapCarApiAppService()
                .removeUserFromBlock(HEADER, userId, apiToken);
    }

    Single<InterestRequiredApiResponse> getRequiredDataFroInterest(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getRequiredDataFroInterest(HEADER, apiToken);
    }

    Completable addInterest(String apiToken, int gearType, int specificationId, int paymentType,
                            int colorId, int mvpi, int brandId, int modelId, String priceFrom, String priceTo,
                            String year, String yearTo, int carCondition, int sellerType,
                            int nearby, int cityId, int countryId, String kmFrom, String kmTo,
                            int categoryId, int vehicle) {
//                            int bigSale
        return apiUtils.getSnapCarApiAppService()
                .addInterest(HEADER, apiToken, gearType, specificationId, paymentType, colorId, mvpi,
                        brandId, modelId, priceFrom, priceTo, year, yearTo, carCondition, sellerType,
                        nearby, cityId, countryId, kmFrom, kmTo, categoryId, vehicle);
//                        bigSale
    }

    Completable updateInterest(String apiToken, int interestId, int gearType, int specificationId, int paymentType,
                               int colorId, int mvpi, int brandId, int modelId, String priceFrom, String priceTo,
                               String year, String yearTo, int carCondition, int sellerType,
                               int nearby, int cityId, int countryId, String kmFrom, String kmTo,
                               int categoryId, int vehicle) {
        return apiUtils.getSnapCarApiAppService()
                .updateInterest(HEADER, interestId, apiToken, gearType, specificationId, paymentType, colorId, mvpi,
                        brandId, modelId, priceFrom, priceTo, year, yearTo, carCondition, sellerType,
                        nearby, cityId, countryId, kmFrom, kmTo, categoryId, vehicle);
    }

    Single<CarResourcesApiResponse> getCarResources(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getGetCarResources(HEADER, apiToken);
    }

    Single<FavoritesApiResponse> getFavorites(String apiToken, int pageNumber) {
        return apiUtils.getSnapCarApiAppService().fetchFavorites(HEADER, apiToken, pageNumber);
    }

    Single<ResponseBody> deleteUserFavorite(String apiToken, int userId) {
        return apiUtils.getSnapCarApiAppService().deleteShowroomFavorite(HEADER, apiToken, userId);
    }

    Single<ResponseBody> deleteCarFavorite(String apiToken, int carId) {
        return apiUtils.getSnapCarApiAppService().deleteCarFavorite(HEADER, apiToken, carId);
    }

    Single<ResponseBody> deleteCar(int id, String reason, String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .deleteCar(HEADER, id, reason, apiToken);
    }

    Single<CarDTO> addCar(
            String apiToken,
            int contactOption,
            int postType,
            int isTracked,
            int condition,
            int manufacturingYear,
            int importer,
            int transmission,
            int colorId,
            String kmTo,
            int warranty,
            String agentName,
            int mvpi,
            String notes,
            int modelId,
            String priceBefore,
            String priceAfter,
            String engineCapacity,
            String image,
            String installmentFrom,
            String installmentTo,
            int exchange,
            String longitude,
            String latitude,
            String examinationImage,
            int categoryId,
            int brandId,
            String price, int vehicleRegistration, int priceType) {
        return apiUtils.getSnapCarApiAppService()
                .addCar(HEADER, apiToken, contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                        colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                        installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType);
    }

    Single<ResponseBody> updateCar(
            String carId,
            String apiToken,
            int contactOption,
            int postType,
            int isTracked,
            int condition,
            int manufacturingYear,
            int importer,
            int transmission,
            int colorId,
            String kmTo,
            int warranty,
            String agentName,
            int mvpi,
            String notes,
            int modelId,
            String priceBefore,
            String priceAfter,
            String engineCapacity,
            String image,
            String installmentFrom,
            String installmentTo,
            int exchange,
            String longitude,
            String latitude,
            String examinationImage,
            int categoryId,
            int brandId,
            String price, int vehicleRegistration, int priceType) {
        return apiUtils.getSnapCarApiAppService()
                .updateCar(HEADER, carId, apiToken, contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                        colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                        installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType);
    }

    Single<MessageApiResponse> addItemToFavorite(String apiToken, String carId, String userId) {
        return apiUtils.getSnapCarApiAppService()
                .addItemToFavorite(HEADER, apiToken, carId, userId);
    }

    Completable hotzoneLocationClicked(String apiToken, int hotzoneId) {
        return apiUtils.getSnapCarApiAppService()
                .hotzoneLocationClicked(HEADER, hotzoneId, apiToken);
    }

    Single<FilterApiResponse> filterHome(String apiToken,
                                         int carModelId,
                                         int brandId,
                                         int categoryId,
                                         int showAll,
                                         int menCar,
                                         int womenCar,
                                         int damagedCar,
                                         int vipShowRoom,
                                         int showRoom,
                                         int vipHotZone,
                                         int hotZone,
                                         double longitude,
                                         double latitude) {
        return apiUtils.getSnapCarApiAppService()
                .filterHome(
                        HEADER,
                        apiToken,
                        carModelId,
                        brandId,
                        categoryId,
                        showAll,
                        menCar,
                        womenCar,
                        damagedCar,
                        vipShowRoom,
                        showRoom,
                        vipHotZone,
                        hotZone,
                        longitude,
                        latitude);
    }

    Single<FilterApiResponse> fetchUntrackedCars(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .fetchUnTrackedCar(HEADER, apiToken);
    }

    Single<Triple<Integer, Boolean, CarSearchDTO>>
    fetchCarSearch(String apiToken,
                   int brandId,
                   int modelId,
                   int categoryId,
                   int gender,
                   String yearFrom,
                   String yearTo,
                   String priceFrom,
                   String priceTo,
                   String longitude,
                   String latitude,
                   int pageNumber) {
        return apiUtils.getSnapCarApiAppService()
                .fetchCarSearch(HEADER, apiToken, brandId, modelId, categoryId, gender, yearFrom, yearTo, priceFrom, priceTo, longitude, latitude, pageNumber)
                .map(carSearchDTO -> {
                    int currentPage = carSearchDTO.getCars().getCurrentPage();
                    boolean canLoadMore = carSearchDTO.getCars().getCurrentPage() < carSearchDTO.getCars().getLastPage();
                    return new Triple<>(currentPage, canLoadMore, carSearchDTO);
                });
    }

    Single<Triple<Integer, Boolean, CarSearchDTO>>
    fetchAdvancedSearch(String apiToken,
                        int brandId,
                        int modelId,
                        int categoryId,
                        int gender,
                        String yearFrom,
                        String yearTo,
                        String priceFrom,
                        String priceTo,
                        String longitude,
                        String latitude,
                        int pageNumber,
                        int warranty,
                        int engineCapacity,
                        int color_id,
                        String kilometer_from,
                        String kilometer_to,
                        int gear_type,
                        int car_status,
                        int tracked,
                        int postType,
                        int installment,
                        int bigSale) {
        return apiUtils.getSnapCarApiAppService()
                .fetchAdvancedCarSearch(HEADER, apiToken, brandId, modelId, categoryId, gender, yearFrom, yearTo, priceFrom, priceTo, longitude, latitude, pageNumber,
                        warranty, engineCapacity, color_id, kilometer_from, kilometer_to, gear_type, car_status, tracked, postType, installment, bigSale)
                .map(carSearchDTO -> {
                    int currentPage = carSearchDTO.getCars().getCurrentPage();
                    boolean canLoadMore = carSearchDTO.getCars().getCurrentPage() < carSearchDTO.getCars().getLastPage();
                    return new Triple<>(currentPage, canLoadMore, carSearchDTO);
                });
    }

    public Single<FilterApiResponse>
    filterExploreList(String apiToken,
                      int carModelId,
                      int brandId,
                      int categoryId,
                      int showAll,
                      int menCar,
                      int womenCar,
                      int damagedCar,
                      int vipShowRoom,
                      int showRoom,
                      int vipHotZone,
                      int hotZone,
                      double longitude,
                      double latitude) {
        return apiUtils.getSnapCarApiAppService()
                .filterExplore(
                        HEADER,
                        apiToken,
                        carModelId,
                        brandId,
                        categoryId,
                        showAll,
                        menCar,
                        womenCar,
                        damagedCar,
                        vipShowRoom,
                        showRoom,
                        vipHotZone,
                        hotZone,
                        longitude,
                        latitude);
    }

    Single<Triple<Integer, Boolean, List<CarApiResponse>>>
    fetchMyInterest(String apiToken, int pageNumber) {
        return apiUtils
                .getSnapCarApiAppService()
                .fetchMyInterests(HEADER, apiToken, pageNumber)
                .map(interestDTO -> {
                    int currentPage = interestDTO.getCurrentPage();
                    boolean canLoadMore = interestDTO.getCurrentPage() < interestDTO.getLastPage();
                    return new Triple<>(currentPage, canLoadMore, interestDTO.getDataList());
                });
    }

    Completable removeUserFromInterest(String apiToken, int interestId) {
        return apiUtils.getSnapCarApiAppService()
                .removeInterest(HEADER, interestId, apiToken);
    }

    Single<MyCarsApiResponse> fetchMyCars(String apiToken, int pageNumber) {
        return apiUtils.getSnapCarApiAppService().fetchMyCars(HEADER, apiToken, pageNumber);
    }

    Single<DiscountApiResponse> fetchCoupons(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .fetchCoupons(HEADER, apiToken);
    }

    Single<CarVisitorDTO> fetchVisitorCar(String apiToken, int userId, int brandId, int modelId, int categoryId,
                                          String yearFrom, String yearTo, String priceFrom, String priceTo, String longitude,
                                          String latitude) {
        return apiUtils.getSnapCarApiAppService()
                .fetchVisitorCars(HEADER, apiToken, userId, brandId, modelId, categoryId,
                        yearFrom, yearTo, priceFrom, priceTo, longitude,
                        latitude);
    }

    Single<SettingsApiResponse> getSittings() {
        return apiUtils.getSnapCarApiAppService().getSittings(HEADER);
    }

    Completable updateCarLocation(String apiToken,
                                  String carId,
                                  String lon,
                                  String lat) {
        return apiUtils.getSnapCarApiAppService()
                .updateCarLocation(HEADER, apiToken, carId, lon, lat);
    }

    Completable updateUserLocation(String apiToken,
                                   String lon,
                                   String lat) {
        return apiUtils.getSnapCarApiAppService()
                .updateUserLocation(HEADER, apiToken, lon, lat);
    }

    Single<CarDTO> getExpiredCars(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getExpiredCars(HEADER, apiToken);
    }

    Single<CarApiResponse> renwCar(String carId, String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .renewCar(HEADER, carId, apiToken);
    }

    Single<ResponseBody> requestVIP(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .requestVIP(HEADER, apiToken);
    }

    Single<ResponseBody> requestAds(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .requestAds(HEADER, apiToken);
    }

    Single<ResponseBody> requestHotZone(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .requestHotZone(HEADER, apiToken);
    }

    Single<List<PaymentHistoryApiResponse>> getPaymentHistory(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getPaymentHistory(HEADER, apiToken);
    }

    Single<FeedbackApiResponse> getFeedBackSubjects(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .getFeedbackSubjects(HEADER, apiToken);
    }

    Single<ResponseBody> postfeedback(RequestBody apiToken, RequestBody categoryId, RequestBody details, MultipartBody.Part attachment, RequestBody subj) {
        return apiUtils.getSnapCarApiAppService()
                .postFeedback(HEADER, apiToken, categoryId, details, attachment, subj);
    }

    Single<SurveyApiResponse> fetchSurvies(String apiToken) {
        return apiUtils.getSnapCarApiAppService().fetchSurvies(HEADER, apiToken);
    }

    Single<ResponseBody> postSurvey(String apiToken, String surveyId, String answers) {
        return apiUtils.getSnapCarApiAppService().postSurvey(HEADER, apiToken, surveyId, answers);
    }

    Single<ResponseBody> postCanceldSurvey(String apiToken, String surveyId) {
        return apiUtils.getSnapCarApiAppService().postCanceledSurvey(HEADER, apiToken, surveyId);
    }

    Single<ResponseBody> sendVerifiyLetter(RequestBody apiToken, MultipartBody.Part attachment) {
        return apiUtils.getSnapCarApiAppService()
                .sendVerfiyLetter(HEADER, apiToken, attachment);
    }

    Single<ResponseBody> updatePassword(String apiToken, String oldPass, String newPass) {
        return apiUtils.getSnapCarApiUserService().updatePassword(HEADER, apiToken, oldPass, newPass);
    }


    Single<ResponseBody> skipPayment(String carId, String apiToken , String commition , String totalPrice) {
        return apiUtils.getSnapCarApiAppService()
                .skipPayment(HEADER, carId, apiToken ,commition , totalPrice );
    }

    Single<List<ChatApiResponse>> fetchInboxList(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .fetchInboxList(HEADER, apiToken);
    }

    Completable deleteChat(String apiToken,int chatId) {
        return apiUtils.getSnapCarApiAppService()
                .deleteChat(chatId,HEADER, apiToken);
    }

    Single<ResponseBody> fetchNotifications(String apiToken) {
        return apiUtils.getSnapCarApiUserService()
                .fetchMyNotifications(HEADER, apiToken);
    }

    Single<ResponseBody> changeLanguage(String apiToken, String language) {
        return apiUtils.getSnapCarApiUserService()
                .changeLanguage(HEADER, apiToken, language);
    }

    Completable updateSeen(String apiToken, String notificationId) {
        return apiUtils.getSnapCarApiUserService()
                .updateSeen(HEADER, notificationId, apiToken);
    }

    Single<ChatApiResponse> startChat(String apiToken, int userId) {
        return apiUtils.getSnapCarApiAppService()
                .startChat(HEADER, userId, apiToken);
    }

    Completable reportUser(String apiToken, int userId, String reason) {
        return apiUtils.getSnapCarApiAppService()
                .reportUser(HEADER, apiToken, userId, reason);
    }

    Completable manageNotification(String apiToken, String notificationSettingBody) {
        return apiUtils.getSnapCarApiAppService()
                .manageNotification(HEADER, apiToken, notificationSettingBody);
    }

    Completable postContactUs(String name, String email, String message) {
        return apiUtils.getSnapCarApiAppService()
                .postContactUs(name, email, message);
    }

    Completable termsAgreed(String apiToken, String terms) {
        return apiUtils.getSnapCarApiAppService()
                .termsAgreed(HEADER, apiToken, terms);
    }

    Completable logout(String apiToken) {
        return apiUtils.getSnapCarApiAppService()
                .logout(HEADER, apiToken);
    }

    Completable getDirection(String apiToken, int hotZoneId) {
        return apiUtils.getSnapCarApiAppService()
                .getDirection(HEADER, hotZoneId, apiToken);
    }
}