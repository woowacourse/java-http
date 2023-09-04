package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatus of(final int code) {
        for (HttpStatus httpStatus : values()) {
            if (httpStatus.code == code) {
                return httpStatus;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 상태 코드입니다.");
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStatusResponse() {
        return code + " " + message;
    }
}
