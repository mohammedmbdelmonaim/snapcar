package com.intcore.snapcar.store.model.hotzone;

import android.view.Gravity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class HotZoneMapper {

    @Inject
    HotZoneMapper() {

    }
    DefaultUserMapper defaultUserMapper ;
    public List<HotZoneModel> toHotZoneModels(List<HotZone> responses) {
        if (responses == null) return null;
        List<HotZoneModel> models = new ArrayList<>();
        for (HotZone response :
                responses) {
            models.add(toHotZoneModel(response));
        }
        return models;
    }
    public List<HotZoneModel> toHotZoneModels(List<HotZone> responses  , DefaultUserMapper defaultUserMapper ) {
        if (responses == null) return null;
        List<HotZoneModel> models = new ArrayList<>();
        for (HotZone response :
                responses) {
            models.add(toHotZoneModel(response , defaultUserMapper));
        }
        return models;
    }







    public HotZoneModel toHotZoneModel(HotZone response , DefaultUserMapper defaultUserMapper) {
        if (response == null) return null;

        List<DefaultUserDataApiResponse.DefaultUserApiResponse> models = new ArrayList<>();
        models.add(response.user);
        DefaultUserModel showRoomModel = defaultUserMapper.toDefaultUserModels(models).get(0);

        return new HotZoneModel(response.getId(),
                response.getName(),
                response.getPhone(),
                response.getUserId(),
                response.getGender(),
                response.getLatitude(),
                response.getLongitude(),
                response.getDeletedAt(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.isPremium(),
                response.getActivation(),
                response.getProvideDiscount(),
                response.getImage(),response.getShowroom(), showRoomModel);
    }

    public HotZoneModel toHotZoneModel(HotZone response) {
        if (response == null) return null;
        return new HotZoneModel(response.getId(),
                response.getName(),
                response.getPhone(),
                response.getUserId(),
                response.getGender(),
                response.getLatitude(),
                response.getLongitude(),
                response.getDeletedAt(),
                response.getCreatedAt(),
                response.getUpdatedAt(),
                response.isPremium(),
                response.getActivation(),
                response.getProvideDiscount(),
                response.getImage(),response.getShowroom(), null);
    }
    public HotZoneViewModel toHotZoneViewModel(HotZoneModel model) {
        if (model == null) return null;
        return new HotZoneViewModel(model.getId(),
                model.getName(),
                model.getPhone(),
                model.getImage(),
                model.getUserId(),
                model.getGender(),
                model.getLatitude(),
                model.getLongitude(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.isPremium(),
                model.getActivation(),
                model.getProvideDiscount(),
                model.getShowroom(),
                model.getShowRoomModel()
                );
    }

    public List<HotZoneViewModel> toHotZoneViewModels(List<HotZoneModel> models) {
        List<HotZoneViewModel> viewModels = new ArrayList<>();
        for (HotZoneModel model : models) {
            viewModels.add(toHotZoneViewModel(model));
        }
        return viewModels;
    }
}