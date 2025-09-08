package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    ;

    private final int status;

    HttpStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.join(" ",
                String.valueOf(this.status),
                this.name()
        );
    }
}
