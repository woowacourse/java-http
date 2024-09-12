package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpStatusCode;

public class HttpResponseStartLine {

    private final HttpStatusCode statusCode;

    public HttpResponseStartLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode.getValue();
    }
}
