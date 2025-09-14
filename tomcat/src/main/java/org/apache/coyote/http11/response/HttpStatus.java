package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200),
    NO_CONTENT(204),
    FOUND(302),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    NONE(0),
    ;

    private final int status;

    HttpStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.join(" ",
                String.valueOf(this.status),
                this.name()
        );
    }
}
