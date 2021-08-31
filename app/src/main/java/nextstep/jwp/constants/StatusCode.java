package nextstep.jwp.constants;

public enum StatusCode {
    OK("200", "OK"),
    FOUND("302", "Found"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String statusCode;
    private final String status;

    StatusCode(String statusCode, String status) {
        this.statusCode = statusCode;
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }
}
