package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

    private final int status;

    HttpStatus(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
