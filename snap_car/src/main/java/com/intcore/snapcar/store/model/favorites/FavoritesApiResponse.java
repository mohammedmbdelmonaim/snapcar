package com.intcore.snapcar.store.model.favorites;

import com.google.gson.annotations.SerializedName;
import com.intcore.snapcar.store.model.carresource.CarResourcesApiResponse;

import java.util.List;

public class FavoritesApiResponse {
    @SerializedName("data")
    private List<Data> favoritesData;

    public List<Data> getFavoritesData() {
        return favoritesData;
    }


    public class Data {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int userid;
        @SerializedName("favouriteable_type")
        private String favouriteableType;
        @SerializedName("favouriteable_id")
        private int favouriteableId;
        @SerializedName("type")
        private String type;
        @SerializedName("favouriteable")
        private Favouriteable favouriteable;


        public int getId() {
            return id;
        }

        public int getUserid() {
            return userid;
        }

        public String getFavouriteableType() {
            return favouriteableType;
        }

        public int getFavouriteableId() {
            return favouriteableId;
        }

        public String getType() {
            return type;
        }

        public Favouriteable getFavouriteable() {
            return favouriteable;
        }

    }

    public class Favouriteable {
        @SerializedName("id")
        private int id;
        private int contactOption;
        private int posttype;
        private int isTracked;
        private int carStatus;
        @SerializedName("name")
        private String name;
        @SerializedName("gender")
        private int gender;
        @SerializedName("available_car")
        private String showroomCars;
        @SerializedName("image")
        private String showroomImage;
        @SerializedName("manufacturing_year")
        private String manufacturingYear;
        private int gearType;
        private String price;
        private String currency;
        private String commission;
        private int carSpecificationId;
        @SerializedName("transmission")
        private int transmission;
        private int methodOfSale;
        @SerializedName("car_color_id")
        private int ColorId;
        private String kilometer;
        @SerializedName("under_warranty")
        private int underWarranty;
        private int mvpi;
        private int carCondition;
        private int views;
        private int brandId;
        private int modelId;
        private String longitude;
        private String latitude;
        private int isSold;
        @SerializedName("showroom_info")
        private ShowroomInfo showroomInfo;
        private int guarantee;
        @SerializedName("price_after")
        private String priceAfter;
        @SerializedName("price_before")
        private String priceBefore;
        private int engineCapacity;
        @SerializedName("warranty")
        private String warranty;
        @SerializedName("kilometer_from")
        private String kmFrom;
        @SerializedName("kilometer_to")
        private String kmTo;
        @SerializedName("installment_price_from")
        private String installmentFrom;
        @SerializedName("installment_price_to")
        private String installmenTo;
        @SerializedName("exchange")
        private int exchange;
        @SerializedName("examination_image")
        private String examinationImage;
        private int categoryId;
        @SerializedName("is_premium")
        private boolean isPremium;
        @SerializedName("car_name")
        private String carName;
        @SerializedName("car_name_en")
        private String carNameEn;
        @SerializedName("is_fav")
        private boolean isFav;
        @SerializedName("car_color")
        private CarResourcesApiResponse.Colors carCalor;
        @SerializedName("images")
        private List<Image> carImages;
        @SerializedName("user")
        private User user;
        @SerializedName("car_specification")
        private CarResourcesApiResponse.Specifications carSpecification;
        @SerializedName("price_type")
        private int priceType;

        public CarResourcesApiResponse.Specifications getCarSpecification() {
            return carSpecification;
        }

        public int getPriceType() {
            return priceType;
        }

        public String getCarNameEn() {
            return carNameEn;
        }

        public ShowroomInfo getShowroomInfo() {
            return showroomInfo;
        }

        public String getName() {
            return name;
        }

        public String getShowroomCars() {
            return showroomCars;
        }

        public String getShowroomImage() {
            return showroomImage;
        }

        public int getId() {
            return id;
        }

        public int getContactOption() {
            return contactOption;
        }

        public int getPosttype() {
            return posttype;
        }

        public int getIsTracked() {
            return isTracked;
        }

        public int getCarStatus() {
            return carStatus;
        }

        public String getManufacturingYear() {
            return manufacturingYear;
        }

        public int getGearType() {
            return gearType;
        }

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public String getCommission() {
            return commission;
        }

        public int getCarSpecificationId() {
            return carSpecificationId;
        }

        public int getTransmission() {
            return transmission;
        }

        public int getMethodOfSale() {
            return methodOfSale;
        }

        public int getColorId() {
            return ColorId;
        }

        public String getKilometer() {
            return kilometer;
        }

        public int getUnderWarranty() {
            return underWarranty;
        }

        public int getMvpi() {
            return mvpi;
        }

        public int getCarCondition() {
            return carCondition;
        }

        public int getViews() {
            return views;
        }

        public int getBrandId() {
            return brandId;
        }

        public int getModelId() {
            return modelId;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public int getIsSold() {
            return isSold;
        }

        public int getGender() {
            return gender;
        }

        public int getGuarantee() {
            return guarantee;
        }

        public String getPriceAfter() {
            return priceAfter;
        }

        public String getPriceBefore() {
            return priceBefore;
        }

        public int getEngineCapacity() {
            return engineCapacity;
        }

        public String getWarranty() {
            return warranty;
        }

        public String getKmFrom() {
            return kmFrom;
        }

        public String getKmTo() {
            return kmTo;
        }

        public String getInstallmentFrom() {
            return installmentFrom;
        }

        public String getInstallmenTo() {
            return installmenTo;
        }

        public int getExchange() {
            return exchange;
        }

        public String getExaminationImage() {
            return examinationImage;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public boolean isPremium() {
            return isPremium;
        }

        public String getCarName() {
            return carName;
        }

        public boolean isFav() {
            return isFav;
        }

        public CarResourcesApiResponse.Colors getCarCalor() {
            return carCalor;
        }

        public List<Image> getCarImages() {
            return carImages;
        }

        public User getUser() {
            return user;
        }
    }

    public class User {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("email")
        private String email;
        @SerializedName("phone")
        private String phone;
        @SerializedName("type")
        private int type;
        @SerializedName("image")
        private String image;
        @SerializedName("gender")
        private String gender;
        @SerializedName("api_token")
        private String apiToken;
        @SerializedName("country_id")
        private int countryId;
        @SerializedName("city_id")
        private int cityId;
        @SerializedName("area")
        private String area;
        @SerializedName("user_rate")
        private float useRate;
        @SerializedName("total_rate")
        private float totalRate;
        @SerializedName("city")
        private City city;

        public City getCity() {
            return city;
        }
//        @SerializedName("tracked_car")
//        private List<Car> trackedCar;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public int getType() {
            return type;
        }

        public String getImage() {
            return image;
        }

        public String getGender() {
            return gender;
        }

        public String getApiToken() {
            return apiToken;
        }

        public int getCountryId() {
            return countryId;
        }

        public int getCityId() {
            return cityId;
        }

        public String getArea() {
            return area;
        }

        public float getUseRate() {
            return useRate;
        }

        public float getTotalRate() {
            return totalRate;
        }

//        public List<Car> getTrackedCar() {
//            return trackedCar;
//        }
    }

    public class Car {
        @SerializedName("id")
        private int id;
        @SerializedName("user_id")
        private int usrId;
        @SerializedName("contact_option")
        private int contactOption;
        @SerializedName("post_type")
        private int postType;
        @SerializedName("is_tracked")
        private int isTracked;
        @SerializedName("car_status")
        private int carStatus;
        @SerializedName("manufacturing_year")
        private int carYear;
        @SerializedName("gear_type")
        private int gearTyped;
        @SerializedName("price")
        private String price;
        @SerializedName("currency")
        private String currency;
        @SerializedName("commission")
        private String commission;
        @SerializedName("car_specification_id")
        private int specificationId;
        @SerializedName("method_of_sale_id")
        private int methodOfSale;
        @SerializedName("car_color_id")
        private int colorId;
        @SerializedName("transmission")
        private int transmission;
        @SerializedName("kilometer")
        private String kilometer;
        @SerializedName("under_warranty")
        private int underWarranty;
        @SerializedName("mvpi")
        private int mvpi;
        @SerializedName("car_condition")
        private String condition;
        @SerializedName("notes")
        private String notes;
        @SerializedName("views")
        private int views;
        @SerializedName("brand_id")
        private int brandId;
        @SerializedName("car_model_id")
        private int modelId;
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("is_sold")
        private int isSold;
        @SerializedName("gender")
        private int gender;
        @SerializedName("price_after")
        private String priceAfter;
        @SerializedName("price_before")
        private String priceBefore;
        @SerializedName("engine_capacity_cc")
        private int engineCapacity;
        @SerializedName("warranty")
        private String warranty;
        @SerializedName("kilometer_from")
        private String kmFrom;
        @SerializedName("kilometer_to")
        private String kmTo;
        @SerializedName("installment_price_to")
        private String installmentTo;
        @SerializedName("installment_price_from")
        private String installmentFrom;
        @SerializedName("exchange")
        private int exchange;
        @SerializedName("examination_image")
        private String examinationImage;
        @SerializedName("car_name")
        private String carName;
        @SerializedName("car_name_en")
        private String carNameEn;
        @SerializedName("category_id")
        private int categoryId;
        @SerializedName("is_fav")
        private boolean isFav;
        @SerializedName("brand")
        private CarResourcesApiResponse.Brands brand;
        @SerializedName("car_model")
        private CarResourcesApiResponse.Brands model;
        @SerializedName("car_specification")
        private CarResourcesApiResponse.Specifications specifications;

        public int getId() {
            return id;
        }

        public int getUsrId() {
            return usrId;
        }

        public int getContactOption() {
            return contactOption;
        }

        public int getPostType() {
            return postType;
        }

        public int getIsTracked() {
            return isTracked;
        }

        public int getCarStatus() {
            return carStatus;
        }

        public int getCarYear() {
            return carYear;
        }

        public String getCarNameEn() {
            return carNameEn;
        }

        public int getGearTyped() {
            return gearTyped;
        }

        public String getPrice() {
            return price;
        }

        public String getCurrency() {
            return currency;
        }

        public String getCommission() {
            return commission;
        }

        public int getSpecificationId() {
            return specificationId;
        }

        public int getMethodOfSale() {
            return methodOfSale;
        }

        public int getColorId() {
            return colorId;
        }

        public int getTransmission() {
            return transmission;
        }

        public String getKilometer() {
            return kilometer;
        }

        public int getUnderWarranty() {
            return underWarranty;
        }

        public int getMvpi() {
            return mvpi;
        }

        public String getCondition() {
            return condition;
        }

        public String getNotes() {
            return notes;
        }

        public int getViews() {
            return views;
        }

        public int getBrandId() {
            return brandId;
        }

        public int getModelId() {
            return modelId;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public int getIsSold() {
            return isSold;
        }

        public int getGender() {
            return gender;
        }

        public String getPriceAfter() {
            return priceAfter;
        }

        public String getPriceBefore() {
            return priceBefore;
        }

        public int getEngineCapacity() {
            return engineCapacity;
        }

        public String getWarranty() {
            return warranty;
        }

        public String getKmFrom() {
            return kmFrom;
        }

        public String getKmTo() {
            return kmTo;
        }

        public String getInstallmentTo() {
            return installmentTo;
        }

        public String getInstallmentFrom() {
            return installmentFrom;
        }

        public int getExchange() {
            return exchange;
        }

        public String getExaminationImage() {
            return examinationImage;
        }

        public String getCarName() {
            return carName;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public boolean isFav() {
            return isFav;
        }

        public CarResourcesApiResponse.Brands getBrand() {
            return brand;
        }

        public CarResourcesApiResponse.Brands getModel() {
            return model;
        }

        public CarResourcesApiResponse.Specifications getSpecifications() {
            return specifications;
        }
    }


    public class Image {
        @SerializedName("id")
        private String id;
        @SerializedName("car_id")
        private String carId;
        @SerializedName("image")
        private String image;
        @SerializedName("place")
        private String place;
        @SerializedName("is_main")
        private String isMain;

        public String getId() {
            return id;
        }

        public String getCarId() {
            return carId;
        }

        public String getImage() {
            return image;
        }

        public String getPlace() {
            return place;
        }

        public String getIsMain() {
            return isMain;
        }
    }


    public class City {
        @SerializedName("id")
        private int id;
        @SerializedName("name_ar")
        private String nameAr;
        @SerializedName("name_en")
        private String nameEn;
        @SerializedName("country_id")
        private String countryId;

        public int getId() {
            return id;
        }

        public String getNameAr() {
            return nameAr;
        }

        public String getNameEn() {
            return nameEn;
        }

        public String getCountryId() {
            return countryId;
        }
    }

    public class ShowroomInfo{
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("latitude")
        private String latitude;

        public String getLongitude() {
            return longitude;
        }

        public String getLatitude() {
            return latitude;
        }
    }
}

