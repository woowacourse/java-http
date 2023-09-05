package org.apache.coyote.exception;

public abstract class CoyoteException extends RuntimeException {

    protected CoyoteException(final String message) {
        super(message);
    }
}
