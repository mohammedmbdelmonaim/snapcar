package com.intcore.snapcar.ui.editphoneactivition;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;
import com.intcore.snapcar.core.util.authentication.event.AuthenticationOperationListener;

public class EditPhoneActivationActivityArgs implements ActivityArgs {

    private static final String KEY_TOKEN = "IntentApiTokenKey";
    private static final String KEY_PHONE = "IntentPhoneNumberKey";
    private static final String KEY_CODE = "IntentCodeKey";
    private static final String KEY_FLAG = "IntentFlagKey";
    private static final String KEY_PRE_ACTIVTY = "IntentFlagKey";
    private static final String KEY_PRE_PHONE = "phone";
    @AuthenticationOperationListener.OperationType
    private final int typeFlag;
    private final String phoneNumber;
    private final String apiToken;
    private final String code;
    private final String preActivity ;

    public EditPhoneActivationActivityArgs(@AuthenticationOperationListener.OperationType int typeFlag,
                                           String apiToken,
                                           String phoneNumber,
                                           String code,
                                           String preActivity) {
        this.phoneNumber = phoneNumber;
        this.typeFlag = typeFlag;
        this.apiToken = apiToken;
        this.code = code;
        this.preActivity = preActivity;
    }


    static EditPhoneActivationActivityArgs deserializeFrom(Intent intent) {
        return new EditPhoneActivationActivityArgs(
                intent.getIntExtra(KEY_FLAG, -1),
                intent.getStringExtra(KEY_TOKEN),
                intent.getStringExtra(KEY_PHONE),
                intent.getStringExtra(KEY_CODE),
                intent.getStringExtra(KEY_PRE_ACTIVTY));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, EditPhoneActivationActivity.class);
        intent.putExtra(KEY_FLAG, typeFlag);
        intent.putExtra(KEY_PHONE, phoneNumber);
        intent.putExtra(KEY_TOKEN, apiToken);
        intent.putExtra(KEY_CODE, code);
        intent.putExtra(KEY_PRE_ACTIVTY, preActivity);
        return intent;
    }



    String getPhoneNumber() {
        return phoneNumber;
    }

    String getApiToken() {
        return apiToken;
    }

    int getTypeFlag() {
        return typeFlag;
    }

    String getCode() {
        return code;
    }

    public String getPreActivity() {
        return preActivity;
    }
}