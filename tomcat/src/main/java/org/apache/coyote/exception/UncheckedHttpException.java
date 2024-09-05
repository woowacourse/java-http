package org.apache.coyote.exception;

public class UncheckedHttpException extends RuntimeException {

    public UncheckedHttpException(Exception e) {
        super(e);
    }
}
