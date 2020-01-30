package com.intcore.snapcar.ui.phasetworegisteration;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.AuthEndPoints;
import com.intcore.snapcar.store.model.constant.UserType;
import com.intcore.snapcar.store.model.country.CountryViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserDataApiResponse;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.authentication.AuthenticationFactory;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;
import com.intcore.snapcar.core.util.authentication.event.HttpException;
import com.intcore.snapcar.core.widget.rxedittext.area.AreaEditText;
import com.intcore.snapcar.core.widget.rxedittext.email.EmailEditText;
import com.intcore.snapcar.core.widget.rxedittext.name.NameEditText;
import com.intcore.snapcar.core.widget.rxedittext.password.PasswordEditText;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class PhaseTwoRegistrationActivity extends BaseActivity implements PhaseTwoRegistrationScreen, AuthenticationOperationListener<DefaultUserDataApiResponse> {

    @Inject
    PhaseTwoRegistrationPresenter presenter;

    @BindView(R.id.et_name)
    NameEditText nameEditText;
    @BindView(R.id.et_area)
    AreaEditText areaEditText;
    @BindView(R.id.et_gender)
    NameEditText genderEditText;
    @BindView(R.id.et_city)
    NameEditText cityEditText;
    @BindView(R.id.et_email)
    EmailEditText emailEditText;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @BindView(R.id.et_country)
    NameEditText countryEditText;
    @BindView(R.id.et_password)
    PasswordEditText passwordEditText;
    @BindView(R.id.et_user_type)
    NameEditText userTypeEditText;
    @BindView(R.id.btn_register)
    TextView registerButton;
    @BindView(R.id.input_layout_area)
    TextInputLayout areaTextInputLayout;
    @BindView(R.id.input_layout_name)
    TextInputLayout nameTextInputLayout;
    @BindView(R.id.input_layout_email)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.input_layout_gender)
    TextInputLayout genderTextInputLayout;
    @BindView(R.id.et_confirm_password)
    PasswordEditText confirmPasswordEditText;
    @BindView(R.id.input_layout_password)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.input_layout_confirm_password)
    TextInputLayout confirmPasswordTextInputLayout;
    @BindView(R.id.back_btn)
    ImageView backBtn;
    private MaterialDialog materialDialog;
    @UserType
    private int userType;
    @Gender
    private int gender;

    private String phone;
    private String password;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);

        ButterKnife.bind(this);
        setupBackIcon();
    }

    private void setupBackIcon() {
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_white_ic);
        backBtn.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_phase_two_registeration;
    }

    @Override
    public void setupEditText() {
        nameEditText.setValidityListener(presenter::onAfterNameChange);
        areaEditText.setValidityListener(presenter::onAfterAreaChange);
        emailEditText.setValidityListener(presenter::onAfterEmailChange);
        passwordEditText.setValidityListener(presenter::onAfterPasswordChange);
        passwordEditText.checIfPasswordsMatches(confirmPasswordEditText, result -> presenter.observeIfPasswordsMatch(result));
    }

    @Override
    public void shouldNavigateToHost(String name, String email, String area, String password, String phone) {
        getUiUtil().hideKeyboard(this);
        this.phone = phone;
        this.password = password ;
        showLoadingAnimation();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.createUserWithEmailAndPassword(phone.concat("@amotorz.com"), password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        JsonObject userJsonObject = new JsonObject();
//                        if (userType == UserType.USER) {
//                            userJsonObject.addProperty("gender", gender);
//                        }
//                        userJsonObject.addProperty("name", name);
//                        userJsonObject.addProperty("area", area);
//                        userJsonObject.addProperty("type", userType);
//                        userJsonObject.addProperty("email", email);
//                        userJsonObject.addProperty("phone", phone);
//                        userJsonObject.addProperty("locale", Locale.getDefault().getLanguage());
//                        userJsonObject.addProperty("language", Locale.getDefault().getLanguage());
//                        userJsonObject.addProperty("firebase_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                        userJsonObject.addProperty("os", "android");
//                        userJsonObject.addProperty("country_id", presenter.getSelectedCountry().getId());
//                        userJsonObject.addProperty("city_id", presenter.getSelectedCity().getId());
//                        userJsonObject.addProperty("password", password);
//                        userJsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
//
//                        AuthenticationFactory.getRegistrationFactory(this, AuthEndPoints.BASE_URL, UserManager.getClassOfUserApiResponse())
//                                .needsAccountActivation(false)
//                                .createDefaultRegistrationUtil(userJsonObject, AuthEndPoints.DEFAULT_REGISTRATION_END_POINT)
//                                .register(this);
//                    } else {
//                        if (task.getException() instanceof com.google.firebase.FirebaseNetworkException) {
//                            hideLoadingAnimation();
//                            if (getResourcesUtil().isEnglish()) {
//                                showErrorMessage("There is No Internet Connection");
//                            } else {
//                                showErrorMessage("لا يوجد اتصال بالإنترنت");
//                            }
//                            return;
//                        }
//                        showErrorMessage(getString(R.string.phone_aleady_taken));
//                        hideLoadingAnimation();
//                    }
//                });



        JsonObject userJsonObject = new JsonObject();
        if (userType == UserType.USER) {
            userJsonObject.addProperty("gender", gender);
        }
        userJsonObject.addProperty("name", name);
        userJsonObject.addProperty("area", area);
        userJsonObject.addProperty("type", userType);
        userJsonObject.addProperty("email", email);
        userJsonObject.addProperty("phone", phone);
        userJsonObject.addProperty("locale", Locale.getDefault().getLanguage());
        userJsonObject.addProperty("language", Locale.getDefault().getLanguage());
//        userJsonObject.addProperty("firebase_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        userJsonObject.addProperty("os", "android");
        userJsonObject.addProperty("country_id", presenter.getSelectedCountry().getId());
        userJsonObject.addProperty("city_id", presenter.getSelectedCity().getId());
        userJsonObject.addProperty("password", password);
        userJsonObject.addProperty("mobile_token", FirebaseInstanceId.getInstance().getToken());
        AuthenticationFactory.getRegistrationFactory(this, AuthEndPoints.BASE_URL, UserManager.getClassOfUserApiResponse())
                .needsAccountActivation(false)
                .createDefaultRegistrationUtil(userJsonObject, AuthEndPoints.DEFAULT_REGISTRATION_END_POINT)
                .register(this);
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
    public void updateCountry(CountryViewModel countryViewModel) {
        updateCity(countryViewModel.getCountryViewModels().get(0));
        countryEditText.setText(countryViewModel.getName());
        presenter.setSelectedCountry(countryViewModel);
    }

    private void updateCity(CountryViewModel viewModel) {
        presenter.setSelectedCity(viewModel);
        cityEditText.setText(viewModel.getName());
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
    public void showPasswordErrorMessage(String passwordErrorMsg) {
        passwordTextInputLayout.setError(passwordErrorMsg);
    }

    @Override
    public void showConfirmPasswordErrorMessage(String confirmPasswordErrorMsg) {
        confirmPasswordTextInputLayout.setError(confirmPasswordErrorMsg);
    }

    @OnClick(R.id.btn_register)
    void onRegisterClicked() {
        presenter.onRegisterClicked(nameEditText.getText().toString(),
                emailEditText.getText().toString(),
                areaEditText.getText().toString(),
                passwordEditText.getText().toString(),
                userType,
                gender);
    }

    @Override
    public void onPreOperation() {
        showLoadingAnimation();
    }

    @Override
    public void onPostOperation() {
        hideLoadingAnimation();
    }

    @Override
    public void onSuccess(DefaultUserDataApiResponse value, int type) {

        //TODO LOGIN IN FIREBASE  SAM
        Log.e("hassan" , phone);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(
                phone.concat("@amotorz.com"), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("hassan" ,  task.getResult().getUser().getEmail());

                hideLoadingAnimation();
                if (value.getDefaultUserApiResponse().getType() == UserType.SHOW_ROOM)       {
                    showWarningMessage(getString(R.string.update_your_profile));
                }
                Intent intent = new Intent(this, HostActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {

                if (task.getException() instanceof com.google.firebase.FirebaseNetworkException) {
                    hideLoadingAnimation();
                    if (getResourcesUtil().isEnglish()) {
                        showErrorMessage("There is No Internet Connection");
                    } else {
                        showErrorMessage("لا يوجد اتصال بالإنترنت");
                    }
                    return;
                }
                showErrorMessage(getString(R.string.firebae_message));
                hideLoadingAnimation();
            }
        });


    }

    @Override
    public void onHttpError(HttpException e) {
        hideLoadingAnimation();
        presenter.processError(e);
    }

    @Override
    public void onNetworkError(IOException e) {
        hideLoadingAnimation();
        presenter.processError(e);
    }

    @Override
    public void onUnExpectedError(Throwable e) {

        hideLoadingAnimation();
        presenter.processError(e);
    }

    @OnClick(R.id.et_user_type)
    void onUserTypeClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_user_type, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onUserTypeFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onUserTypeFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.user) {
            userTypeEditText.setText(getString(R.string.user));
            userType = UserType.USER;
            genderTextInputLayout.setVisibility(View.VISIBLE);
            return true;
        } else if (menuItem.getItemId() == R.id.show_room) {
            userTypeEditText.setText(getString(R.string.show_room));
            userType = UserType.SHOW_ROOM;
            genderTextInputLayout.setVisibility(View.GONE);
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_gender)
    void onGenderClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_gender, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onGenderFlowMenuItemClicked);
        popupMenu.show();
    }

    private boolean onGenderFlowMenuItemClicked(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.male) {
            genderEditText.setText(getString(R.string.male));
            gender = Gender.MALE;
            return true;
        } else if (menuItem.getItemId() == R.id.female) {
            genderEditText.setText(getString(R.string.female));
            gender = Gender.FEMALE;
            return true;
        }
        return true;
    }

    @OnClick(R.id.et_country)
    void onCountryClicked() {
        materialDialog = new MaterialDialog.Builder(this)
                .adapter(new CountryAdapter(this, viewModel -> {
                            presenter.setSelectedCountry(viewModel);
                            updateCountry(viewModel);
                            materialDialog.dismiss();
                        }, presenter.getCountryList()),
                        new LinearLayoutManager(this))
                .build();
        materialDialog.show();
    }

    @OnClick(R.id.et_city)
    void onCityClicked() {
        if (countryEditText.getText().toString().isEmpty()) {
            showWarningMessage(getString(R.string.you_should_select_country));
            return;
        }
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

    @OnClick({R.id.back_btn})
    void onBackClicked() {
        finish();
    }
}