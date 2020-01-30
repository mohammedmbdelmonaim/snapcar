package com.intcore.snapcar.store.model.images;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@ApplicationScope
public class ImageMapper {

    @Inject
    ImageMapper() {

    }

    public ImageModel toImageModel(ImagesApiResponse response) {
        if (response == null) return null;
        return new ImageModel(response.getId(),
                response.getCarId(),
                response.getPlace(),
                response.getIsMain(),
                response.getImage(),
                response.getDeletedAt(),
                response.getCreatedAt(),
                response.getUpdatedAt());
    }

    public List<ImageModel> toImageModels(List<ImagesApiResponse> responses) {
        if (responses == null) return null;
        List<ImageModel> models = new ArrayList<>();
        if(responses==null)return null;
        for (ImagesApiResponse response : responses) {
            models.add(toImageModel(response));
        }
        return models;
    }

    public ImageViewModel toImageViewModel(ImageModel model) {
        if (model == null) return null;
        return new ImageViewModel(model.getId(),
                model.getCarId(),
                model.getPlace(),
                model.getIsMain(),
                model.getImage(),
                model.getDeletedAt(),
                model.getCreatedAt(),
                model.getUpdatedAt());
    }

    public List<ImageViewModel> toImageViewModels(List<ImageModel> models) {
        if (models == null) return null;
        List<ImageViewModel> viewModels = new ArrayList<>();
        for (ImageModel model : models) {
            viewModels.add(toImageViewModel(model));
        }
        return viewModels;
    }
}