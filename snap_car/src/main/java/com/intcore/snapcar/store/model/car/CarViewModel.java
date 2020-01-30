package com.intcore.snapcar.store.model.car;

import android.os.Parcel;
import android.os.Parcelable;

import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.carcolor.CarColorViewModel;
import com.intcore.snapcar.store.model.cartracking.CarTrackingViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.GearType;
import com.intcore.snapcar.store.model.constant.PaymentType;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.images.ImageViewModel;
import com.intcore.snapcar.store.model.importer.ImporterViewModel;
import com.intcore.snapcar.store.model.model.ModelViewModel;

import java.util.List;

public class CarViewModel implements Parcelable {

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
    private String warranty;
    private Integer gearType;
    private String kilometer;
    private String latitude;
    private String expiredAt;
    private String longitude;
    private Integer guarantee;
    private Boolean isExpired;
    private String commission;
    private Integer postType;
    private String createdAt;
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
    private ModelViewModel modelViewModel;
    private BrandsViewModel brandsViewModel;
    private CarTrackingViewModel carTrackingViewModel;
    private List<ImageViewModel> imageViewModelList;
    private ImporterViewModel importerViewModel;
    private CarColorViewModel carColorViewModel;
    private CountryViewModel countryViewModel;
    private CountryViewModel cityViewModel;
    private CategoryViewModel categoryViewModel;
    private String priceFrom;
    private String priceTo;
    private String yearFrom;
    private String carNameEn;
    private String yearTo;
    private int nearby;
    private int sellerType;
    private int vehical;
    private int carCondition;
    private int saleId;
    private int priceType;
    private Integer gearTypeInterest;

    CarViewModel(Integer id,
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
                 String warranty,
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
                 ModelViewModel modelViewModel,
                 BrandsViewModel brandsViewModel,
                 CarTrackingViewModel carTrackingViewModel,
                 String createdAt,
                 List<ImageViewModel> imageViewModels,
                 CarColorViewModel carColorViewModel,
                 ImporterViewModel importerViewModel,
                 Boolean isFavorite,
                 CategoryViewModel categoryViewModel,
                 CountryViewModel countryViewModel,
                 CountryViewModel cityViewModel,
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
        this.price = price;
        this.carCondition = carCondition;
        this.sellerType = sellerType;
        this.mvpi = mvpi;
        this.notes = notes;
        this.views = views;
        this.userId = userId;
        this.vehical = vehical;
        this.brandId = brandId;
        this.isSold = isSold;
        this.gender = gender;
        this.carName = carName;
        this.exchange = exchange;
        this.currency = currency;
        this.warranty = warranty;
        this.gearType = gearType;
        this.kilometer = kilometer;
        this.latitude = latitude;
        this.gearTypeInterest = gearTypeInterest;
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
        this.isFavorite = isFavorite;
        this.priceBefore = priceBefore;
        this.kilometerTo = kilometerTo;
        this.carColorId = carColorId;
        this.categoryId = categoryId;
        this.carModelId = carModelId;
        this.nearby = nearby;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
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
        this.modelViewModel = modelViewModel;
        this.brandsViewModel = brandsViewModel;
        this.createdAt = createdAt;
        this.imageViewModelList = imageViewModels;
        this.carTrackingViewModel = carTrackingViewModel;
        this.categoryViewModel = categoryViewModel;
        this.countryViewModel = countryViewModel;
        this.cityViewModel = cityViewModel;
        this.carColorViewModel = carColorViewModel;
        this.importerViewModel = importerViewModel;
        this.saleId = saleId;
        this.priceType = priceType;
    }

    protected CarViewModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        price = in.readString();
        if (in.readByte() == 0) {
            mvpi = null;
        } else {
            mvpi = in.readInt();
        }
        notes = in.readString();
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        if (in.readByte() == 0) {
            brandId = null;
        } else {
            brandId = in.readInt();
        }
        if (in.readByte() == 0) {
            isSold = null;
        } else {
            isSold = in.readInt();
        }
        if (in.readByte() == 0) {
            gender = null;
        } else {
            gender = in.readInt();
        }
        carName = in.readString();
        exchange = in.readString();
        currency = in.readString();
        warranty = in.readString();
        if (in.readByte() == 0) {
            gearType = null;
        } else {
            gearType = in.readInt();
        }
        kilometer = in.readString();
        latitude = in.readString();
        expiredAt = in.readString();
        longitude = in.readString();
        if (in.readByte() == 0) {
            guarantee = null;
        } else {
            guarantee = in.readInt();
        }
        byte tmpIsExpired = in.readByte();
        isExpired = tmpIsExpired == 0 ? null : tmpIsExpired == 1;
        commission = in.readString();
        if (in.readByte() == 0) {
            postType = null;
        } else {
            postType = in.readInt();
        }
        createdAt = in.readString();
        if (in.readByte() == 0) {
            isTracked = null;
        } else {
            isTracked = in.readInt();
        }
        if (in.readByte() == 0) {
            carStatus = null;
        } else {
            carStatus = in.readInt();
        }
        priceAfter = in.readString();
        byte tmpIsPremium = in.readByte();
        isPremium = tmpIsPremium == 0 ? null : tmpIsPremium == 1;
        priceBefore = in.readString();
        kilometerTo = in.readString();
        if (in.readByte() == 0) {
            carColorId = null;
        } else {
            carColorId = in.readInt();
        }
        if (in.readByte() == 0) {
            categoryId = null;
        } else {
            categoryId = in.readInt();
        }
        if (in.readByte() == 0) {
            carModelId = null;
        } else {
            carModelId = in.readInt();
        }
        byte tmpIsFavorite = in.readByte();
        isFavorite = tmpIsFavorite == 0 ? null : tmpIsFavorite == 1;
        kilometerFrom = in.readString();
        if (in.readByte() == 0) {
            transmission = null;
        } else {
            transmission = in.readInt();
        }
        if (in.readByte() == 0) {
            contactOption = null;
        } else {
            contactOption = in.readInt();
        }
        if (in.readByte() == 0) {
            underWarranty = null;
        } else {
            underWarranty = in.readInt();
        }
        if (in.readByte() == 0) {
            methodOfSaleId = null;
        } else {
            methodOfSaleId = in.readInt();
        }
        expiredPremiumAt = in.readString();
        examinationImage = in.readString();
        if (in.readByte() == 0) {
            engineCapacityCc = null;
        } else {
            engineCapacityCc = in.readInt();
        }
        manufacturingYear = in.readString();
        if (in.readByte() == 0) {
            carSpecificationId = null;
        } else {
            carSpecificationId = in.readInt();
        }
        installmentPriceTo = in.readString();
        installmentPriceFrom = in.readString();
        modelViewModel = in.readParcelable(ModelViewModel.class.getClassLoader());
        brandsViewModel = in.readParcelable(BrandsViewModel.class.getClassLoader());
        carTrackingViewModel = in.readParcelable(CarTrackingViewModel.class.getClassLoader());
        imageViewModelList = in.createTypedArrayList(ImageViewModel.CREATOR);
        importerViewModel = in.readParcelable(ImporterViewModel.class.getClassLoader());
        carColorViewModel = in.readParcelable(CarColorViewModel.class.getClassLoader());
        countryViewModel = in.readParcelable(CountryViewModel.class.getClassLoader());
        cityViewModel = in.readParcelable(CountryViewModel.class.getClassLoader());
        categoryViewModel = in.readParcelable(CategoryViewModel.class.getClassLoader());
        priceFrom = in.readString();
        priceTo = in.readString();
        yearFrom = in.readString();
        carNameEn = in.readString();
        yearTo = in.readString();
        nearby = in.readInt();
        sellerType = in.readInt();
        vehical = in.readInt();
        carCondition = in.readInt();
        saleId = in.readInt();
        priceType = in.readInt();
        if (in.readByte() == 0) {
            gearTypeInterest = null;
        } else {
            gearTypeInterest = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(price);
        if (mvpi == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mvpi);
        }
        dest.writeString(notes);
        if (views == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(views);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        if (brandId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(brandId);
        }
        if (isSold == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isSold);
        }
        if (gender == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gender);
        }
        dest.writeString(carName);
        dest.writeString(exchange);
        dest.writeString(currency);
        dest.writeString(warranty);
        if (gearType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gearType);
        }
        dest.writeString(kilometer);
        dest.writeString(latitude);
        dest.writeString(expiredAt);
        dest.writeString(longitude);
        if (guarantee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(guarantee);
        }
        dest.writeByte((byte) (isExpired == null ? 0 : isExpired ? 1 : 2));
        dest.writeString(commission);
        if (postType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(postType);
        }
        dest.writeString(createdAt);
        if (isTracked == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isTracked);
        }
        if (carStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(carStatus);
        }
        dest.writeString(priceAfter);
        dest.writeByte((byte) (isPremium == null ? 0 : isPremium ? 1 : 2));
        dest.writeString(priceBefore);
        dest.writeString(kilometerTo);
        if (carColorId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(carColorId);
        }
        if (categoryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(categoryId);
        }
        if (carModelId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(carModelId);
        }
        dest.writeByte((byte) (isFavorite == null ? 0 : isFavorite ? 1 : 2));
        dest.writeString(kilometerFrom);
        if (transmission == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(transmission);
        }
        if (contactOption == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(contactOption);
        }
        if (underWarranty == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(underWarranty);
        }
        if (methodOfSaleId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(methodOfSaleId);
        }
        dest.writeString(expiredPremiumAt);
        dest.writeString(examinationImage);
        if (engineCapacityCc == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(engineCapacityCc);
        }
        dest.writeString(manufacturingYear);
        if (carSpecificationId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(carSpecificationId);
        }
        dest.writeString(installmentPriceTo);
        dest.writeString(installmentPriceFrom);
        dest.writeParcelable(modelViewModel, flags);
        dest.writeParcelable(brandsViewModel, flags);
        dest.writeParcelable(carTrackingViewModel, flags);
        dest.writeTypedList(imageViewModelList);
        dest.writeParcelable(importerViewModel, flags);
        dest.writeParcelable(carColorViewModel, flags);
        dest.writeParcelable(countryViewModel, flags);
        dest.writeParcelable(cityViewModel, flags);
        dest.writeParcelable(categoryViewModel, flags);
        dest.writeString(priceFrom);
        dest.writeString(priceTo);
        dest.writeString(yearFrom);
        dest.writeString(carNameEn);
        dest.writeString(yearTo);
        dest.writeInt(nearby);
        dest.writeInt(sellerType);
        dest.writeInt(vehical);
        dest.writeInt(carCondition);
        dest.writeInt(saleId);
        dest.writeInt(priceType);
        if (gearTypeInterest == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gearTypeInterest);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CarViewModel> CREATOR = new Creator<CarViewModel>() {
        @Override
        public CarViewModel createFromParcel(Parcel in) {
            return new CarViewModel(in);
        }

        @Override
        public CarViewModel[] newArray(int size) {
            return new CarViewModel[size];
        }
    };

    public int getSaleId() {
        return saleId;
    }

    public CarColorViewModel getCarColorViewModel() {
        return carColorViewModel;
    }

    public ImporterViewModel getImporterViewModel() {
        return importerViewModel;
    }

    public Integer getCarCondition() {
        return carCondition;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public Integer getMvpi() {
        if (examinationImage == null || examinationImage.isEmpty()){
            return 0 ;
        }
        return 1;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getUserId() {
        return userId;
    }

    public int getPriceType() {
        return priceType;
    }

    public Boolean isFavorite() {
        return isFavorite;
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

    public String getWarranty() {
        return warranty;
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

    public int getVehical() {
        return vehical;
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

    public Integer getSellerType() {
        return sellerType;
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

    public Integer getGearTypeInterest() {
        return gearTypeInterest;
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

    @PaymentType
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

    public ModelViewModel getModelViewModel() {
        return modelViewModel;
    }

    public BrandsViewModel getBrandsViewModel() {
        return brandsViewModel;
    }

    public List<ImageViewModel> getImageViewModelList() {
        return imageViewModelList;
    }

    public CarTrackingViewModel getCarTrackingViewModel() {
        return carTrackingViewModel;
    }

    public CategoryViewModel getCategoryViewModel() {
        return categoryViewModel;
    }

    public CountryViewModel getCityViewModel() {
        return cityViewModel;
    }

    public CountryViewModel getCountryViewModel() {
        return countryViewModel;
    }
}