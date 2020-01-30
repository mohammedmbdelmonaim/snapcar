package com.intcore.snapcar.ui.paymenactivity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentInterface {
    @GET("api/v1/user/app/car/actions/pay-commission/{id}")
    Call<PaymentResponseModel> getPayment (@Path ("id") int id , @Query("api_token") String token ,  @Query("price") String price ,  @Query("locale") String locale , @Query("commission") String commission);
}
