package org.apache.catalina.http.startline;

import java.util.Objects;

public class HttpResponseLine {

    private final HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public HttpResponseLine(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public boolean isValid() {
        return Objects.nonNull(httpVersion) && Objects.nonNull(httpStatus);
    }

    public String read() {
        return httpVersion.getValue() + " " + httpStatus.getValue();
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
