package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK", "/"),
    Found(302, "Found", "/index.html"),
    BAD_REQUEST(400, "Bad Request", "/400.html"),
    UNAUTHORIZED(401, "Unauthorized", "/401.html"),
    NOT_FOUND(404, "Not Found", "/404.html"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "/4xx.html"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "/500.html");

    private final int statusCode;
    private final String reasonPhrase;
    private final String resourcePath;

    HttpStatus(int statusCode, String reasonPhrase, String resourcePath) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.resourcePath = resourcePath;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
