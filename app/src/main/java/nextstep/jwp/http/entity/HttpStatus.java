package nextstep.jwp.http.entity;

public enum HttpStatus {
    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    // 3xx Redirection
    FOUND(302, "Found"),

    // --- 4xx Client Error ---

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    // --- 5xx Server Error ---

    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int value;
    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus valueOf(int statusCode) {
        HttpStatus httpStatus = resolve(statusCode);
        if (httpStatus == null) {
            throw new IllegalArgumentException("StatusCode " + statusCode + "에 해당하는 HttpStatus는 존재하지 않습니다.");
        }
        return httpStatus;
    }

    private static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    public int value() {
        return this.value;
    }

    public String reasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public String toString() {
        return value + " " + reasonPhrase;
    }
}
