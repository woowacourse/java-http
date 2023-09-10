package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

public class StatusLine {

    private static final String DELIMITER = " ";
    private static final String SPACE = "";

    private final HttpVersion version;
    private final StatusCode statusCode;

    public StatusLine(final HttpVersion version, final StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String toMessage() {
        return String.join(
                DELIMITER,
                version.getValue(),
                statusCode.getCode(),
                statusCode.name(),
                SPACE
        );
    }
}
