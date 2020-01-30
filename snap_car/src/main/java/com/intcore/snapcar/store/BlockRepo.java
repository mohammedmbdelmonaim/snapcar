package com.intcore.snapcar.store;

import com.intcore.snapcar.store.model.blocklist.BlockDTO;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.core.scope.ActivityScope;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import kotlin.Triple;

@ActivityScope
public class BlockRepo {

    private final DefaultUserMapper defaultUserMapper;
    private final WebServiceStore webServiceStore;
    private boolean canLoadMoreBlockList = true;
    private int currentBlockListPage = 0;

    @Inject
    BlockRepo(DefaultUserMapper defaultUserMapper, WebServiceStore webServiceStore) {
        this.defaultUserMapper = defaultUserMapper;
        this.webServiceStore = webServiceStore;
    }

    public Single<List<DefaultUserModel>> fetchBlockList(String apiToken) {
        if (canLoadMoreBlockList) {
            return webServiceStore.fetchBlockList(apiToken, currentBlockListPage + 1)
                    .doOnSuccess(this::updateCurrentCommentsState)
                    .map(Triple::getThird)
                    .map(defaultUserMapper::toModels);
        } else {
            return Single.just(Collections.emptyList());
        }
    }

    private void updateCurrentCommentsState(Triple<Integer, Boolean, List<BlockDTO.BlockListData>> currentPageCanLoadMoreTriple) {
        if (currentPageCanLoadMoreTriple.getSecond()) {
            currentBlockListPage = currentPageCanLoadMoreTriple.getFirst();
        } else {
            canLoadMoreBlockList = false;
        }
    }

    public Completable removeUserFromList(String apiToken, long userId) {
        return webServiceStore.removeUserFromBlock(apiToken, userId);
    }

    public void tearDown() {
        currentBlockListPage = 0;
        canLoadMoreBlockList = true;
    }
}