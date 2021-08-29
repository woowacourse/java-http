package nextstep.jwp.http.response;

public enum ResponseStatus {
    OK("OK", 200), FOUND("Found", 302), NOT_FOUND("Not Found", 404), UNAUTHORIZED("Unauthorized", 401);

    private final String message;
    private final int code;

    ResponseStatus(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getCodeAsString() {
        return String.valueOf(code);
    }
}
