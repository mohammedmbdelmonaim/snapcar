package com.intcore.snapcar.ui.addcar;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.backgroundServices.JopDispatcher;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.api.RequestBodyProgress;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.carresource.CarResourcesMapper;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

@ActivityScope
class AddCarPresenter extends BaseActivityPresenter {

    private final FirebaseJobDispatcher firebaseJobDispatcher;
    private final ThreadSchedulers threadSchedulers;
    private final LocationCallback locationCallback;
    private final PreferencesUtil preferencesUtil;
    private final CompositeDisposable disposable;
    private final PermissionUtil permissionUtil;
    private final UserManager userManager;
    private final AddCarScreen screen;
    private final UserRepo userRepo;
    private final Context context;
    private final Job job;
    private final CarResourcesMapper carResourcesMapper;
    private final BehaviorRelay<List<BrandsViewModel>> brandsViewModelBehaviorRelay;
    private final BehaviorRelay<List<CarColorViewModel>> carColorViewModelBehaviorRelay;
    private final BehaviorRelay<List<ImporterViewModel>> importerViewModelBehaviorRelay;
    private final BehaviorRelay<Integer> hasTrackedCar;
    private final BehaviorRelay<Integer> publishedCars;
    private final BehaviorRelay<Integer> availableCarToAdd;
    private List<String> conditions = new ArrayList<>();
    private int currentExaminationPosition;

    @Inject
    AddCarPresenter(@IOThread ThreadSchedulers threadSchedulers,
                    @ForActivity CompositeDisposable disposable,
                    PreferencesUtil preferencesUtil,
                    PermissionUtil permissionUtil,
                    @ForActivity Context context,
                    UserManager userManager,
                    CarResourcesMapper carResourcesMapper,
                    AddCarScreen screen,
                    UserRepo userRepo) {
        super(screen);
        this.screen = screen;
        this.context = context;
        this.userRepo = userRepo;
        this.disposable = disposable;
        this.userManager = userManager;
        this.permissionUtil = permissionUtil;
        this.preferencesUtil = preferencesUtil;
        this.threadSchedulers = threadSchedulers;
        this.locationCallback = createLocationCallback();
        this.carResourcesMapper = carResourcesMapper;
        JopDispatcher jopDispatcher = new JopDispatcher(context);
        this.firebaseJobDispatcher = jopDispatcher.getInstance();
        this.job = jopDispatcher.createJob(firebaseJobDispatcher);
        this.carColorViewModelBehaviorRelay = BehaviorRelay.createDefault(new ArrayList<>());
        this.importerViewModelBehaviorRelay = BehaviorRelay.createDefault(new ArrayList<>());
        this.brandsViewModelBehaviorRelay = BehaviorRelay.createDefault(new ArrayList<>());
        this.hasTrackedCar = BehaviorRelay.createDefault(0);
        this.publishedCars = BehaviorRelay.createDefault(0);
        this.availableCarToAdd = BehaviorRelay.createDefault(0);
        initData();
    }

    @Override
    protected void onCreate() {
        screen.initEditTexts();
        fetchUserData();
        fetchSwearData();
    }

    private void initData() {
        if (getResourcesUtil().isEnglish()) {
            conditions.add("New");
            conditions.add("Used");
            conditions.add("Damaged");
        } else {
            conditions.add("جديدة");
            conditions.add("مستعملة");
            conditions.add("متضررة");
        }
    }

    @Override
    protected void onResume() {
        updateUser();
        super.onResume();
    }

    private void updateUser() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(screen::isSuspend, this::processError));
        }
    }

    private void fetchUserData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(screen::isSuspend, this::processError));
            disposable.add(userRepo.getCarResources(userManager.getCurrentUser().getApiToken())
                    .map(carResourcesMapper::toViewModel)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(carResourcesApiViewModel ->
                    {
                        brandsViewModelBehaviorRelay.accept(carResourcesApiViewModel.getBrands());
                        carColorViewModelBehaviorRelay.accept(carResourcesApiViewModel.getColors());
                        importerViewModelBehaviorRelay.accept(carResourcesApiViewModel.getSpecifications());
                        hasTrackedCar.accept(carResourcesApiViewModel.getHasTrackedCar());
                        availableCarToAdd.accept(carResourcesApiViewModel.getAvailableCarToAdd());
                        publishedCars.accept(carResourcesApiViewModel.getPublishedCars());
                    }, this::processError));
        } else {
            disposable.add(userRepo.getCarResources(null)
                    .map(carResourcesMapper::toViewModel)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(carResourcesApiViewModel ->
                    {
                        brandsViewModelBehaviorRelay.accept(carResourcesApiViewModel.getBrands());
                        carColorViewModelBehaviorRelay.accept(carResourcesApiViewModel.getColors());
                        importerViewModelBehaviorRelay.accept(carResourcesApiViewModel.getSpecifications());
                        hasTrackedCar.accept(carResourcesApiViewModel.getHasTrackedCar());
                        availableCarToAdd.accept(carResourcesApiViewModel.getAvailableCarToAdd());
                        publishedCars.accept(carResourcesApiViewModel.getPublishedCars());
                        Log.d("mina", String.valueOf(brandsViewModelBehaviorRelay.getValue().size()));
                    }, this::processError));
        }
    }

    List<String> getConditions() {
        return conditions;
    }

    List<String> getPostTypes() {
        List<String> category = new ArrayList<>();
        category.add(SnapCarApplication.getRes().getString(R.string.free));
        category.add(SnapCarApplication.getRes().getString(R.string.special));
        return category;
    }

    List<String> getTransmissionTypes() {
        List<String> types = new ArrayList<>();
        types.add(SnapCarApplication.getRes().getString(R.string.auto));
        types.add(SnapCarApplication.getRes().getString(R.string.normal));
        return types;
    }

    List<String> getWarrantyOptions() {
        List<String> types = new ArrayList<>();
        if (isEnglishLang()) {
            types.add("Yes");
            types.add("No");
        } else {
            types.add("نعم");
            types.add("لا");
        }
        return types;
    }

    List<BrandsViewModel> getBrands() {
        if (brandsViewModelBehaviorRelay.getValue() == null) return Collections.emptyList();
        return brandsViewModelBehaviorRelay.getValue();
    }

    List<CarColorViewModel> getColors() {
        if (carColorViewModelBehaviorRelay.getValue() == null) return Collections.emptyList();
        return carColorViewModelBehaviorRelay.getValue();
    }

    List<ImporterViewModel> getImporter() {
        if (importerViewModelBehaviorRelay.getValue() == null) return Collections.emptyList();
        return importerViewModelBehaviorRelay.getValue();
    }

    List<ModelViewModel> getModels(int brandId) {
        for (int x = 0; x < brandsViewModelBehaviorRelay.getValue().size(); x++) {
            if (brandsViewModelBehaviorRelay.getValue().get(x).getId() == brandId) {
                return brandsViewModelBehaviorRelay.getValue().get(x).getBrandsModels();
            }
        }
        return Collections.emptyList();
    }

    List<CategoryViewModel> getCategory(int brandId, int modelId) {
        for (int x = 0; x < brandsViewModelBehaviorRelay.getValue().size(); x++) {
            if (brandsViewModelBehaviorRelay.getValue().get(x).getId() == brandId) {
                for (int y = 0; y < brandsViewModelBehaviorRelay.getValue().get(x).getBrandsModels().size(); y++) {
                    if (brandsViewModelBehaviorRelay.getValue().get(x).getBrandsModels().get(y).getId() == modelId) {
                        return brandsViewModelBehaviorRelay.getValue().get(x).getBrandsModels().get(y).getCategoryViewModels();
                    }
                }
            }

        }
        return Collections.emptyList();
    }

    @Override
    protected void onStart() {
        startReceivingLocationUpdates();
    }

    @Override
    protected void onPause() {
        stopReceivingLocationUpdates();
    }

    public void startReceivingLocationUpdates() {
        if (permissionUtil.hasLocationPermission()) {
            if (permissionUtil.isGPSEnabled()) {
                screen.startReceivingLocationUpdates(locationCallback);
            } else {
                screen.showGPSIsRequiredMessage();
            }
        } else {
            permissionUtil.requestLocationPermission();
        }
    }

    private void stopReceivingLocationUpdates() {
        screen.stopReceivingLocationUpdates(locationCallback);
    }

    boolean isShowRoom() {
        return userManager.sessionManager().isSessionActive() && userManager.getCurrentUser().getType() == 2;
    }

    boolean isHasTrackedCar() {
        return hasTrackedCar.getValue() == 1;
    }

    boolean isHasAvailable() {
        return availableCarToAdd.getValue() > publishedCars.getValue();
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location result = locationResult.getLastLocation();
                if (result != null) {
                    screen.updateLatLong(new LatLng(result.getLatitude(), result.getLongitude()));
                }
            }
        };
    }

    void openGallery(RxPaparazzo.SingleSelectionBuilder<AddCarActivity> pickedImageObservable, int pos) {
        currentExaminationPosition = pos;
        disposable.add(
                pickedImageObservable.usingGallery()
                        .filter(response -> response.resultCode() == RESULT_OK)
                        .map(com.miguelbcr.ui.rx_paparazzo2.entities.Response::data)
                        .map(FileData::getFile)
                        .map(File::getPath)
                        .map(File::new)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::examinationUploaded, Timber::e));
    }

    private void examinationUploaded(File file) {
        String apiToken;
        if (userManager.sessionManager().isSessionActive())
            apiToken = userManager.getCurrentUser().getApiToken();
        else apiToken = null;
        disposable.add(userRepo.uploadFile(apiToken, file,
                new RequestBodyProgress.UploadCallbacks() {
                    @Override
                    public void onProgressUpdate(int percentage) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onFinish() {

                    }
                })
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::setNewImagePath, this::processError));
        screen.setCurrentExaminationPosition(currentExaminationPosition);
    }

    void addCar(
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
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.addCar(userManager.getCurrentUser().getApiToken(), contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                    colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                    installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::carAdded, this::processError));
        } else {
            CashedCar c = new CashedCar(contactOption,
                    postType,
                    isTracked,
                    condition,
                    manufacturingYear,
                    importer,
                    transmission,
                    colorId,
                    kmTo,
                    warranty,
                    agentName,
                    mvpi,
                    notes,
                    modelId,
                    priceBefore,
                    priceAfter,
                    engineCapacity,
                    image,
                    installmentFrom,
                    installmentTo,
                    exchange,
                    longitude,
                    latitude,
                    examinationImage,
                    categoryId,
                    brandId
                    , price, vehicleRegistration, postType);
            String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().
                    create().toJson(c);
            preferencesUtil.saveOrUpdateString("skippedCar", json);
            screen.showErrorMessage(getResourcesUtil().getString(R.string.you_must_login_first));
            screen.navigateToLogin();
        }

    }

    private void fetchSwearData() {
        disposable.add(userRepo.getSittings()
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(screen::setSwearData, this::processError));

    }

    public void requestVIP() {
        if (userManager.getCurrentUser().getRequestVip()) {
            screen.showWarningMessage(getResourcesUtil().getString(R.string.request_in_review));
            screen.finishActivity();
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            if (!userManager.getCurrentUser().getShowRoomInfoModel().getPremium()) {
                disposable.add(userRepo.requestVIP(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                        .doFinally(screen::hideLoadingAnimation)
                        .subscribe(screen::requestVipSent, this::processError));
                disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .subscribe(screen::updatedUser, this::processError));
            } else {
                screen.showWarningMessage(getResourcesUtil().getString(R.string.you_are_vip));
            }
        }
    }

    void openService() {
        if (userManager.getCurrentUser().getCarModel() != null) {
            firebaseJobDispatcher.schedule(job);
        }
    }

    void stopService() {
        if (userManager.getCurrentUser().getCarModel() != null) {
            firebaseJobDispatcher.cancelAll();
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
        } else if (t instanceof IOException) {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            screen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
        }
    }

    private String getHttpErrorMessage(HttpException httpException) {
        Gson gson = new Gson();
        try {
            ErrorUserApiResponse errorResponse = gson.fromJson(httpException.response().errorBody().string(), ErrorUserApiResponse.class);
            if (errorResponse.getErrorResponseList().size() > 0) {
                return errorResponse.getErrorResponseList().get(0).getMessage();
            } else {
                return getResourcesUtil().getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return getResourcesUtil().getString(R.string.error_communicating_with_server);
        }
    }

    private boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}