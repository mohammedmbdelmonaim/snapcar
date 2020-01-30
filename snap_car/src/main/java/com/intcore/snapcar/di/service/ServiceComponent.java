package com.intcore.snapcar.di.service;


import com.intcore.snapcar.service.FCMService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(modules = {ServiceModule.class})
public interface ServiceComponent {

    void inject(FCMService fcmService);
}
