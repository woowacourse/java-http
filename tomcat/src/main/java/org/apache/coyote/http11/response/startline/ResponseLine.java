package org.apache.coyote.http11.response.startline;

public class ResponseLine {

    private final String httpVersion;
    private final HttpStatusCode statusCode;

    public ResponseLine(final String httpVersion, final HttpStatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public static ResponseLine of(final HttpStatusCode statusCode) {
        return new ResponseLine("HTTP/1.1", statusCode);
    }

    public String toResponseText() {
        return String.format("%s %s %s", httpVersion, statusCode.getStatusCode(), statusCode.getStatusMessage());
    }
}
