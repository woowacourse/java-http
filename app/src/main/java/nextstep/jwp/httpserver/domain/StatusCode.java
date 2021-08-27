package nextstep.jwp.httpserver.domain;

public enum StatusCode {
    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String statusText;

    StatusCode(int code, String statusText) {
        this.code = code;
        this.statusText = statusText;
    }

    public int getCode() {
        return code;
    }

    public String getStatusText() {
        return statusText;
    }
}
