package nextstep.jwp.model.web;

public enum StatusCode {
    OK("OK", 200),
    NOT_FOUND("NOT FOUND", 404),
    FOUND("FOUND", 302),
    CREATED("CREATED", 201),
    NOT_AUTHORIZED("NOT AUTHORIZED", 401);

    private final String statusMessage;
    private final int statusCode;

    StatusCode(String statusMessage, int statusCode) {
        this.statusMessage = statusMessage;
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
