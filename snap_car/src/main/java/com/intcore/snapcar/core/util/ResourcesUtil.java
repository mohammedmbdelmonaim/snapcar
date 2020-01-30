package com.intcore.snapcar.core.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intcore.snapcar.SnapCarApplication;

import javax.inject.Inject;

public class ResourcesUtil {

    private final Context context;

    @Inject
    public ResourcesUtil(Context context) {
        this.context = SnapCarApplication.getContext();
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    public String getString(@StringRes int resourceId) {
        return context.getString(resourceId);
    }

    public int getColor(@ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public Drawable getDrawable(@DrawableRes int resourceId) {
        return ContextCompat.getDrawable(context, resourceId);
    }

    public Bitmap loadBitmapFromView(View view) {
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Context getContext() {
        return context;
    }

    public boolean isOrientationPortrait() {
        return Configuration.ORIENTATION_PORTRAIT == context.getResources().getConfiguration().orientation;
    }

    public Bitmap resizeMapIcons(Bitmap icon, int width, int height) {
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }

    public boolean isEnglish() {
        return LocaleUtil.getLanguage(context).equals("en");
    }
}
