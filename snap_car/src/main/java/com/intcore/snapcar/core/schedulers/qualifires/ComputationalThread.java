package com.intcore.snapcar.core.schedulers.qualifires;

import com.intcore.snapcar.core.schedulers.ThreadSchedulers;

import javax.inject.Qualifier;

/**
 This qualifier is used for distinguishing between different {@link ThreadSchedulers} for dependency injection.
 */

@Qualifier
public @interface ComputationalThread {
}