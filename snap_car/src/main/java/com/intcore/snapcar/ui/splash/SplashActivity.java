package com.intcore.snapcar.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.tutorial.TutorialActivity;
import com.intcore.snapcar.util.UserManager;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.PreferencesUtil;

import javax.inject.Inject;

@ActivityScope
public class SplashActivity extends BaseActivity {

    private final String IS_FIRST_TIME = "isFirstTime";

    @Inject
    UserManager userManager;
    @Inject
    PreferencesUtil preferencesUtil;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        new Handler().postDelayed(() -> {
            if (userManager.sessionManager().isSessionActive()) {
               Log.d("MyApiToken",userManager.getCurrentUser().getApiToken());
                Intent intent = new Intent(this, HostActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (preferencesUtil.getBoolean(IS_FIRST_TIME)) {
                    preferencesUtil.saveOrUpdateBoolean(IS_FIRST_TIME, false);
                    startActivity(new Intent(this, TutorialActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            }
        }, 1000);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }
}