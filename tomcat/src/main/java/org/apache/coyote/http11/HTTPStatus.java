package org.apache.coyote.http11;

public enum HTTPStatus {
    OK("OK", 200),
    FOUND("FOUND", 302);

    private final String status;
    private final int statusCode;

    HTTPStatus(String status, int statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
