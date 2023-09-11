package common.http;

public enum HttpStatus {
    OK("OK", 200),
    CREATED("Created", 201),
    ACCEPTED("Accepted", 202),
    NO_CONTENT("No Content", 204),
    MOVED_PERMANENTLY("Moved Permanently", 301),
    PERMANENT_REDIRECT("Permanent Redirect", 308),
    FOUND("Found", 302),
    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    FORBIDDEN("Forbidden", 403),
    NOT_FOUND("Not Found", 404),
    CONFLICT("Conflict", 409),
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
