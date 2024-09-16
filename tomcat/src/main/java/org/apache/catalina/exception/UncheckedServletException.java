package org.apache.catalina.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(Exception e) {
        super("UncheckedServletException occured", e);
    }
}
