package com.intcore.snapcar.ui.search;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.ui.ActivityArgs;

public class SearchActivityArgs implements ActivityArgs {

    private static String SEARCH_KEY = "SearchIntentKey";
    private static String IS_BASIC_KEY = "isBasicIntentKey";
    private SearchRequestModel searchRequestModel;
    private boolean isBasicSearch;

    public SearchActivityArgs(SearchRequestModel searchRequestModel, boolean isBasicSearch) {
        this.searchRequestModel = searchRequestModel;
        this.isBasicSearch = isBasicSearch;
    }

    public static SearchActivityArgs deserializeFrom(Intent intent) {
        return new SearchActivityArgs(intent.getParcelableExtra(SEARCH_KEY),
                intent.getBooleanExtra(IS_BASIC_KEY, false));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(SEARCH_KEY, searchRequestModel);
        intent.putExtra(IS_BASIC_KEY, isBasicSearch);
        return intent;
    }

    public boolean isBasicSearch() {
        return isBasicSearch;
    }

    public SearchRequestModel getSearchRequestModel() {
        return searchRequestModel;
    }
}
