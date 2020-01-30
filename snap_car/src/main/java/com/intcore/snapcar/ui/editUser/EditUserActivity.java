package com.intcore.snapcar.ui.editUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.ui.editPhone.EditPhoneActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.phasetworegisteration.CityAdapter;
import com.intcore.snapcar.ui.phasetworegisteration.CountryAdapter;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.widget.rxedittext.area.AreaEditText;
import com.intcore.snapcar.core.widget.rxedittext.email.EmailEditText;
import com.intcore.snapcar.core.widget.rxedittext.name.NameEditText;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityScope
public class EditUserActivity extends BaseActivity implements EditUserScreen {

    @BindView(R.id.et_city)
    NameEditText cityEditText;
    @BindView(R.id.ed_name)
    NameEditText name;
    @BindView(R.id.ed_area)
    AreaEditText area;
    @BindView(R.id.ed_email)
    EmailEditText email;
    @BindView(R.id.et_country)
    NameEditText countryEditText;
    @BindView(R.id.image_avatar)
    CircleImageView userImage;
    @BindView(R.id.input_layout_name)
    TextInputLayout nameTextInputLayout;
    @BindView(R.id.input_layout_area)
    TextInputLayout areaTextInputLayout;
    @BindView(R.id.input_layout_email)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    EditUserPresenter presenter;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.number_cell)
    ImageView numberCell;
    private String ImageUrl;
    private MaterialDialog materialDialog;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
        numberCell.setImageDrawable(icons.getDrawable(1));
    }


    @Override
    protected int getLayout() {
        return R.layout.actvity_edit_user;
    }

    @Override
    public void updateCountry(CountryViewModel countryViewModels) {
        countryEditText.setText(countryViewModels.getName());
    }

    private void updateCity(CountryViewModel viewModel) {
        cityEditText.setText(viewModel.getName());
    }

    @Override
    public void updateCountryFromDialog(CountryViewModel countryViewModel) {
        updateCityFromDialog(countryViewModel.getCountryViewModels().get(0));
        countryEditText.setText(countryViewModel.getName());
    }

    @Override
    public void showAreaErrorMessage(String areaErrorMsg) {
        areaTextInputLayout.setError(areaErrorMsg);
    }

    @Override
    public void showNameErrorMessage(String nameErrorMsg) {
        nameTextInputLayout.setError(nameErrorMsg);
    }

    @Override
    public void showEmailErrorMessage(String emailErrorMsg) {
        emailTextInputLayout.setError(emailErrorMsg);
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
        name.setValidityListener(presenter::onAfterNameChange);
        area.setValidityListener(presenter::onAfterAreaChange);
        email.setValidityListener(presenter::onAfterEmailChange);
    }

    @Override
    public void setSelectedImage(File file) {
        Glide.with(this)
                .load(file.getAbsolutePath())
                .centerCrop()
                .into(userImage);
    }

    @Override
    public void setNewImagePath(String path) {
        ImageUrl = path;
    }

    @Override
    public void onUpdatedSuccessfully() {
        showSuccessMessage(getString(R.string.updated));
        new Handler().postDelayed(this::finish, 1500);
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
    public void updateUi(DefaultUserModel defaultUserModelSingle) {
        if (defaultUserModelSingle.getCountryModel() != null) {
            if (isEnglishLang()) {
                countryEditText.setText(defaultUserModelSingle.getCountryModel().getNameEn());
                cityEditText.setText(defaultUserModelSingle.getCityModel().getNameEn());
            } else {
                countryEditText.setText(defaultUserModelSingle.getCountryModel().getNameAr());
                cityEditText.setText(defaultUserModelSingle.getCityModel().getNameAr());
            }
        }
        name.setText(defaultUserModelSingle.getFirstName());
        email.setText(defaultUserModelSingle.getEmail());
        area.setText(defaultUserModelSingle.getArea());
        if (!isEnglishLang()) {
            name.setGravity(Gravity.RIGHT);
            email.setGravity(Gravity.RIGHT);
            area.setGravity(Gravity.RIGHT);
        } else {
            name.setGravity(Gravity.LEFT);
            email.setGravity(Gravity.LEFT);
            area.setGravity(Gravity.LEFT);
        }
        Glide.with(this)
                .load(ApiUtils.BASE_URL.concat(defaultUserModelSingle.getImageUrl()))
                .centerCrop()
                .into(userImage);
        ImageUrl = defaultUserModelSingle.getImageUrl();
    }

    private void updateCityFromDialog(CountryViewModel viewModel) {
        cityEditText.setText(viewModel.getName());
    }

    @OnClick(R.id.et_country)
    public void onCountryClicked() {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new CountryAdapter(this, viewModel -> {
                            presenter.setSelectedCountry(viewModel);
                            presenter.setSelectedCity(viewModel.getCountryViewModels().get(0));
                            updateCity(viewModel.getCountryViewModels().get(0));
                            updateCountryFromDialog(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getCountryList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @OnClick(R.id.et_city)
    void onCityClicked() {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new CityAdapter(this, viewModel -> {
                            presenter.setSelectedCity(viewModel);
                            updateCity(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getSelectedCountry().getCountryViewModels()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @OnClick({R.id.save_btn, R.id.back_btn, R.id.et_update_phone})
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                if (area.getText().toString().isEmpty()) {
                    showAreaErrorMessage(getString(R.string.type_Your_area));
                } else {
                    presenter.saveIsClicked(name.getText().toString(),
                            email.getText().toString(),
                            presenter.getSelectedCountry().getId(),
                            presenter.getSelectedCity().getId(),
                            area.getText().toString(),
                            ImageUrl);
                }
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.et_update_phone:
                startActivity(new Intent(this, EditPhoneActivity.class));
                break;

        }
    }

    @OnClick({R.id.image_avatar})
    public void onImageClicked(View v) {
        presenter.openGallery(RxPaparazzo.single(this));
    }

    boolean isEnglishLang() {
        return LocaleUtil.getLanguage(this).equals("en");
    }

}