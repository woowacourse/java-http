package org.apache.coyote.controller;

import java.util.function.BiConsumer;

public interface ThrowableBiConsumer<T, U, E extends Throwable> extends BiConsumer<T, U> {

    default void accept(T t, U u) {
        try {
            acceptNow(t, u);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    void acceptNow(T t, U u) throws E;
}
