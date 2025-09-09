package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOTFOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    HttpStatusCode(int status, String statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    private int status;
    private String statusCode;

    public int getStatus() {
        return status;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
