package com.intcore.snapcar.store.model.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SurveyApiResponse {

    @SerializedName("survies")
    private List<Surveies> survies;

    public List<Surveies> getSurvies() {
        return survies;
    }

    public class Surveies {
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("questions")
        private List<Questions> questions;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public List<Questions> getQuestions() {
            return questions;
        }
    }

    public class Questions implements Serializable {
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("type")
        private int type;
        @SerializedName("answers")
        private String answers;
        @SerializedName("survey_id")
        private int surveyId;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public int getType() {
            return type;
        }

        public String getAnswers() {
            return answers;
        }

        public int getSurveyId() {
            return surveyId;
        }
    }
}
