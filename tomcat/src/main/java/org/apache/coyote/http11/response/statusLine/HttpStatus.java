package org.apache.coyote.http11.response.statusLine;

public enum HttpStatus {

    OK(200, "OK", ""),
    CREATED(201, "Created", ""),
    NO_CONTENT(204, "No Content", ""),
    FOUND(302, "Found", ""),
    BAD_REQUEST(400, "Bad Request", ""),
    UNAUTHORIZED(401, "Unauthorized", "401.html"),
    FORBIDDEN(403, "Forbidden", ""),
    NOT_FOUND(404, "Not Found", "404.html"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "500.html");

    private final int code;
    private final String message;
    private final String resourceName;

    HttpStatus(final int code, final String message, final String resourceName) {
        this.code = code;
        this.message = message;
        this.resourceName = resourceName;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getResourceName() {
        return resourceName;
    }
}
