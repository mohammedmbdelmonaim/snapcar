package com.intcore.snapcar.core.widget.rxedittext.area;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.intcore.snapcar.core.widget.rxedittext.TextChangesListener;

public class AreaEditText extends RxEditText {

    private AreaPresenter presenter;

    public AreaEditText(Context context) {
        super(context);
    }

    public AreaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AreaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        presenter = new AreaPresenter(context);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    @Override
    public void setValidityListener(TextChangesListener<TextUtil.Result> validityListener) {
        presenter.listenIfValid(RxTextView.afterTextChangeEvents(this), validityListener);
    }
}