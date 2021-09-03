package nextstep.jwp.http.response;

public enum ResponseStatus {
    NOT_FOUND("404", "Not Found"),
    FOUND("302", "Found"),
    OK("200", "OK");

    private final String statusCode;
    private final String statusMessage;

    ResponseStatus(String statusCode, String statusMessage) {
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
