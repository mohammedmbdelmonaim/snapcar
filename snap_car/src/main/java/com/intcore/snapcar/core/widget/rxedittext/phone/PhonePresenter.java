package com.intcore.snapcar.core.widget.rxedittext.phone;

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

class PhonePresenter extends RxEditTextPresenter {

    PhonePresenter(Context context) {
        super(context);
    }

    void listenIfValid(InitialValueObservable<TextViewAfterTextChangeEvent> afterTextChangeObservable,
                       TextChangesListener<TextUtil.Result> validityListener,
                       String phoneRegex) {
        Preconditions.checkNonNull(validityListener, "validityListener cannot be null");
        disposable.add(
                afterTextChangeObservable
                        .filter(et -> et.view() != null)
                        .map(TextViewAfterTextChangeEvent::editable)
                        .map(CharSequence::toString)
                        .map(String::trim)
                        .map(phone -> textUtil.getIfValidPhoneNumberResult(phone, phoneRegex))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(validityListener::onChanged, Timber::e));
    }
}