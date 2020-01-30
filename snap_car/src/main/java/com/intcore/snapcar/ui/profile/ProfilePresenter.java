package com.intcore.snapcar.ui.profile;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.UserRepo;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.qualifier.ForFragment;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@FragmentScope
class ProfilePresenter {

    private final ThreadSchedulers threadSchedulers;
    private final PreferencesUtil preferencesUtil;
    private final CompositeDisposable disposable;
    private final ProfileScreen profileScreen;
    private final ResourcesUtil resourcesUtil;
    private final UserManager userManager;
    private final UserRepo userRepo;

    @Inject
    ProfilePresenter(@IOThread ThreadSchedulers threadSchedulers,
                     @ForFragment CompositeDisposable disposable,
                     PreferencesUtil preferencesUtil,
                     ResourcesUtil resourcesUtil,
                     UserManager userManager,
                     ProfileScreen screen,
                     UserRepo userRepo) {
        this.threadSchedulers = threadSchedulers;
        this.preferencesUtil = preferencesUtil;
        this.resourcesUtil = resourcesUtil;
        this.userManager = userManager;
        this.disposable = disposable;
        this.profileScreen = screen;
        this.userRepo = userRepo;
    }

    void onResume() {
        if (userManager.sessionManager().isSessionActive())
            profileScreen.setDefaultUi(userManager.getCurrentUser());
        fetchUserData();
    }

    private void fetchUserData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> profileScreen.showLoadingAnimation())
                    .doFinally(profileScreen::hideLoadingAnimation)
                    .subscribe(profileScreen::updateUi, this::processError));
        } else {
            profileScreen.setGuestScreen();
        }
    }

    void onLocationClicked() {
        if (userManager.getCurrentUser().getShowRoomInfoModel() != null) {
            profileScreen.showLocation(userManager.getCurrentUser().getShowRoomInfoModel());
        }
    }

    void requestVip() {
        if (userManager.getCurrentUser().getRequestVip()) {
            profileScreen.showWarningMessage(resourcesUtil.getString(R.string.undr_review));
//            if (resourcesUtil.isEnglish()) {
//                profileScreen.showWarningMessage("Your request is under review");
//            } else {
//                profileScreen.showWarningMessage("تم استلام طلبك ، سيتم التواصل معك");
//            }
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            if (!userManager.getCurrentUser().getShowRoomInfoModel().getPremium()) {
                disposable.add(userRepo.requestVIP(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> profileScreen.showLoadingAnimation())
                        .doFinally(profileScreen::hideLoadingAnimation)
                        .subscribe(profileScreen::requestVipSent, this::processError));
                disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .subscribe(defaultUserModel -> Timber.tag("done"), this::processError));
            } else {
                profileScreen.showWarningMessage(resourcesUtil.getString(R.string.you_are_vip_showroom));
//                if (resourcesUtil.isEnglish()) {
//                    profileScreen.showWarningMessage("You are already VIP Showroom");
//                } else {
//                    profileScreen.showWarningMessage("VIP انت الآن معرض");
//                }
            }
        }
    }

    void requestHotZone() {
        if (userManager.getCurrentUser().getRequestHotzone()) {
            profileScreen.showWarningMessage(resourcesUtil.getString(R.string.undr_review));
//            if (resourcesUtil.isEnglish()) {
//                profileScreen.showWarningMessage("Your request is under review");
//            } else {
//                profileScreen.showWarningMessage("تم استلام طلبك ، سيتم التواصل معك");
//            }
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            if (!userManager.getCurrentUser().getHasHotZone()) {
                disposable.add(userRepo.requestHotZone(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .doOnSubscribe(ignored -> profileScreen.showLoadingAnimation())
                        .doFinally(profileScreen::hideLoadingAnimation)
                        .subscribe(profileScreen::requestVipSent, this::processError));

                disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                        .subscribeOn(threadSchedulers.workerThread())
                        .observeOn(threadSchedulers.mainThread())
                        .subscribe(defaultUserModel -> Timber.tag("done"), this::processError));
            } else {
                profileScreen.showWarningMessage(resourcesUtil.getString(R.string.you_are_hotzonee));
//                if (resourcesUtil.isEnglish()) {
//                    profileScreen.showWarningMessage("You are already a Hot Zone");
//                } else {
//                    profileScreen.showWarningMessage("انت الآن نقطة إلتقاء");
//                }
            }
        }
    }

    private boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    void requestAd() {
        if (userManager.getCurrentUser().hasRequestAds()) {
            profileScreen.showWarningMessage(resourcesUtil.getString(R.string.undr_review));
//            if (resourcesUtil.isEnglish()) {
//                profileScreen.showWarningMessage("Your request is under review");
//            } else {
//                profileScreen.showWarningMessage("تم استلام طلبك ، سيتم التواصل معك");
//            }
            return;
        }
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(userRepo.requestAds(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> profileScreen.showLoadingAnimation())
                    .doFinally(profileScreen::hideLoadingAnimation)
                    .subscribe(profileScreen::requestVipSent, this::processError));
            disposable.add(userRepo.fetchUserProfile(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .subscribe(defaultUserModel -> Timber.tag("done"), this::processError));
        }
    }

    void onLogoutClicked() {
        if (userManager.sessionManager().isSessionActive()) {
            preferencesUtil.saveOrUpdateInt("interest_count", 0);
            String apiToken = userManager.getCurrentUser().getApiToken();
            disposable.add(userRepo.logout(apiToken)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> profileScreen.showLoadingAnimation())
                    .doFinally(profileScreen::hideLoadingAnimation)
                    .subscribe(() -> {
                        userManager.sessionManager().logout();
                        processLogout();
                    }, this::processError));
        } else {
            processLogout();
        }
    }

    private void processLogout() {
        preferencesUtil.delete("vipShowRoom");
        preferencesUtil.delete("vipHotZone");
        preferencesUtil.delete("damagedCar");
        preferencesUtil.delete("showRoom");
        preferencesUtil.delete("womenCar");
        preferencesUtil.delete("showAll");
        preferencesUtil.delete("hotZone");
        preferencesUtil.delete("menCar");
        preferencesUtil.delete("vipShowRoomNearBy");
        preferencesUtil.delete("vipHotZoneNearBy");
        preferencesUtil.delete("damagedCarNearBy");
        preferencesUtil.delete("showRoomNearBy");
        preferencesUtil.delete("womenCarNearBy");
        preferencesUtil.delete("showAllNearBy");
        preferencesUtil.delete("hotZoneNearBy");
        preferencesUtil.delete("menCarNearBy");
        profileScreen.shouldNavigateToLogin();
    }

    private void processError(Throwable t) {
        Timber.e(t);
        if (t instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t).code() == 401) {
                userManager.sessionManager().logout();
                profileScreen.processLogout();
            } else {
                profileScreen.showErrorMessage(getHttpErrorMessage(HttpException.wrapJakewhartonException((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) t)));
            }
        }else if (t instanceof IOException) {
            profileScreen.showErrorMessage(resourcesUtil.getString(R.string.error_network));
        } else {
            profileScreen.showErrorMessage(resourcesUtil.getString(R.string.error_communicating_with_server));
        }
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(resourcesUtil.getContext()).equals("en");
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

    void onDestroy() {
        disposable.clear();
    }
}