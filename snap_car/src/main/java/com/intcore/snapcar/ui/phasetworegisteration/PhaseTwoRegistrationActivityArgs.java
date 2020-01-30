package com.intcore.snapcar.ui.phasetworegisteration;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;

public class PhaseTwoRegistrationActivityArgs implements ActivityArgs {

    private static final String KEY_PHONE = "IntentPhoneNumberKey";
    private final String phoneNumber;

    public PhaseTwoRegistrationActivityArgs(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    static PhaseTwoRegistrationActivityArgs deserializeFrom(Intent intent) {
        return new PhaseTwoRegistrationActivityArgs(intent.getStringExtra(KEY_PHONE));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, PhaseTwoRegistrationActivity.class);
        intent.putExtra(KEY_PHONE, phoneNumber);
        return intent;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

}