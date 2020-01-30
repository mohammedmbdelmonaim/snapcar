package com.intcore.snapcar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.StrictMode;

import com.github.anrwatchdog.ANRWatchDog;
import com.google.android.gms.maps.model.LatLng;
import com.hadilq.liveevent.LiveEvent;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.di.application.AppComponent;
import com.intcore.snapcar.di.application.AppModule;
import com.intcore.snapcar.di.application.DaggerAppComponent;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.FontUtil;
import com.squareup.leakcanary.LeakCanary;
import com.vanillaplacepicker.presentation.builder.VanillaPlacePicker;
import com.vanillaplacepicker.utils.PickerLanguage;
import com.vanillaplacepicker.utils.PickerType;

import timber.log.Timber;

@ApplicationScope
public class SnapCarApplication extends Application {

    private static Resources resources;
    private static SnapCarApplication app;
    private final AppComponent appComponent = createAppComponent();
    private static Context context;

    public static String ANDROID_KEY = "AIzaSyCHVQEOEhCk4JV69T2UgJMrm5gFHHbD6Ic";
    public static String MAP_KEY = "AIzaSyAkHamd64WSRFaew4dcMOEIjKTkV9xsfD0";

    public LiveEvent<Location> locationLiveData = new LiveEvent<>();

    public static SnapCarApplication getInstance(){
        return app;
    }

    public static AppComponent getComponent(Context context) {
        return getApp(context).appComponent;
    }

    //This is a hack to get a non-static field from a static method (i.e. appComponent)
    private static SnapCarApplication getApp(Context context) {
        return (SnapCarApplication) context.getApplicationContext();
    }

    public static Resources getRes() {
        return resources;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        context = LocaleUtil.onAttach(base);
        resources = context.getResources();
        super.attachBaseContext(context);
    }

    public void notifyLanguageChanged(){
        context = LocaleUtil.onAttach(context);
        resources = context.getResources();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        RxPaparazzo.register(this);
        new ANRWatchDog().start();
        setStrictModeEnabledForDebug();

        setupTimberTree();
        FontUtil.overrideDefaultMonoSpaceFont(getAssets(), "cairo_regular.ttf");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//        installLeakCanary();
    }

    public static Intent getPickerIntent(Context context){
        return new VanillaPlacePicker.Builder(context)
                .with(PickerType.MAP_WITH_AUTO_COMPLETE) // Select Picker type to enable autocompelte, map or both
                //.withLocation(23.057582, 72.534458)
                .setPickerLanguage(PickerLanguage.ENGLISH) // Apply language to picker
                //.setTintColor(R.color.primary_dark) // Apply Tint color to Back, Clear button of AutoComplete UI
                .setLanguage("en")
                .isOpenNow(true) // Returns only those places that are open for business at the time the query is sent.
                .setAutoCompletePlaceHolder(R.drawable.ic_undraw_search) // To add custom place holder in autocomplete screen
                .build();
    }

    /*
     * When enabled it detects things you might be doing by accident and brings them to your attention so you can fix them.
     * Thread policy is used to catch accidental disk or network operations on the app's MAIN thread.
     * VmPolicy is used to detect when a closeable or other object with an explicit termination method is finalized without having been closed.
     */
    private void setStrictModeEnabledForDebug() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .penaltyLog()
                    .build());
        }
    }

    private void setupTimberTree() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        //TODO setup different tree for release (if needed)
    }

    private void installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }


}