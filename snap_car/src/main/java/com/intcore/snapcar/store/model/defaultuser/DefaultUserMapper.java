package com.intcore.snapcar.store.model.defaultuser;

import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.blocklist.BlockDTO;
import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.country.CountryMapper;
import com.intcore.snapcar.store.model.showroom.ShowRoomInfoMapper;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.chat.model.user.UserModel;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.ResourcesUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class DefaultUserMapper {

    private final ShowRoomInfoMapper showRoomInfoMapper;
    private final ResourcesUtil resourcesUtil;
    private final CountryMapper countryMapper;
    private final CarMapper carMapper;

    @Inject
    DefaultUserMapper(ShowRoomInfoMapper showRoomInfoMapper,
                      ResourcesUtil resourcesUtil,
                      CountryMapper countryMapper,
                      CarMapper carMapper) {
        this.showRoomInfoMapper = showRoomInfoMapper;
        this.resourcesUtil = resourcesUtil;
        this.countryMapper = countryMapper;
        this.carMapper = carMapper;
    }

    public static UserModel toUserModel(DefaultUserModel currentUser) {
        return new UserModel(currentUser.getId(), currentUser.getFirstName(), currentUser.getEmail(),
                currentUser.getPhone(), currentUser.getActivation(),
                currentUser.getType(), currentUser.getImageUrl(), currentUser.getGender(),
                currentUser.getResetPasswordCode(), currentUser.getApiToken(),
                currentUser.getAndroidFirebaseToken(), currentUser.getCreatedAt(),
                currentUser.getUpdatedAt(), currentUser.getResetPhoneCode(), currentUser.getTempPhone());
    }

    public DefaultUserEntity toEntity(DefaultUserDataApiResponse.DefaultUserApiResponse userApiResponse) {
        return new DefaultUserEntity(
                userApiResponse.getId(),
                userApiResponse.getType(),
                userApiResponse.getGender(),
                userApiResponse.getEmail(),
                userApiResponse.getPhone(),
                userApiResponse.getWebsite(),
                userApiResponse.getAddress(),
                userApiResponse.getSocialType(),
                userApiResponse.getSocialId(),
                userApiResponse.getImageUrl(),
                userApiResponse.getCoverUrl(),
                userApiResponse.getApiToken(),
                userApiResponse.getFirstName(),
                userApiResponse.getCreatedAt(),
                userApiResponse.getUpdatedAt(),
                userApiResponse.getBirthYear(),
                userApiResponse.getTempPhone(),
                userApiResponse.getActivation(),
                userApiResponse.getResetPhoneCode(),
                userApiResponse.getResetPasswordCode(),
                userApiResponse.getAndroidFirebaseToken(),
                userApiResponse.getCityId(),
                userApiResponse.getCountryId(),
                userApiResponse.getArea());
    }

//    public DefaultUserModel toModel(DefaultUserEntity entity) {
//        return new DefaultUserModel(entity.getId(),
//                entity.getType(),
//                entity.getGender(),
//                entity.getEmail(),
//                entity.getPhone(),
//                entity.getWebsite(),
//                entity.getAddress(),
//                entity.getSocialType(),
//                entity.getSocialId(),
//                entity.getApiToken(),
//                entity.getImageUrl(),
//                entity.getCoverUrl(),
//                entity.getFirstName(),
//                entity.getCreatedAt(),
//                entity.getUpdatedAt(),
//                entity.getTempPhone(),
//                entity.getBirthYear(),
//                entity.getActivation(),
//                entity.getResetPhoneCode(),
//                entity.getResetPasswordCode(),
//                entity.getAndroidFirebaseToken(),
//                entity.getCityId(),
//                entity.getCountryId(),
//                entity.getArea());
//    }

    public DefaultUserModel toModel(DefaultUserDataApiResponse.DefaultUserApiResponse userApiResponse) {
        if (userApiResponse == null) return null;
        return new DefaultUserModel(userApiResponse.getId(),
                userApiResponse.getType(),
                userApiResponse.getGender(),
                userApiResponse.getEmail(),
                userApiResponse.getPhone(),
                userApiResponse.getWebsite(),
                userApiResponse.getAddress(),
                userApiResponse.getSocialType(),
                userApiResponse.getSocialId(),
                userApiResponse.getApiToken(),
                userApiResponse.getImageUrl(),
                userApiResponse.getCoverUrl(),
                userApiResponse.getFirstName(),
                userApiResponse.getCreatedAt(),
                userApiResponse.getUpdatedAt(),
                userApiResponse.getBirthYear(),
                userApiResponse.getTempPhone(),
                userApiResponse.getActivation(),
                userApiResponse.getResetPhoneCode(),
                userApiResponse.getResetPasswordCode(),
                userApiResponse.getAndroidFirebaseToken(),
                userApiResponse.getCityId(),
                userApiResponse.getCountryId(),
                userApiResponse.getArea(),
                countryMapper.toCuntryModelWithoutCity(userApiResponse.getCountryApiResponse()),
                countryMapper.toCityModel(userApiResponse.getCityApiResponse()),
                userApiResponse.getUserRate(),
                userApiResponse.getTotalRate(),
                userApiResponse.getAvailableCar(),
                userApiResponse.getLimitCarNumber(),
                showRoomInfoMapper.toShowRoomInfoModel(userApiResponse.getShowroomInfo()),
                userApiResponse.getFav(), carMapper.toCarModel(userApiResponse.getTrackedCar()),
                userApiResponse.isRequestVip(), userApiResponse.isRequestHotzone(),
                userApiResponse.getIsVerified(),
                userApiResponse.isHasHotZone(),
                userApiResponse.getNotificationSettingDTO(),
                userApiResponse.isHasRequestAd(),
                userApiResponse.getPhonesPositionsApiResponses(), userApiResponse.isTermsAgreed());

    }

    public DefaultUserViewModel toViewModel(DefaultUserModel userModel) {
        if (userModel == null) {
            return null;
        }
        String gender;
        if (userModel.getGender() == Gender.MALE) {
            gender = resourcesUtil.getString(R.string.male);
        } else if (userModel.getGender() == Gender.FEMALE) {
            gender = resourcesUtil.getString(R.string.female);
        } else {
            gender = resourcesUtil.getString(R.string.not_specified);
        }
        return new DefaultUserViewModel(userModel.getId(),
                userModel.getFirstName(), userModel.getEmail(),
                userModel.getPhone(),
                ApiUtils.BASE_URL.concat(userModel.getImageUrl()),
                ApiUtils.BASE_URL.concat(userModel.getCoverUrl()),
                userModel.getWebsite(), userModel.getAddress(), gender,
                userModel.getResetPhoneCode(), userModel.getTempPhone(),
                userModel.getBirthYear(),
                showRoomInfoMapper.toShowRoomInfoViewModel(userModel.getShowRoomInfoModel()));
    }

    public List<DefaultUserModel> toModels(List<BlockDTO.BlockListData> responses) {
        List<DefaultUserModel> models = new ArrayList<>();
        for (BlockDTO.BlockListData response : responses) {
            models.add(new DefaultUserModel(response.getId(),
                    response.getUserApiResponse().getType(),
                    response.getUserApiResponse().getGender(),
                    response.getUserApiResponse().getEmail(),
                    response.getUserApiResponse().getPhone(),
                    response.getUserApiResponse().getWebsite(),
                    response.getUserApiResponse().getAddress(),
                    response.getUserApiResponse().getSocialType(),
                    response.getUserApiResponse().getSocialId(),
                    response.getUserApiResponse().getApiToken(),
                    response.getUserApiResponse().getImageUrl(),
                    response.getUserApiResponse().getCoverUrl(),
                    response.getUserApiResponse().getFirstName(),
                    response.getUserApiResponse().getCreatedAt(),
                    response.getUserApiResponse().getUpdatedAt(),
                    response.getUserApiResponse().getBirthYear(),
                    response.getUserApiResponse().getTempPhone(),
                    response.getUserApiResponse().getActivation(),
                    response.getUserApiResponse().getResetPhoneCode(),
                    response.getUserApiResponse().getResetPasswordCode(),
                    response.getUserApiResponse().getAndroidFirebaseToken(),
                    response.getUserApiResponse().getCityId(),
                    response.getUserApiResponse().getCountryId(),
                    response.getUserApiResponse().getArea(),
                    countryMapper.toCuntryModelWithoutCity(response.getUserApiResponse().getCountryApiResponse()),
                    countryMapper.toCityModel(response.getUserApiResponse().getCityApiResponse()),
                    response.getUserApiResponse().getUserRate(),
                    response.getUserApiResponse().getTotalRate(),
                    response.getUserApiResponse().getAvailableCar(),
                    response.getUserApiResponse().getLimitCarNumber(),
                    showRoomInfoMapper.toShowRoomInfoModel(response.getUserApiResponse().getShowroomInfo()),
                    response.getUserApiResponse().getFav(),
                    carMapper.toCarModel(response.getUserApiResponse().getTrackedCar()),
                    response.getUserApiResponse().isRequestVip(),
                    response.getUserApiResponse().isRequestHotzone(), response.getUserApiResponse().getIsVerified(),
                    response.getUserApiResponse().isHasHotZone(),
                    response.getUserApiResponse().getNotificationSettingDTO(),
                    response.getUserApiResponse().isHasRequestAd(), response.getUserApiResponse().getPhonesPositionsApiResponses(),
                    response.getUserApiResponse().isTermsAgreed()));
        }
        return models;
    }

    public List<DefaultUserModel> toDefaultUserModels(List<DefaultUserDataApiResponse.DefaultUserApiResponse> responses) {
        if (responses == null) return null;
        List<DefaultUserModel> models = new ArrayList<>();
        for (DefaultUserDataApiResponse.DefaultUserApiResponse response : responses) {
            models.add(toModel(response));
        }
        return models;
    }
}