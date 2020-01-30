package com.intcore.snapcar.di.application;

import android.app.Application;
import androidx.room.Room;
import android.content.Context;

import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.room.RoomStore;
import com.intcore.snapcar.core.qualifier.ForApplication;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.ResourcesUtil;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.util.TimeUtil;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * This class is responsible for providing the requested objects to {@link ApplicationScope} annotated classes
 */

@Module(includes = {SchedulersModule.class})
public class AppModule {

    private final SnapCarApplication application;

    public AppModule(SnapCarApplication application) {
        this.application = application;
    }

    @ApplicationScope
    @Provides
    Application providesApplication() {
        return application;
    }

    @ApplicationScope
    @Provides
    @ForApplication
    Context providesApplicationContext() {
        return application;
    }

    @ApplicationScope
    @Provides
    TextUtil providesTextUtil() {
        return new TextUtil(application);
    }

    @ApplicationScope
    @Provides
    PreferencesUtil providesPreferencesUtil() {
        return new PreferencesUtil(application);
    }

    @ApplicationScope
    @Provides
    ResourcesUtil providesResourcesUtil() {
        return new ResourcesUtil(application.getContext());
    }

    @ApplicationScope
    @Provides
    TimeUtil providesTimeUtil() {
        return new TimeUtil(application);
    }

    @ApplicationScope
    @ForApplication
    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    RoomStore providesAppDatabase(Application application) {
        return Room.databaseBuilder(application, RoomStore.class, RoomStore.DATABASE_NAME).build();
    }

}