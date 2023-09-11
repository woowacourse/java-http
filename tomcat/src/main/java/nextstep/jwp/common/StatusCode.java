package nextstep.jwp.common;

public enum StatusCode {
    OK("200", "OK"),
    FOUND("302", "Found"),
    NOT_FOUND("404", "Not Found"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String statusCode;
    private final String statusMessage;

    StatusCode(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
