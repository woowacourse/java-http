package org.apache.catalina.exception;

public class MethodNotAllowedException extends Http4xxException {

    public MethodNotAllowedException(String message) {
        super(message);
    }

    public MethodNotAllowedException(Exception e) {
        super(e.getMessage());
    }
}
