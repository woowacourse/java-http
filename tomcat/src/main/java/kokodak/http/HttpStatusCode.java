package kokodak.http;

public enum HttpStatusCode {

    OK(200, "OK"),

    FOUND(302, "Found"),

    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private int statusCode;
    private String statusMessage;

    HttpStatusCode(final int statusCode, final String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
