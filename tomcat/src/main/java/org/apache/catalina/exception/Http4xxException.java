package org.apache.catalina.exception;

public class Http4xxException extends RuntimeException {

    public Http4xxException(String message) {
        super(message);
    }

    public Http4xxException(Exception e) {
        super(e.getMessage());
    }
}
