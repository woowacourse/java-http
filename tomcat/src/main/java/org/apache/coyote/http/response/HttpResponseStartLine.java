package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpStatusCode;

public class HttpResponseStartLine {

    private HttpStatusCode statusCode;

    public String getStatusCode() {
        return statusCode.getValue();
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
