package com.intcore.snapcar.store.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.constant.CarCondition;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.core.chat.model.constants.Gender;

public class SearchRequestModel implements Parcelable {

    public static final Creator<SearchRequestModel> CREATOR = new Creator<SearchRequestModel>() {
        @Override
        public SearchRequestModel createFromParcel(Parcel in) {
            return new SearchRequestModel(in);
        }

        @Override
        public SearchRequestModel[] newArray(int size) {
            return new SearchRequestModel[size];
        }
    };
    private int mvpi;
    private int gender;
    private int brand_id;
    private int model_id;
    private int category_id;
    private String year_form;
    private String year_to;
    private String price_from;
    private String price_to;
    private int color_id;
    private int car_status;
    private int gear_type;
    private int warranty;
    private int tracked;
    private int postType;
    private int seller_type;
    private String latitude;
    private String longitude;
    private int engineCapacity;
    private String kilometer_to;
    private String kilometer_from;
    private int method_of_sale_id;
    private int car_specification_id;
    private int isInstallment;
    private int isBigSale;

    SearchRequestModel(int mvpi,
                       int gender,
                       int brand_id,
                       int model_id,
                       int category_id,
                       String year_form,
                       String year_to,
                       String price_from,
                       String price_to,
                       String kilometer_from,
                       int color_id,
                       int method_of_sale_id,
                       int car_status,
                       int gear_type,
                       int seller_type,
                       String latitude,
                       String longitude,
                       String kilometer_to,
                       int car_specification_id,
                       int engineCapacity,
                       int warranty,
                       int tracked,
                       int postType, int isInstallment, int isBigSale) {
        this.mvpi = mvpi;
        this.gender = gender;
        this.brand_id = brand_id;
        this.model_id = model_id;
        this.tracked = tracked;
        this.postType = postType;
        this.category_id = category_id;
        this.year_form = year_form;
        this.year_to = year_to;
        this.price_from = price_from;
        this.price_to = price_to;
        this.kilometer_from = kilometer_from;
        this.color_id = color_id;
        this.warranty = warranty;
        this.engineCapacity = engineCapacity;
        this.method_of_sale_id = method_of_sale_id;
        this.car_status = car_status;
        this.gear_type = gear_type;
        this.seller_type = seller_type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kilometer_to = kilometer_to;
        this.car_specification_id = car_specification_id;
        this.isInstallment = isInstallment;
        this.isBigSale = isBigSale;
    }

    protected SearchRequestModel(Parcel in) {
        isInstallment = in.readInt();
        isBigSale = in.readInt();
        mvpi = in.readInt();
        gender = in.readInt();
        brand_id = in.readInt();
        model_id = in.readInt();
        category_id = in.readInt();
        year_form = in.readString();
        year_to = in.readString();
        price_from = in.readString();
        price_to = in.readString();
        color_id = in.readInt();
        car_status = in.readInt();
        gear_type = in.readInt();
        warranty = in.readInt();
        tracked = in.readInt();
        postType = in.readInt();
        seller_type = in.readInt();
        latitude = in.readString();
        longitude = in.readString();
        engineCapacity = in.readInt();
        kilometer_to = in.readString();
        kilometer_from = in.readString();
        method_of_sale_id = in.readInt();
        car_specification_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isInstallment);
        dest.writeInt(isBigSale);
        dest.writeInt(mvpi);
        dest.writeInt(gender);
        dest.writeInt(brand_id);
        dest.writeInt(model_id);
        dest.writeInt(category_id);
        dest.writeString(year_form);
        dest.writeString(year_to);
        dest.writeString(price_from);
        dest.writeString(price_to);
        dest.writeInt(color_id);
        dest.writeInt(car_status);
        dest.writeInt(gear_type);
        dest.writeInt(warranty);
        dest.writeInt(tracked);
        dest.writeInt(postType);
        dest.writeInt(seller_type);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeInt(engineCapacity);
        dest.writeString(kilometer_to);
        dest.writeString(kilometer_from);
        dest.writeInt(method_of_sale_id);
        dest.writeInt(car_specification_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getMvpi() {
        return mvpi;
    }

    @Gender
    public int getGender() {
        return gender;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public int getModel_id() {
        return model_id;
    }

    public String getYear_form() {
        return year_form;
    }

    public String getYear_to() {
        return year_to;
    }

    public String getPrice_from() {
        return price_from;
    }

    public String getPrice_to() {
        return price_to;
    }

    public int getPostType() {
        return postType;
    }

    public int getTracked() {
        return tracked;
    }

    public String getKilometer_from() {
        return kilometer_from;
    }

    public int getColor_id() {
        return color_id;
    }

    public int getMethod_of_sale_id() {
        return method_of_sale_id;
    }

    @CarCondition
    public int getCar_status() {
        return car_status;
    }

    @GearType
    public int getGear_type() {
        return gear_type;
    }

    public int getWarranty() {
        return warranty;
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    @Gender
    public int getSeller_type() {
        return seller_type;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getKilometer_to() {
        return kilometer_to;
    }

    public static Creator<SearchRequestModel> getCREATOR() {
        return CREATOR;
    }

    public int getIsInstallment() {
        return isInstallment;
    }

    public int getIsBigSale() {
        return isBigSale;
    }

    public int getCar_specification_id() {
        return car_specification_id;
    }

    public Builder builder() {
        return new Builder()
                .mvpi(this.mvpi)
                .gender(this.gender)
                .brandId(this.brand_id)
                .modelId(this.model_id)
                .categoryId(this.category_id)
                .yearFrom(this.year_form)
                .yearTo(this.year_to)
                .priceFrom(this.price_from)
                .price_to(this.price_to)
                .kilometer_from(this.kilometer_from)
                .colorId(this.color_id)
                .method_of_sale_id(this.method_of_sale_id)
                .carStatus(this.car_status)
                .gear_type(this.gear_type)
                .sellerType(this.seller_type)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .kilometer_to(this.kilometer_to)
                .engineCapacity(this.engineCapacity)
                .warranty(this.warranty)
                .tracked(this.tracked)
                .postType(this.postType)
                .car_specification_id(this.car_specification_id);
    }

    public static class Builder {

        private int mvpi;
        private int gender;
        private int brand_id;
        private int model_id;
        private int category_id;
        private String year_form;
        private String year_to;
        private String price_from;
        private String price_to;
        private int color_id;
        private int car_status;
        private int gear_type;
        private int seller_type;
        private int tracked;
        private int postType;
        private String latitude;
        private String longitude;
        private String kilometer_to;
        private String kilometer_from;
        private int method_of_sale_id;
        private int car_specification_id;
        private int engineCapacity;
        private int warranty;
        private int isInstallment;
        private int isBigSale;

        public Builder() {

        }

        public Builder isInstallment(int isInstallment) {
            this.isInstallment = isInstallment;
            return this;
        }

        public Builder isBigSale(int isBigSale) {
            this.isBigSale = isBigSale;
            return this;
        }

        public Builder mvpi(int mvpi) {
            this.mvpi = mvpi;
            return this;
        }

        public Builder gender(int gender) {
            this.gender = gender;
            return this;
        }

        public Builder warranty(int warranty) {
            this.warranty = warranty;
            return this;
        }

        public Builder engineCapacity(int engineCapacity) {
            this.engineCapacity = engineCapacity;
            return this;
        }

        public Builder brandId(int brandId) {
            this.brand_id = brandId;
            return this;
        }

        public Builder modelId(int modelId) {
            this.model_id = modelId;
            return this;
        }

        public Builder tracked(int tracked) {
            this.tracked = tracked;
            return this;
        }

        public Builder postType(int postType) {
            this.postType = postType;
            return this;
        }

        public Builder categoryId(int categoryId) {
            this.category_id = categoryId;
            return this;
        }

        public Builder yearFrom(String year_form) {
            this.year_form = year_form;
            return this;
        }

        public Builder yearTo(String yearTo) {
            this.year_to = yearTo;
            return this;
        }

        public Builder priceFrom(String priceFrom) {
            this.price_from = priceFrom;
            return this;
        }

        public Builder price_to(String price_to) {
            this.price_to = price_to;
            return this;
        }

        public Builder colorId(int color_id) {
            this.color_id = color_id;
            return this;
        }

        public Builder carStatus(int car_status) {
            this.car_status = car_status;
            return this;
        }

        public Builder gear_type(int gear_type) {
            this.gear_type = gear_type;
            return this;
        }

        public Builder sellerType(int seller_type) {
            this.seller_type = seller_type;
            return this;
        }

        public Builder latitude(String latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(String longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder kilometer_to(String kilometer_to) {
            this.kilometer_to = kilometer_to;
            return this;
        }

        public Builder kilometer_from(String kilometer_from) {
            this.kilometer_from = kilometer_from;
            return this;
        }

        public Builder method_of_sale_id(int method_of_sale_id) {
            this.method_of_sale_id = method_of_sale_id;
            return this;
        }

        public Builder car_specification_id(int car_specification_id) {
            this.car_specification_id = car_specification_id;
            return this;
        }

        public SearchRequestModel build() {
            return new SearchRequestModel(mvpi, gender, brand_id, model_id, category_id, year_form, year_to,
                    price_from, price_to, kilometer_from, color_id, method_of_sale_id, car_status, gear_type,
                    seller_type, latitude, longitude, kilometer_to, car_specification_id, engineCapacity, warranty, tracked,
                    postType,isInstallment,isBigSale);
        }
    }
}
