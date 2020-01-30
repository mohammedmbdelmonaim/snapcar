package com.intcore.snapcar.ui.addinterest;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.ui.ActivityArgs;

public class AddInterestActivityArgs implements ActivityArgs {

    private static String SEARCH_KEY = "SearchIntentKey";
    private SearchRequestModel searchRequestModel;

    public AddInterestActivityArgs(SearchRequestModel searchRequestModel) {
        this.searchRequestModel = searchRequestModel;
    }

    public static AddInterestActivityArgs desrilizeFrom(Intent intent) {
        return new AddInterestActivityArgs(intent.getParcelableExtra(SEARCH_KEY));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, AddInterestActivity.class);
        intent.putExtra(SEARCH_KEY, searchRequestModel);
        return intent;
    }

    public SearchRequestModel getSearchRequestModel() {
        return searchRequestModel;
    }
}