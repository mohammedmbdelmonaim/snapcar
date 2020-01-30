package com.intcore.snapcar.events;

public interface OperationListener<T> {

    void onPreOperation();

    void onPostOperation();

    void onSuccess(T element);


    void onError(Throwable t);
}