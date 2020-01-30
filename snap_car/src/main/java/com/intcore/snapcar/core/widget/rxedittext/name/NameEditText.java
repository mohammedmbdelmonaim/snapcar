package com.intcore.snapcar.core.widget.rxedittext.name;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.intcore.snapcar.core.widget.rxedittext.TextChangesListener;

public class NameEditText extends RxEditText {

    private NamePresenter presenter;

    public NameEditText(Context context) {
        super(context);
    }

    public NameEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NameEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        presenter = new NamePresenter(context);
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    }

    @Override
    public void setValidityListener(TextChangesListener<TextUtil.Result> validityListener) {
        presenter.listenIfValid(RxTextView.afterTextChangeEvents(this), validityListener);
    }
}
