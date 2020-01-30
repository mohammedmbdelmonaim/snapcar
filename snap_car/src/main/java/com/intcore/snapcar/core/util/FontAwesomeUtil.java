package com.intcore.snapcar.core.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class FontAwesomeUtil extends androidx.appcompat.widget.AppCompatTextView {

    public FontAwesomeUtil(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontAwesomeUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeUtil(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "MaterialIcons-Regular.ttf");
        setTypeface(tf);
    }
}