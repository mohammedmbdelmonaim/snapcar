package com.intcore.snapcar.core.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements LifecycleOwner, BaseActivityScreen {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    @Inject
    UiUtil uiUtil;
    @Inject
    ResourcesUtil resourcesUtil;
    @Inject
    PermissionUtil permissionUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);


        if ("ar".equals(LocaleUtil.getLanguage(this))) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        onCreateActivityComponents();
        if (lifecycleRegistry != null ){
            lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        }

        Timber.tag("Muhammad").d("Lifecycle activity: CREATED");
    }

    protected abstract void onCreateActivityComponents();

    @LayoutRes
    protected abstract int getLayout();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.onAttach(base));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
        Timber.tag("Muhammad").d("Lifecycle activity: STARTED");
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
        Timber.tag("Muhammad").d("Lifecycle activity: RESUMED");
    }

    @Override
    protected void onDestroy() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
        Timber.tag("Muhammad").d("Lifecycle activity: DESTROYED");
        super.onDestroy();
    }

    @Override
    public void showDefaultMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessToast(message)
                .show();
    }

    @Override
    public void showAnnouncementMessage(String message) {
        uiUtil.getAnnouncementToast(message)
                .show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningToast(message)
                .show();
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorToast(message)
                .show();
    }

    @Override
    public void showLoadingAnimation() {
        uiUtil.getProgressDialog(getString(R.string.please_wait))
                .show();
    }

    @Override
    public void hideLoadingAnimation() {
        uiUtil.getProgressDialog()
                .dismiss();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        if (lifecycleRegistry == null){
            lifecycleRegistry = new LifecycleRegistry(this);
        }

        return lifecycleRegistry;
    }

    @Override
    public ResourcesUtil getResourcesUtil() {
        return resourcesUtil;
    }

    @Override
    public PermissionUtil getPermissionUtil() {
        return permissionUtil;
    }

    public UiUtil getUiUtil() {
        return uiUtil;
    }

}
