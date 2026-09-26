package org.apache.coyote.http11;

import java.util.Objects;

public class StatusLine {

    private final String protocol;
    private final HttpStatus status;

    public StatusLine(String protocol, HttpStatus status) {
        this.protocol = Objects.requireNonNull(protocol);
        this.status = Objects.requireNonNull(status);
    }

    @Override
    public String toString() {
        return protocol + " " + status.getCode() + " " + status.getReasonPhrase();
    }
}
