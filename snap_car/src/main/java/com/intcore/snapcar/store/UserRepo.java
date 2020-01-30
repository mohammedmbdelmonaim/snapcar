package com.intcore.snapcar.store;

import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.car.CarModel;
import com.intcore.snapcar.store.model.carresource.CarResourcesMapper;
import com.intcore.snapcar.store.model.carresource.CarResourcesModel;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.country.CountryModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.store.model.feedback.FeedbackApiResponse;
import com.intcore.snapcar.store.model.mycars.MyCarsApiResponse;
import com.intcore.snapcar.store.model.paymenthistory.PaymentHistoryApiResponse;
import com.intcore.snapcar.store.model.settings.SettingsMapper;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.store.model.visitor.VisitorMapper;
import com.intcore.snapcar.store.model.visitor.VisitorModel;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.chat.ChatMapper;
import com.intcore.snapcar.core.chat.ChatModel;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@ActivityScope
public class UserRepo {

    private final InterestRequiredMapper interestRequiredMapper;
    private final DefaultUserMapper defaultUserMapper;
    private final WebServiceStore webServiceStore;
    private final VisitorMapper visitorMapper;
    private final CountryMapper countryMapper;
    private final UserManager userManager;
    private final ChatMapper chatMapper;
    private final CarMapper carMapper;
    private final SettingsMapper settingsMapper;
    private boolean canLoadMoreBlockList = true;
    private int currentBlockListPage = 0;
    private final CarResourcesMapper carResourcesMapper;

    @Inject
    UserRepo(InterestRequiredMapper interestRequiredMapper,
             WebServiceStore webServiceStore, DefaultUserMapper
                     defaultUserMapper, VisitorMapper visitorMapper,
             CountryMapper countryMapper, UserManager userManager,
             ChatMapper chatMapper, CarMapper carMapper,
             SettingsMapper settingsMapper, CarResourcesMapper carResourcesMapper) {
        this.interestRequiredMapper = interestRequiredMapper;
        this.defaultUserMapper = defaultUserMapper;
        this.webServiceStore = webServiceStore;
        this.visitorMapper = visitorMapper;
        this.countryMapper = countryMapper;
        this.userManager = userManager;
        this.chatMapper = chatMapper;
        this.carMapper = carMapper;
        this.carResourcesMapper = carResourcesMapper;
        this.settingsMapper = settingsMapper;
    }

    public Single<String> validatePhone(String phone,String lang) {
        return webServiceStore.validatePhone(phone,lang)
                .map(responseBody -> new JSONObject(responseBody.string()))
                .map(jsonObject -> jsonObject.getString("code"));
    }

    public Single<DefaultUserModel> updateLocation(String ApiToken, int country, int city, String area, String lon, String lat) {
        return webServiceStore.updateLocation(ApiToken, country, city, area, lon, lat)
                .doOnSuccess(userManager::updateProfile)
                .map(defaultUserDataApiResponse -> defaultUserMapper.
                        toModel(defaultUserDataApiResponse.getDefaultUserApiResponse()));
    }

    public Single<List<CountryModel>> fetchCountryList() {
        return webServiceStore.fetchCountryList()
                .map(countryDataApiResponse -> countryMapper.toCountryModels(countryDataApiResponse.getCountryList()));
    }

    public Single<DefaultUserModel> fetchUserProfile(String ApiToken) {
        return webServiceStore.fetchUserProfile(ApiToken)
                .doOnSuccess(userManager::updateProfile)
                .map(defaultUserDataApiResponse -> defaultUserMapper.
                        toModel(defaultUserDataApiResponse.getDefaultUserApiResponse()));
    }

    public Single<VisitorModel> fetchVisitorProfile(String ApiToken, long userId) {
        return webServiceStore.fetchVisitorProfile(ApiToken, userId)
                .map(visitorMapper::toVisitorModel);
    }

    public Completable updateUser(String ApiToken, String name, String email, int country, int city, String area, String image) {
        return webServiceStore.updateUser(ApiToken, name, email, country, city, area, image)
                .doOnSuccess(userManager::updateProfile)
                .toCompletable();
    }

    public Completable updateUserPhone(String ApiToken, String phone, String code) {
        return webServiceStore.updateUserPhone(ApiToken, phone, code)
                .doOnSuccess(userManager::updateProfile)
                .toCompletable();
    }

    public Single<DefaultUserModel> updateShowRoom(String ApiToken, String name, String email, String from, String to, String fromTwo, String toTwo, String image, String dealingValue) {
        return webServiceStore.updateShowRoom(ApiToken, name, email, from, to, fromTwo, toTwo, image, dealingValue)
                .doOnSuccess(userManager::updateProfile)
                .map(defaultUserDataApiResponse -> defaultUserMapper.
                        toModel(defaultUserDataApiResponse.getDefaultUserApiResponse()));
    }

    public Single<DefaultUserModel> updateShowRoomPhones(String ApiToken, String mainPhone, String phones) {
        return webServiceStore.updateShowRoomPhones(ApiToken, mainPhone, phones)
                .doOnSuccess(userManager::updateProfile)
                .map(defaultUserDataApiResponse -> defaultUserMapper.
                        toModel(defaultUserDataApiResponse.getDefaultUserApiResponse()));
    }

    public Single<DefaultUserModel> updateUserPhone(String ApiToken, String mainPhone) {
        return webServiceStore.updatUserPhonee(ApiToken, mainPhone)
                .doOnSuccess(userManager::updateProfile)
                .map(defaultUserDataApiResponse -> defaultUserMapper.
                        toModel(defaultUserDataApiResponse.getDefaultUserApiResponse()));
    }

    public Single<String> uploadFile(String apiToken, File image, RequestBodyProgress.UploadCallbacks uploadCallbacks) {
        return webServiceStore.uploadFile(apiToken, image, uploadCallbacks)
                .map(ResponseBody::string)
                .map(s -> new JSONObject(s).getString("path"));
    }

    public Completable blockUser(String ApiToken, long userId, String reason) {
        return webServiceStore.blockUser(ApiToken, userId, reason)
                .map(ResponseBody::string)
                .toCompletable();
    }

    public Completable rateUser(String apiToken, long userId, int rate, String note) {
        return webServiceStore.rateUser(apiToken, userId, rate, note);
    }

    public Single<CarResourcesModel> getCarResources(String ApiToken) {
        return webServiceStore.getCarResources(ApiToken).map(carResourcesMapper::toModel);
    }

    public Single<FavoritesApiResponse> getFavorites(String ApiToken) {
        if (canLoadMoreBlockList) {
            return webServiceStore.getFavorites(ApiToken, currentBlockListPage + 1);
        } else {
            return null;
        }
    }

    public Single<ResponseBody> deleteCarFavorite(String ApiToken, int carId) {
        return webServiceStore.deleteCarFavorite(ApiToken, carId);
    }

    public Single<ResponseBody> deleteUserFavorite(String ApiToken, int userId) {
        return webServiceStore.deleteUserFavorite(ApiToken, userId);
    }

    public Single<CarDTO> getCar(int id, String ApiToken) {
        return webServiceStore.getCar(id, ApiToken);
    }

    public Single<ResponseBody> deleteCar(int id, String reason, String apiToken) {
        return webServiceStore.deleteCar(id, reason, apiToken);
    }

    public Single<CarDTO> addCar(String apiToken,
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
                                 String price,
                                 int vehicleRegistration,
                                 int priceType) {
        return webServiceStore.addCar(apiToken, contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType);
    }

    public Single<ResponseBody> updateCar(String carId, String apiToken,
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
                                          String price,
                                          int vehicleRegistration, int priceType) {
        return webServiceStore.updateCar(carId, apiToken, contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType);
    }

    public Single<InterestRequiredModel> fetchRequiredDataForInterest(String apiToken) {
        return webServiceStore.getRequiredDataFroInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredModel);
    }

    public Single<List<CarModel>> fetchCarVisitor(String apiToken, long userId,
                                                  int brandId, int modelId,
                                                  int categoryId, String yearFrom,
                                                  String yearTo, String priceFrom,
                                                  String priceTo, String longitude,
                                                  String latitude) {
        return webServiceStore.fetchVisitorCar(apiToken, (int) userId, brandId, modelId, categoryId,
                yearFrom, yearTo, priceFrom, priceTo, longitude, latitude)
                .map(carVisitorDTO -> carMapper.toCarModels(carVisitorDTO.getCarApiResponses()));
    }


    public Single<MyCarsApiResponse> fetchMyCars(String ApiToken) {
        if (canLoadMoreBlockList) {
            return webServiceStore.fetchMyCars(ApiToken, currentBlockListPage + 1);
        } else {
            MyCarsApiResponse c = new MyCarsApiResponse();
            return null;
        }
    }

    public Single<SettingsModel> getSittings() {
        return webServiceStore.getSittings()
                .map(settingsMapper::toModel);
    }

    public Single<CarDTO> getExpiredCars(String apiToken) {
        return webServiceStore.getExpiredCars(apiToken);
    }

    public Single<CarApiResponse> renewCar(String carId, String apiToken) {
        return webServiceStore.renwCar(carId, apiToken);
    }

    public Single<ResponseBody> requestVIP(String apiToken) {
        return webServiceStore.requestVIP(apiToken);
    }

    public Single<ResponseBody> requestAds(String apiToken) {
        return webServiceStore.requestAds(apiToken);
    }

    public Single<ResponseBody> requestHotZone(String apiToken) {
        return webServiceStore.requestHotZone(apiToken);
    }

    public Single<List<PaymentHistoryApiResponse>> getPaymentHistory(String apiToken) {
        return webServiceStore.getPaymentHistory(apiToken);
    }

    public Single<FeedbackApiResponse> getFeedbacksubjects(String apiToken) {
        return webServiceStore.getFeedBackSubjects(apiToken);
    }

    public Single<ResponseBody> postFeedback(String apiToken, String categoryId, String details, File attachment, String subject) {
        MultipartBody.Part ImgBody = null;
        if (attachment != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), attachment);
            ImgBody = MultipartBody.Part.createFormData("attachment", attachment.getName(), reqFile);
        }
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), categoryId);
        RequestBody det = RequestBody.create(MediaType.parse("text/plain"), details);
        RequestBody subj = RequestBody.create(MediaType.parse("text/plain"), subject);
        RequestBody api = RequestBody.create(MediaType.parse("text/plain"), apiToken);
        return webServiceStore.postfeedback(api, category, det, ImgBody, subj);
    }

    public Single<SurveyApiResponse> fetchSurvies(String ApiToken) {
        return webServiceStore.fetchSurvies(ApiToken);
    }

    public Single<ResponseBody> postSurvey(String ApiToken, String surveyId, String answer) {
        return webServiceStore.postSurvey(ApiToken, surveyId, answer);
    }

    public Single<ResponseBody> postCanceledSurvey(String ApiToken, String surveyId) {
        return webServiceStore.postCanceldSurvey(ApiToken, surveyId);
    }

    public Single<ResponseBody> sendVerfiyLetter(String apiToken, File attachment) {
        MultipartBody.Part ImgBody = null;
        if (attachment != null) {
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), attachment);
            ImgBody = MultipartBody.Part.createFormData("verify_latter", attachment.getName(), reqFile);
        }
        RequestBody api = RequestBody.create(MediaType.parse("text/plain"), apiToken);
        return webServiceStore.sendVerifiyLetter(api, ImgBody);
    }

    public Single<ResponseBody> updatePassword(String ApiToken, String oldPass, String newPass) {
        return webServiceStore.updatePassword(ApiToken, oldPass, newPass);
    }

    public Single<ChatModel> startChat(String apiToken, long userId) {
        return webServiceStore.startChat(apiToken, (int) userId)
                .map(chatMapper::toChatEntity)
                .map(chatMapper::toChatModel);
    }

    public Single<ResponseBody> skipPayment(String carId, String apiToken , String commition , String totalPrice) {
        return webServiceStore.skipPayment(carId, apiToken , commition , totalPrice);
    }

    public Completable reportUser(String apiToken, int userId, String reason) {
        return webServiceStore.reportUser(apiToken, userId, reason);
    }

    public Completable manageNotification(String apiToken, String notificationSettingBody) {
        return webServiceStore.manageNotification(apiToken, notificationSettingBody);
    }

    public Completable postContactUs(String name, String email, String message) {
        return webServiceStore.postContactUs(name, email, message);
    }

    public Single<ResponseBody> changeLanguage(String apiToken, String language) {
        return webServiceStore.changeLanguage(apiToken, language);
    }

    public Completable termsAgreed(String apiToken, String terms) {
        return webServiceStore.termsAgreed(apiToken, terms);
    }

    public Completable logout(String apiToken) {
        return webServiceStore.logout(apiToken);
    }
}