package com.intcore.snapcar.ui.blocklist;

import com.google.gson.Gson;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.BlockRepo;
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
class BlockListPresenter extends BaseActivityPresenter {

    private final ThreadSchedulers threadSchedulers;
    private final BlockListScreen blockListScreen;
    private final CompositeDisposable disposable;
    private final UserManager userManager;
    private final BlockRepo blockRepo;

    @Inject
    BlockListPresenter(@IOThread ThreadSchedulers threadSchedulers,
                       @ForActivity CompositeDisposable disposable,
                       BlockListScreen blockListScreen,
                       UserManager userManager,
                       BlockRepo blockRepo) {
        super(blockListScreen);
        this.threadSchedulers = threadSchedulers;
        this.blockListScreen = blockListScreen;
        this.userManager = userManager;
        this.disposable = disposable;
        this.blockRepo = blockRepo;
    }

    @Override
    protected void onCreate() {
        blockListScreen.setupRefreshLayout();
        blockListScreen.setupRecyclerView();
        refreshComments();
    }

    void refreshComments() {
        if (userManager.sessionManager().isSessionActive()) {
            blockRepo.tearDown();
            disposable.add(blockRepo.fetchBlockList(userManager.getCurrentUser().getApiToken())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> blockListScreen.showLoadingAnimation())
                    .doFinally(blockListScreen::hideLoadingAnimation)
                    .subscribe(blockListScreen::setBlockList, this::processError));
        } else {
            blockListScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    void fetchComments() {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(blockRepo.fetchBlockList(userManager.getCurrentUser().getApiToken())
                    .filter(commentModels -> !commentModels.isEmpty())
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> blockListScreen.showLoadingAnimation())
                    .doFinally(blockListScreen::hideLoadingAnimation)
                    .subscribe(blockListScreen::appendBlockListToBottom, this::processError));
        } else {
            blockListScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    private void processError(Throwable throwable) {
        Timber.e(throwable);
        if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            if (((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).code() == 401) {
                userManager.sessionManager().logout();
                blockListScreen.processLogout();
            } else {
                blockListScreen.showErrorMessage(getHttpErrorMessage(
                        HttpException.wrapJakewhartonException(
                                (com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable)));
            }
        }else if (throwable instanceof IOException) {
            blockListScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_network));
        } else {
            blockListScreen.showErrorMessage(getResourcesUtil().getString(R.string.error_communicating_with_server));
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

    void onUnBlockClicked(int blockItemPosition, long userModelId) {
        if (userManager.sessionManager().isSessionActive()) {
            disposable.add(blockRepo.removeUserFromList(userManager.getCurrentUser().getApiToken(), userModelId)
                    .subscribeOn(threadSchedulers.workerThread())
                    .observeOn(threadSchedulers.mainThread())
                    .doOnSubscribe(ignored -> blockListScreen.showLoadingAnimation())
                    .doFinally(blockListScreen::hideLoadingAnimation)
                    .subscribe(() -> {
                        if (getResourcesUtil().isEnglish()) {
                            blockListScreen.showSuccessMessage("You have successfully Unblocked the user");
                        } else {
                            blockListScreen.showSuccessMessage("تم إلغاء الحظر بنجاح");
                        }
                        blockListScreen.removeUnBlockedItem(blockItemPosition);
                    }, this::processError));
        } else {
            blockListScreen.showWarningMessage(getResourcesUtil().getString(R.string.please_sign_in));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
    }
}