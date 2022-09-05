package org.apache.coyote.http11.exception;

public class ContentTypeNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Content Type Not Found";

    public ContentTypeNotFoundException() {
        super(MESSAGE);
    }
}
