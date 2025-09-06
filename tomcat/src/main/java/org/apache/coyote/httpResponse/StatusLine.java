package org.apache.coyote.httpResponse;

public class StatusLine {

    private final String protocol;
    private final StatusCode statusCode;

    public StatusLine(
            final String protocol,
            final StatusCode statusCode
    ) {
        this.protocol = protocol;
        this.statusCode = statusCode;
    }

    public String getStatusLine() {
        return protocol + " " + statusCode.getCode() + " " + statusCode.getMessage() + " ";
    }
}
