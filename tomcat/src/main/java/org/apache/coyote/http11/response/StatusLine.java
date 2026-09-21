package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String DEFAULT_PROTOCOL = "HTTP";
    private static final String DEFAULT_VERSION = "1.1";
    private static final String TOKEN_SEPARATOR = " ";
    private static final String PROTOCOL_SEPARATOR = "/";

    private final String protocol;
    private final String version;
    private final HttpStatus status;

    private StatusLine(final String protocol, final String version, final HttpStatus status) {
        this.protocol = protocol;
        this.version = version;
        this.status = status;
    }

    public static StatusLine from(final HttpStatus status) {
        return new StatusLine(DEFAULT_PROTOCOL, DEFAULT_VERSION, status);
    }

    @Override
    public String toString() {
        return protocol + PROTOCOL_SEPARATOR + version + TOKEN_SEPARATOR
                + status.getCode() + TOKEN_SEPARATOR + status.getReasonPhrase() + TOKEN_SEPARATOR;
    }
}