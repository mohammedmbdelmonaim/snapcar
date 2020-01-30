package com.intcore.snapcar.ui.editshowroomlocation;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.phasetworegisteration.CityAdapter;
import com.intcore.snapcar.ui.phasetworegisteration.CountryAdapter;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.widget.rxedittext.area.AreaEditText;
import com.intcore.snapcar.core.widget.rxedittext.name.NameEditText;
import com.vanillaplacepicker.data.VanillaAddress;
import com.vanillaplacepicker.utils.KeyUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

@ActivityScope
public class EditShowRoomLocationActivity extends BaseActivity implements EditShowRoomLocationScreen {

    private final static int PLACE_PICKER_REQUEST = 999;
    @BindView(R.id.ed_area)
    AreaEditText areaEditText;
    @BindView(R.id.et_country)
    NameEditText countryEditText;
    @BindView(R.id.et_city)
    NameEditText cityEditText;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    EditShowRoomLocationPresenter presenter;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    private String lat = "";
    private String lon = "";
    private MaterialDialog materialDialog;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
        areaEditText.setValidityListener(presenter::onAreaChange);
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_edit_showroom_location;
    }

    @OnClick({R.id.back_btn, R.id.pic_location_btn, R.id.save_btn})
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.pic_location_btn:
                showWarningMessage(getString(R.string.recommended));
                startActivityForResult(SnapCarApplication.getPickerIntent(this),PLACE_PICKER_REQUEST);
                break;
            case R.id.save_btn:
                presenter.saveIsClicked(areaEditText.getText().toString(), lon, lat);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    VanillaAddress place = (VanillaAddress) data.getSerializableExtra(KeyUtils.SELECTED_PLACE);
                    lat = String.valueOf(place.getLatitude());
                    lon = String.valueOf(place.getLongitude());
            }
        }
    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void setupEditText() {
        areaEditText.setValidityListener(presenter::onAfterNameChange);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showNameErrorMessage(String nameErrorMsg) {
        areaEditText.setError(nameErrorMsg);
    }

    @Override
    public void updateUi(DefaultUserModel defaultUserModel) {
        if (defaultUserModel.getCountryModel() != null) {
            countryEditText.setText(defaultUserModel.getCountryModel().getNameEn());
            cityEditText.setText(defaultUserModel.getCityModel().getNameEn());
        }
    }

    @Override
    public void updateUi(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.location_updated_successfully));
        finish();
    }

    @Override
    public void updateCountry(CountryViewModel countryViewModels) {
        countryEditText.setText(countryViewModels.getName());
    }

    @Override
    public void updateCity(CountryViewModel viewModel) {
        cityEditText.setText(viewModel.getName());
    }

    @Override
    public void updateArea(DefaultUserModel area) {
        areaEditText.setText(area.getArea());
        lat = area.getShowRoomInfoModel().getLatitude();
        lon = area.getShowRoomInfoModel().getLongitude();
    }

    @Override
    public void updateCountryFromDialog(CountryViewModel countryViewModel) {
        updateCityFromDialog(countryViewModel.getCountryViewModels().get(0));
        countryEditText.setText(countryViewModel.getName());
    }

    @Override
    public void onUpdatedSuccessfully(DefaultUserModel defaultUserModel) {
        showSuccessMessage(getString(R.string.location_updated_successfully));
        finish();
    }

    private void updateCityFromDialog(CountryViewModel viewModel) {
        cityEditText.setText(viewModel.getName());
    }

    @OnClick(R.id.et_country)
    public void onCountryClicked() {
        materialDialog = new MaterialDialog.Builder(this).adapter(new CountryAdapter(this, viewModel -> {
            presenter.setSelectedCountry(viewModel);
            presenter.setSelectedCity(viewModel.getCountryViewModels().get(0));
            updateCity(viewModel.getCountryViewModels().get(0));
            updateCountryFromDialog(viewModel);
            materialDialog.dismiss();
        }, presenter.getCountryList()), new LinearLayoutManager(this)).build();
        materialDialog.show();
    }

    @OnClick(R.id.et_city)
    void onCityClicked() {
        materialDialog = new MaterialDialog.Builder(this).adapter(new CityAdapter(this, viewModel -> {
            presenter.setSelectedCity(viewModel);
            updateCity(viewModel);
            materialDialog.dismiss();
        }, presenter.getSelectedCountry().getCountryViewModels()), new LinearLayoutManager(this)).build();
        materialDialog.show();
    }
}