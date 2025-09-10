package org.apache.coyote.http11.message;

public enum HttpStatus {

    OK("OK", 200),
    FOUND("FOUND", 302),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    ;

    // TODO: Status - StatusCode 네이밍 정리하기
    private final String status;
    private final int statusCode;

    HttpStatus(String status, int statusCode) {
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
