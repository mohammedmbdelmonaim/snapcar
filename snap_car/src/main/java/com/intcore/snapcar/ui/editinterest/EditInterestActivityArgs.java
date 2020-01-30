package com.intcore.snapcar.ui.editinterest;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.ui.ActivityArgs;

public class EditInterestActivityArgs implements ActivityArgs {

    private static String SEARCH_KEY = "SearchIntentKey";
    private CarViewModel carViewModel;

    public EditInterestActivityArgs(CarViewModel carViewModel) {
        this.carViewModel = carViewModel;
    }

    public static EditInterestActivityArgs desrilizeFrom(Intent intent) {
        return new EditInterestActivityArgs(intent.getParcelableExtra(SEARCH_KEY));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, EditInterestActivity.class);
        intent.putExtra(SEARCH_KEY, carViewModel);
        return intent;
    }

    public CarViewModel getCarViewModel() {
        return carViewModel;
    }
}