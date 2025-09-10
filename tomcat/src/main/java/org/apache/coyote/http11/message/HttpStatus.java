package org.apache.coyote.http11.message;

public enum HttpStatus {

    OK("OK", 200),
    FOUND("FOUND", 302),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    ;

    private final String reasonPhrase;
    private final int code;

    HttpStatus(String reasonPhrase, int code) {
        this.reasonPhrase = reasonPhrase;
        this.code = code;
    }

    public String getStatus() {
        return reasonPhrase;
    }

    public int getStatusCode() {
        return code;
    }
}
