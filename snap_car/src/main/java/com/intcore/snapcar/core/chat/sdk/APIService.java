package com.intcore.snapcar.core.chat.sdk;

import com.intcore.snapcar.core.chat.model.message.MessageDTO;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface APIService {

    @POST("{fullUrl}")
    @Multipart
    Single<ResponseBody> upload(
            @Header("Accept") String header,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Part("api_token") RequestBody apiToken,
            @Part MultipartBody.Part file);

    @GET("{fullUrl}")
    Observable<List<MessageDTO>> fetchUnseenMessages(
            @Header("Accept") String header,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Query("api_token") String apiToken);
}