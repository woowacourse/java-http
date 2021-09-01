package nextstep.jwp.http.response;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("Found", 302),
    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500);

    private final String status;
    private final int code;

    HttpStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }

    public int code() {
        return code;
    }

    public String status() {
        return status;
    }
}
