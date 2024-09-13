package org.apache.coyote.http;

public enum HttpStatusCode implements HttpComponent {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT_FOUND"),
    ;

    private final int code;
    private final String reasonPhrase;

    HttpStatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public String asString() {
        return code + " " + reasonPhrase;
    }
}
