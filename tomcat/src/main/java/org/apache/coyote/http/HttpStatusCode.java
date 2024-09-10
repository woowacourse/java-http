package org.apache.coyote.http;

public enum HttpStatusCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    ;

    private final int value;
    private final String reasonPhrase;

    HttpStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return value;
    }

    public String asString() {
        return value + " " + reasonPhrase;
    }
}
