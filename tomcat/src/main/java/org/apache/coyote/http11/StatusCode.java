package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, "HTTP/1.1 200 OK "),
    UNAUTHORIZED(401, "HTTP/1.1 401 Unauthorized"),
    NOT_FOUND(404, "HTTP/1.1 404 Not Found"),
    REDIRECT(302, "HTTP/1.1 302 Found");

    private final int code;
    private final String statusLine;

    StatusCode(int code, String statusLine) {
        this.code = code;
        this.statusLine = statusLine;
    }

    public int getCode() {
        return code;
    }

    public String getStatusLine() {
        return statusLine;
    }
}
