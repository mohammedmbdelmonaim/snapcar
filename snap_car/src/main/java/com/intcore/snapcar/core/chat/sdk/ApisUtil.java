package com.intcore.snapcar.core.chat.sdk;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.chat.ChatSDKManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApisUtil {

    public static final String BASE_URL = "http://18.224.193.88/backend/public/";
    private Retrofit retrofit;

    private Retrofit createRetrofitClient() {
        return new Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(ChatSDKManager.getConfig().getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.newThread()))
                .build();
    }

    OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    Request createWebSocketRequest() {
        return new Request.Builder()
                .url(ChatSDKManager.getConfig().getSocketUrl())
                .build();
    }

    APIService getApiService() {
        if (retrofit == null) {
            retrofit = createRetrofitClient();
        }
        return retrofit.create(APIService.class);
    }
}
