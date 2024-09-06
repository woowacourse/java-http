package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "UnAuthorized"),
    NOT_FOUND(404, "Not Found")
    ;

    private final int value;
    private final String name;

    HttpStatusCode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
