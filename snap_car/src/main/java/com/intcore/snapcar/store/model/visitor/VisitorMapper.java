package com.intcore.snapcar.store.model.visitor;

import android.util.Log;

import com.intcore.snapcar.store.model.car.CarMapper;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserMapper;
import com.intcore.snapcar.core.scope.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class VisitorMapper {

    private final DefaultUserMapper defaultUserMapper;
    private final CarMapper carMapper;

    @Inject
    VisitorMapper(DefaultUserMapper defaultUserMapper, CarMapper carMapper) {
        this.defaultUserMapper = defaultUserMapper;
        this.carMapper = carMapper;
    }

    public VisitorModel toVisitorModel(VisitorApiResponse response) {
        return new VisitorModel(defaultUserMapper.toModel(response.getUserApiResponse()),
                carMapper.toCarModels(response.getCarApiResponses()),
                carMapper.toCarModels(response.getSoldCarApiResponse()));
    }

    public VisitorViewModel toVisitorViewModel(VisitorModel model) {
        Log.d("ITS", "toVisitorViewModel");
        return new VisitorViewModel(model.getDefaultUserModel(),
                carMapper.toCarViewModels(model.getCarModels()),
                carMapper.toCarViewModels(model.getSoldCarList()));
    }

}