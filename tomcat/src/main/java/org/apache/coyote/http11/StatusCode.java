package org.apache.coyote.http11;

public enum StatusCode {
    OK(200), FOUND(302), BAD_REQUEST(400), UNAUTHORIZED(401), INTERNAL_SERVER_ERROR(500);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
