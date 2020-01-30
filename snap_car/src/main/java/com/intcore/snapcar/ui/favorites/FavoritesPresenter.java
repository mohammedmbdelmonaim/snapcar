package com.intcore.snapcar.ui.favorites;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@FragmentScope
class FavoritesPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final ResourcesUtil resourcesUtil;
    private final FavoritesScreen screen;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    FavoritesPresenter(@IOThread ThreadSchedulers threadSchedulers,
                       @ForActivity CompositeDisposable disposable,
                       ResourcesUtil resourcesUtil,
                       FavoritesScreen favScreen,
                       UserRepo userRepo,
                       UserManager userManager) {
        super(favScreen);
        this.threadSchedulers = threadSchedulers;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.userRepo = userRepo;
        this.screen = favScreen;
    }

    @Override
    protected void onCreate() {
        fetchFavData();
    }

    @Override
    protected void onResume() {
        fetchFavData();
    }

    public void fetchFavData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.getFavorites(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::updateUi, this::processError));
        } else {
            screen.showAnnouncementMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    public void deleteCarFavorite(int carId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.deleteCarFavorite(userManager.getCurrentUser().getApiToken(), carId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::deleted, this::processError));
        } else {
            screen.showAnnouncementMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    public void deleteUserFavorite(int userId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.deleteUserFavorite(userManager.getCurrentUser().getApiToken(), userId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> screen.showLoadingAnimation())
                    .doFinally(screen::hideLoadingAnimation)
                    .subscribe(screen::deleted, this::processError));
        } else {
            screen.showAnnouncementMessage(resourcesUtil.getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                screen.processLogout();
            } else {
                screen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            }
        }else if (t instanceof IOException) {
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
                return resourcesUtil.getString(R.string.error_communicating_with_server);
            }
        } catch (Exception e) {
            return resourcesUtil.getString(R.string.error_communicating_with_server);
        }
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
    }
}