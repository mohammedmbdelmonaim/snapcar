package com.intcore.snapcar.core.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 unit-testing-specific Schedulers
 */

public class TestThreadSchedulers implements ThreadSchedulers {

    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler workerThread() {
        return Schedulers.newThread();
    }
}