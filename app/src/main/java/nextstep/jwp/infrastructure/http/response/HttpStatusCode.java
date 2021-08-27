package nextstep.jwp.infrastructure.http.response;

public enum HttpStatusCode {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500);

    private final int value;

    HttpStatusCode(final int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
