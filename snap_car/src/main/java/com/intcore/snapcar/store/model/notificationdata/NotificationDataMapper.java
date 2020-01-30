package com.intcore.snapcar.store.model.notificationdata;

import com.intcore.snapcar.core.scope.ApplicationScope;

import java.util.Locale;

import javax.inject.Inject;

@ApplicationScope
public class NotificationDataMapper {

    @Inject
    NotificationDataMapper() {

    }

    public NotificationDataModel toNotificationDataModel(NotificationDataApiResponse entity) {
        if (entity == null) return null;
        return new NotificationDataModel(entity.getCarId(),
                entity.getType(),
                entity.getTitleEn(),
                entity.getTitleAr(),
                entity.getMessageEn(),
                entity.getMessageAr(),
                entity.getCommision());
    }

    public NotificationDataViewModel toNotificationDataViewModel(NotificationDataModel model) {
        if (model == null) return null;
        String title = model.getTitleAr();
        String message = model.getMessageAr();
        if (Locale.getDefault().getLanguage().contentEquals("en")) {
            title = model.getTitleEn();
            message = model.getMessageEn();
        }
        return new NotificationDataViewModel(model.getCarId(),
                model.getType(),
                title,
                message,
                model.getCommision());
    }
}