package com.intcore.snapcar.ui.visitorprofile;

import android.app.Dialog;
import android.content.Context;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.store.model.visitor.VisitorMapper;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.ComputationalThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class VisitorProfilePresenter extends BaseActivityPresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<List<CarViewModel>> availableCarsRelay;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final BehaviorRelay<List<CarViewModel>> soldCarsRelay;
    private final BehaviorRelay<DefaultUserModel> userModelRelay;
    private final InterestRequiredMapper interestRequiredMapper;
    private final VisitorProfileScreen visitorProfileScreen;
    private final ThreadSchedulers threadSchedulers;
    public final BehaviorRelay<Long> userIdRelay;
    private final CompositeDisposable disposable;
    private final VisitorMapper visitorMapper;
    private final UserManager userManager;
    private final CarMapper carMapper;
    private final UserRepo userRepo;
    private final Context context;

    @Inject
    VisitorProfilePresenter(InterestRequiredMapper interestRequiredMapper, VisitorProfileScreen visitorProfileScreen,
                            @ComputationalThread ThreadSchedulers threadSchedulers,
                            @ForActivity CompositeDisposable disposable,
                            @ForActivity Context context,
                            VisitorMapper visitorMapper,
                            UserManager userManager,
                            CarMapper carMapper, UserRepo userRepo) {
        super(visitorProfileScreen);
        this.interestRequiredRelay = BehaviorRelay.createDefault(InterestRequiredViewModel.createDefault());
        this.selectedCategoryModel = BehaviorRelay.createDefault(CategoryViewModel.createDefault());
        this.selectedModelModel = BehaviorRelay.createDefault(ModelViewModel.createDefault());
        this.selectedBrandModel = BehaviorRelay.createDefault(BrandsViewModel.createDefault());
        this.availableCarsRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.soldCarsRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.userModelRelay = BehaviorRelay.create();
        this.userIdRelay = BehaviorRelay.create();
        this.interestRequiredMapper = interestRequiredMapper;
        this.visitorProfileScreen = visitorProfileScreen;
        this.threadSchedulers = threadSchedulers;
        this.visitorMapper = visitorMapper;
        this.userManager = userManager;
        this.disposable = disposable;
        this.carMapper = carMapper;
        this.userRepo = userRepo;
        this.context = context;
    }

    @Override
    protected void onCreate() {
        fetchRequiredDataForAddInterest();
        visitorProfileScreen.setupRecyclerView();
        VisitorProfileActivityArgs visitorProfileActivityArgs = VisitorProfileActivityArgs.deserializeFrom(getIntent());
        userIdRelay.accept(visitorProfileActivityArgs.getUserId());
    }

    private void fetchRequiredDataForAddInterest() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.fetchRequiredDataForInterest(apiToken)
                .map(interestRequiredMapper::toInterestRequiredViewModel)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(interestRequiredRelay, this::processError));
    }

    @Override
    protected void onResume() {
        fetchData();
    }

    void fetchData() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        long value = userIdRelay.getValue();
        disposable.add(userRepo.fetchVisitorProfile(apiToken, value)
                .observeOn(threadSchedulers.workerThread())
                .map(visitorMapper::toVisitorViewModel)
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> visitorProfileScreen.showLoadingAnimation())
                .doFinally(visitorProfileScreen::hideLoadingAnimation)
                .subscribe(visitorModel -> {
                    visitorProfileScreen.updateUi(visitorModel.getDefaultUserModel());
                    visitorProfileScreen.updateCars(visitorModel.getCarViewModel());
                    if (!userManager.sessionManager().isSessionActive()) {
                        visitorProfileScreen.setupSkipLogic();
                    }
                    if (visitorModel.getDefaultUserModel() != null) {
                        availableCarsRelay.accept(visitorModel.getCarViewModel());
                        userModelRelay.accept(visitorModel.getDefaultUserModel());
                        soldCarsRelay.accept(visitorModel.getSoldCarViewModel());
                    }
                }, this::processError));
    }

    void onRateClicked(int rate, String review, Dialog dialog) {
        if (rate == 0) {
            if (isEnglishLang())
                visitorProfileScreen.showWarningMessage("You should rate the user");
            else
                visitorProfileScreen.showWarningMessage("يجب تقييم هذا المعرض");
            return;
        } else {
            if (userManager.sessionManager().isSessionActive()) {
                disposable.add(userRepo.rateUser(userManager.getCurrentUser().getApiToken(),
                        userIdRelay.getValue(), rate, review)
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> visitorProfileScreen.showLoadingAnimation())
                        .doFinally(visitorProfileScreen::hideLoadingAnimation)
                        .subscribe(() ->
                                {
                                    userModelRelay.getValue().setUserRate(1);
                                    visitorProfileScreen.onRateSuccessfully(dialog);
                                }
                               , this::processError));
            } else {
                if (getResourcesUtil().isEnglish()) {
                    visitorProfileScreen.showErrorMessage("You Should be Logged in");
                } else {
                    visitorProfileScreen.showErrorMessage("يجب تسجيل الدخول");
                }
            }
        }
    }

    public void onBlockClicked(String reason) {
        if (reason == null) {
            if (isEnglishLang())
                visitorProfileScreen.showWarningMessage("please type block reason");
            else
                visitorProfileScreen.showWarningMessage("فضلا قم بإدخال سبب الحظر");
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.blockUser(userManager.getCurrentUser().getApiToken(), userIdRelay.getValue(), reason)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> visitorProfileScreen.showLoadingAnimation())
                    .doFinally(visitorProfileScreen::hideLoadingAnimation)
                    .subscribe(visitorProfileScreen::onBlockSuccess, this::processError));
        } else {
            if (getResourcesUtil().isEnglish()) {
                visitorProfileScreen.showErrorMessage("You Should be Logged in");
            } else {
                visitorProfileScreen.showErrorMessage("يجب تسجيل الدخول");
            }
        }
    }

    public boolean canRateThisUser() {
        return userModelRelay.getValue().getUserRate() == 0;
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            visitorProfileScreen.showErrorMessage(getHttpErrorMessage(
                    HttpException.wrapJakewhartonException(
                            (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                visitorProfileScreen.processLogout();
            }
        } else if (throwable instanceof NullPointerException) {
            visitorProfileScreen.showBlockScreen();
        } else if (throwable instanceof IOException) {
            visitorProfileScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            visitorProfileScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    void onPhoneClicked() {
        if (userModelRelay.getValue().getShowRoomInfoModel() != null) {
            visitorProfileScreen.showUserPhones(userModelRelay.getValue().getPhone(), userModelRelay.getValue().getShowRoomInfoModel().getPhones());
        } else {
            visitorProfileScreen.showUserPhones(userModelRelay.getValue().getPhone(), null);
        }
    }

    void onLocationClicked() {
        if (userModelRelay.getValue().getShowRoomInfoModel() != null) {
            visitorProfileScreen.showLocation(userModelRelay.getValue().getShowRoomInfoModel());
        }
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

    String getMinPrice() {
        return interestRequiredRelay.getValue().getMinPrice();
    }

    String getMaxPrice() {
        return interestRequiredRelay.getValue().getMaxPrice();
    }

    List<CarViewModel> getAvailableCars() {
        return availableCarsRelay.getValue();
    }

    List<CarViewModel> getSoldCars() {
        return soldCarsRelay.getValue();
    }

    void onSearchClicked(SearchRequestModel searchRequestModel) {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(userRepo.fetchCarVisitor(apiToken,
                userIdRelay.getValue(),
                searchRequestModel.getBrand_id(),
                searchRequestModel.getModel_id(),
                searchRequestModel.getCategory_id(),
                searchRequestModel.getYear_form(),
                searchRequestModel.getYear_to(),
                searchRequestModel.getPrice_from(),
                searchRequestModel.getPrice_to(),
                searchRequestModel.getLongitude(),
                searchRequestModel.getLatitude())
                .map(carMapper::toCarViewModels)
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .subscribe(visitorProfileScreen::updateCars, this::processError));
    }

    void onChatClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.startChat(userManager.getCurrentUser().getApiToken(),
                    userIdRelay.getValue())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> visitorProfileScreen.showLoadingAnimation())
                    .doFinally(visitorProfileScreen::hideLoadingAnimation)
                    .subscribe(visitorProfileScreen::shouldNavigateToChatThread, this::processError));
        } else {
            if (getResourcesUtil().isEnglish()) {
                visitorProfileScreen.showErrorMessage("You Should be Logged in");
            } else {
                visitorProfileScreen.showErrorMessage("يجب تسجيل الدخول");
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }

    private boolean isEnglishLang() {
        return LocaleUtil.getLanguage(context).equals("en");
    }
}