package com.intcore.snapcar.ui.myinterstes;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.InterestRepo;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class MyInterestPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final MyInterestScreen myInterestScreen;
    private final CompositeDisposable disposable;
    private final InterestRepo interestRepo;
    private final UserManager userManager;
    private final CarMapper carMapper;

    @Inject
    MyInterestPresenter(@IOThread ThreadSchedulers threadSchedulers,
                        @ForActivity CompositeDisposable disposable,
                        MyInterestScreen myInterestScreen,
                        InterestRepo interestRepo,
                        UserManager userManager,
                        CarMapper carMapper) {
        super(myInterestScreen);
        this.threadSchedulers = threadSchedulers;
        this.myInterestScreen = myInterestScreen;
        this.interestRepo = interestRepo;
        this.userManager = userManager;
        this.disposable = disposable;
        this.carMapper = carMapper;
    }

    @Override
    protected void onCreate() {
        myInterestScreen.setupRecyclerView();
        myInterestScreen.setupRefreshLayout();
    }

    @Override
    protected void onResume() {
        refreshData();
    }

    void refreshData() {
        if (userManager.sessionManager().isSessionActive()) {
            interestRepo.tearDown();
            disposable.add(interestRepo.fetchMyInterest(userManager.getCurrentUser().getApiToken())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> myInterestScreen.showLoadingAnimation())
                    .doFinally(myInterestScreen::hideLoadingAnimation)
                    .subscribe(myInterestScreen::setInterestList, this::processError));
        }
    }

    void fetchInterest() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(interestRepo.fetchMyInterest(userManager.getCurrentUser().getApiToken())
                    .filter(commentModels -> !commentModels.isEmpty())
                    .map(carMapper::toCarViewModels)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> myInterestScreen.showLoadingAnimation())
                    .doFinally(myInterestScreen::hideLoadingAnimation)
                    .subscribe(myInterestScreen::appendInterestListToBottom, this::processError));
        }
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            myInterestScreen.showErrorMessage(getHttpErrorMessage(
                    HttpException.wrapJakewhartonException(
                            (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                myInterestScreen.processLogout();
            }
        } else if (throwable instanceof IOException) {
            myInterestScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            myInterestScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    void removeInterestItem(int interestItemPosition, int userModelId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(interestRepo.removeInterestFromList(userManager.getCurrentUser().getApiToken(), userModelId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> myInterestScreen.showLoadingAnimation())
                    .doFinally(myInterestScreen::hideLoadingAnimation)
                    .subscribe(() -> {
                        if (isEnglishLang()) {
                            myInterestScreen.showSuccessMessage("Interest has been removed from your list");
                        } else {
                            myInterestScreen.showSuccessMessage("تم حذف الاهتمام بنجاح");
                        }
                        myInterestScreen.removeInterestItem(interestItemPosition);
                    }, this::processError));
        }
    }

    private boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}