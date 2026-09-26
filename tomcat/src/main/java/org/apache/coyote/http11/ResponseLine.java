package org.apache.coyote.http11;

public class ResponseLine {
    private final String version;
    private final HttpStatus status;

    public ResponseLine(String version, HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public ResponseLine changeStatus(HttpStatus status) {
        return new ResponseLine(version, status);
    }

    @Override
    public String toString() {
        return String.format("%s %d %s", version, status.getCode(), status.getMessage());
    }
}
