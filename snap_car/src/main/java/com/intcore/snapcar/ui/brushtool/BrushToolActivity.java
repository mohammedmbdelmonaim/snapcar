package com.intcore.snapcar.ui.brushtool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.core.base.BaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrushToolActivity extends BaseActivity implements BrushToolScreen {

    @BindView(R.id.drawing)
    DrawingView drawingView;
    @BindView(R.id.eraser)
    ImageView pen;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.place_holder_text_view)
    TextView placeHolderTextView;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.btn_gallery)
    Button btnGallery;
    @BindView(R.id.btn_camera)
    Button btnCamera;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    @Inject
    BrushToolPresenter presenter;
    private String uploadedUrl;
    private BitmapDrawable ob;
    private int imagePos;
    private Bitmap photo;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreateActivityComponents() {
        SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this))
                .inject(this);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        imagePos = getIntent().getIntExtra("imagePos", 0);
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_brush_tool;
    }

    @Override
    public void getSelectedFile(File file) {
        photo = BitmapFactory.decodeFile(file.getPath());
        ob = new BitmapDrawable(getResources(), photo);
        placeHolderTextView.setVisibility(View.GONE);
        drawingView.setImageView(ob);
        btnGallery.setVisibility(View.GONE);
        btnCamera.setVisibility(View.GONE);
    }

    @Override
    public void setUploadedUrl(String s) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("uri", s);
        returnIntent.putExtra("pos", imagePos);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.reset)
    void onResetClicked() {
        drawingView.reset();
    }

    @OnClick(R.id.btnSubmit)
    void onSubmitClicked() {
        if (photo == null) {
            showWarningMessage(getString(R.string.please_select_image_first));
            return;
        }
        drawingView.setDrawingCacheEnabled(true);
        Bitmap bm = drawingView.getDrawingCache();
        File file = new File(this.getCacheDir(), "image");
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        presenter.uploadFile(file);
    }

    @OnClick({R.id.iv_back})
    void onBackClicked() {
        finish();
    }

    @OnClick(R.id.btn_gallery)
    void onGalleryClicked() {
        drawingView.reset();
        CropImage.activity()
                .setAutoZoomEnabled(false)
                .setAllowRotation(false)
                .setAllowFlipping(false)
                .setCropMenuCropButtonTitle(getString(R.string.crop))
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .start(this);
    }

    @OnClick(R.id.btn_camera)
    void onCameraClicked() {
        drawingView.reset();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 200);
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
    public void showPercentageLoadingAnimation() {
        if (progressDialog != null) progressDialog.show();
    }

    @Override
    public void hidePercentageLoadingAnimation() {
        if (progressDialog != null) progressDialog.hide();
    }

    @Override
    public void updatePercentage(int percentage) {
        progressDialog.setProgress(percentage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                photo = loadBitmap(resultUri.toString());
                ob = new BitmapDrawable(getResources(), photo);
                placeHolderTextView.setVisibility(View.GONE);
                drawingView.setBackground(ob);
                btnGallery.setVisibility(View.GONE);
                btnCamera.setVisibility(View.GONE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public Bitmap loadBitmap(String url) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }
}