package com.intcore.snapcar.ui.confirmcommision;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivity;
import com.intcore.snapcar.core.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class ConfirmCommissionActivity extends BaseActivity implements ConfirmCommissionScreen {

    @BindView(R.id.ed_commission)
    EditText commissionEditText;
    @BindView(R.id.ed_price)
    EditText priceEditText;
    @BindView(R.id.tv_commission)
    TextView commissionText;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    ConfirmCommissionPresenter presenter;
    private String commission;
    private int carId;

    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        commission = getIntent().getStringExtra("commission");
        carId = getIntent().getIntExtra("carId", 0);
        commissionText.setText(getString(R.string.commission_is) + " " + commission + getString(R.string.from_total_price));
        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    if (!priceEditText.getText().toString().isEmpty()) {
                        int x = (Integer.parseInt(priceEditText.getText().toString()) * Integer.parseInt(commission)) / 100;
                        commissionEditText.setText(String.valueOf(x));
                    }
                }
                catch (Exception e){

                }
            }
        });
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
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

    @Override
    protected int getLayout() {
        return R.layout.activity_confirm_commission;
    }

    @OnClick(R.id.btn_sign_in)
    void payBtn() {
        if (priceEditText.getText().toString().equals("0")){
            showErrorMessage(getString(R.string.type_price));
            return;
        }
        if (priceEditText.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.type_price));
        } else {
            String price = null;
            String comm = null;
            try {
                double p = Double.parseDouble(priceEditText.getText().toString());
                double c = Double.parseDouble(commissionEditText.getText().toString());

                price = String.valueOf(p);
                comm = String.valueOf(c);
            }
            catch (Exception e){
                showErrorMessage(getString(R.string.valid_data));
                return;
            }

            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("price", price);
            i.putExtra("commission", comm);
            i.putExtra("carId", carId);
            startActivityForResult(i, 5);
            finish();
        }

    }

    @OnClick(R.id.iv_back)
    void onBackClicked() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void paymentSkiped(ResponseBody responseBody) {
        showSuccessMessage(getString(R.string.your_car_marked_as_sold));
        new Handler().postDelayed(this::finish, 1500);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancelClicked() {

        if (priceEditText.getText().toString().equals("0")){
            showErrorMessage(getString(R.string.type_price));
            return;
        }
        if (priceEditText.getText().toString().isEmpty()) {
            showErrorMessage(getString(R.string.type_price));
        }else {
            String price = null;
            String comm = null;
            try {
                double p = Double.parseDouble(priceEditText.getText().toString());
                double c = Double.parseDouble(commissionEditText.getText().toString());

                price = String.valueOf(p);
                comm = String.valueOf(c);
            }
            catch (Exception e){
                showErrorMessage(getString(R.string.valid_data));
                return;
            }
            presenter.cancelPayment(String.valueOf(carId) , comm , price);

        }

    }

}
