package com.intcore.snapcar.ui.coupon;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.CouponRepo;
import com.intcore.snapcar.store.model.discount.DiscountMapper;
import com.intcore.snapcar.store.model.error.ErrorUserApiResponse;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.util.authentication.event.HttpException;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

@ActivityScope
class CouponPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final CompositeDisposable disposable;
    private final DiscountMapper discountMapper;
    private final CouponScreen couponScreen;
    private final UserManager userManager;
    private final CouponRepo couponRepo;

    @Inject
    CouponPresenter(@IOThread ThreadSchedulers threadSchedulers,
                    @ForActivity CompositeDisposable disposable,
                    DiscountMapper discountMapper,
                    CouponScreen couponScreen,
                    UserManager userManager,
                    CouponRepo couponRepo) {
        super(couponScreen);
        this.threadSchedulers = threadSchedulers;
        this.discountMapper = discountMapper;
        this.couponScreen = couponScreen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.couponRepo = couponRepo;
    }

    @Override
    protected void onCreate() {
        couponScreen.setupViewPager();
        couponScreen.setupRefreshLayout();
        fetchData();
    }

    void fetchData() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(couponRepo.fetchCoupons(userManager.getCurrentUser().getApiToken())
                    .map(discountMapper::toDiscountViewModel)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> couponScreen.showLoadingAnimation())
                    .doFinally(couponScreen::hideLoadingAnimation)
                    .subscribe(couponScreen::updateUi, this::processError));
        } else {
            couponScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            couponScreen.showErrorMessage(getHttpErrorMessage(
                    HttpException.wrapJakewhartonException(
                            (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                couponScreen.processLogout();
            }
        } else if (throwable instanceof IOException) {
            couponScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            couponScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}