package nextstep.jwp.http;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatus() {
        return code + " " + message;
    }
}
