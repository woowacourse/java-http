package nextstep.jwp.http.response;

public enum HttpStatus {
    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
