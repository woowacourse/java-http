package nextstep.jwp.web.network.response;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String name;

    HttpStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int toCode() {
        return code;
    }

    public String toReasonPhrase() {
        return name;
    }
}
