package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200),
    FOUND(302),
    NOT_FOUND(404);

    private final int value;

    HttpStatusCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
