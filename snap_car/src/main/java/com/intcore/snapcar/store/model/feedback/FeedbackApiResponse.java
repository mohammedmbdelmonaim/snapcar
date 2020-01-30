package com.intcore.snapcar.store.model.feedback;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class FeedbackApiResponse {
    @SerializedName("feedbackCategories")
    private List<FeedBackData> feedBackData;

    public List<FeedBackData> getFeedBackData() {
        return feedBackData;
    }
    public static class FeedBackData {
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String getNameEn;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getGetNameEn() {
            return getNameEn;
        }
    }
}
