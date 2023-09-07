package nextstep.jwp.http;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "FOUND");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
