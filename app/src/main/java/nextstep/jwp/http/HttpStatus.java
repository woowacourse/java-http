package nextstep.jwp.http;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    SEE_OTHER(303, "See Other");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
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
