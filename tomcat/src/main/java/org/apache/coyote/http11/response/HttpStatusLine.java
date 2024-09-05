package org.apache.coyote.http11.response;

public class HttpStatusLine {

    private final String httpVersion;
    private HttpStatus httpStatus;

    public HttpStatusLine(HttpStatus httpStatus) {
        this.httpVersion = "HTTP/1.1";
        this.httpStatus = httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return httpVersion + " " + httpStatus.getStatusCode()+ " " + httpStatus.getMessage();
    }
}
