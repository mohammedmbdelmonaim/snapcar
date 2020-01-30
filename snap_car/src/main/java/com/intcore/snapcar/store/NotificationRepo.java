package com.intcore.snapcar.store;

import com.intcore.snapcar.store.model.notification.NotificationMapper;
import com.intcore.snapcar.store.model.notification.NotificationModel;
import com.intcore.snapcar.core.scope.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

@FragmentScope
public class NotificationRepo {

    private final NotificationMapper notificationMapper;
    private final WebServiceStore webServiceStore;

    @Inject
    NotificationRepo(NotificationMapper notificationMapper, WebServiceStore webServiceStore) {
        this.notificationMapper = notificationMapper;
        this.webServiceStore = webServiceStore;
    }

    public Single<List<List<NotificationModel>>> fetchNotificationList(String apiToken) {
        return webServiceStore.fetchNotifications(apiToken)
                .map(notificationMapper::toNotificationModels);
    }

    public Completable updateSeen(String apiToken, String notificationId) {
        return webServiceStore.updateSeen(apiToken, notificationId);
    }
}