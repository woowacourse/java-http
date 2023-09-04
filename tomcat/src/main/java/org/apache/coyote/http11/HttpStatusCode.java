package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200),
    FOUND(302),
    BAD_REQUEST(400);

    private final int statusCode;

    HttpStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isRedirect() {
        return statusCode >= 300 && statusCode < 400;
    }

    public String toResponseFormat() {
        return statusCode + " " + name();
    }
}
