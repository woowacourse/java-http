package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int code;
    private final String responsePhrase;

    HttpStatus(final int code, final String responsePhrase) {
        this.code = code;
        this.responsePhrase = responsePhrase;
    }

    public int getCode() {
        return code;
    }

    public String getResponsePhrase() {
        return responsePhrase;
    }
}
