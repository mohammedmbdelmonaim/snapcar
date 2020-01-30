package com.intcore.snapcar.core.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.LayoutRes;
import com.google.android.material.snackbar.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intcore.snapcar.R;
import com.intcore.snapcar.core.widget.bottomsheet.BottomSheetBuilder;
import com.intcore.snapcar.core.widget.dialog.DialogBuilder;
import com.intcore.snapcar.core.widget.snackbar.SnackBarBuilder;

import javax.inject.Inject;

public class UiUtil {

    private final Context context;
    private ProgressDialog progressDialog;

    @Inject
    public UiUtil(Context context) {
        Preconditions.checkNonNull(context, "should not pass null context reference");
        this.context = context;
    }

    public SnackBarBuilder getSnackBarBuilder(Context context, View containerLayout) {
        return new SnackBarBuilder(context, containerLayout);
    }

    public DialogBuilder getDialogBuilder(Context context, @LayoutRes int layout) {
        return new DialogBuilder(context, layout);
    }

    public BottomSheetBuilder getBottomSheetBuilder(Context context, @LayoutRes int layout) {
        return new BottomSheetBuilder(context, layout);
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(context);
    }

    public ProgressDialog getProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(Preconditions.requireStringNotEmpty(message));
        return progressDialog;
    }

    private Toast createToast(View view, String message) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        ((TextView) view.findViewById(R.id.tv_message)).setText(message);
        toast.setView(view);
        return toast;
    }

    public Toast getAnnouncementToast(String message) {
        return createToast(getLayoutInflater().inflate(R.layout.layout_announcement_toast, new FrameLayout(context)), message);
    }

    public Toast getWarningToast(String message) {
        return createToast(getLayoutInflater().inflate(R.layout.layout_warning_toast, new FrameLayout(context)), message);
    }

    public Toast getSuccessToast(String message) {
        return createToast(getLayoutInflater().inflate(R.layout.layout_success_toast, new FrameLayout(context)), message);
    }

    public Toast getErrorToast(String message) {
        return createToast(getLayoutInflater().inflate(R.layout.layout_error_toast, new FrameLayout(context)), message);
    }

    public ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        return progressDialog;
    }

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

//    public void hideKeyboard(View view) {
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        assert imm != null;
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    public Snackbar getSuccessSnackBar(View view, String content) {
        hideKeyboard((Activity) view.getContext());
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setBackgroundColor(0x00000000);
        snackBarLayout.setPadding(0, 0, 0, 0);
        snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        View snackContainer = LayoutInflater.from(context).inflate(R.layout.view_snackbar, null, false);
        TextView contentView = snackContainer.findViewById(R.id.snackbar_content);
        Button dismiss = snackContainer.findViewById(R.id.btn_dismiss);
        contentView.setText(content);
        snackBarLayout.addView(snackContainer, 0);
        dismiss.setOnClickListener(v -> snackbar.dismiss());
        return snackbar;
    }

    public Snackbar getWarningSnackBar(View view, String content) {
        hideKeyboard((Activity) view.getContext());
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setBackgroundColor(0x00000000);
        snackBarLayout.setPadding(0, 0, 0, 0);
        snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        View snackContainer = LayoutInflater.from(context).inflate(R.layout.view_warning_snackbar, null, false);
        TextView contentView = snackContainer.findViewById(R.id.snackbar_content);
        Button dismiss = snackContainer.findViewById(R.id.btn_dismiss);
        contentView.setText(content);
        snackBarLayout.addView(snackContainer, 0);
        dismiss.setOnClickListener(v -> snackbar.dismiss());
        return snackbar;
    }

    public Snackbar getErrorSnackBar(View view, String content) {
        hideKeyboard((Activity) view.getContext());
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarLayout.setBackgroundColor(0x00000000);
        snackBarLayout.setPadding(0, 0, 0, 0);
        snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        View snackContainer = LayoutInflater.from(context).inflate(R.layout.view_error_snackbar, null, false);
        TextView contentView = snackContainer.findViewById(R.id.snackbar_content);
        if (LocaleUtil.getLanguage(context).equals("en")) {
            contentView.setGravity(Gravity.LEFT);
        } else {
            contentView.setGravity(Gravity.RIGHT);
        }
        Button dismiss = snackContainer.findViewById(R.id.btn_dismiss);
        contentView.setText(content);
        snackBarLayout.addView(snackContainer, 0);
        dismiss.setOnClickListener(v -> snackbar.dismiss());
        return snackbar;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}