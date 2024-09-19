package com.techcourse.exception;

public class IllegalConstructionException extends IllegalStateException {
    public IllegalConstructionException(Class<?> clazz) {
        throw new IllegalStateException(clazz.getName() + " cant not be constructed.");
    }
}
