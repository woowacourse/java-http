package nextstep.jwp.framework.http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
