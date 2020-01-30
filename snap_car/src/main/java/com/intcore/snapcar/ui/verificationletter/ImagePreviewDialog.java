package com.intcore.snapcar.ui.verificationletter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intcore.snapcar.R;
import com.intcore.snapcar.store.api.ApiUtils;

class ImagePreviewDialog extends Dialog {

    ImagePreviewDialog(@NonNull Context context, String link) {
        super(context);
        setContentView(R.layout.dialog_examination_image);
        getWindow().getAttributes().windowAnimations = R.style.MyCustomTheme;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView v = findViewById(R.id.exmaination_image);
        Glide.with(context)
                .load(ApiUtils.BASE_URL.concat(link))
                .centerCrop()
                .into(v);
    }
}