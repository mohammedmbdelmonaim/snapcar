package com.intcore.snapcar.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface ActivityArgs {

    /**
     @return an intent that can be used to launch this activity
     */
    Intent intent(Context activity);

    /**
     launches the activity given your activity context, the default implementation uses the intent generated from {@link ActivityArgs#intent(Context)}
     */
    default void launch(Context activity) {
        activity.startActivity(intent(activity));
    }

    default void launchForResult(Context activity,int requestCode) {
        ((Activity) activity).startActivityForResult(intent(activity),requestCode);
    }
}
