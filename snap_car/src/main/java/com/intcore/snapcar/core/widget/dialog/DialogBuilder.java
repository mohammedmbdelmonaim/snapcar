package com.intcore.snapcar.core.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;


import static androidx.transition.Slide.GravityFlag;

public class DialogBuilder {

    private final View view;
    private OnCounterFinish counterFinish;
    private CountDownTimer countDownTimer;
    private Dialog dialog;

    public DialogBuilder(Context context, @LayoutRes int layoutRes) {
        Preconditions.checkNonNull(context, "context must be non null");
        view = LayoutInflater.from(context).inflate(layoutRes, null);
        dialog = new Dialog(context);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = Gravity.CENTER;
            window.setAttributes(attributes);
        }
        dialog.setContentView(view);
    }

    public DialogBuilder clickListener(@IdRes int viewId, @NonNull OnClickListener onClickListener) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(v -> onClickListener.onClick(dialog, v));
        return this;
    }

    public DialogBuilder editText(@IdRes int viewId, @NonNull OnTextChangeListener onTextChangeListener) {
        EditText view = this.view.findViewById(viewId);
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onTextChangeListener.onTextChange(editable.toString());
            }
        });
        return this;
    }

    public DialogBuilder rateChangeListener(@IdRes int viewId, @NonNull OnRateChangeListener onRateChangeListener) {
        RatingBar view = this.view.findViewById(viewId);
        view.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> onRateChangeListener.onRateChange(rating));
        return this;
    }

    public DialogBuilder countDownTimer(@IdRes int viewId, long timeOut, long interval) {
        if (viewId == 0) {
            if (timeOut < 0) {
                timeOut = 0;
            }
            if (interval < 0) {
                interval = 0;
            }
            countDownTimer = new CountDownTimer(timeOut, interval) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            };
            dialog.setOnShowListener(dialog1 -> countDownTimer.start());
            dialog.setOnDismissListener(dialog1 -> countDownTimer.cancel());
        } else {
            View view = this.view.findViewById(viewId);
            view.setVisibility(View.VISIBLE);
            if (timeOut < 0) {
                timeOut = 0;
            }
            if (interval < 0) {
                interval = 0;
            }
            countDownTimer = new CountDownTimer(timeOut, interval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    ((TextView) view).setText("00:".concat(String.valueOf(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            };
            dialog.setOnShowListener(dialog1 -> countDownTimer.start());
            dialog.setOnDismissListener(dialog1 -> {
                countDownTimer.cancel();
                if (counterFinish != null) {
                    counterFinish.onFinish();
                }
            });
        }
        return this;
    }

    private boolean isEnglish() {
        return LocaleUtil.getLanguage(view.getContext()).equals("en");
    }

    public DialogBuilder background(@DrawableRes int drawableRes) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.getAttributes();
            window.setBackgroundDrawableResource(drawableRes);
        }
        return this;
    }

    public DialogBuilder text(@IdRes int viewId, String text) {
        if (!TextUtils.isEmpty(text)) {
            View view = this.view.findViewById(viewId);
            view.setVisibility(View.VISIBLE);
            ((TextView) view).setText(text);

        } else {
            View view = this.view.findViewById(viewId);
            view.setVisibility(View.GONE);
        }
        return this;
    }

    public DialogBuilder textGravity(@IdRes int viewId, @GravityFlag int gravity) {
        View view = this.view.findViewById(viewId);
        view.setVisibility(View.VISIBLE);
        ((TextView) view).setGravity(gravity);
        return this;
    }

    public DialogBuilder gravity(@GravityFlag int gravity) {
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = gravity;
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        return this;
    }

    public DialogBuilder cancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public DialogBuilder background(Drawable drawable) {
        Window window = dialog.getWindow();
        if (window != null) {
            dialog.getWindow().setBackgroundDrawable(drawable);
        }
        return this;
    }

    public DialogBuilder counterFinishListener(OnCounterFinish counterFinish) {
        this.counterFinish = counterFinish;
        return this;
    }

    public Dialog build() {
        return dialog;
    }

    public interface OnRateChangeListener {

        void onRateChange(float rate);
    }

    public interface OnClickListener {

        void onClick(Dialog dialog, View view);
    }

    public interface OnCounterFinish {

        void onFinish();
    }

    public interface OnTextChangeListener {

        void onTextChange(String text);
    }

}