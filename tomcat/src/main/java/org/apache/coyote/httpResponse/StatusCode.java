package org.apache.coyote.httpResponse;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "BadRequest"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "NotFound"),
    NOT_ALLOW_METHOD(405, "MethodNotAllowed"),
    INTERNAL_SERVER_ERROR(500, "InternalServerError");

    private final int code;
    private final String message;

    StatusCode(
            final int code,
            final String message
    ) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
