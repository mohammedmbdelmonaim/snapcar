package com.intcore.snapcar.core.chat.model.user;

import android.content.Context;

import com.intcore.snapcar.R;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.chat.sdk.ApisUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;

import javax.inject.Inject;

public class UserMapper {

    private final ResourcesUtil resourcesUtil;

    @Inject
    public UserMapper(Context context) {
        this.resourcesUtil = new ResourcesUtil(context);
    }

    public UserEntity toEntity(UserDataApiResponse.UserApiResponse userApiResponse) {
        if (userApiResponse == null) return null;
        return new UserEntity(userApiResponse.getId(),
                userApiResponse.getFirstName(),
                userApiResponse.getEmail(),
                userApiResponse.getPhone(),
                userApiResponse.getActivation(),
                userApiResponse.getType(),
                userApiResponse.getImageUrl(),
                userApiResponse.getGender(),
                userApiResponse.getResetPasswordCode(),
                userApiResponse.getApiToken(),
                userApiResponse.getAndroidFirebaseToken(),
                userApiResponse.getCreatedAt(),
                userApiResponse.getUpdatedAt(),
                userApiResponse.getResetPhoneCode(),
                userApiResponse.getTempPhone());
    }

    public UserModel toModel(UserDataApiResponse.UserApiResponse userApiResponse) {
        return new UserModel(userApiResponse.getId(),
                userApiResponse.getFirstName(),
                userApiResponse.getEmail(),
                userApiResponse.getPhone(),
                userApiResponse.getActivation(),
                userApiResponse.getType(),
                userApiResponse.getImageUrl(),
                userApiResponse.getGender(),
                userApiResponse.getResetPasswordCode(),
                userApiResponse.getApiToken(),
                userApiResponse.getAndroidFirebaseToken(),
                userApiResponse.getCreatedAt(),
                userApiResponse.getUpdatedAt(),
                userApiResponse.getResetPhoneCode(),
                userApiResponse.getTempPhone());
    }

    public UserModel toModel(UserEntity userApiResponse) {
        if (userApiResponse == null) return null;
        return new UserModel(userApiResponse.getId(),
                userApiResponse.getFirstName(),
                userApiResponse.getEmail(),
                userApiResponse.getPhone(),
                userApiResponse.getActivation(),
                userApiResponse.getType(),
                userApiResponse.getImageUrl(),
                userApiResponse.getGender(),
                userApiResponse.getResetPasswordCode(),
                userApiResponse.getApiToken(),
                userApiResponse.getAndroidFirebaseToken(),
                userApiResponse.getCreatedAt(),
                userApiResponse.getUpdatedAt(),
                userApiResponse.getResetPhoneCode(),
                userApiResponse.getTempPhone());
    }

    public UserViewModel toViewModel(UserModel userModel) {
        if (userModel == null) return null;
        String gender = getGender(userModel);
        return new UserViewModel(
                userModel.getId(),
                userModel.getFirstName(),
                userModel.getEmail(),
                userModel.getPhone(),
                ApisUtil.BASE_URL.concat(userModel.getImageUrl()),
                gender,
                userModel.getResetPhoneCode(),
                userModel.getTempPhone());
    }

    private String getGender(UserModel userModel) {
        if (resourcesUtil == null) {
            return String.valueOf(userModel.getGender());
        }
        if (userModel.getGender() == Gender.MALE) {
            return resourcesUtil.getString(R.string.male);
        } else if (userModel.getGender() == Gender.FEMALE) {
            return resourcesUtil.getString(R.string.female);
        } else {
            return resourcesUtil.getString(R.string.not_specified);
        }
    }
}