package org.apache.coyote;

public class HttpResponseStartLine {

    private final HttpStatusCode statusCode;

    public HttpResponseStartLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode.getValue();
    }
}
