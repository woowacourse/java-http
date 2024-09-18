package org.apache.coyote.http;

public class HttpStatusLine implements HttpComponent {

    private HttpVersion version;
    private HttpStatusCode statusCode;

    public HttpStatusLine(final HttpStatusCode statusCode) {
        this.version = HttpVersion.HTTP11;
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String asString() {
        return version.asString() + SPACE + statusCode.asString() + SPACE;
    }
}
