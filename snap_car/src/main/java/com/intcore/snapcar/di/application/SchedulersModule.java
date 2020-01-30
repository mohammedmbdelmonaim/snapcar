package com.intcore.snapcar.di.application;

import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.schedulers.ComputationalThreadSchedulers;
import com.intcore.snapcar.core.schedulers.IOThreadSchedulers;
import com.intcore.snapcar.core.schedulers.MainThreadSchedulers;
import com.intcore.snapcar.core.schedulers.TestThreadSchedulers;
import com.intcore.snapcar.core.schedulers.ThreadSchedulers;
import com.intcore.snapcar.core.schedulers.qualifires.ComputationalThread;
import com.intcore.snapcar.core.schedulers.qualifires.IOThread;
import com.intcore.snapcar.core.schedulers.qualifires.MainThread;
import com.intcore.snapcar.core.schedulers.qualifires.TestThread;

import dagger.Module;
import dagger.Provides;

/**
 This class is responsible for providing the requested objects for {@link ThreadSchedulers} objects
 */

@Module
public class SchedulersModule {

    @ApplicationScope
    @Provides
    @MainThread
    ThreadSchedulers providesMainThreadSchedulers() {
        return new MainThreadSchedulers();
    }

    @ApplicationScope
    @Provides
    @IOThread
    ThreadSchedulers providesIOThreadSchedulers() {
        return new IOThreadSchedulers();
    }

    @ApplicationScope
    @Provides
    @ComputationalThread
    ThreadSchedulers providesComputationalThreadSchedulers() {
        return new ComputationalThreadSchedulers();
    }

    @ApplicationScope
    @Provides
    @TestThread
    ThreadSchedulers providesUnitTestingThreadSchedulers() {
        return new TestThreadSchedulers();
    }
}