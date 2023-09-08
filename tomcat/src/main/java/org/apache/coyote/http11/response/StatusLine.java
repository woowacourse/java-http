package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocol;
    private final HttpStatus httpStatus;

    private StatusLine(final String protocol, final HttpStatus httpStatus) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
    }

    public static StatusLine of(final String protocol, final HttpStatus httpStatus) {
        return new StatusLine(protocol, httpStatus);
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ", protocol, String.valueOf(httpStatus.getCode()), httpStatus.getMessage());
    }
}
