package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

public class ResponseStatusLine {

    private final String version;
    private HttpStatus status;

    public ResponseStatusLine(final HttpStatus status) {
        this("HTTP/1.1", status);
    }

    public ResponseStatusLine(final String version, final HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.join(
                " ",
                version,
                String.valueOf(status.getCode()),
                status.getMessage(),
                ""
                );
    }
}
