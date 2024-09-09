package org.apache.coyote.http11.exception;

public class  CanNotHandleRequest extends RuntimeException {
    public CanNotHandleRequest(String message) {
        super(message);
    }
}
