package org.apache.coyote.http11;

enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error")
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    int getCode() {
        return code;
    }

    String getReasonPhrase() {
        return reasonPhrase;
    }
}
