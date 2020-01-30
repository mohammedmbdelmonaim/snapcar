package com.intcore.snapcar.ui.resetpassword;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;

public class PasswordResetActivityArgs implements ActivityArgs {

    private static final String KEY_PHONE = "IntentPhoneNumberKey";
    private static final String KEY_CODE = "IntentCodeKey";
    private final String phoneNumber;
    private final String code;

    public PasswordResetActivityArgs(String phoneNumber, String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
    }

    static PasswordResetActivityArgs deserializeFrom(Intent intent) {
        return new PasswordResetActivityArgs(
                intent.getStringExtra(KEY_PHONE),
                intent.getStringExtra(KEY_CODE));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, ResetPasswordActivity.class);
        intent.putExtra(KEY_PHONE, phoneNumber);
        intent.putExtra(KEY_CODE, code);
        return intent;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getCode() {
        return code;
    }
}