package com.intcore.snapcar.core.widget.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intcore.snapcar.R;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetBuilder {

    private final View view;
    private BottomSheetDialog dialog;

    public BottomSheetBuilder(Context context, @LayoutRes int layoutRes) {
        Preconditions.checkNonNull(context, "context must be non null");
        view = LayoutInflater.from(context).inflate(layoutRes, null);
        dialog = new BottomSheetDialog(context);
        dialog.setCancelable(false);
        if ("ar".equals(LocaleUtil.getLanguage(context))) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = Gravity.BOTTOM;
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        dialog.setContentView(view);
    }

    public BottomSheetBuilder clickListener(@IdRes int viewId, @NonNull OnClickListener onClickListener) {
        view.findViewById(viewId).setOnClickListener(v -> onClickListener.onClick(dialog, v));
        return this;
    }

    public BottomSheetBuilder transparentBackground(boolean transparent) {
        if (transparent) {
            dialog.setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet == null) {
                    return;
                }
                bottomSheet.setBackground(null);
            });
        }
        return this;
    }

    public BottomSheetBuilder background(@DrawableRes int drawableRes) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes();
            window.setBackgroundDrawableResource(drawableRes);
        }
        return this;
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(view.getContext()).equals("en");
    }

    public BottomSheetBuilder text(@IdRes int viewId, String text) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        if (isEnglish()) {
            ((TextView) view).setGravity(Gravity.LEFT);
        } else {
            ((TextView) view).setGravity(Gravity.RIGHT);
        }
        ((TextView) view).setText(text);
        return this;
    }

    public BottomSheetBuilder drawableEnd(@IdRes int viewId, Drawable drawable) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        ((TextView) view).setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
        return this;
    }

    public BottomSheetBuilder image(@IdRes int viewId, String imageUrl) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        CircleImageView avatar = ((CircleImageView) view);
        Glide.with(view)
                .load(imageUrl)
                .centerCrop()
                .into(avatar);
        return this;
    }

    public BottomSheetBuilder textColor(@IdRes int viewId, int colorRes) {
        View view = this.view.findViewById(viewId);
        ((TextView) view).setTextColor(colorRes);
        return this;
    }

    public BottomSheetBuilder setActivated(@IdRes int viewId, Boolean isFavorite) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        view.setActivated(isFavorite);
        return this;
    }

    public BottomSheetBuilder setVisbility(@IdRes int viewId, Boolean isVisible) {
        View view = this.view.findViewById(viewId);
        if (isVisible)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
        return this;
    }

    public BottomSheetBuilder cancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public Dialog build() {
        return dialog;
    }

    public BottomSheetBuilder visibility(@IdRes int viewId, @IdRes int viewIdTwo, boolean isDamaged) {
        if (isDamaged) {
            View view = this.view.findViewById(viewId);
            view.setVisibility(View.VISIBLE);
            View view2 = this.view.findViewById(viewIdTwo);
            view2.setVisibility(View.GONE);
        }
        return this;
    }

    public interface OnClickListener {

        void onClick(BottomSheetDialog dialog, View view);
    }
}