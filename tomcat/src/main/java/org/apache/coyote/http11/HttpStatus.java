package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    BAD_REQUEST(400),
    ;

    private final int status;

    HttpStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
