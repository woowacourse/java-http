package org.apache.catalina.exception;

public class PathNotFoundException extends Http4xxException {

    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(Exception e) {
        super(e.getMessage());
    }
}
