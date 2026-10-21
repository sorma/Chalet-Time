package com.francesco.chalettime.ui.common;

/** A result handled once by the active screen, including after a configuration change. */
public final class UiEvent<T> {
    private final T value;
    private boolean handled;
    public UiEvent(T value) { this.value = value; }
    public T consume() {
        if (handled) return null;
        handled = true;
        return value;
    }
}
