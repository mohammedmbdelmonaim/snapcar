package com.intcore.snapcar.ui.paymenactivity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentCallbackInterface {
    @GET("api/v1/user/app/transaction-status/{paymentReference}")
    Call<PaymentCallbackResponseModel> getPaymentCallback(@Path("paymentReference") String paymentReference , @Query("api_token") String token);
}
