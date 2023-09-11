package org.apache.coyote.http11.response;

public class StatusLine {
    public static final StatusLine EMPTY = new StatusLine("", -1, "");

    private String httpVersion;
    private int httpStatusCode;
    private String httpStatusName;

    private StatusLine(final String httpVersion, final int httpStatusCode, final String httpStatusName) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.httpStatusName = httpStatusName;
    }

    public String getStatusLine() {
        return httpVersion + " " + httpStatusCode + " " + httpStatusName + " ";
    }

    public void setHttpStatusLine(final HttpStatus httpStatus) {
        this.httpVersion = "HTTP/1.1";
        this.httpStatusCode = httpStatus.getCode();
        this.httpStatusName = httpStatus.name();
    }
}
