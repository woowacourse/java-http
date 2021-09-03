package nextstep.jwp.http;

public enum HttpStatus {

    OK("OK", 200),
    FOUND("Found", 302),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404),
    METHOD_NOT_ALLOWED("Method Not Allowed", 405);

    private final String message;
    private final int code;

    HttpStatus(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
