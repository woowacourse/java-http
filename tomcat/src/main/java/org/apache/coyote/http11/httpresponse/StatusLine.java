package org.apache.coyote.http11.httpresponse;

public class StatusLine {

    private final String httpVersion;
    private final int statusCode;
    private final String statusMessage;

    public StatusLine(String httpVersion, HttpStatusCode httpStatusCode) {
        this(httpVersion, httpStatusCode.getCode(), httpStatusCode.getMessage());
    }

    public StatusLine(String httpVersion, int statusCode, String statusMessage) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getMessage() {
        return httpVersion + " " + statusCode + " " + statusMessage + " ";
    }
}
