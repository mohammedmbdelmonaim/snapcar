package com.intcore.snapcar.ui.favorites;

import com.intcore.snapcar.store.model.favorites.FavoritesApiResponse;
import com.intcore.snapcar.core.base.BaseActivityScreen;

import okhttp3.ResponseBody;

public interface FavoritesScreen extends BaseActivityScreen{
    void updateUi(FavoritesApiResponse apiResponse);

    void deleted(ResponseBody responseBody);

    void processLogout();
}
