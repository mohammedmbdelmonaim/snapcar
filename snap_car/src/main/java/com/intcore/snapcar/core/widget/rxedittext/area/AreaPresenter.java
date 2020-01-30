package com.intcore.snapcar.core.widget.rxedittext.area;

import android.content.Context;

import com.jakewharton.rxbinding2.InitialValueObservable;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.TextUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditTextPresenter;
import com.intcore.snapcar.core.widget.rxedittext.TextChangesListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

class AreaPresenter extends RxEditTextPresenter {

    AreaPresenter(Context context) {
        super(context);
    }

    void listenIfValid(InitialValueObservable<TextViewAfterTextChangeEvent> afterTextChangeObservable, TextChangesListener<TextUtil.Result> validityListener) {
        Preconditions.checkNonNull(validityListener, "validityListener cannot be null");
        disposable.add(afterTextChangeObservable
                .filter(et -> et.view() != null)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(CharSequence::toString)
                .map(String::trim)
                .filter(s -> !textUtil.isEmpty(s))
                .map(textUtil::getIfValidAreaResult)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(validityListener::onChanged, Timber::e));
    }
}