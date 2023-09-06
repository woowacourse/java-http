package org.apache.coyote.http11;

public class HttpResponseStatusLine {

    public static final String HTTP_1_1 = "HTTP/1.1";

    private final HttpStatusCode statusCode;

    public HttpResponseStatusLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return HTTP_1_1 + " " + statusCode;
    }
}
