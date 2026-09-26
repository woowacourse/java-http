package org.apache.coyote.http11.request;

import java.io.IOException;

public class BadRequestException extends IOException {

    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
