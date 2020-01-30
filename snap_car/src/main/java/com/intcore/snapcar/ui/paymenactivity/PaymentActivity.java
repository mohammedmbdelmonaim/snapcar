package com.intcore.snapcar.ui.paymenactivity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.schedulers.NetworkThreadSchedulers;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.ui.mycars.MyCarsActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends BaseActivity implements PaymentScreen {

    @BindView(R.id.wb_paymnet)
    WebView webView;
    @Inject
    PaymentPresenter presenter;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private String price;
    private String commission;
    private int carId;
    Retrofit retrofit;
    String payment_refrence;
    String token;
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    public OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }
    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        showLoadingAnimation();
        ButterKnife.bind(this);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
        token = presenter.getUserApiToken();
        String locale = LocaleUtil.getLanguage(this);
        if (presenter.getUserApiToken() != null) {
            price = getIntent().getStringExtra("price");
            commission = getIntent().getStringExtra("commission");
            carId = getIntent().getIntExtra("carId", 0);

            OkHttpClient okHttpClient = createOkHttpClient();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiUtils.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(new NetworkThreadSchedulers().workerThread()))
                    .client(okHttpClient)
                    .build();

            PaymentInterface paymentInterface = retrofit.create(PaymentInterface.class);
            paymentInterface.getPayment(carId ,token ,  price , locale , commission).enqueue(new Callback<PaymentResponseModel>() {
                @Override
                public void onResponse(Call<PaymentResponseModel> call, Response<PaymentResponseModel> response) {
                    payment_refrence = response.body().getPaymentReference();
                    start_web(response.body().getCallbackUrl() ,  response.body().getScriptUrl());
                }

                @Override
                public void onFailure(Call<PaymentResponseModel> call, Throwable t) {
                    Log.e("" , "");
                    hideLoadingAnimation();
                }
            });
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_payment;
    }

    @Override
    public void setUrl(String apiToken) {

    }

    @Override
    public void showErrorMessage(String message) {
        getUiUtil().getErrorSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        getUiUtil().getSuccessSnackBar(snackBarContainer, message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        getUiUtil().getWarningSnackBar(snackBarContainer, message).show();
    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    public void start_web(String app_url, String form_url) {
        webView.requestFocus();
        webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setSoundEffectsEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingAnimation();
                if (url.contains("pay-commission-confirm") && !url.contains("<form name")) {
                    PaymentCallbackInterface paymentCallbackInterface = retrofit.create(PaymentCallbackInterface.class);
                    paymentCallbackInterface.getPaymentCallback(payment_refrence , token).enqueue(new Callback<PaymentCallbackResponseModel>() {
                        @Override
                        public void onResponse(Call<PaymentCallbackResponseModel> call, Response<PaymentCallbackResponseModel> response) {
                            PaymentCallbackResponseModel responseModel = response.body();
                            if (responseModel.getCode() == 200){
                                if (responseModel.getData().getPaymentStatus().equalsIgnoreCase("success")){
                                    startActivity(new Intent(PaymentActivity.this, MyCarsActivity.class));
                                    finish();
                                    Toast.makeText(PaymentActivity.this, "Success Payment", Toast.LENGTH_LONG).show();
                                }else{
                                    startActivity(new Intent(PaymentActivity.this, MyCarsActivity.class));
                                    finish();
                                    Toast.makeText(PaymentActivity.this, "Error Payment , try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PaymentCallbackResponseModel> call, Throwable t) {
                            Log.e("" , "");
                        }
                    });
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        String aa = "<form name=\"myForm\" action=\"" + app_url + "\" target=\"cnpIframe\" class=\"paymentWidgets\" data-brands=\"VISA MASTER AMEX\"></form><script src=\"" + form_url + "\"></script>";
        webView.loadData(aa, "text/html", "UTF-8");
    }

}
