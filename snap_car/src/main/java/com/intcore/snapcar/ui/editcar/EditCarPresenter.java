package com.intcore.snapcar.ui.editcar;

import android.content.Context;
import android.location.Location;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.intcore.snapcar.R;
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
class EditCarPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final LocationCallback locationCallback;
    private final CompositeDisposable disposable;
    private final PermissionUtil permissionUtil;
    private final UserManager userManager;
    private final EditCarScreen screen;
    private final UserRepo userRepo;
    private final Context context;
    private int currentExaminationPosition;
    private final CarResourcesMapper carResourcesMapper;
    private final BehaviorRelay<List<BrandsViewModel>> brandsViewModelBehaviorRelay;
    private final BehaviorRelay<List<CarColorViewModel>> carColorViewModelBehaviorRelay;
    private final BehaviorRelay<List<ImporterViewModel>> importerViewModelBehaviorRelay;
    private final BehaviorRelay<Integer> hasTrackedCar;
    private final BehaviorRelay<Integer> publishedCars;
    private final BehaviorRelay<Integer> availableCarToAdd;
    private final FirebaseJobDispatcher firebaseJobDispatcher;
    private final Job job;

    @Inject
    EditCarPresenter(@ForActivity CompositeDisposable disposable,
                     EditCarScreen screen,
                     @ForActivity Context context,
                     @IOThread ThreadSchedulers threadSchedulers,
                     UserManager userManager,
                     CarResourcesMapper carResourcesMapper,
                     PermissionUtil permissionUtil,
                     UserRepo userRepo) {
        super(screen);
        this.screen = screen;
        this.userRepo = userRepo;
        this.disposable = disposable;
        this.userManager = userManager;
        this.context = context;
        this.permissionUtil = permissionUtil;
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
    }

    @Override
    protected void onCreate() {
        getCarResources();
        fetchSwearData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void getCarResources() {
        disposable.add(userRepo.getCarResources(userManager.getCurrentUser().getApiToken())
                .map(carResourcesMapper::toViewModel)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(carResourcesApiViewModel ->
                {
                    brandsViewModelBehaviorRelay.accept(carResourcesApiViewModel.getBrands());
                    carColorViewModelBehaviorRelay.accept(carResourcesApiViewModel.getColors());
                    importerViewModelBehaviorRelay.accept(carResourcesApiViewModel.getSpecifications());
                    hasTrackedCar.accept(carResourcesApiViewModel.getHasTrackedCar());
                    availableCarToAdd.accept(carResourcesApiViewModel.getAvailableCarToAdd());
                    publishedCars.accept(carResourcesApiViewModel.getPublishedCars());
                }, this::processError));
    }

    void getCarData(int carId) {
        disposable.add(userRepo.getCar(carId, userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(screen::updateUi, this::processError));
    }

    List<ImporterViewModel> getImporter() {
        if (importerViewModelBehaviorRelay.getValue() == null) return Collections.emptyList();
        return importerViewModelBehaviorRelay.getValue();
    }

    List<String> getConditions() {
        List<String> conditions = new ArrayList<>();
        conditions.add(getResourcesUtil().getString(R.string.old));
        conditions.add(getResourcesUtil().getString(R.string.newc));
        conditions.add(getResourcesUtil().getString(R.string.damaged));
        return conditions;
    }

    List<String> getPostTypes() {
        List<String> category = new ArrayList<>();
            category.add(getResourcesUtil().getString(R.string.free));
            category.add(getResourcesUtil().getString(R.string.special));

        return category;
    }

    List<String> getTransmissionTypes() {
        List<String> types = new ArrayList<>();
        types.add(getResourcesUtil().getString(R.string.auto));
        types.add(getResourcesUtil().getString(R.string.normal));
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

    List<String> getWarrantyOptions() {
        List<String> types = new ArrayList<>();
        types.add(getResourcesUtil().getString(R.string.yes));
        types.add(getResourcesUtil().getString(R.string.no));
        return types;
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

    boolean isShowRoom() {
        return userManager.getCurrentUser().getType() == 2;
    }

    boolean isHasTrackedCar() {
        return hasTrackedCar.getValue() == 1;
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

    void openGallery(RxPaparazzo.SingleSelectionBuilder<EditCarActivity> pickedImageObservable, int pos) {
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
        disposable.add(userRepo.uploadFile(userManager.getCurrentUser().getApiToken(), file,
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

    private void processError(Throwable t) {
        screen.hideLoadingAnimation();
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            }
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

    void updateCar(String carId,
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
                   String priceAfter,
                   String priceBefore,
                   String engineCapacity,
                   String image,
                   String installmentTo,
                   String installmentFrom,
                   int exchange,
                   String longitude,
                   String latitude,
                   String examinationImage,
                   int categoryId,
                   int brandId
            , String price,
                   int vehicleRegistration, int priceType) {
        disposable.add(userRepo.updateCar(carId, userManager.getCurrentUser().getApiToken(), contactOption, postType, isTracked, condition, manufacturingYear, importer, transmission,
                colorId, kmTo, warranty, agentName, mvpi, notes, modelId, priceBefore, priceAfter, engineCapacity, image,
                installmentFrom, installmentTo, exchange, longitude, latitude, examinationImage, categoryId, brandId, price, vehicleRegistration, priceType)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                .subscribe(v -> this.fetchUserData(), this::processError));

    }

    private void fetchUserData() {
        disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doFinally(screen::hideLoadingAnimation)
                .subscribe(v -> screen.carUpdated(), this::processError));
    }

    private void fetchSwearData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getSittings()
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(screen::setSwearData, this::processError));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

}