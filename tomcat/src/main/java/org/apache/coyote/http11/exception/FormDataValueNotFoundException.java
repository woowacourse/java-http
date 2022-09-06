package org.apache.coyote.http11.exception;

public class FormDataValueNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Query String Not Found";

    public FormDataValueNotFoundException() {
        super(MESSAGE);
    }
}
