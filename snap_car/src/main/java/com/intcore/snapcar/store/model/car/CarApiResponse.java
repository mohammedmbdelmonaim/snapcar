package com.intcore.snapcar.store.model.car;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.brands.BrandsApiResponse;
import com.intcore.snapcar.store.model.carcolor.CarColorApiResponse;
import com.intcore.snapcar.store.model.cartracking.CarTrackingApiResponse;
import com.intcore.snapcar.store.model.category.CategoryApiResponse;
import com.intcore.snapcar.store.model.constant.FilterType;
import com.intcore.snapcar.store.model.country.CountryDataApiResponse;
import com.intcore.snapcar.store.model.images.ImagesApiResponse;
import com.intcore.snapcar.store.model.importer.ImporterApiResponse;
import com.intcore.snapcar.store.model.model.ModelApiResponse;

import java.util.List;
import java.util.Locale;

public class CarApiResponse {
    @SerializedName("id")
    private Integer id;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("contact_option")
    private Integer contactOption;
    @SerializedName("post_type")
    private Integer postType;
    @SerializedName("is_tracked")
    private Integer isTracked;
    @SerializedName("car_status")
    private Integer carStatus;
    @SerializedName("manufacturing_year")
    private String manufacturingYear;
    @SerializedName("transmissionnn")
    private Integer gearType;
    @SerializedName("price")
    private String price;
    @SerializedName("currency")
    private String currency;
    @SerializedName("commission")
    private String commission;
    @SerializedName("car_specification_id")
    private Integer carSpecificationId;
    @SerializedName("transmission")
    private Integer transmission;
    @SerializedName("method_of_sale_id")
    private Integer methodOfSaleId;
    @SerializedName("car_color_id")
    private Integer carColorId;
    @SerializedName("kilometer")
    private String kilometer;
    @SerializedName("under_warranty")
    private Integer underWarranty;
    @SerializedName("mvpi")
    private Integer mvpi;
    @SerializedName("car_date_en")
    private String carDateEn;
    @SerializedName("car_date_ar")
    private String carDateAr;
    @SerializedName("notes")
    private String notes;
    @SerializedName("views")
    private Integer views;
    @SerializedName("brand_id")
    private Integer brandId;
    @SerializedName("car_model_id")
    private Integer carModelId;
    @SerializedName("expired_premium_at")
    private String expiredPremiumAt;
    @SerializedName("expired_at")
    private String expiredAt;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("is_sold")
    private Integer isSold;
    @SerializedName("gender")
    private Integer gender;
    @SerializedName("deleted_at")
    private String deletedAt;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("guarantee")
    private Integer guarantee;
    @SerializedName("is_expired")
    private Boolean isExpired;
    @SerializedName("price_after")
    private String priceAfter;
    @SerializedName("price_before")
    private String priceBefore;
    @SerializedName("engine_capacity_cc")
    private Integer engineCapacityCc;
    @SerializedName("warranty")
    private String warranty;
    @SerializedName("kilometer_from")
    private String kilometerFrom;
    @SerializedName("kilometer_to")
    private String kilometerTo;
    @SerializedName("installment_price_from")
    private String installmentPriceFrom;
    @SerializedName("car_condition")
    private int carCondition;
    @SerializedName("installment_price_to")
    private String installmentPriceTo;
    @SerializedName("exchange")
    private String exchange;
    @SerializedName("examination_image")
    private String examinationImage;
    @SerializedName("category_id")
    private Integer categoryId;
    @SerializedName("is_premium")
    private Boolean isPremium;
    @SerializedName("car_name")
    private String carName;
    @SerializedName("car_name_en")
    private String carNameEn;
    @SerializedName("is_fav")
    private Boolean isFavorite;
    @SerializedName("price_from")
    private String priceFrom;
    @SerializedName("price_to")
    private String priceTo;
    @SerializedName("year_from")
    private String yearFrom;
    @SerializedName("year_to")
    private String yearTo;
    @SerializedName("nearby")
    private int nearby;
    @SerializedName("seller_type")
    private int sellerType;
    @SerializedName("images")
    private List<ImagesApiResponse> imagesApiResponses;
    @SerializedName("car_tracking")
    private CarTrackingApiResponse carTrackingApiResponse;
    @SerializedName("car_specification")
    private ImporterApiResponse importerApiResponse;
    @SerializedName("car_color")
    private CarColorApiResponse carColorApiResponse;
    @SerializedName("country")
    private CountryDataApiResponse.CountryApiResponse countryApiResponse;
    @SerializedName("city")
    private CountryDataApiResponse.CountryApiResponse cityApiResponse;
    @SerializedName("brand")
    private BrandsApiResponse brandsApiResponse;
    @SerializedName("car_model")
    private ModelApiResponse modelApiResponse;
    @SerializedName("category")
    private CategoryApiResponse categoryApiResponse;
    @SerializedName("user")
    private User user;
    @SerializedName("vehicle_registration")
    private int vehicleRegistration;
    @SerializedName("price_type")
    private int priceType;
    @SerializedName("big_sale_id")
    private int saleId;
    @SerializedName("gear_type")
    private Integer gearTypeInterest;


    //custom
    private int carType;

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getPriceType() {
        return priceType;
    }

    public int getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getCarNameEn() {
        return carNameEn;
    }

    public CarColorApiResponse getCarColorApiResponse() {
        return carColorApiResponse;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public User getUser() {
        return user;
    }

    public Integer getId() {
        return id;
    }

    public Integer getGearTypeInterest() {
        return gearTypeInterest;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getCarDateAr() {
        return carDateAr;
    }

    public String getCarDateEn() {
        return carDateEn;
    }

    public Integer getContactOption() {
        return contactOption;
    }

    public Integer getPostType() {
        return postType;
    }

    public Integer getIsTracked() {
        return isTracked;
    }

    public Integer getCarStatus() {
        return carStatus;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public Integer getGearType() {
        return gearType;
    }

    public String getPrice() {
        return price;
    }

    public int getSellerType() {
        return sellerType;
    }

    public int getNearby() {
        return nearby;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public String getPriceTo() {
        return priceTo;
    }

    public String getYearFrom() {
        return yearFrom;
    }

    public String getYearTo() {
        return yearTo;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCommission() {
        return commission;
    }

    public Integer getCarSpecificationId() {
        return carSpecificationId;
    }

    public Integer getTransmission() {
        return transmission;
    }

    public Integer getMethodOfSaleId() {
        return methodOfSaleId;
    }

    public Integer getCarColorId() {
        return carColorId;
    }

    public String getKilometer() {
        return kilometer;
    }

    public int getCarCondition() {
        return carCondition;
    }

    public Integer getUnderWarranty() {
        return underWarranty;
    }

    public Integer getMvpi() {
        return mvpi;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public String getExpiredPremiumAt() {
        return expiredPremiumAt;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public Integer getIsSold() {
        return isSold;
    }

    public Integer getGender() {
        return gender;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Boolean isFvorite() {
        return isFavorite;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getGuarantee() {
        return guarantee;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public String getPriceAfter() {
        return priceAfter;
    }

    public String getPriceBefore() {
        return priceBefore;
    }

    public Integer getEngineCapacityCc() {
        return engineCapacityCc;
    }

    public String getWarranty() {
        return warranty;
    }

    public String getKilometerFrom() {
        return kilometerFrom;
    }

    public String getKilometerTo() {
        return kilometerTo;
    }

    public String getInstallmentPriceFrom() {
        return installmentPriceFrom;
    }

    public String getInstallmentPriceTo() {
        return installmentPriceTo;
    }

    public String getCarName() {
        if (isEnglishLang())
            return carNameEn;
        else
            return carName;
    }

    public String getExchange() {
        return exchange;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public String getExaminationImage() {
        return examinationImage;
    }

    public BrandsApiResponse getBrandsApiResponse() {
        return brandsApiResponse;
    }

    public CountryDataApiResponse.CountryApiResponse getCityApiResponse() {
        return cityApiResponse;
    }

    public CountryDataApiResponse.CountryApiResponse getCountryApiResponse() {
        return countryApiResponse;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImporterApiResponse getImporterApiResponse() {
        return importerApiResponse;
    }

    public CategoryApiResponse getCategoryApiResponse() {
        return categoryApiResponse;
    }

    public List<ImagesApiResponse> getImagesApiResponses() {
        return imagesApiResponses;
    }

    public CarTrackingApiResponse getCarTrackingApiResponse() {
        return carTrackingApiResponse;
    }

    public ModelApiResponse getModelApiResponse() {
        return modelApiResponse;
    }

    public LatLng getLocation(){
        if (latitude == null || longitude == null || latitude.isEmpty() || longitude.isEmpty())
            return null;

        return new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
    }

    private boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    public class User {
        @SerializedName("name")
        private String name;
        @SerializedName("image")
        private String image;
        @SerializedName("area")
        private String area;
        @SerializedName("phone")
        private String phone;
        @SerializedName("gender")
        private Integer gender;
        @SerializedName("city")
        private City city;
        @SerializedName("country")
        private Country country;
        @SerializedName("type")
        private int type;

        public int getType() {
            return type;
        }

        public String getPhone() {
            return phone;
        }

        public City getCity() {
            return city;
        }

        public Country getCountry() {
            return country;
        }

        public Integer getGender() {
            return gender;
        }

        public String getImage() {
            return image;
        }

        public String getArea() {
            return area;
        }

        public String getName() {
            return name;
        }
    }

    public class City {
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }
    }

    public class Country {
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }
    }
}
