package com.intcore.snapcar.backgroundServices.di;


import com.intcore.snapcar.backgroundServices.ScheduledJobService;
import com.intcore.snapcar.di.service.ServiceScope;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(modules = {JopDispatcherModule.class})
public interface JopDispatcherComponent {

    void inject(ScheduledJobService scheduledJobService);
}
