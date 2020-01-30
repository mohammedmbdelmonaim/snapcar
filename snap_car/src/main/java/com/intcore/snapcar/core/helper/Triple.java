package com.intcore.snapcar.core.helper;

public class Triple<T, V, U> {

    private final T first;
    private final V second;
    private final U third;

    public Triple(T first, V second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    public U getThird() {
        return third;
    }
}
