package com.intcore.snapcar.store.model.carresource;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.brands.BrandsApiResponse;
import com.intcore.snapcar.store.model.carcolor.CarColorApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterApiResponse;

import java.util.List;

public class CarResourcesApiResponse  {

    @SerializedName("brands")
    private List<BrandsApiResponse> brands;
    @SerializedName("currencies")
    private List<Currencies> currencies;

    @SerializedName("car_colors")
    private List<CarColorApiResponse> colors;

    @SerializedName("specifications")
    private List<ImporterApiResponse> specifications;
    @SerializedName("has_tracked_car")
    private int hasTrackedCar ;
    @SerializedName("published_cars")
    private int publishedCars ;
    @SerializedName("available_car_to_add")
    private int availableCarToAdd ;

    public List<BrandsApiResponse> getBrands() {
        return brands;
    }

    public List<Currencies> getCurrencies() {
        return currencies;
    }

    public List<CarColorApiResponse> getColors() {
        return colors;
    }

    public List<ImporterApiResponse> getSpecifications() {
        return specifications;
    }

    public int getHasTrackedCar() {
        return hasTrackedCar;
    }

    public int getPublishedCars() {
        return publishedCars;
    }

    public int getAvailableCarToAdd() {
        return availableCarToAdd;
    }

    public static class Brands{
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("car_models")
        private List<Models> models;
        @SerializedName("image")
        private String brandImage;

        public String getBrandImage() {
            return brandImage;
        }

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public List<Models> getModels() {
            return models;
        }
    }

    public static class Models {
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("brand_id")
        private String brandId;

        @SerializedName("category")
        private List<Category> categories;
        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getBrandId() {
            return brandId;
        }


        public List<Category> getCategories() {
            return categories;
        }

    }

    public static class Currencies{
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }
    }

    public static class Colors{
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("hex")
        private String hex;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getHex() {
            return hex;
        }
    }

    public static class Specifications{
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }
    }

    public static class Category{
        @SerializedName("id")
        private int id ;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("car_model_id")
        private String model_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public void setNameAr(String nameAr) {
            this.nameAr = nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getModel_id() {
            return model_id;
        }

        public void setModel_id(String model_id) {
            this.model_id = model_id;
        }
    }
}
