package org.apache.coyote.http11;

public class StatusLine {

    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final int OK_STATUS_CODE = 200;
    private static final String OK_STATUS_TEXT = "OK";
    private static final int REDIRECT_STATUS_CODE = 302;
    private static final String REDIRECT_STATUS_TEXT = "Found";
    private static final int NOT_FOUND_STATUS_CODE = 404;
    private static final String NOT_FOUND_STATUS_TEXT = "Not Found";

    private final String httpVersion;
    private final int statusCode;
    private final String statusText;

    public StatusLine(String httpVersion, int statusCode, String statusText) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public static StatusLine ok() {
        return new StatusLine(HTTP_1_1, OK_STATUS_CODE, OK_STATUS_TEXT);
    }

    public static StatusLine redirect() {
        return new StatusLine(HTTP_1_1, REDIRECT_STATUS_CODE, REDIRECT_STATUS_TEXT);
    }

    public static StatusLine notFound() {
        return new StatusLine(HTTP_1_1, NOT_FOUND_STATUS_CODE, NOT_FOUND_STATUS_TEXT);
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + statusText;
    }
}
