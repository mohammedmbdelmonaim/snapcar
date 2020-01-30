package com.intcore.snapcar.ui.addcarphotes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intcore.snapcar.R;
import com.intcore.snapcar.SnapCarApplication;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.activity.ActivityModule;
import com.intcore.snapcar.store.api.ApiUtils;
import com.intcore.snapcar.ui.brushtool.BrushToolActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.UiUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;

@ActivityScope
public class AddCarPhotosActivity extends BaseActivity implements AddCarPhotosScreen {

    @BindView(R.id.prev_layout)
    RelativeLayout backPrevLayout;
    @BindView(R.id.image_prev_back)
    ImageView backPrevImage;
    @BindView(R.id.left_prev_layout)
    RelativeLayout leftPrevLayout;
    @BindView(R.id.image_prev_left)
    ImageView leftPrevImage;
    @BindView(R.id.right_prev_layout)
    RelativeLayout rightPrevLayout;
    @BindView(R.id.image_prev_right)
    ImageView rightPrevImage;
    @BindView(R.id.front_prev_layout)
    RelativeLayout frontPrevLayout;
    @BindView(R.id.image_prev_front)
    ImageView frontPrevImage;
    @BindView(R.id.inside1_prev_layout)
    RelativeLayout inside1PrevLayout;
    @BindView(R.id.image_prev_inside1)
    ImageView inside1PrevImage;
    @BindView(R.id.inside2_prev_layout)
    RelativeLayout inside2PrevLayout;
    @BindView(R.id.image_prev_inside2)
    ImageView inside2PrevImage;
    @BindView(R.id.left_side)
    ImageView leftAdd;
    @BindView(R.id.right_side)
    ImageView rightAdd;
    @BindView(R.id.front_side)
    ImageView frontAdd;
    @BindView(R.id.back_side)
    ImageView backAdd;
    @BindView(R.id.inside_1)
    ImageView inside1Add;
    @BindView(R.id.inside_2)
    ImageView insid2Add;
    @Inject
    AddCarPhotosPresenter presenter;
    @Inject
    UiUtil uiUtil;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.cancel_right)
    ImageView cancelRight;
    @BindView(R.id.cancel_back)
    ImageView cancelBack;
    @BindView(R.id.right_layout)
    RelativeLayout rightLayout;
    @BindView(R.id.leftLayout)
    RelativeLayout leftLayout;
    @BindView(R.id.cancel_inside1)
    ImageView cancelInside1;
    @BindView(R.id.cancel_inside2)
    ImageView cancelInside2;
    @BindView(R.id.save_btn)
    AppCompatButton saveBtn;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.snack_bar_container)
    FrameLayout snackBarContainer;
    private ActivityComponent component;
    private int uploadedPos;
    private File file;
    private List<Image> uploadedImages = new ArrayList<>();
    private String imagesJeson;
    private String json;
    private ProgressDialog progressDialog;
    private Uri imageUri;



    @Override
    protected void onCreateActivityComponents() {
        component = SnapCarApplication.getComponent(this)
                .plus(new ActivityModule(this));
        component.inject(this);

        ButterKnife.bind(this);

        if(!getResourcesUtil().isEnglish()){
            rightLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            leftLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        imagesJeson = getIntent().getStringExtra("json");
        if (!imagesJeson.equals("")) {
            Type listType = new TypeToken<List<Image>>() {
            }.getType();
            this.uploadedImages = new Gson().fromJson(imagesJeson, listType);
            for (Image i : uploadedImages) {
                if (i.getPlace() == 1) {
                    frontPrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(frontPrevImage);
                    frontAdd.setClickable(false);
                } else if (i.getPlace() == 2) {
                    leftPrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(leftPrevImage);
                    leftAdd.setClickable(false);
                } else if (i.getPlace() == 3) {
                    rightPrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(rightPrevImage);
                    rightAdd.setClickable(false);
                } else if (i.getPlace() == 4) {
                    backPrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(backPrevImage);
                    backAdd.setClickable(false);
                } else if (i.getPlace() == 5) {
                    inside1PrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(inside1PrevImage);
                    inside1Add.setClickable(false);
                } else if (i.getPlace() == 6) {
                    inside2PrevLayout.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(i.getImage()))
                            .centerCrop()
                            .into(inside2PrevImage);
                    insid2Add.setClickable(false);
                }
            }
        }
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.back_black_ic);
        ivBack.setImageDrawable(icons.getDrawable(0));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_add_car_photes;
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

    @OnClick({R.id.left_side, R.id.right_side, R.id.front_side, R.id.back_side, R.id.inside_1, R.id.inside_2})
    public void onPickPicture(View v) {
        Intent intent = new Intent(this, BrushToolActivity.class);
        switch (v.getId()) {
            case R.id.left_side:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 2) {
                        uploadedImages.remove(i);
                    }
                }
                uiUtil.getDialogBuilder(this, R.layout.dialog_pic_photo)
                        .clickListener(R.id.btn_reject, (dialog, view) -> {
                            presenter.openGallery(RxPaparazzo.single(this), 2);
                            dialog.dismiss();
                        })
                        .clickListener(R.id.btn_accept, (dialog, view) -> {
                            presenter.openCamera(RxPaparazzo.single(this), 2);
                            dialog.dismiss();
                        }).background(R.drawable.inset_bottomsheet_background)
                        .cancelable(true)
                        .build()
                        .show();
                break;

            case R.id.right_side:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 3) {
                        uploadedImages.remove(i);
                    }
                }
                uiUtil.getDialogBuilder(this, R.layout.dialog_pic_photo)
                        .clickListener(R.id.btn_reject, (dialog, view) -> {
                            presenter.openGallery(RxPaparazzo.single(this), 3);
                            dialog.dismiss();
                        })
                        .clickListener(R.id.btn_accept, (dialog, view) -> {
                            presenter.openCamera(RxPaparazzo.single(this), 3);
                            dialog.dismiss();
                        })
                        .background(R.drawable.inset_bottomsheet_background)
                        .cancelable(true)
                        .build()
                        .show();
                break;
            case R.id.front_side:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 1) {
                        uploadedImages.remove(i);
                    }
                }
                intent.putExtra("imagePos", 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.back_side:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 4) {
                        uploadedImages.remove(i);
                    }
                }
                intent.putExtra("imagePos", 4);
                startActivityForResult(intent, 1);
                break;
            case R.id.inside_1:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 5) {
                        uploadedImages.remove(i);
                    }
                }
                uiUtil.getDialogBuilder(this, R.layout.dialog_pic_photo)
                        .clickListener(R.id.btn_reject, (dialog, view) -> {
                            presenter.openGallery(RxPaparazzo.single(this), 5);
                            dialog.dismiss();
                        })
                        .clickListener(R.id.btn_accept, (dialog, view) -> {
                            presenter.openCamera(RxPaparazzo.single(this), 5);
                            dialog.dismiss();
                        })
                        .background(R.drawable.inset_bottomsheet_background)
                        .cancelable(true)
                        .build()
                        .show();
                break;
            case R.id.inside_2:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 6) {
                        uploadedImages.remove(i);
                    }
                }
                uiUtil.getDialogBuilder(this, R.layout.dialog_pic_photo)
                        .clickListener(R.id.btn_reject, (dialog, view) -> {
                            presenter.openGallery(RxPaparazzo.single(this), 6);
                            dialog.dismiss();
                        })
                        .clickListener(R.id.btn_accept, (dialog, view) -> {
                            presenter.openCamera(RxPaparazzo.single(this), 6);
                            dialog.dismiss();
                        })
                        .background(R.drawable.inset_bottomsheet_background)
                        .cancelable(true)
                        .build()
                        .show();
                break;
        }
    }

    @Override
    public void setNewImagePath(String s) {

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        if (!s.isEmpty()) {
            if (uploadedPos == 1) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 1) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 1));
                frontPrevLayout.setVisibility(View.VISIBLE);
                frontPrevImage.setImageBitmap(bitmap);
                frontAdd.setClickable(false);
            } else if (uploadedPos == 2) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 6) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 0));
                leftPrevLayout.setVisibility(View.VISIBLE);
                leftPrevImage.setImageBitmap(bitmap);
                leftAdd.setClickable(false);
            } else if (uploadedPos == 3) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 3) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 0));
                rightPrevLayout.setVisibility(View.VISIBLE);
                rightPrevImage.setImageBitmap(bitmap);
                rightAdd.setClickable(false);
            } else if (uploadedPos == 4) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 4) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 0));
                backPrevLayout.setVisibility(View.VISIBLE);
                backPrevImage.setImageBitmap(bitmap);
                backAdd.setClickable(false);
            } else if (uploadedPos == 5) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 5) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 0));
                inside1PrevLayout.setVisibility(View.VISIBLE);
                inside1PrevImage.setImageBitmap(bitmap);
                inside1Add.setClickable(false);
            } else if (uploadedPos == 6) {
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 6) {
                        uploadedImages.remove(i);
                    }
                }
                uploadedImages.add(new Image(s, uploadedPos, 0));
                inside2PrevLayout.setVisibility(View.VISIBLE);
                inside2PrevImage.setImageBitmap(bitmap);
                insid2Add.setClickable(false);
            }
        }
    }

    @Override
    public void setuploadedPosition(int currentImagePos, File f) {
        this.uploadedPos = currentImagePos;
        this.file = f;
    }

    @Override
    public void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "image");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(cameraIntent, 200);
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

    @OnClick({R.id.save_btn})
    public void onSaveClicked(View v) {
        ArrayList<Integer> positions = new ArrayList<>();
        if (uploadedImages.size() == 0) {
            showErrorMessage(getString(R.string.front_back_is_mandatory));
            return;
        }
        for (int x = 0; x < uploadedImages.size(); x++) {
            positions.add(uploadedImages.get(x).getPlace());
            if (x == uploadedImages.size() - 1) {
                if (!positions.contains(1)) {
                    showErrorMessage(getString(R.string.front_back_is_mandatory));
                    return;
                } else {
                    json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().
                            create().toJson(uploadedImages);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", json);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        }
    }

    @OnClick({R.id.cancel_back, R.id.cancel_left, R.id.cancel_right, R.id.cancel_front, R.id.cancel_inside1, R.id.cancel_inside2})
    public void onRemoveImage(View v) {
        switch (v.getId()) {
            case R.id.cancel_back:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 4) {
                        uploadedImages.remove(i);
                    }
                }
                backPrevLayout.setVisibility(View.INVISIBLE);
                backAdd.setClickable(true);
                break;
            case R.id.cancel_left:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 2) {
                        uploadedImages.remove(i);
                    }
                }
                leftPrevLayout.setVisibility(View.INVISIBLE);
                leftAdd.setClickable(true);
                break;
            case R.id.cancel_right:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 3) {
                        uploadedImages.remove(i);
                    }
                }
                rightPrevLayout.setVisibility(View.INVISIBLE);
                rightAdd.setClickable(true);
                break;
            case R.id.cancel_front:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 1) {
                        uploadedImages.remove(i);
                    }
                }
                frontPrevLayout.setVisibility(View.INVISIBLE);
                frontAdd.setClickable(true);
                break;
            case R.id.cancel_inside1:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 5) {
                        uploadedImages.remove(i);
                    }
                }
                inside1PrevLayout.setVisibility(View.INVISIBLE);
                inside1Add.setClickable(true);
                break;
            case R.id.cancel_inside2:
                for (int i = 0; i < uploadedImages.size(); i++) {
                    if (uploadedImages.get(i).getPlace() == 6) {
                        uploadedImages.remove(i);
                    }
                }
                inside2PrevLayout.setVisibility(View.INVISIBLE);
                insid2Add.setClickable(true);
                break;
        }
    }

    @OnClick({R.id.iv_back})
    public void onBackClicked(View v) {
        if (json == null || json.isEmpty()) {
            getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                    .text(R.id.tv_message, getString(R.string.are_sure_want_exit))
                    .clickListener(R.id.no, (dialog, view) -> {
                        dialog.dismiss();
                    })
                    .clickListener(R.id.yes, (dialog, view) -> {
                        finish();
                        dialog.dismiss();
                    })
                    .background(R.color.transparent)
                    .gravity(Gravity.CENTER)
                    .build()
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (json == null || json.isEmpty()) {
            getUiUtil().getDialogBuilder(this, R.layout.layout_hotzone_filter_dialog)
                    .text(R.id.tv_message, getString(R.string.are_sure_want_exit))
                    .clickListener(R.id.no, (dialog, view) -> {
                        dialog.dismiss();
                    })
                    .clickListener(R.id.yes, (dialog, view) -> {
                        super.onBackPressed();
                        dialog.dismiss();
                    })
                    .background(R.color.transparent)
                    .gravity(Gravity.CENTER)
                    .build()
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {

            try {
                presenter.uploadFile(new Compressor(SnapCarApplication.getContext()).compressToFile(new File(imageUri.getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String uri = data.getStringExtra("uri");
                int pos = data.getIntExtra("pos", 0);
                if (pos == 1) {
                    uploadedImages.add(new Image(uri, pos, 1));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(frontPrevImage);
                    frontPrevLayout.setVisibility(View.VISIBLE);
                    frontAdd.setClickable(false);
                } else if (pos == 2) {
                    uploadedImages.add(new Image(uri, pos, 0));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(leftPrevImage);
                    leftPrevLayout.setVisibility(View.VISIBLE);
                    leftAdd.setClickable(false);
                } else if (pos == 3) {
                    uploadedImages.add(new Image(uri, pos, 0));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(rightPrevImage);
                    rightPrevLayout.setVisibility(View.VISIBLE);
                    rightAdd.setClickable(false);
                } else if (pos == 4) {
                    uploadedImages.add(new Image(uri, pos, 0));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(backPrevImage);
                    backPrevLayout.setVisibility(View.VISIBLE);
                    backAdd.setClickable(false);
                } else if (pos == 5) {
                    uploadedImages.add(new Image(uri, pos, 0));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(inside1PrevImage);
                    inside1PrevLayout.setVisibility(View.VISIBLE);
                    inside1Add.setClickable(false);
                } else if (pos == 6) {
                    uploadedImages.add(new Image(uri, pos, 0));
                    Glide.with(this)
                            .load(ApiUtils.BASE_URL.concat(uri))
                            .centerCrop()
                            .into(inside2PrevImage);
                    inside2PrevLayout.setVisibility(View.VISIBLE);
                    insid2Add.setClickable(false);
                }
            }
        }
    }


}