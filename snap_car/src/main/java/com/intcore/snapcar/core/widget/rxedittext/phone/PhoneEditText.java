package com.intcore.snapcar.core.widget.rxedittext.phone;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.intcore.snapcar.core.widget.rxedittext.TextChangesListener;

public class PhoneEditText extends RxEditText {

    private PhonePresenter presenter;

    public PhoneEditText(Context context) {
        super(context);
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        presenter = new PhonePresenter(context);
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_PHONE);
    }

    @Override
    public void setPhoneValidityListener(TextChangesListener<TextUtil.Result> validityListener, String phoneRegex) {
        presenter.listenIfValid(RxTextView.afterTextChangeEvents(this), validityListener, phoneRegex);
    }
}
