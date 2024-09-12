package org.apache.coyote.http11;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String path) {
        super("Resource not found: " + path);
    }
}
