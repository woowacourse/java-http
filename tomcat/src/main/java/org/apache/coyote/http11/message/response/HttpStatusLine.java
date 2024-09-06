package org.apache.coyote.http11.message.response;

public class HttpStatusLine {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private final String httpVersion;
    private HttpStatus httpStatus;


    public HttpStatusLine(String httpVersion, HttpStatus httpStatus) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
    }

    public HttpStatusLine(HttpStatus httpStatus) {
        this(DEFAULT_VERSION, httpStatus);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return httpVersion + " " + httpStatus.getStatusCode() + " " + httpStatus.getMessage() + " ";
    }
}
