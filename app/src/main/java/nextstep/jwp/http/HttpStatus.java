package nextstep.jwp.http;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int status;
    private final String statusMessage;

    HttpStatus(int httpStatus, String statusMessage) {
        this.status = httpStatus;
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
