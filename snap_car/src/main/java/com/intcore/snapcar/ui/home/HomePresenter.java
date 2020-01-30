package com.intcore.snapcar.ui.home;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.car.CarDTO;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.explore.ExploreItem;
import com.intcore.snapcar.store.model.explore.ExploreMapper;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.filter.FilterMapper;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.ui.addcar.CashedCar;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import timber.log.Timber;

@FragmentScope
class HomePresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final InterestRequiredMapper interestRequiredMapper;
    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ExploreMapper exploreMapper;
    private final ResourcesUtil resourcesUtil;
    private final Context context;
    private final FilterMapper filterMapper;
    private final UserManager userManager;
    private final HomeScreen homeScreen;
    private final HomeRepo homeRepo;
    private final UserRepo userRepo;
    private final PreferencesUtil preferencesUtil;
    private final JopDispatcher jopDispatcher;
    private Location lastLocation;
    private CashedCar car;
    private SettingsModel settingsModel;

    @Inject
    HomePresenter(InterestRequiredMapper interestRequiredMapper,
                  @IOThread ThreadSchedulers threadSchedulers,
                  @ForFragment CompositeDisposable disposable,
                  PreferencesUtil preferencesUtil,
                  ExploreMapper exploreMapper,
                  ResourcesUtil resourcesUtil,
                  @ForFragment Context context,
                  FilterMapper filterMapper,
                  UserManager userManager,
                  HomeScreen homeScreen,
                  UserRepo userRepo,
                  HomeRepo homeRepo,
                  JopDispatcher jopDispatcher) {
        this.context = context;
        this.jopDispatcher = jopDispatcher;
        this.interestRequiredRelay = BehaviorRelay.createDefault(InterestRequiredViewModel.createDefault());
        this.selectedCategoryModel = BehaviorRelay.createDefault(CategoryViewModel.createDefault());
        this.selectedBrandModel = BehaviorRelay.createDefault(BrandsViewModel.createDefault());
        this.selectedModelModel = BehaviorRelay.createDefault(ModelViewModel.createDefault());
        this.interestRequiredMapper = interestRequiredMapper;
        this.threadSchedulers = threadSchedulers;
        this.exploreMapper = exploreMapper;
        this.resourcesUtil = resourcesUtil;
        this.filterMapper = filterMapper;
        this.userManager = userManager;
        this.disposable = disposable;
        this.homeScreen = homeScreen;
        this.homeRepo = homeRepo;
        this.userRepo = userRepo;
        this.preferencesUtil = preferencesUtil;
    }

    void onViewCreated() {
        homeScreen.initializeFragment();
        fetchCountryList();
        fetchSwearData();
        checkValidity();
        checkSurvey();
        checkIfCashedCar();
        showTerms();
    }


    private void showTerms() {
        if (userManager.sessionManager().isSessionActive()) {
            if (userManager.getCurrentUser().getTermsAgreed() != null) {
                if (userManager.getCurrentUser().getTermsAgreed().equals("0")) {
                    homeScreen.showTermsDialog();
                }
            }
        }
    }

    private void fetchSwearData() {
        disposable.add(userRepo.getSittings()
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(this::setSwearData, this::processError));
    }

    private void setSwearData(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }

    private void checkIfCashedCar() {
        if (userManager.sessionManager().isSessionActive()) {
            if (preferencesUtil.getString("skippedCar") != null) {
                car = new Gson().fromJson(preferencesUtil.getString("skippedCar"), CashedCar.class);
                if (userManager.getCurrentUser().getType() == 2) {
                    if (resourcesUtil.isEnglish()) {
                        homeScreen.showErrorMessage("Sorry, Your Car is not added");
                    } else {
                        homeScreen.showErrorMessage("عذراً ، لم يتم إضافة سيارتك");
                    }
                    preferencesUtil.delete("skippedCar");
                    return;
                }
                disposable.add(userRepo.addCar(userManager.getCurrentUser().getApiToken(), car.getContactOption(), car.getPostType(), car.getIsTracked()
                        , car.getCondition(), car.getManufacturingYear(), car.getImporter(), car.getTransmission(),
                        car.getColorId(), car.getKmTo(), car.getWarranty(), car.getAgentName(), car.getMvpi(), car.getNotes(), car.getModelId(), car.getPriceBefore()
                        , car.getPriceAfter(), car.getEngineCapacity(), car.getImage(),
                        car.getInstallmentFrom(), car.getInstallmentTo(), car.getExchange(), car.getLongitude(), car.getLatitude(), car.getExaminationImage(), car.getCategoryId(), car.getBrandId(), car.getPrice(), car.getVehicleRegistration(), car.getPriceType())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> homeScreen.showLoadingAnimation())
                        .doFinally(homeScreen::hideLoadingAnimation)
                        .subscribe(this::cashedCarAdded, this::processError));
            }
        }
    }

    private void cashedCarAdded(CarDTO carDTO) {
        preferencesUtil.delete("skippedCar");
        if (resourcesUtil.isEnglish()) {
            homeScreen.showSuccessMessage("Car added successfully");
        } else {
            homeScreen.showSuccessMessage("تم إضافة السيارة بنجاح");
        }
        if (car.getIsTracked() == 1) {
            FirebaseJobDispatcher dispatcher = jopDispatcher.getInstance();
            Job job = jopDispatcher.createJob(dispatcher);
            dispatcher.schedule(job);
        }
        if (car.getPostType() == 2) {
            homeScreen.showSwearDialog(settingsModel, carDTO.getInsertedCar().getId());
        }
    }

    private void fetchCountryList() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(homeRepo.fetchRequiredDataForInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredViewModel)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(interestRequiredRelay, this::processError));
    }

    List<BrandsViewModel> getBrandsList() {
        List<BrandsViewModel> brandsModels = new ArrayList<>();
        brandsModels.add(BrandsViewModel.createDefault());
        brandsModels.addAll(interestRequiredRelay.getValue().getBrandsViewModels());
        return brandsModels;
    }

    BrandsViewModel getSelectedBrandModel() {
        return selectedBrandModel.getValue();
    }

    void setSelectedBrandModel(BrandsViewModel brandModel) {
        selectedBrandModel.accept(brandModel);
    }

    ModelViewModel getSelectedModelModel() {
        return selectedModelModel.getValue();
    }

    void setSelectedModelModel(ModelViewModel brandModel) {
        selectedModelModel.accept(brandModel);
    }

    CategoryViewModel getSelectedCategoryModel() {
        return selectedCategoryModel.getValue();
    }

    void setSelectedCategoryModel(CategoryViewModel brandModel) {
        selectedCategoryModel.accept(brandModel);
    }

    void processError(Throwable throwable) {

        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                homeScreen.processLogout();
            } else {
                homeScreen.showErrorMessage(getHttpErrorMessage(
                        HttpException.wrapJakewhartonException(
                                (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            }
        } else if (throwable instanceof IOException) {
            homeScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            homeScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    public void onNearByFilterClicked(int showAll,
                                      int menCar,
                                      int womenCar,
                                      int damagedCar,
                                      int vipShowRoom,
                                      int showRoom,
                                      int vipHotZone,
                                      int hotZone,
                                      OperationListener<FilterApiResponse> operationListener) {
        saveOrUpdateFilter(showAll, menCar, womenCar, damagedCar, vipShowRoom, showRoom, vipHotZone, hotZone);
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        if (selectedBrandModel.getValue() != null &&
                selectedModelModel.getValue() != null &&
                selectedCategoryModel.getValue() != null &&
                lastLocation != null) {
            if (showAll == 0 && menCar == 0 && womenCar == 0 && damagedCar == 0 && vipHotZone == 0
                    && hotZone == 0 && showRoom == 0 && vipShowRoom == 0) {
                if (isEnglish()) {
                    homeScreen.showWarningMessage("You should select one option at least!");
                } else {
                    homeScreen.showWarningMessage("فضلاً تحديد خيار واحد على الأقل");
                }
                return;
            }
            disposable.add(homeRepo.filterHome(apiToken,
                    selectedBrandModel.getValue().getId(),
                    selectedModelModel.getValue().getId(),
                    selectedCategoryModel.getValue().getId(),
                    showAll,
                    menCar,
                    womenCar,
                    damagedCar,
                    vipShowRoom,
                    showRoom,
                    vipHotZone,
                    hotZone,
                    lastLocation.getLongitude(),
                    lastLocation.getLatitude())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(operationListener::onSuccess, operationListener::onError));
        }
    }

    private void saveOrUpdateFilter(int showAll,
                                    int menCar,
                                    int womenCar,
                                    int damagedCar,
                                    int vipShowRoom,
                                    int showRoom,
                                    int vipHotZone,
                                    int hotZone) {
        preferencesUtil.saveOrUpdateInt("showAll", showAll);
        preferencesUtil.saveOrUpdateInt("menCar", menCar);
        preferencesUtil.saveOrUpdateInt("womenCar", womenCar);
        preferencesUtil.saveOrUpdateInt("damagedCar", damagedCar);
        preferencesUtil.saveOrUpdateInt("vipShowRoom", vipShowRoom);
        preferencesUtil.saveOrUpdateInt("showRoom", showRoom);
        preferencesUtil.saveOrUpdateInt("vipHotZone", vipHotZone);
        preferencesUtil.saveOrUpdateInt("hotZone", hotZone);
    }

    private void saveOrUpdateFilterNearBy(int showAll,
                                          int menCar,
                                          int womenCar,
                                          int damagedCar,
                                          int vipShowRoom,
                                          int showRoom,
                                          int vipHotZone,
                                          int hotZone) {
        preferencesUtil.saveOrUpdateInt("showAllNearBy", showAll);
        preferencesUtil.saveOrUpdateInt("menCarNearBy", menCar);
        preferencesUtil.saveOrUpdateInt("womenCarNearBy", womenCar);
        preferencesUtil.saveOrUpdateInt("damagedCarNearBy", damagedCar);
        preferencesUtil.saveOrUpdateInt("vipShowRoomNearBy", vipShowRoom);
        preferencesUtil.saveOrUpdateInt("showRoomNearBy", showRoom);
        preferencesUtil.saveOrUpdateInt("vipHotZoneNearBy", vipHotZone);
        preferencesUtil.saveOrUpdateInt("hotZoneNearBy", hotZone);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onExploreFilterClicked(int showAll,
                                       int menCar,
                                       int womenCar,
                                       int damagedCar,
                                       int vipShowRoom,
                                       int showRoom,
                                       int vipHotZone,
                                       int hotZone,
                                       OperationListener<List<ExploreItem>> operationListener) {
        saveOrUpdateFilterNearBy(showAll, menCar, womenCar, damagedCar, vipShowRoom, showRoom, vipHotZone, hotZone);
        int brandId = 0, modelId = 0, categoryId = 0;
        if (getSelectedBrandModel() != null) {
            brandId = getSelectedBrandModel().getId();
        }
        if (getSelectedModelModel() != null) {
            modelId = getSelectedModelModel().getId();
        }
        if (getSelectedCategoryModel() != null) {
            categoryId = getSelectedCategoryModel().getId();
        }
        if (showAll == 0 && menCar == 0 && womenCar == 0 && damagedCar == 0 && vipHotZone == 0
                && hotZone == 0 && showRoom == 0 && vipShowRoom == 0) {
            if (isEnglish()) {
                homeScreen.showWarningMessage("You should select one option at least!");
            } else {
                homeScreen.showWarningMessage("فضلاً تحديد خيار واحد على الأقل");
            }
            return;
        }
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        if (lastLocation != null) {
            disposable.add(homeRepo.filterExploreList(
                    apiToken,
                    modelId,
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
                    lastLocation.getLongitude(),
                    lastLocation.getLatitude())
                    .map(filterMapper::toFilterViewModel)
                    .map(filterViewModel -> exploreMapper.toExploreItems(
                            filterViewModel.getFeatureCarList(),
                            filterViewModel.getHotZoneList(),
                            filterViewModel.getShowRoomsList(),
                            filterViewModel.getCarList(),
                            filterViewModel.getAdsList()))
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(operationListener::onSuccess, operationListener::onError));
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    private void checkValidity() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getExpiredCars(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(this::checkExpiredCars, this::processError));
        }
    }

    private void checkSurvey() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchSurvies(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(this::checkSurviesIsFound, this::processError));
        }
    }

    private void checkSurviesIsFound(SurveyApiResponse surveyApiResponse) {
        if (surveyApiResponse.getSurvies() != null) {
            if (surveyApiResponse.getSurvies().size() != 0) {
                homeScreen.showSurveyDialog(surveyApiResponse.getSurvies().get(0));
            }
        }
    }

    private void checkExpiredCars(CarDTO carDTO) {
        if (carDTO.getCars() != null) {
            if (carDTO.getCars().size() != 0)
                homeScreen.showCarExpiredDialog(carDTO.getCars().get(0));
        }
    }

    public void renewCar(Integer id) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.renewCar(String.valueOf(id), userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(this::isRenewed, this::processError));
        }
    }

    private void isRenewed(CarApiResponse carApiResponse) {
    }

    public void markCarSold(Integer id) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getSittings()
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> homeScreen.showLoadingAnimation())
                    .doFinally(homeScreen::hideLoadingAnimation)
                    .subscribe(homeScreen::setSwearData, this::processError));
        }
    }

    public void postCanceledSurvey(int id) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.postCanceledSurvey(userManager.getCurrentUser().getApiToken(), String.valueOf(id))
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> homeScreen.showLoadingAnimation())
                    .doFinally(homeScreen::hideLoadingAnimation)
                    .subscribe(this::surveyIsSent, this::processError));
        }
    }

    void onDestroy() {
        disposable.clear();
    }

    private void surveyIsSent(ResponseBody responseBody) {
    }

}