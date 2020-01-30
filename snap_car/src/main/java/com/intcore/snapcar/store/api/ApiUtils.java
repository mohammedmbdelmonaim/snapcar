package com.intcore.snapcar.store.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.intcore.snapcar.core.schedulers.NetworkThreadSchedulers;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    //public static final String BASE_URL = "http://18.224.193.88/backend/public/"; //dev
    //public static final String SOCKET_URL = "ws://18.224.193.88:8080/chat"; //dev

    public static final String BASE_URL = "https://dashboard.snapcar.sa/backend/public/"; //Release
    public static final String SOCKET_URL = "ws://3.10.62.230:8080/chat"; //Release

    private static final String USER_URL = "api/v1/user/auth/";
    private static final String APP_URL = "api/v1/user/app/";
    public static final String UPLOAD_URL = BASE_URL.concat(APP_URL).concat("file/upload");
    private static Retrofit userRetrofit;
    private static Retrofit appRetrofit;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private final OkHttpClient okHttpClient = createOkHttpClient();
    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    @Inject
    ApiUtils() {

    }

    public SnapCarApiUserService getSnapCarApiUserService() {
        return getUserRetrofit().create(SnapCarApiUserService.class);
    }

    public SnapCarApiAppService getSnapCarApiAppService() {
        return getAppRetrofit().create(SnapCarApiAppService.class);
    }

    private Retrofit getUserRetrofit() {
        if (ApiUtils.userRetrofit == null) {
            ApiUtils.userRetrofit = new Retrofit.Builder()
                    .baseUrl(ApiUtils.BASE_URL.concat(ApiUtils.USER_URL))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(new NetworkThreadSchedulers().workerThread()))
                    .client(okHttpClient)
                    .build();
        }
        return ApiUtils.userRetrofit;
    }

    private Retrofit getAppRetrofit() {
        if (ApiUtils.appRetrofit == null) {
            ApiUtils.appRetrofit = new Retrofit.Builder()
                    .baseUrl(ApiUtils.BASE_URL.concat(ApiUtils.APP_URL))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(new NetworkThreadSchedulers().workerThread()))
                    .client(okHttpClient)
                    .build();
        }
        return ApiUtils.appRetrofit;
    }

    public OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    public Request createWebSocketRequest(String apiToken) {
        return new Request.Builder()
                .url(ApiUtils.SOCKET_URL + "?api_token=" + apiToken)
                .build();
    }
}