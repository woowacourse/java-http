package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
