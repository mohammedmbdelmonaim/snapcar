package com.intcore.snapcar.ui.contactus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.intcore.snapcar.core.widget.rxedittext.email.EmailEditText;
import com.intcore.snapcar.core.widget.rxedittext.name.NameEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@ActivityScope
public class ContactUsActivity extends BaseActivity implements ContactUsScreen {

    @BindView(R.id.iv_back)
    ImageView backImageView;
    @BindView(R.id.et_name)
    NameEditText nameEditText;
    @BindView(R.id.et_email)
    EmailEditText emailEditText;
    @BindView(R.id.et_message)
    RxEditText messageEditText;
    @BindView(R.id.tv_mail)
    TextView mailTextView;
    @BindView(R.id.tv_phones)
    TextView phonesTextView;
    @BindView(R.id.tv_location)
    TextView locationTextView;
    @BindView(R.id.input_layout_email)
    TextInputLayout emailInputLayout;
    @BindView(R.id.input_layout_name)
    TextInputLayout nameInputLayout;
    @BindView(R.id.input_layout_message)
    TextInputLayout messageInputLayout;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;

    @Inject
    ContactUsPresenter presenter;

    private boolean isEnglish;
    private MaterialDialog showRoomDialog;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        isEnglish = isEnglishLang();
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        backImageView.setImageDrawable(icons.getDrawable(0));
    }


    @Override
    protected int getLayout() {
        return R.layout.activity_contact_us;
    }

    @OnClick(R.id.iv_back)
    public void onBackClicked(View v) {
        finish();
    }

    @Override
    public void showMessageErrorMsg(String messageErrorMsg) {
        messageInputLayout.setError(messageErrorMsg);
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
    public void showEmailErrorMsg(String emailErrorMsg) {
        emailInputLayout.setError(emailErrorMsg);
    }

    @Override
    public void updateUi(SettingsModel settingsModel) {
        mailTextView.setText(settingsModel.getMail());
        locationTextView.setText(settingsModel.getAddress());
        if (isEnglishLang()) {
            mailTextView.setGravity(Gravity.LEFT);
            locationTextView.setGravity(Gravity.LEFT);
            phonesTextView.setGravity(Gravity.LEFT);
        } else {
            mailTextView.setGravity(Gravity.RIGHT);
            locationTextView.setGravity(Gravity.RIGHT);
            phonesTextView.setGravity(Gravity.RIGHT);
        }
        String[] phones = {};
        if (!TextUtils.isEmpty(settingsModel.getPhones())) {
            phones = settingsModel.getPhones().replace(" ", "").
                    replace("'", "").replace("]", "").
                    replace("[", "").split(",");
        }
        List<String> phoneList = new ArrayList<>(Arrays.asList(phones));
        StringBuilder phoneString = new StringBuilder();
        for (int i = 0; i < phoneList.size(); i++) {
            if (i != phoneList.size() - 1) {
                phoneString.append(phoneList.get(i).concat(" | "));
            } else {
                phoneString.append(phoneList.get(i));
            }
        }
        phonesTextView.setText(phoneString);
    }

    @Override
    public void showNameErrorMsg(String nameErrorMsg) {
        nameInputLayout.setError(nameErrorMsg);
    }

    @Override
    public void onPhoneClicked(String phones) {
        String[] showRoomPhones = {"", ""};
        if (!TextUtils.isEmpty(phones)) {
            showRoomPhones = phones.replace(" ", "").
                    replace("'", "").replace("]", "").
                    replace("[", "").split(",");
        }
        String[] finalShowRoomPhones = showRoomPhones;
        showRoomDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.layout_show_room_phone, false)
                .build();
        View inflater = showRoomDialog.getCustomView();
        TextView phone1 = inflater.findViewById(R.id.phone_one);
        TextView phone2 = inflater.findViewById(R.id.phone_two);
        List<TextView> phoneArray = new ArrayList<>();
        phoneArray.add(phone1);
        phoneArray.add(phone2);
        for (int i = 0; i < finalShowRoomPhones.length; i++) {
            if (!TextUtils.isEmpty(finalShowRoomPhones[i])) {
                phoneArray.get(i).setText(finalShowRoomPhones[i]);
                phoneArray.get(i).setVisibility(View.VISIBLE);
                int finalI = i;
                phoneArray.get(i).setOnClickListener(v -> {
                    Uri number = Uri.parse("tel:" + finalShowRoomPhones[finalI]);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                    showRoomDialog.dismiss();
                });
            }
        }
        inflater.findViewById(R.id.tv_cancel).setOnClickListener(v -> showRoomDialog.dismiss());
        showRoomDialog.show();
    }

    @Override
    public void onSaveSuccessfully() {
        showSuccessMessage(getString(R.string.message_sent_successfully));
//        getUiUtil().hideKeyboard(mailTextView);
        new Handler().postDelayed(this::finish, 1500);
    }

    @Override
    public void setupEditText() {
        nameEditText.setValidityListener(presenter::onAfterNameChange);
        emailEditText.setValidityListener(presenter::onAfterEmailChange);
        messageEditText.setValidityListener(presenter::onAfterMessageChange);
    }

    boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    @OnClick(R.id.tv_send)
    void onSendClicked() {
        presenter.onSaveClicked(nameEditText.getText().toString(), emailEditText.getText().toString(),
                messageEditText.getText().toString());
    }

    @OnClick(R.id.tv_phones)
    void onPhonesClicked() {
        presenter.onPhonesClicked();
    }

    @OnClick(R.id.tv_mail)
    void onMailClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("HotZone name", mailTextView.getText());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        showSuccessMessage(getString(R.string.text_copied));
    }

    @OnClick(R.id.tv_location)
    void onLocationClicked() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("HotZone name", locationTextView.getText());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        showSuccessMessage(getString(R.string.text_copied));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}