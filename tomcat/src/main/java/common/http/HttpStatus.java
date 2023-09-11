package common.http;

public enum HttpStatus {
    OK("OK", 200),
    CREATED("Created", 201),
    FOUND("Found", 302),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500);

    private final String statusMessage;
    private final int statusCode;

    HttpStatus(String statusMessage, int statusCode) {
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
