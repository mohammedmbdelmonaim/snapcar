package com.intcore.snapcar.di.application;

import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.backgroundServices.di.JopDispatcherComponent;
import com.intcore.snapcar.backgroundServices.di.JopDispatcherModule;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.di.service.ServiceComponent;
import com.intcore.snapcar.di.service.ServiceModule;
import com.intcore.snapcar.core.scope.ApplicationScope;

import dagger.Component;

/**
 * This interface is used by dagger to generate the code that defines the connection between the provider of objects
 * (i.e. {@link AppModule}), and the object which expresses a dependency.
 */

@ApplicationScope
@Component(modules = {AppModule.class, SchedulersModule.class})
public interface AppComponent {

    ActivityComponent plus(ActivityModule activityModule);

    ServiceComponent plus(ServiceModule serviceModule);

    JopDispatcherComponent plus(JopDispatcherModule activityModule);

    void inject(SnapCarApplication snapCarApplication);
}