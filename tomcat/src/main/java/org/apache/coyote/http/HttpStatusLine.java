package org.apache.coyote.http;

public class HttpStatusLine implements HttpComponent {

    private final HttpVersion version;
    private final HttpStatusCode statusCode;

    public HttpStatusLine(final HttpVersion version, final HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String asString() {
        return version.asString() + SPACE + statusCode.asString() + SPACE;
    }
}
