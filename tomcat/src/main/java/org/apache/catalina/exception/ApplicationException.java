package org.apache.catalina.exception;

public abstract class ApplicationException extends RuntimeException {

    ApplicationException(String message) {
        super(message);
    }
}
