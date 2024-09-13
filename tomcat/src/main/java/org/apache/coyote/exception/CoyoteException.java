package org.apache.coyote.exception;

public class CoyoteException extends RuntimeException {

    public CoyoteException(Exception e) {
        super(e);
    }

    public CoyoteException(String message) {
        super(message);
    }
}
