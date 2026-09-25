package org.apache.coyote.http11;

import java.util.Objects;

public class ResponseLine {
    private final HttpVersion httpVersion;
    private final HttpStatusCode httpStatusCode;
    private final ReasonPhrase reasonPhrase;

    public ResponseLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode, ReasonPhrase reasonPhrase) {
        this.httpVersion = Objects.requireNonNull(httpVersion);
        this.httpStatusCode = Objects.requireNonNull(httpStatusCode);
        this.reasonPhrase = Objects.requireNonNull(reasonPhrase);
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
