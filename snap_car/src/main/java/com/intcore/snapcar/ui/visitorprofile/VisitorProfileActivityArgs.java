package com.intcore.snapcar.ui.visitorprofile;

import android.content.Context;
import android.content.Intent;

import com.intcore.snapcar.ui.ActivityArgs;

public class VisitorProfileActivityArgs implements ActivityArgs {

    private static String USER_ID_KEY = "UserIdKey";
    private long userId;

    public VisitorProfileActivityArgs(long userId) {
        this.userId = userId;
    }

    static VisitorProfileActivityArgs deserializeFrom(Intent intent) {
        return new VisitorProfileActivityArgs(intent.getLongExtra(USER_ID_KEY, 0));
    }

    @Override
    public Intent intent(Context activity) {
        Intent intent = new Intent(activity, VisitorProfileActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        return intent;
    }

    public long getUserId() {
        return userId;
    }
}