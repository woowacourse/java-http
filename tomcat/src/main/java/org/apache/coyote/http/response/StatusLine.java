package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpVersion;

public class StatusLine {

    private static final String DELIMITER = " ";

    private final HttpVersion version;
    private final HttpStatus status;

    private StatusLine(HttpVersion version, HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public static StatusLine of(final HttpVersion version, final HttpStatus status) {
        return new StatusLine(version, status);
    }

    public String value() {
        return version.getValue() + DELIMITER + status.code() + DELIMITER + status.message();
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
