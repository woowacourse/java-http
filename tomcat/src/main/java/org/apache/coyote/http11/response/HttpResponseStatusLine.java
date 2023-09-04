package org.apache.coyote.http11.response;

public class HttpResponseStatusLine {

    private final String httpVersion;
    private final HttpStatusCode statusCode;

    public HttpResponseStatusLine(final String httpVersion, final HttpStatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.format("%s %s ", httpVersion, statusCode.toString());
    }
}
