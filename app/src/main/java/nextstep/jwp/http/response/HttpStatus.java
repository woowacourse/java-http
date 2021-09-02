package nextstep.jwp.http.response;

public enum HttpStatus {
    OK("OK", "200"),
    FOUND("Found", "302"),
    UNAUTHORIZED("Unauthorized", "401"),
    NOT_FOUND("Not Found", "404"),
    INTERNAL_SERVER_ERROR("Internal Server Error", "500");

    private final String message;
    private final String code;

    HttpStatus(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
