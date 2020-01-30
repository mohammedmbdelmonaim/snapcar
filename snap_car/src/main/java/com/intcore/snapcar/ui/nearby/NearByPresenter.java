package com.intcore.snapcar.ui.nearby;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.HomeRepo;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredMapper;
import com.intcore.snapcar.store.model.addinterest.InterestRequiredViewModel;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.store.model.explore.ExploreItem;
import com.intcore.snapcar.store.model.explore.ExploreMapper;
import com.intcore.snapcar.store.model.filter.FilterMapper;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.util.UserManager;
import com.jakewharton.rxrelay2.BehaviorRelay;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@FragmentScope
class NearByPresenter {

    private final BehaviorRelay<InterestRequiredViewModel> interestRequiredRelay;
    private final BehaviorRelay<CategoryViewModel> selectedCategoryModel;
    private final BehaviorRelay<List<ExploreItem>> exploreItemsRelay;
    private final BehaviorRelay<List<CarViewModel>> featureListRelay;
    private final BehaviorRelay<BrandsViewModel> selectedBrandModel;
    private final BehaviorRelay<ModelViewModel> selectedModelModel;
    private final InterestRequiredMapper interestRequiredMapper;
    private final ThreadSchedulers threadSchedulers;
    private final PreferencesUtil preferencesUtil;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final ExploreMapper exploreMapper;
    private final FilterMapper filterMapper;
    private final NearByScreen nearByScreen;
    private final UserManager userManager;
    private final HomeRepo homeRepo;
    private List<ExploreItem> paginateexploreItemsRelay;
    private int counter;

    @Inject
    NearByPresenter(InterestRequiredMapper interestRequiredMapper,
                    @IOThread ThreadSchedulers threadSchedulers,
                    @ForFragment CompositeDisposable disposable,
                    PreferencesUtil preferencesUtil,
                    ResourcesUtil resourcesUtil,
                    ExploreMapper exploreMapper,
                    FilterMapper filterMapper,
                    NearByScreen nearByScreen,
                    UserManager userManager,
                    HomeRepo homeRepo) {
        this.interestRequiredRelay = BehaviorRelay.createDefault(InterestRequiredViewModel.createDefault());
        this.selectedCategoryModel = BehaviorRelay.createDefault(CategoryViewModel.createDefault());
        this.selectedBrandModel = BehaviorRelay.createDefault(BrandsViewModel.createDefault());
        this.selectedModelModel = BehaviorRelay.createDefault(ModelViewModel.createDefault());
        this.exploreItemsRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.featureListRelay = BehaviorRelay.createDefault(Collections.emptyList());
        this.interestRequiredMapper = interestRequiredMapper;
        this.paginateexploreItemsRelay = new ArrayList<>();
        this.threadSchedulers = threadSchedulers;
        this.preferencesUtil = preferencesUtil;
        this.exploreMapper = exploreMapper;
        this.resourcesUtil = resourcesUtil;
        this.filterMapper = filterMapper;
        this.nearByScreen = nearByScreen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.homeRepo = homeRepo;
        counter = 10;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void onViewCreated() {
        nearByScreen.setupRecyclerView();
        nearByScreen.setupRefreshLayout();
        fetchRequiredDataForAddInterest();
//        fetchData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void onResume() {
        fetchData();
    }

    private void fetchRequiredDataForAddInterest() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    void fetchData() {
        String apiToken = null;
        if (userManager.sessionManager().isSessionActive()) {
            apiToken = userManager.getCurrentUser().getApiToken();
        }
        disposable.add(homeRepo.filterExploreList(apiToken,
                0,
                0,
                0,
                preferencesUtil.getInt("showAllNearBy", 1),
                preferencesUtil.getInt("menCarNearBy", 0),
                preferencesUtil.getInt("womenCarNearBy", 0),
                preferencesUtil.getInt("damagedCarNearBy", 0),
                preferencesUtil.getInt("vipShowRoomNearBy", 0),
                preferencesUtil.getInt("showRoomNearBy", 0),
                preferencesUtil.getInt("vipHotZoneNearBy", 0),
                preferencesUtil.getInt("hotZoneNearBy", 0),
                0.0,
                0.0)
                .map(filterMapper::toFilterViewModel)
                .map(filterViewModel -> exploreMapper.toExploreItems(
                        filterViewModel.getFeatureCarList(),
                        filterViewModel.getHotZoneList(),
                        filterViewModel.getShowRoomsList(),
                        filterViewModel.getCarList(),
                        filterViewModel.getAdsList()))
                .subscribeOn(threadSchedulers.workerThread())
                .observeOn(threadSchedulers.mainThread())
                .doOnSubscribe(ignored -> nearByScreen.showLoadingAnimation())
                .doFinally(nearByScreen::hideLoadingAnimation)
                .subscribe(exploreItems -> {
                    exploreItemsRelay.accept(exploreItems);
                    paginateexploreItemsRelay = new ArrayList<>();
                    if (exploreItems.size() >= 10) {
                        paginateexploreItemsRelay.addAll(exploreItems.subList(0, 10));
                        counter = 10;
                    } else {
                        paginateexploreItemsRelay.addAll(exploreItems.subList(0, exploreItems.size()));
                        counter = exploreItems.size();
                    }
                    if (paginateexploreItemsRelay.size() > 0) {
                        nearByScreen.updateUi(paginateexploreItemsRelay);
                    } else {
                        nearByScreen.showNoDataIndicator();
                    }
                }, this::processError));
    }

    void setExploreItems(List<ExploreItem> exploreItems) {
        exploreItemsRelay.accept(exploreItems);
        paginateexploreItemsRelay = new ArrayList<>();
        if (exploreItems.size() > 10) {
            paginateexploreItemsRelay.addAll(exploreItems.subList(0, 10));
            counter = 10;
        } else {
            paginateexploreItemsRelay.addAll(exploreItems.subList(0, exploreItems.size()));
            counter = exploreItems.size();
        }
        if (paginateexploreItemsRelay.size() > 0) {
            nearByScreen.updateUi(paginateexploreItemsRelay);
        } else {
            nearByScreen.showNoDataIndicator();
            nearByScreen.updateUi(paginateexploreItemsRelay);
        }
    }

    void processError(Throwable t) {

        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            nearByScreen.showErrorMessage(getHttpErrorMessage(
                    HttpException.wrapJakewhartonException(
                            (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                nearByScreen.processLogout();
            }
        } else if (t instanceof IOException) {
            nearByScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            nearByScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
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

    void fetchPaginateExploreItems() {
        if (paginateexploreItemsRelay.size() != exploreItemsRelay.getValue().size()) {
            int size = exploreItemsRelay.getValue().size();
            List<ExploreItem> list = exploreItemsRelay.getValue();
            if (size > counter + 10) {
                paginateexploreItemsRelay.addAll(list.subList(counter, counter + 10));
                nearByScreen.appendData(list.subList(counter, counter + 10));
            } else {
                paginateexploreItemsRelay.addAll(list.subList(counter, size));
                nearByScreen.appendData(list.subList(counter, size));
            }
            counter = counter + 10;
        }
    }

    List<ExploreItem> getPaginationList() {
        return paginateexploreItemsRelay;
    }

    List<CarViewModel> getFeatureList() {
        return featureListRelay.getValue();
    }

    public void setFeatureList(List<CarViewModel> featureList) {
        featureListRelay.accept(featureList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void onFavoriteClicked(String carId, String userId, OperationListener<Void> operationListener) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(homeRepo.addItemToFavorite(userManager.getCurrentUser().getApiToken(), carId, userId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> operationListener.onPreOperation())
                    .doFinally(operationListener::onPostOperation)
                    .subscribe(v -> {
                        // samuel
                        nearByScreen.showSuccessMessage(v.getMessage());
                        operationListener.onSuccess(null);
//                        fetchData();
                    }, operationListener::onError));
        } else {
            if (resourcesUtil.isEnglish()) {
                nearByScreen.showErrorMessage("You Should be Logged in");
            } else {
                nearByScreen.showErrorMessage("يجب تسجيل الدخول");
            }
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

    void onDestroy() {
        disposable.clear();
    }

    public void locationNotAvailable() {

        nearByScreen.showErrorMessage(resourcesUtil.getString(R.string.location_not_available));
    }
}