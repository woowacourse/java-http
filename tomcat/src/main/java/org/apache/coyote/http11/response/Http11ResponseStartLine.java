package org.apache.coyote.http11.response;

public class Http11ResponseStartLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatusCode statusCode;

    public Http11ResponseStartLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static Http11ResponseStartLine defaultLine() {
        return new Http11ResponseStartLine(HttpStatusCode.OK);
    }

    @Override
    public String toString() {
        return HTTP_VERSION + " " + statusCode.toString() + " ";
    }
}
