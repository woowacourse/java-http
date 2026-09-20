package org.apache.coyote.http11;

public class ResponseLine {
    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;
    private final ReasonPhrase reasonPhrase;

    public ResponseLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode, ReasonPhrase reasonPhrase) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public ReasonPhrase getReasonPhrase() {
        return reasonPhrase;
    }
}
