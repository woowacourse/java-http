package org.apache.coyote.http11.response.header;

import org.apache.coyote.http11.common.HttpMessageDelimiter;

public class StatusLine {

    private final String httpVersion;
    private final StatusCode statusCode;

    public StatusLine(final String httpVersion, final StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public String toMessage() {
        final StringBuilder message = new StringBuilder().append(httpVersion)
            .append(HttpMessageDelimiter.WORD.getValue())
            .append(statusCode.toMessage())
            .append(HttpMessageDelimiter.WORD.getValue());
        return new String(message);
    }
}
