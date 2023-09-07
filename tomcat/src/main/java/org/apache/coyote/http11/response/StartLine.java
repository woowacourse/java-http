package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

public class StartLine {

    private final HttpVersion version;
    private final StatusCode statusCode;

    public StartLine(final HttpVersion version, final StatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String toMessage() {
        return String.join(" ",
                version.getValue(),
                statusCode.getCode(),
                statusCode.name()
        );
    }

    public HttpVersion getVersion() {
        return version;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
