package com.bigpanda.commons.async;

/**
 * Used for future composition, for final declaration issues
 * Created by erik on 8/13/18.
 */
public class Wrapper<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
