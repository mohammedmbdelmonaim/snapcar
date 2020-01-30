package com.intcore.snapcar.store.model.car;

import com.intcore.snapcar.store.model.brands.BrandsMapper;
import com.intcore.snapcar.store.model.carcolor.CarColorMapper;
import com.intcore.snapcar.store.model.cartracking.CarTrackingMapper;
import com.intcore.snapcar.store.model.category.CategoryMapper;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.images.ImageMapper;
import com.intcore.snapcar.store.model.importer.ImporterMapper;
import com.intcore.snapcar.store.model.model.ModelMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class CarMapper {

    private final CarTrackingMapper carTrackingMapper;
    private final CategoryMapper categoryMapper;
    private final ImporterMapper importerMapper;
    private final CarColorMapper carColorMapper;
    private final CountryMapper countryMapper;
    private final BrandsMapper brandsMapper;
    private final ModelMapper modelMapper;
    private final ImageMapper imageMapper;

    @Inject
    CarMapper(CarTrackingMapper carTrackingMapper, CategoryMapper categoryMapper, ImporterMapper importerMapper, CarColorMapper carColorMapper, CountryMapper countryMapper, BrandsMapper brandsMapper, ModelMapper modelMapper, ImageMapper imageMapper) {
        this.carTrackingMapper = carTrackingMapper;
        this.categoryMapper = categoryMapper;
        this.importerMapper = importerMapper;
        this.carColorMapper = carColorMapper;
        this.countryMapper = countryMapper;
        this.brandsMapper = brandsMapper;
        this.modelMapper = modelMapper;
        this.imageMapper = imageMapper;
    }

    public List<CarModel> toCarModels(List<CarApiResponse> responses) {
        if (responses == null) return null;
        List<CarModel> models = new ArrayList<>();
        for (CarApiResponse response : responses) {
            models.add(toCarModel(response));
        }
        return models;
    }


    public CarModel toCarModels(CarApiResponse responses) {
        return toCarModel(responses);
    }

    public List<CarViewModel> toCarViewModels(List<CarModel> models) {
        if (models == null) return null;
        List<CarViewModel> viewModels = new ArrayList<>();
        for (CarModel model : models) {
            viewModels.add(toCarViewModel(model));
        }
        return viewModels;
    }

    public CarModel toCarModel(CarApiResponse response) {
        if (response == null) return null;
        String carName;
        if (isEnglishLang())
            carName = response.getCarNameEn();
        else
            carName = response.getCarName();
        return new CarModel(
                response.getId(),
                response.getPrice(),
                response.getMvpi(),
                response.getNotes(),
                response.getViews(),
                response.getUserId(),
                response.getBrandId(),
                response.getIsSold(),
                response.getGender(),
                carName,
                response.getExchange(),
                response.getCurrency(),
                response.getDeletedAt(),
                response.getCreatedAt(),
                response.getWarranty(),
                response.getUpdatedAt(),
                response.getGearType(),
                response.getKilometer(),
                response.getLatitude(),
                response.getExpiredAt(),
                response.getLongitude(),
                response.getGuarantee(),
                response.getExpired(),
                response.getCommission(),
                response.getPostType(),
                response.getIsTracked(),
                response.getCarStatus(),
                response.getPriceAfter(),
                response.getPremium(),
                response.getPriceBefore(),
                response.getKilometerTo(),
                response.getCarColorId(),
                response.getCategoryId(),
                response.getCarModelId(),
                response.getKilometerFrom(),
                response.getTransmission(),
                response.getContactOption(),
                response.getUnderWarranty(),
                response.getMethodOfSaleId(),
                response.getExpiredPremiumAt(),
                response.getExaminationImage(),
                response.getEngineCapacityCc(),
                response.getManufacturingYear(),
                response.getCarSpecificationId(),
                response.getInstallmentPriceTo(),
                response.getInstallmentPriceFrom(),
                modelMapper.toModelModel(response.getModelApiResponse()),
                brandsMapper.toBrandsModel(response.getBrandsApiResponse()),
                carTrackingMapper.toCarTrackingModel(response.getCarTrackingApiResponse()),
                response.isFvorite(),
                imageMapper.toImageModels(response.getImagesApiResponses()),
                importerMapper.toImporterModel(response.getImporterApiResponse()),
                carColorMapper.toCarColorModel(response.getCarColorApiResponse()),
                categoryMapper.toCategoryModel(response.getCategoryApiResponse()),
                countryMapper.toCountryModel(response.getCountryApiResponse()),
                countryMapper.toCityModel(response.getCityApiResponse()),
                response.getPriceFrom(),
                response.getPriceTo(),
                response.getYearFrom(),
                response.getYearTo(),
                response.getNearby(),
                response.getSellerType(),
                response.getCarCondition(),
                response.getVehicleRegistration(),
                response.getSaleId(),
                response.getPriceType(),
                response.getGearTypeInterest());
    }

    public CarViewModel toCarViewModel(CarModel model) {
        if (model == null) return null;
        return new CarViewModel(model.getId(),
                model.getPrice(),
                model.getMvpi(),
                model.getNotes(),
                model.getViews(),
                model.getUserId(),
                model.getBrandId(),
                model.getIsSold(),
                model.getGender(),
                model.getCarName(),
                model.getExchange(),
                model.getCurrency(),
                model.getWarranty(),
                model.getGearType(),
                model.getKilometer(),
                model.getLatitude(),
                model.getExpiredAt(),
                model.getLongitude(),
                model.getGuarantee(),
                model.getExpired(),
                model.getCommission(),
                model.getPostType(),
                model.getIsTracked(),
                model.getCarStatus(),
                model.getPriceAfter(),
                model.getPremium(),
                model.getPriceBefore(),
                model.getKilometerTo(),
                model.getCarColorId(),
                model.getCategoryId(),
                model.getCarModelId(),
                model.getKilometerFrom(),
                model.getTransmission(),
                model.getContactOption(),
                model.getUnderWarranty(),
                model.getMethodOfSaleId(),
                model.getExpiredPremiumAt(),
                model.getExaminationImage(),
                model.getEngineCapacityCc(),
                model.getManufacturingYear(),
                model.getCarSpecificationId(),
                model.getInstallmentPriceTo(),
                model.getInstallmentPriceFrom(),
                modelMapper.toModelViewModel(model.getModelModel()),
                brandsMapper.toBrandsViewModel(model.getBrandsModel()),
                carTrackingMapper.toCarTrackingViewModel(model.getCarTrackingModel()),
                model.getCreatedAt(),
                imageMapper.toImageViewModels(model.getImageModels()),
                carColorMapper.toCarColorViewModel(model.getCarColorModel()),
                importerMapper.toImporterViewModel(model.getImporterModel()),
                model.getIsFavorite(),
                categoryMapper.toCategoryViewModel(model.getCategoryModel()),
                countryMapper.toCountryViewModel(model.getCountryModel()),
                countryMapper.toCityViewModel(model.getCityModel()),
                model.getPriceFrom(),
                model.getPriceTo(),
                model.getYearFrom(),
                model.getYearTo(),
                model.getNearby(),
                model.getSellerType(),
                model.getCarCondition(),
                model.getVehical(),
                model.getSaleId(),
                model.getPriceType(),
                model.getGearTypeInterest());
    }

    private boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }
}