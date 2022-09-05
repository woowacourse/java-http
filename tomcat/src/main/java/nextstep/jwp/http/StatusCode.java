package nextstep.jwp.http;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized")
    ;

    private static final String STATUS_FORMAT = "%d %s";
    private final int statusCode;
    private final String reason;

    StatusCode(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public String getStatus() {
        return String.format(STATUS_FORMAT, this.statusCode, this.reason);
    }
}
