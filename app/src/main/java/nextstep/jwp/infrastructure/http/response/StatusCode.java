package nextstep.jwp.infrastructure.http.response;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERRROR");

    private final int value;
    private final String message;

    StatusCode(final int value, final String message) {
        this.value = value;
        this.message = message;
    }

    public int value() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
