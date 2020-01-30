package com.intcore.snapcar.store.model.blocklist;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;

import java.util.List;

public class BlockDTO {

    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("last_page")
    private int lastPage;
    @SerializedName("data")
    private List<BlockListData> dataList;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public List<BlockListData> getDataList() {
        return dataList;
    }

    public static class BlockListData {

        @SerializedName("user")
        private DefaultUserDataApiResponse.DefaultUserApiResponse userApiResponse;
        @SerializedName("id")
        private long id;

        public DefaultUserDataApiResponse.DefaultUserApiResponse getUserApiResponse() {
            return userApiResponse;
        }

        public long getId() {
            return id;
        }
    }
}