package com.intcore.snapcar.core.util.authentication;

import com.google.gson.JsonObject;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface APIService {

    @POST("{fullUrl}")
    Single<JsonObject> execute(
            @Header("Accept") String header,
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @Body JsonObject userModel);
}
