package com.intcore.snapcar.core.widget.snackbar;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intcore.snapcar.core.util.Preconditions;

public class SnackBarBuilder {

    private int duration = Snackbar.LENGTH_SHORT;
    private final View containerView;
    private final Context context;
    private View snackBarView;
    private String text = "";

    public SnackBarBuilder(Context context, View containerView) {
        Preconditions.checkNonNull(context, "context must be non null");
        Preconditions.checkNonNull(containerView, "snack bar container must be non null");
        this.containerView = containerView;
        this.context = context;
    }

    public SnackBarBuilder layout(@LayoutRes int snackBarLayout) {
        snackBarView = LayoutInflater.from(context).inflate(snackBarLayout, null);
        return this;
    }

    public SnackBarBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public SnackBarBuilder clickListener(@IdRes int viewId, View.OnClickListener clickListener) {
        snackBarView.findViewById(viewId).setOnClickListener(clickListener);
        return this;
    }

    public SnackBarBuilder text(@IdRes int viewId, String text) {
        this.text = text;
        if (snackBarView != null) {
            ((TextView) snackBarView.findViewById(viewId)).setText(text);
        }
        return this;
    }

    public Snackbar build() {
        Snackbar snackbar = Snackbar.make(containerView, text, duration);
        if (snackBarView != null) {
            Snackbar.SnackbarLayout sbl = (Snackbar.SnackbarLayout) snackbar.getView();
            sbl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            sbl.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
            sbl.setPadding(0, 0, 0, 0);
            sbl.setBackgroundColor(0x00000000);
            sbl.addView(snackBarView, 0);
        }
        return snackbar;
    }
}
