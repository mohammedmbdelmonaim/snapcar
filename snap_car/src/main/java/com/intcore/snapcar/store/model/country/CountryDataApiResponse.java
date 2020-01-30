package com.intcore.snapcar.store.model.country;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryDataApiResponse {

    @SerializedName("countries")
    private List<CountryApiResponse> countryList;

    public List<CountryApiResponse> getCountryList() {
        return countryList;
    }

    public static class CountryApiResponse {
        @SerializedName("cities")
        private List<CountryApiResponse> countryApiResponses;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("phone_code")
        private String phoneCode;
        @SerializedName("image")
        private String image;
        @SerializedName("number_allow_digit")
        private String phoneRegex;
        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getPhoneCode() {
            return phoneCode;
        }

        public String getImage() {
            return image;
        }

        public String getPhoneRegex() {
            return phoneRegex;
        }

        public List<CountryApiResponse> getCountryApiResponses() {
            return countryApiResponses;
        }
    }
}