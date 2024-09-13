package com.techcourse.resolver;

public enum HttpStatus {
    OK(200, "ok"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "method not allowed"),
    INTERNAL_SERVER_ERROR(500, "internal server error");

    private final Integer code;
    private final String message;

    HttpStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
