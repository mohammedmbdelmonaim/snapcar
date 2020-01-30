package com.intcore.snapcar.store.model.notification;

import com.google.gson.Gson;
import com.intcore.snapcar.store.model.notificationdata.NotificationDataMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class NotificationMapper {

    private final NotificationDataMapper notificationDataMapper;
    private Gson gson;

    @Inject
    NotificationMapper(NotificationDataMapper notificationDataMapper) {
        this.notificationDataMapper = notificationDataMapper;
        this.gson = new Gson();
    }

    public NotificationModel toNotificationModel(NotificationApiResponse entity) {
        if (entity == null) return null;
        return new NotificationModel(entity.getId(),
                entity.getNotifierId(),
                entity.getReadAt(),
                entity.getNotifiableId(),
                entity.getCreatedAt(),
                entity.getHeadTime(),
                entity.getDate(),
                notificationDataMapper.toNotificationDataModel(entity.getDataResponse()));
    }

    public NotificationViewModel toNotificationViewModel(NotificationModel model) {
        if (model == null) return null;
        return new NotificationViewModel(model.getId(),
                model.getNotifierId(),
                model.getReadAt(),
                model.getNotifiableId(),
                model.getCreatedAt(),
                model.getHeadTime(),
                model.getDate(),
                notificationDataMapper.toNotificationDataViewModel(model.getDataModel()));
    }

    public List<List<NotificationModel>> toNotificationModels(ResponseBody responseBody) throws Exception {
        JSONObject jsonObject = new JSONObject(responseBody.string());
        List<List<NotificationModel>> models = new ArrayList<>();
        List<NotificationModel> miniModels = new ArrayList<>();
        Iterator<String> iterator;
        try {
            iterator = jsonObject.getJSONObject("notifications").keys();
        } catch (Exception e) {
            iterator = Collections.emptyIterator();
        }
        while (iterator.hasNext()) {
            String key = iterator.next();
            JSONArray value = jsonObject.getJSONObject("notifications").getJSONArray(key);
            for (int i = 0; i < value.length(); i++) {
                NotificationApiResponse sessionResponse = gson.fromJson(value.get(i).toString(), NotificationApiResponse.class);
                miniModels.add(toNotificationModel(sessionResponse));
            }
            models.add(miniModels);
            miniModels = new ArrayList<>();
        }
        return models;
    }

    public List<List<NotificationViewModel>> toNotificationViewModels(List<List<NotificationModel>> models) {
        List<List<NotificationViewModel>> viewModels = new ArrayList<>();
        List<NotificationViewModel> miniViewModels = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            for (int j = 0; j < models.get(i).size(); j++) {
                miniViewModels.add(toNotificationViewModel(models.get(i).get(j)));
            }
            viewModels.add(miniViewModels);
            miniViewModels = new ArrayList<>();
        }
        return viewModels;
    }
}