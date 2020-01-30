package com.intcore.snapcar.ui.paymenactivity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentForAddCarInterface {
    @GET("api/v1/user/app/car/actions/boost-premium/{id}")
    Call<PaymentResponseModel> getPaymentToAddCar(@Path("id") int id, @Query("api_token") String token, @Query("locale") String locale);
}
