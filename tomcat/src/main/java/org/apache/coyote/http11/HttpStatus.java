package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_ERROR(500, "INTERNAL ERROR"),
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus of(int code, String reasonPhrase) {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (httpStatus.code == code && httpStatus.reasonPhrase.equals(reasonPhrase)) {
                return httpStatus;
            }
        }
        return INTERNAL_ERROR;
    }
}
