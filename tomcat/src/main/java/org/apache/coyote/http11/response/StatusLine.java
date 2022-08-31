package org.apache.coyote.http11.response;

public class StatusLine {

    private final String version;
    private final HttpStatus status;

    public StatusLine(final HttpStatus status) {
        this("HTTP/1.1", status);
    }

    public StatusLine(final String version, final HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.join(
                " ",
                version,
                String.valueOf(status.getCode()),
                status.getMessage()
                );
    }
}
