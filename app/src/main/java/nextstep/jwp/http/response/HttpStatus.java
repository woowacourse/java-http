package nextstep.jwp.http.response;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "CREATED"),

    FOUND(302, "FOUND"),

    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),

    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
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
