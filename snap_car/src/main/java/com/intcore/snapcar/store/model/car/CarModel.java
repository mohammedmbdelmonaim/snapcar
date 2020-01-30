package com.intcore.snapcar.store.model.car;

import com.intcore.snapcar.store.model.brands.BrandsModel;
import com.intcore.snapcar.store.model.carcolor.CarColorModel;
import com.intcore.snapcar.store.model.cartracking.CarTrackingModel;
import com.intcore.snapcar.store.model.category.CategoryModel;
import com.intcore.snapcar.store.model.country.CountryModel;
import com.intcore.snapcar.store.model.images.ImageModel;
import com.intcore.snapcar.store.model.importer.ImporterModel;
import com.intcore.snapcar.store.model.model.ModelModel;

import java.util.List;

public class CarModel {

    private Integer id;
    private String price;
    private Integer mvpi;
    private String notes;
    private Integer views;
    private Integer userId;
    private Integer brandId;
    private Integer isSold;
    private Integer gender;
    private String carName;
    private String exchange;
    private String currency;
    private String deletedAt;
    private String createdAt;
    private String warranty;
    private String updatedAt;
    private Integer gearType;
    private String kilometer;
    private String latitude;
    private String expiredAt;
    private String longitude;
    private Integer guarantee;
    private Boolean isExpired;
    private String commission;
    private Integer postType;
    private Integer isTracked;
    private Integer carStatus;
    private String priceAfter;
    private Boolean isPremium;
    private String priceBefore;
    private String kilometerTo;
    private Integer carColorId;
    private Integer categoryId;
    private Integer carModelId;
    private Boolean isFavorite;
    private String kilometerFrom;
    private Integer transmission;
    private Integer contactOption;
    private Integer underWarranty;
    private Integer methodOfSaleId;
    private String expiredPremiumAt;
    private String examinationImage;
    private Integer engineCapacityCc;
    private String manufacturingYear;
    private Integer carSpecificationId;
    private String installmentPriceTo;
    private String installmentPriceFrom;
    private ModelModel modelModel;
    private BrandsModel brandsModel;
    private CarTrackingModel carTrackingModel;
    private List<ImageModel> imageModels;
    private ImporterModel importerModel;
    private CarColorModel carColorModel;
    private CountryModel countryModel;
    private CountryModel cityModel;
    private CategoryModel categoryModel;
    private String priceFrom;
    private String priceTo;
    private String yearFrom;
    private String yearTo;
    private int nearby;
    private int vehical;
    private int sellerType;
    private int carCondition;
    private int saleId;
    private int priceType;
    private Integer gearTypeInterest;

    public CarModel(Integer id,
                    String price,
                    Integer mvpi,
                    String notes,
                    Integer views,
                    Integer userId,
                    Integer brandId,
                    Integer isSold,
                    Integer gender,
                    String carName,
                    String exchange,
                    String currency,
                    String deletedAt,
                    String createdAt,
                    String warranty,
                    String updatedAt,
                    Integer gearType,
                    String kilometer,
                    String latitude,
                    String expiredAt,
                    String longitude,
                    Integer guarantee,
                    Boolean isExpired,
                    String commission,
                    Integer postType,
                    Integer isTracked,
                    Integer carStatus,
                    String priceAfter,
                    Boolean isPremium,
                    String priceBefore,
                    String kilometerTo,
                    Integer carColorId,
                    Integer categoryId,
                    Integer carModelId,
                    String kilometerFrom,
                    Integer transmission,
                    Integer contactOption,
                    Integer underWarranty,
                    Integer methodOfSaleId,
                    String expiredPremiumAt,
                    String examinationImage,
                    Integer engineCapacityCc,
                    String manufacturingYear,
                    Integer carSpecificationId,
                    String installmentPriceTo,
                    String installmentPriceFrom,
                    ModelModel modelModel,
                    BrandsModel brandsModel,
                    CarTrackingModel carTrackingModel,
                    Boolean isFavorite,
                    List<ImageModel> imageModels,
                    ImporterModel importerModel,
                    CarColorModel carColorModel,
                    CategoryModel categoryModel,
                    CountryModel countryModel,
                    CountryModel cityModel,
                    String priceFrom,
                    String priceTo,
                    String yearFrom,
                    String yearTo,
                    int nearby,
                    int sellerType,
                    int carCondition,
                    int vehical,
                    int saleId,
                    int priceType,
                    Integer gearTypeInterest) {
        this.id = id;
        this.sellerType = sellerType;
        this.price = price;
        this.mvpi = mvpi;
        this.notes = notes;
        this.views = views;
        this.userId = userId;
        this.brandId = brandId;
        this.isSold = isSold;
        this.vehical = vehical;
        this.carCondition = carCondition;
        this.gender = gender;
        this.carName = carName;
        this.exchange = exchange;
        this.currency = currency;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.warranty = warranty;
        this.updatedAt = updatedAt;
        this.gearType = gearType;
        this.nearby = nearby;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.kilometer = kilometer;
        this.latitude = latitude;
        this.expiredAt = expiredAt;
        this.longitude = longitude;
        this.guarantee = guarantee;
        this.isExpired = isExpired;
        this.commission = commission;
        this.postType = postType;
        this.isTracked = isTracked;
        this.carStatus = carStatus;
        this.priceAfter = priceAfter;
        this.isPremium = isPremium;
        this.priceBefore = priceBefore;
        this.kilometerTo = kilometerTo;
        this.carColorId = carColorId;
        this.categoryId = categoryId;
        this.carModelId = carModelId;
        this.gearTypeInterest = gearTypeInterest;
        this.kilometerFrom = kilometerFrom;
        this.transmission = transmission;
        this.contactOption = contactOption;
        this.underWarranty = underWarranty;
        this.methodOfSaleId = methodOfSaleId;
        this.expiredPremiumAt = expiredPremiumAt;
        this.examinationImage = examinationImage;
        this.engineCapacityCc = engineCapacityCc;
        this.manufacturingYear = manufacturingYear;
        this.carSpecificationId = carSpecificationId;
        this.installmentPriceTo = installmentPriceTo;
        this.installmentPriceFrom = installmentPriceFrom;
        this.modelModel = modelModel;
        this.categoryModel = categoryModel;
        this.countryModel = countryModel;
        this.cityModel = cityModel;
        this.brandsModel = brandsModel;
        this.isFavorite = isFavorite;
        this.carTrackingModel = carTrackingModel;
        this.imageModels = imageModels;
        this.carColorModel = carColorModel;
        this.importerModel = importerModel;
        this.saleId = saleId;
        this.priceType = priceType;
    }

    public int getPriceType() {
        return priceType;
    }

    public int getSaleId() {
        return saleId;
    }

    public Integer getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public Integer getMvpi() {
        return mvpi;
    }

    public String getNotes() {
        return notes;
    }

    public int getVehical() {
        return vehical;
    }

    public int getSellerType() {
        return sellerType;
    }

    public int getCarCondition() {
        return carCondition;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public Integer getIsSold() {
        return isSold;
    }

    public Integer getGender() {
        return gender;
    }

    public String getCarName() {
        return carName;
    }

    public String getExchange() {
        return exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getWarranty() {
        return warranty;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getYearTo() {
        return yearTo;
    }

    public String getYearFrom() {
        return yearFrom;
    }

    public String getPriceTo() {
        return priceTo;
    }

    public String getPriceFrom() {
        return priceFrom;
    }

    public int getNearby() {
        return nearby;
    }

    public Integer getGearType() {
        return gearType;
    }

    public String getKilometer() {
        return kilometer;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public String getLongitude() {
        return longitude;
    }

    public Integer getGuarantee() {
        return guarantee;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public String getCommission() {
        return commission;
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

    public String getPriceAfter() {
        return priceAfter;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public String getPriceBefore() {
        return priceBefore;
    }

    public String getKilometerTo() {
        return kilometerTo;
    }

    public Integer getCarColorId() {
        return carColorId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public String getKilometerFrom() {
        return kilometerFrom;
    }

    public Integer getTransmission() {
        return transmission;
    }

    public Integer getContactOption() {
        return contactOption;
    }

    public Integer getUnderWarranty() {
        return underWarranty;
    }

    public List<ImageModel> getImageModels() {
        return imageModels;
    }

    public CarColorModel getCarColorModel() {
        return carColorModel;
    }

    public ImporterModel getImporterModel() {
        return importerModel;
    }

    public Integer getMethodOfSaleId() {
        return methodOfSaleId;
    }

    public String getExpiredPremiumAt() {
        return expiredPremiumAt;
    }

    public String getExaminationImage() {
        return examinationImage;
    }

    public Integer getEngineCapacityCc() {
        return engineCapacityCc;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public Integer getCarSpecificationId() {
        return carSpecificationId;
    }

    public String getInstallmentPriceTo() {
        return installmentPriceTo;
    }

    public String getInstallmentPriceFrom() {
        return installmentPriceFrom;
    }

    public ModelModel getModelModel() {
        return modelModel;
    }

    public Integer getGearTypeInterest() {
        return gearTypeInterest;
    }

    public BrandsModel getBrandsModel() {
        return brandsModel;
    }

    public CarTrackingModel getCarTrackingModel() {
        return carTrackingModel;
    }

    public CountryModel getCountryModel() {
        return countryModel;
    }

    public CountryModel getCityModel() {
        return cityModel;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }
}