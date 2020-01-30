package com.intcore.snapcar.core.helper;

public final class SwitchCase {

    private final Object reference;

    public SwitchCase(Object reference) {
        this.reference = reference;
    }

    public SwitchCase whenInstanceOf(Class o, OnMatchDo onMatchDo) {
        if (o.equals(reference.getClass())) {
            onMatchDo.task(reference);
        }
        return this;
    }

    public void otherwise(OnMatchDo onMatchDo) {
        onMatchDo.task(reference);
    }

    public interface OnMatchDo {
        void task(Object o);
    }
}
